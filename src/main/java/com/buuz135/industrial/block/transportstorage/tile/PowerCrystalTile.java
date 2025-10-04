package com.buuz135.industrial.block.transportstorage.tile;

import com.buuz135.industrial.api.IMachineSettings;
import com.buuz135.industrial.block.transportstorage.PowerCrystalBlock;
import com.buuz135.industrial.block_network.PowerCrystalNetwork;
import com.buuz135.industrial.block_network.PowerCrystalNetworkManager;
import com.buuz135.industrial.gui.component.PowerCrystalGuiAddon;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.api.client.IScreenAddonProvider;
import com.hrznstudio.titanium.block.tile.BasicTile;
import com.hrznstudio.titanium.block.tile.ITickableBlockEntity;
import com.hrznstudio.titanium.client.screen.addon.StateButtonAddon;
import com.hrznstudio.titanium.client.screen.addon.StateButtonInfo;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.client.screen.asset.IHasAssetProvider;
import com.hrznstudio.titanium.component.button.ButtonComponent;
import com.hrznstudio.titanium.container.BasicAddonContainer;
import com.hrznstudio.titanium.container.addon.IContainerAddon;
import com.hrznstudio.titanium.container.addon.IContainerAddonProvider;
import com.hrznstudio.titanium.network.IButtonHandler;
import com.hrznstudio.titanium.network.locator.LocatorFactory;
import com.hrznstudio.titanium.network.locator.instance.TileEntityLocatorInstance;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PowerCrystalTile extends BasicTile<PowerCrystalTile> implements ITickableBlockEntity<PowerCrystalTile>, IMachineSettings, IScreenAddonProvider, MenuProvider, IButtonHandler, IContainerAddonProvider, IHasAssetProvider {

    private final PowerCrystalBlock base;
    @Save
    private String networkId;
    @Save
    private BlockPosCollection connections;
    @Save
    private BlockPosCollection directConnections;
    @Save
    private DirectionalBlockPosCollection powerConnections;
    @Save
    private double cachedDistance;
    @Save
    private boolean extracting;
    private ButtonComponent extractButton;


    public PowerCrystalTile(PowerCrystalBlock base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state);
        this.networkId = "";
        this.connections = new BlockPosCollection();
        this.directConnections = new BlockPosCollection();
        this.powerConnections = new DirectionalBlockPosCollection();
        this.base = base;
        this.cachedDistance = 0;
        this.extracting = false;
        this.extractButton = new ButtonComponent(15, 22 + 12, 14, 14) {
            @Override
            @OnlyIn(Dist.CLIENT)
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                List<IFactory<? extends IScreenAddon>> addons = new ArrayList<>();
                addons.add(() -> new StateButtonAddon(extractButton, new StateButtonInfo(0, AssetTypes.BUTTON_SIDENESS_PUSH, "Click to change"), new StateButtonInfo(1, AssetTypes.BUTTON_SIDENESS_PULL, "Click to change")) {
                    @Override
                    public int getState() {
                        return extracting ? 1 : 0;
                    }
                });
                return addons;
            }
        }.setPredicate((playerEntity, compoundNBT) -> {
            this.extracting = !this.extracting;
            this.markForUpdate();
        });
        this.extractButton.setId(1);
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, PowerCrystalTile blockEntity) {
        if (level instanceof ServerLevel serverLevel) {
            // Periodically cleanup dead links
            if (serverLevel.getGameTime() % 5 == 0) {
                boolean somethingChanged = false;
                for (BlockPos position : new ArrayList<>(this.connections.getPositions())) {
                    if (!(serverLevel.getBlockEntity(position) instanceof PowerCrystalTile)) {
                        this.connections.getPositions().remove(position);
                        somethingChanged = true;
                    }
                }
                for (BlockPos position : new ArrayList<>(this.directConnections.getPositions())) {
                    if (!(serverLevel.getBlockEntity(position) instanceof PowerCrystalTile)) {
                        this.directConnections.getPositions().remove(position);
                        somethingChanged = true;
                    }
                }
                for (BlockPos position : new ArrayList<>(this.powerConnections.getPositions().keySet())) {
                    if (serverLevel.getCapability(Capabilities.EnergyStorage.BLOCK, position, null) != null) {
                        this.powerConnections.getPositions().remove(position);
                        somethingChanged = true;
                    }
                }
                if (somethingChanged) {
                    syncConnections();
                }
            }

            // Ensure a single network instance per id via the manager, and compute the current id for this tile
            PowerCrystalNetworkManager manager = PowerCrystalNetworkManager.get(serverLevel);
            // This will compute the id from the world graph and store/reuse a single instance per id
            PowerCrystalNetwork network = manager.getOrBuild(serverLevel, this.getBlockPos());

            // Compute and sync network id based on current world component
            String newId = PowerCrystalNetwork.getNetworkId(serverLevel, this.getBlockPos());
            if (!newId.equals(this.networkId)) {
                this.networkId = newId;
                markForUpdate();
                syncObject(this.networkId);
            }
        }
    }

    public Vec3 getStartPosition() {
        return new Vec3(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5);
    }

    public List<BlockPos> getConnections() {
        return connections.getPositions();
    }

    public List<BlockPos> getDirectConnections() {
        return directConnections.getPositions();
    }

    public HashMap<BlockPos, Direction> getPowerConnections() {
        return powerConnections.getPositions();
    }

    public void syncConnections() {
        this.cachedDistance = 0;
        for (BlockPos position : this.connections.getPositions()) {
            this.cachedDistance += Math.sqrt(position.distToCenterSqr(getStartPosition()));
        }
        syncObject(this.cachedDistance);
        syncObject(this.connections);
        syncObject(this.directConnections);
        syncObject(this.powerConnections);
        markForUpdate();
    }

    @Override
    public ItemInteractionResult onActivated(Player player, InteractionHand hand, Direction facing, double hitX, double hitY, double hitZ) {
        if (player instanceof ServerPlayer sp) {
            sp.openMenu(this, (buffer) -> LocatorFactory.writePacketBuffer(buffer, new TileEntityLocatorInstance(this.worldPosition)));
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public boolean loadSettings(Player player, CompoundTag tag) {
        if (tag.contains("PowerCrystalPosition")) {
            var otherPos = BlockPos.of(tag.getLong("PowerCrystalPosition"));
            var otherBe = this.level.getBlockEntity(otherPos);
            if (otherBe instanceof PowerCrystalTile otherTile) {
                if (this.acceptsDistanceConnection(otherPos) &&
                        otherTile.acceptsDistanceConnection(new BlockPos.MutableBlockPos(this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 0.5, this.getBlockPos().getZ() + 0.5))) {
                    // Ensure bidirectional connection
                    if (!otherTile.getConnections().contains(this.getBlockPos())) {
                        otherTile.getConnections().add(this.getBlockPos());
                        otherTile.getDirectConnections().add(this.getBlockPos());
                        otherTile.syncConnections();
                    }
                    if (!this.getConnections().contains(otherPos)) {
                        this.getConnections().add(otherPos);
                        this.syncConnections();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean acceptsDistanceConnection(BlockPos pos) {
        var currentDistance = Math.sqrt(pos.distToCenterSqr(getStartPosition()));
        return currentDistance + this.cachedDistance <= base.getMaxDistance();
    }

    @Override
    public void saveSettings(Player player, CompoundTag tag) {
        tag.putLong("PowerCrystalPosition", this.getBlockPos().asLong());
    }

    @Override
    public @NotNull List<IFactory<? extends IScreenAddon>> getScreenAddons() {
        List<IFactory<? extends IScreenAddon>> list = new ArrayList<>();
        list.add(() -> new PowerCrystalGuiAddon(this, 16, 22));
        list.addAll(extractButton.getScreenAddons());
        return list;
    }

    @Override
    public IAssetProvider getAssetProvider() {
        return IAssetProvider.DEFAULT_PROVIDER;
    }

    @Override
    public @NotNull List<IFactory<? extends IContainerAddon>> getContainerAddons() {
        return List.of();
    }

    @Override
    public void handleButtonMessage(int i, Player player, CompoundTag compoundTag) {
        if (i == 1) {
            this.extracting = !this.extracting;
            syncObject(this.extracting);
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(this.getBasicTileBlock().getDescriptionId()).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY));
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int menu, Inventory inventoryPlayer, Player player) {
        return new BasicAddonContainer(this, new TileEntityLocatorInstance(this.worldPosition), this.getWorldPosCallable(), inventoryPlayer, menu);
    }

    public ContainerLevelAccess getWorldPosCallable() {
        return this.getLevel() != null ? ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()) : ContainerLevelAccess.NULL;
    }

    public PowerCrystalBlock getBase() {
        return base;
    }

    public double getCachedDistance() {
        return cachedDistance;
    }

    public boolean isExtracting() {
        return extracting;
    }

    public class BlockPosCollection implements INBTSerializable<CompoundTag> {

        private final List<BlockPos> positions;

        public BlockPosCollection() {
            this.positions = new ArrayList<>();
        }

        public List<BlockPos> getPositions() {
            return positions;
        }

        @Override
        public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
            CompoundTag compoundTag = new CompoundTag();
            positions.forEach(pos -> compoundTag.putLong(pos.asLong() + "", pos.asLong()));
            return compoundTag;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
            this.positions.clear();
            compoundTag.getAllKeys().forEach(key -> this.positions.add(BlockPos.of(Long.parseLong(key))));
        }
    }

    public class DirectionalBlockPosCollection implements INBTSerializable<CompoundTag> {

        private final HashMap<BlockPos, Direction> positions;

        public DirectionalBlockPosCollection() {
            this.positions = new HashMap<>();
        }

        public HashMap<BlockPos, Direction> getPositions() {
            return positions;
        }

        @Override
        public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
            CompoundTag compoundTag = new CompoundTag();
            positions.forEach((pos, dir) -> compoundTag.putString(pos.asLong() + "", dir.name()));
            return compoundTag;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
            this.positions.clear();
            compoundTag.getAllKeys().forEach(key -> this.positions.put(BlockPos.of(Long.parseLong(key)), Direction.valueOf(compoundTag.getString(key))));
        }
    }

}

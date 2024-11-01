package com.buuz135.industrial.block.tool.tile;

import com.buuz135.industrial.item.infinity.item.ItemInfinityBackpack;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleTool;
import com.buuz135.industrial.utils.IFAttachments;
import com.buuz135.industrial.worlddata.BackpackDataManager;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.api.client.IScreenAddonProvider;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.BasicTile;
import com.hrznstudio.titanium.block.tile.ITickableBlockEntity;
import com.hrznstudio.titanium.container.BasicAddonContainer;
import com.hrznstudio.titanium.network.IButtonHandler;
import com.hrznstudio.titanium.network.locator.LocatorFactory;
import com.hrznstudio.titanium.network.locator.instance.TileEntityLocatorInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InfinityBackpackTile extends BasicTile<InfinityBackpackTile> implements MenuProvider, IScreenAddonProvider, IButtonHandler, ITickableBlockEntity<InfinityBackpackTile> {

    @Save
    private ItemStack backpack;

    public InfinityBackpackTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<InfinityBackpackTile>) ModuleTool.INFINITY_BACKPACK_BLOCK.getBlock(), ModuleTool.INFINITY_BACKPACK_BLOCK.type().get(), pos, state);
        this.backpack = ItemStack.EMPTY;
    }

    public ItemStack getBackpack() {
        return backpack;
    }

    public void setBackpack(ItemStack backpack) {
        this.backpack = backpack;
        if (!this.backpack.has(IFAttachments.INFINITY_BACKPACK_ID)) {
            UUID id = UUID.randomUUID();
            BackpackDataManager.getData(getLevel()).createBackPack(id);
            this.backpack.set(IFAttachments.INFINITY_BACKPACK_ID, id.toString());
        }
        syncObject(this.backpack);
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, InfinityBackpackTile blockEntity) {
        if (level.getGameTime() % 4 == 0) {
            for (Player entitiesOfClass : this.level.getEntitiesOfClass(Player.class, new AABB(this.getBlockPos()).inflate(8))) {
                if (entitiesOfClass instanceof ServerPlayer serverPlayer) {
                    ItemInfinityBackpack.sync(getLevel(), this.backpack.get(IFAttachments.INFINITY_BACKPACK_ID), serverPlayer);
                    syncObject(this.backpack);
                }
            }
        }
        if (level.getGameTime() % 2 == 0 && (ItemInfinityBackpack.getPickUpMode(getBackpack()) == 1 || ItemInfinityBackpack.getPickUpMode(getBackpack()) == 0)) {
            if (ItemInfinityBackpack.isMagnetEnabled(getBackpack())) {
                for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, new AABB(getBlockPos()).inflate(5))) {
                    if (!itemEntity.hasPickUpDelay() && ((ItemInfinityBackpack) ModuleTool.INFINITY_BACKPACK.get()).enoughFuel(getBackpack())) {
                        pickupItem(itemEntity, getBackpack());
                        ((ItemInfinityBackpack) ModuleTool.INFINITY_BACKPACK.get()).consumeFuel(getBackpack());
                        syncObject(this.backpack);
                    }
                }
            }
        }
        if (level.getGameTime() % 2 == 0 && (ItemInfinityBackpack.getPickUpMode(getBackpack()) == 2 || ItemInfinityBackpack.getPickUpMode(getBackpack()) == 0)) {
            var handlerItem = getBackpack().getCapability(Capabilities.FluidHandler.ITEM);
            if (handlerItem != null) {
                for (ExperienceOrb entity : level.getEntitiesOfClass(ExperienceOrb.class, new AABB(getBlockPos()).inflate(5))) {
                    if (((ItemInfinityBackpack) ModuleTool.INFINITY_BACKPACK.get()).enoughFuel(getBackpack())) {
                        if (handlerItem.fill(new FluidStack(ModuleCore.ESSENCE.getSourceFluid().get(), entity.getValue() * 20), IFluidHandler.FluidAction.SIMULATE) > 0) {
                            handlerItem.fill(new FluidStack(ModuleCore.ESSENCE.getSourceFluid().get(), entity.getValue() * 20), IFluidHandler.FluidAction.EXECUTE);
                            entity.remove(Entity.RemovalReason.KILLED);
                        }
                    }
                }
            }
        }
    }

    private void pickupItem(ItemEntity itemEntity, ItemStack stack) {
        BackpackDataManager manager = BackpackDataManager.getData(getLevel());
        if (manager != null && stack.has(IFAttachments.INFINITY_BACKPACK_ID)) {
            BackpackDataManager.BackpackItemHandler handler = manager.getBackpack(stack.get(IFAttachments.INFINITY_BACKPACK_ID));
            if (handler != null) {
                ItemStack picked = itemEntity.getItem();
                for (int pos = 0; pos < handler.getSlots(); pos++) {
                    ItemStack slotStack = handler.getSlotDefinition(pos).getStack().copy();
                    slotStack.setCount(1);
                    if (!slotStack.isEmpty() && ItemStack.isSameItemSameComponents(picked, slotStack)) {
                        ItemStack returned = handler.insertItem(pos, picked.copy(), false);
                        picked.setCount(returned.getCount());
                        return;
                    }
                }
            }
        }
    }

    @Override
    public ItemInteractionResult onActivated(Player player, InteractionHand hand, Direction facing, double hitX, double hitY, double hitZ) {
        if (!player.isShiftKeyDown() && player instanceof ServerPlayer serverPlayer && !backpack.isEmpty()) {
            ItemInfinityBackpack.sync(getLevel(), this.backpack.get(IFAttachments.INFINITY_BACKPACK_ID), serverPlayer);
            syncObject(this.backpack);
            serverPlayer.openMenu(this, buffer ->
                    LocatorFactory.writePacketBuffer(buffer, new TileEntityLocatorInstance(this.worldPosition)));
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.SUCCESS;
    }

    public MenuProvider getStackMenuProvider() {
        return (MenuProvider) ModuleTool.INFINITY_BACKPACK.get();
    }

    @Nullable
    public AbstractContainerMenu createMenu(int menu, Inventory inventoryPlayer, Player entityPlayer) {
        return new BasicAddonContainer(this, new TileEntityLocatorInstance(this.worldPosition), this.getWorldPosCallable(), inventoryPlayer, menu);
    }

    @Nonnull
    public Component getDisplayName() {
        return getStackMenuProvider().getDisplayName();
    }

    public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
        var backpackItem = (ItemInfinityBackpack) ModuleTool.INFINITY_BACKPACK.get();
        List<IFactory<? extends IScreenAddon>> addons = new ArrayList(backpackItem.getScreenAddons(this::getBackpack));
        var energy = this.backpack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy instanceof IScreenAddonProvider addonProvider) {
            addons.addAll(addonProvider.getScreenAddons());
        }
        var fluidHandlerItem = this.backpack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandlerItem instanceof IScreenAddonProvider addonProvider) {
            addons.addAll(addonProvider.getScreenAddons());
        }
        return addons;
    }

    public ContainerLevelAccess getWorldPosCallable() {
        return this.getLevel() != null ? ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()) : ContainerLevelAccess.NULL;
    }

    @Override
    public void handleButtonMessage(int i, Player player, CompoundTag compoundTag) {
        var backpackItem = (ItemInfinityBackpack) ModuleTool.INFINITY_BACKPACK.get();
        backpackItem.handleButtonMessage(i, player, compoundTag, getBackpack());
        syncObject(this.backpack);
    }
}

package com.buuz135.industrial.proxy.block;

import com.buuz135.industrial.api.conveyor.ConveyorUpgrade;
import com.buuz135.industrial.api.conveyor.ConveyorUpgradeFactory;
import com.buuz135.industrial.api.conveyor.IConveyorContainer;
import com.buuz135.industrial.gui.component.FilterGuiComponent;
import com.buuz135.industrial.gui.component.IGuiComponent;
import com.buuz135.industrial.gui.component.StateButtonInfo;
import com.buuz135.industrial.gui.component.TexturedStateButtonGuiComponent;
import com.buuz135.industrial.proxy.block.filter.IFilter;
import com.buuz135.industrial.proxy.block.filter.ItemStackFilter;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ConveyorDetectorUpgrade extends ConveyorUpgrade {

    public static Cuboid BB = new Cuboid(0.0625 * 3, 0.0625, 0.0625 * 3, 0.0625 * 13, 0.0625 * 1.2, 0.0625 * 13, EnumFacing.DOWN.getIndex(), false);

    private ItemStackFilter filter;
    private boolean hasEntity;
    private boolean whitelist;
    private boolean inverted;

    public ConveyorDetectorUpgrade(IConveyorContainer container, ConveyorUpgradeFactory factory, EnumFacing side) {
        super(container, factory, side);
        this.filter = new ItemStackFilter(20, 20, 5, 3);
        this.whitelist = true;
        this.hasEntity = false;
        this.inverted = false;
    }

    @Override
    public void update() {
        if (getWorld().isRemote)
            return;
        boolean previous = hasEntity;
        hasEntity = false;
        List<Entity> entities = getWorld().getEntitiesWithinAABB(Entity.class, getBoundingBox().aabb().offset(getPos()).grow(0.01));
        hasEntity = !entities.isEmpty() && whitelist == someoneMatchesFilter(entities);
        if (inverted) hasEntity = !hasEntity;
        if (previous != hasEntity)
            getWorld().notifyNeighborsOfStateChange(getPos(), getWorld().getBlockState(getPos()).getBlock(), true);
    }

    private boolean someoneMatchesFilter(List<Entity> entities) {
        for (Entity entity : entities) {
            if (filter.matches(entity)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = super.serializeNBT() == null ? new NBTTagCompound() : super.serializeNBT();
        compound.setTag("Filter", filter.serializeNBT());
        compound.setBoolean("Whitelist", whitelist);
        compound.setBoolean("Inverted", inverted);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        if (nbt.hasKey("Filter")) filter.deserializeNBT(nbt.getCompoundTag("Filter"));
        whitelist = nbt.getBoolean("Whitelist");
        inverted = nbt.getBoolean("Inverted");
    }

    @Override
    public int getRedstoneOutput() {
        return hasEntity ? 15 : 0;
    }

    @Override
    public Cuboid getBoundingBox() {
        return BB;
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    public void handleButtonInteraction(int buttonId, NBTTagCompound compound) {
        super.handleButtonInteraction(buttonId, compound);
        if (buttonId >= 0 && buttonId < filter.getFilter().length) {
            this.filter.setFilter(buttonId, new ItemStack(compound));
            this.getContainer().requestSync();
        }
        if (buttonId == 16) {
            whitelist = !whitelist;
            this.getContainer().requestSync();
        }
        if (buttonId == 17) {
            inverted = !inverted;
            this.getContainer().requestSync();
        }
    }

    @Override
    public void addComponentsToGui(List<IGuiComponent> componentList) {
        super.addComponentsToGui(componentList);
        componentList.add(new FilterGuiComponent(this.filter.getLocX(), this.filter.getLocY(), this.filter.getSizeX(), this.filter.getSizeY()) {
            @Override
            public IFilter getFilter() {
                return ConveyorDetectorUpgrade.this.filter;
            }
        });
        ResourceLocation res = new ResourceLocation(Reference.MOD_ID, "textures/gui/machines.png");
        componentList.add(new TexturedStateButtonGuiComponent(16, 133, 20, 18, 18,
                new StateButtonInfo(0, res, 1, 214, new String[]{"whitelist"}),
                new StateButtonInfo(1, res, 20, 214, new String[]{"blacklist"})) {
            @Override
            public int getState() {
                return whitelist ? 0 : 1;
            }
        });
        componentList.add(new TexturedStateButtonGuiComponent(17, 133, 20 + 35, 18, 18,
                new StateButtonInfo(0, res, 96, 214, new String[]{"redstone_normal"}),
                new StateButtonInfo(1, res, 77, 214, new String[]{"redstone_inverted"})) {
            @Override
            public int getState() {
                return inverted ? 1 : 0;
            }
        });
    }

    public static class Factory extends ConveyorUpgradeFactory {
        public Factory() {
            setRegistryName("detection");
        }

        @Override
        public ConveyorUpgrade create(IConveyorContainer container, EnumFacing face) {
            return new ConveyorDetectorUpgrade(container, this, face);
        }

        @Override
        public Set<ResourceLocation> getTextures() {
            return Collections.singleton(new ResourceLocation(Reference.MOD_ID, "blocks/conveyor_detection_upgrade"));
        }

        @Nonnull
        @Override
        public Set<EnumFacing> getValidFacings() {
            return DOWN;
        }

        @Override
        public EnumFacing getSideForPlacement(World world, BlockPos pos, EntityPlayer player) {
            return EnumFacing.DOWN;
        }

        @Override
        @Nonnull
        public ResourceLocation getModel(EnumFacing upgradeSide, EnumFacing conveyorFacing) {
            return new ResourceLocation(Reference.MOD_ID, "block/conveyor_upgrade_detection");
        }

        @Nonnull
        @Override
        public ResourceLocation getItemModel() {
            return new ResourceLocation(Reference.MOD_ID, "conveyor_detection_upgrade");
        }
    }
}
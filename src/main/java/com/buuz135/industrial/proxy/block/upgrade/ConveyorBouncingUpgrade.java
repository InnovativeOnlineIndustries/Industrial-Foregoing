package com.buuz135.industrial.proxy.block.upgrade;

import com.buuz135.industrial.api.conveyor.ConveyorUpgrade;
import com.buuz135.industrial.api.conveyor.ConveyorUpgradeFactory;
import com.buuz135.industrial.api.conveyor.IConveyorContainer;
import com.buuz135.industrial.proxy.block.Cuboid;
import com.buuz135.industrial.utils.Reference;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Set;

public class ConveyorBouncingUpgrade extends ConveyorUpgrade {

    public static Cuboid BB = new Cuboid(0.0625 * 3, 0.0625, 0.0625 * 3, 0.0625 * 13, 0.0625 * 1.2, 0.0625 * 13, EnumFacing.DOWN.getIndex(), false);


    public ConveyorBouncingUpgrade(IConveyorContainer container, ConveyorUpgradeFactory factory, EnumFacing side) {
        super(container, factory, side);
    }

    @Override
    public void handleEntity(Entity entity) {
        super.handleEntity(entity);
    }

    @Override
    public Cuboid getBoundingBox() {
        return BB;
    }

    public static class Factory extends ConveyorUpgradeFactory {

        public Factory() {
            setRegistryName("bouncing");
        }

        @Override
        public ConveyorUpgrade create(IConveyorContainer container, EnumFacing face) {
            return new ConveyorBouncingUpgrade(container, this, face);
        }

        @Override
        public Set<ResourceLocation> getTextures() {
            return ImmutableSet.of(new ResourceLocation(Reference.MOD_ID, "blocks/conveyor_bouncing_upgrade_north"),
                    new ResourceLocation(Reference.MOD_ID, "blocks/conveyor_bouncing_upgrade_east"),
                    new ResourceLocation(Reference.MOD_ID, "blocks/conveyor_bouncing_upgrade_west"),
                    new ResourceLocation(Reference.MOD_ID, "blocks/conveyor_bouncing_upgrade_south"));
        }

        @Override
        @Nonnull
        public ResourceLocation getModel(EnumFacing upgradeSide, EnumFacing conveyorFacing) {
            return new ResourceLocation(Reference.MOD_ID, "block/conveyor_upgrade_bouncing_" + conveyorFacing.getName().toLowerCase());
        }

        @Nonnull
        @Override
        public ResourceLocation getItemModel() {
            return new ResourceLocation(Reference.MOD_ID, "conveyor_bouncing_upgrade");
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
    }
}

package com.buuz135.industrial.api.conveyor;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.Set;

public abstract class ConveyorUpgradeFactory extends IForgeRegistryEntry.Impl<ConveyorUpgradeFactory> {
    public static final ImmutableSet<EnumFacing> HORIZONTAL = ImmutableSet.copyOf(EnumFacing.Plane.HORIZONTAL.facings());
    public static final ImmutableSet<EnumFacing> DOWN = ImmutableSet.of(EnumFacing.DOWN);

    public abstract ConveyorUpgrade create(IConveyorContainer container, EnumFacing face);

    @Nonnull
    public Set<EnumFacing> getValidFacings() {
        return HORIZONTAL;
    }

    @Nonnull
    public abstract ResourceLocation getModel(EnumFacing side);

    @Nonnull
    public abstract ResourceLocation getItemModel();

    public EnumFacing getSideForPlacement(World world, BlockPos pos, EntityPlayer player) {
        return player.getHorizontalFacing();
    }
}
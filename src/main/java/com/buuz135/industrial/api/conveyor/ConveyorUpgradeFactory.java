/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.industrial.api.conveyor;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.Collections;
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
    public abstract ResourceLocation getModel(EnumFacing upgradeSide, EnumFacing conveyorFacing);

    @Nonnull
    public abstract ResourceLocation getItemModel();

    public Set<ResourceLocation> getTextures() {
        return Collections.emptySet();
    }

    public EnumFacing getSideForPlacement(World world, BlockPos pos, EntityPlayer player) {
        return player.getHorizontalFacing();
    }
}
/*
 * This file is part of Titanium
 * Copyright (C) 2021, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.buuz135.industrial.fluid;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.fluids.FluidAttributes;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class OreFluid extends FlowingFluid {

    private final FluidAttributes.Builder fluidAttributes;
    private OreFluidInstance instance;

    public OreFluid(FluidAttributes.Builder fluidAttributes, OreFluidInstance instance) {
        this.fluidAttributes = fluidAttributes;
        this.instance = instance;
    }

    @Override
    @Nonnull
    public Fluid getFlowing() {
        return instance.getFlowingFluid();
    }

    @Override
    @Nonnull
    public Fluid getSource() {
        return instance.getSourceFluid();
    }

    @Override
    protected boolean canConvertToSource() {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void beforeDestroyingBlock(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        // copied from the WaterFluid implementation
        BlockEntity tileentity = state.hasBlockEntity() ? worldIn.getBlockEntity(pos) : null;
        Block.dropResources(state, worldIn, pos, tileentity);
    }

    @Override
    protected int getSlopeFindDistance(@Nonnull LevelReader world) {
        return 4;
    }

    @Override
    protected int getDropOff(@Nonnull LevelReader world) {
        return 1;
    }

    @Override
    @Nonnull
    public Item getBucket() {
        return instance.getBucketFluid();
    }

    @Override
    @ParametersAreNonnullByDefault
    protected boolean canBeReplacedWith(FluidState p_215665_1_, BlockGetter p_215665_2_, BlockPos p_215665_3_, Fluid p_215665_4_, Direction p_215665_5_) {
        return p_215665_5_ == Direction.DOWN && !p_215665_4_.is(FluidTags.WATER);
    }

    @Override
    public int getTickDelay(@Nonnull LevelReader p_205569_1_) {
        return 5;
    }

    @Override
    protected float getExplosionResistance() {
        return 1;
    }

    @Override
    @Nonnull
    protected BlockState createLegacyBlock(@Nonnull FluidState state) {
        return instance.getBlockFluid().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    @Override
    public boolean isSource(@Nonnull FluidState state) {
        return false;
    }

    @Override
    public int getAmount(@Nonnull FluidState p_207192_1_) {
        return 0;
    }

    @Override
    public boolean isSame(Fluid fluidIn) {
        return fluidIn == instance.getFlowingFluid() || fluidIn == instance.getSourceFluid();
    }

    @Override
    @Nonnull
    protected FluidAttributes createAttributes() {
        return new OreTitaniumFluidAttributes(fluidAttributes, this);
    }

    public static class Flowing extends OreFluid {
        {
            registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
        }

        public Flowing(FluidAttributes.Builder fluidAttributes, OreFluidInstance instance) {
            super(fluidAttributes, instance);
        }

        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(@Nonnull FluidState p_207192_1_) {
            return p_207192_1_.getValue(LEVEL);
        }

        @Override
        public boolean isSource(@Nonnull FluidState state) {
            return false;
        }
    }

    public static class Source extends OreFluid {

        public Source(FluidAttributes.Builder fluidAttributes, OreFluidInstance instance) {
            super(fluidAttributes, instance);
        }

        @Override
        public int getAmount(@Nonnull FluidState p_207192_1_) {
            return 8;
        }

        @Override
        public boolean isSource(@Nonnull FluidState state) {
            return true;
        }
    }
}

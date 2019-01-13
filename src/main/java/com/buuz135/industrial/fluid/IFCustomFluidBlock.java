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
package com.buuz135.industrial.fluid;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Consumer;

public class IFCustomFluidBlock extends BlockFluidClassic {

    private Consumer<EntityLivingBase> consumer;

    public IFCustomFluidBlock(IFCustomFluid fluid, Material material, Consumer<EntityLivingBase> consumer) {
        super(fluid, material);
        setRegistryName(new ResourceLocation(Reference.MOD_ID, fluid.getName()));
        setTranslationKey(fluidName);
        setCreativeTab(IndustrialForegoing.creativeTab);
        this.consumer = consumer;
    }

    public void register(IForgeRegistry<Block> block) {
        block.register(this);
    }

    public String getName() {
        return fluidName;
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        super.onEntityCollision(worldIn, pos, state, entityIn);
        if (worldIn.isRemote) return;
        if (entityIn instanceof EntityLivingBase) {
            consumer.accept((EntityLivingBase) entityIn);
        }
    }

}

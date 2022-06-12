/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
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

import com.buuz135.industrial.item.OreBucketItem;
import com.hrznstudio.titanium.module.DeferredRegistryHelper;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.registries.RegistryObject;

public class OreFluidInstance extends net.minecraftforge.registries.ForgeRegistryEntry<OreFluidInstance> {

    private RegistryObject<Fluid> flowingFluid;
    private RegistryObject<Fluid> sourceFluid;
    private RegistryObject<Item> bucketFluid;
    private RegistryObject<Block> blockFluid;
    private String fluid;

    public OreFluidInstance(DeferredRegistryHelper helper, String fluid, FluidAttributes.Builder attributes, CreativeModeTab group) {
        this.fluid = fluid;
        this.sourceFluid = helper.registerGeneric(Fluid.class, fluid, () -> new OreFluid.Source(attributes, this));
        this.flowingFluid = helper.registerGeneric(Fluid.class, fluid + "_flowing", () ->  new OreFluid.Flowing(attributes, this));
        this.bucketFluid = helper.registerGeneric(Item.class, fluid + "_bucket", () -> new BucketItem(this.sourceFluid, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(group)));
        this.blockFluid = helper.registerGeneric(Block.class, fluid, () -> new LiquidBlock(() -> (FlowingFluid) sourceFluid.get(), Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops()));
    }

    public Fluid getFlowingFluid() {
        return flowingFluid.get();
    }

    public Fluid getSourceFluid() {
        return sourceFluid.get();
    }

    public Item getBucketFluid() {
        return bucketFluid.get();
    }

    public Block getBlockFluid() {
        return blockFluid.get();
    }

    public String getFluid() {
        return fluid;
    }
}

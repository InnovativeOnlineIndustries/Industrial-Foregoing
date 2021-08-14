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
import com.hrznstudio.titanium.module.api.IAlternativeEntries;
import com.hrznstudio.titanium.module.api.RegistryManager;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraftforge.fluids.FluidAttributes;

public class OreFluidInstance extends net.minecraftforge.registries.ForgeRegistryEntry<OreFluidInstance> implements IAlternativeEntries {

    private OreFluid flowingFluid;
    private OreFluid sourceFluid;
    private Item bucketFluid;
    private Block blockFluid;

    public OreFluidInstance(String modid, String fluid, FluidAttributes.Builder attributes, boolean hasBucket, ItemGroup group) {
        this.sourceFluid = (OreFluid) new OreFluid.Source(attributes){

        }.setRegistryName(modid, fluid);
        this.flowingFluid = (OreFluid) new OreFluid.Flowing(attributes).setRegistryName(modid, fluid + "_fluid");
        this.sourceFluid = this.sourceFluid.setSourceFluid(sourceFluid).setFlowingFluid(flowingFluid);
        this.flowingFluid = this.flowingFluid.setSourceFluid(sourceFluid).setFlowingFluid(flowingFluid);
        if (hasBucket)
            this.bucketFluid = new OreBucketItem(this::getSourceFluid, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(group)).setRegistryName(modid, fluid + "_bucket");
        this.blockFluid = new FlowingFluidBlock(sourceFluid, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()) {
        }.setRegistryName(modid, fluid + "_block");
        this.sourceFluid.setBlockFluid(blockFluid).setBucketFluid(bucketFluid);
        this.flowingFluid.setBlockFluid(blockFluid).setBucketFluid(bucketFluid);
    }

    @Override
    public void addAlternatives(RegistryManager<?> registry) {
        registry.content(Fluid.class, flowingFluid);
        registry.content(Fluid.class, sourceFluid);
        registry.content(Block.class, blockFluid);
        if (bucketFluid != null) registry.content(Item.class, bucketFluid);
    }

    public OreFluid getFlowingFluid() {
        return flowingFluid;
    }

    public OreFluid getSourceFluid() {
        return sourceFluid;
    }

    public Item getBucketFluid() {
        return bucketFluid;
    }

    public Block getBlockFluid() {
        return blockFluid;
    }

    public void setBucketFluid(Item bucketFluid) {
        this.bucketFluid = bucketFluid;
        this.sourceFluid.setBucketFluid(bucketFluid);
        this.flowingFluid.setBucketFluid(bucketFluid);
    }
}

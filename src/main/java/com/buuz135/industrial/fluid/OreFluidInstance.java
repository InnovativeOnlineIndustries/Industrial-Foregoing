/*
 * This file is part of Titanium
 * Copyright (C) 2021, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.buuz135.industrial.fluid;

import com.buuz135.industrial.item.OreBucketItem;
import com.hrznstudio.titanium.module.api.IAlternativeEntries;
import com.hrznstudio.titanium.module.api.RegistryManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidAttributes;

public class OreFluidInstance extends net.minecraftforge.registries.ForgeRegistryEntry<OreFluidInstance> implements IAlternativeEntries {

    private OreFluid flowingFluid;
    private OreFluid sourceFluid;
    private Item bucketFluid;
    private Block blockFluid;

    public OreFluidInstance(String modid, String fluid, FluidAttributes.Builder attributes, boolean hasBucket, CreativeModeTab group) {
        this.sourceFluid = (OreFluid) new OreFluid.Source(attributes){

        }.setRegistryName(modid, fluid);
        this.flowingFluid = (OreFluid) new OreFluid.Flowing(attributes).setRegistryName(modid, fluid + "_fluid");
        this.sourceFluid = this.sourceFluid.setSourceFluid(sourceFluid).setFlowingFluid(flowingFluid);
        this.flowingFluid = this.flowingFluid.setSourceFluid(sourceFluid).setFlowingFluid(flowingFluid);
        if (hasBucket)
            this.bucketFluid = new OreBucketItem(this::getSourceFluid, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(group)).setRegistryName(modid, fluid + "_bucket");
        this.blockFluid = new LiquidBlock(sourceFluid, Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops()) {
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

/*
 * This file is part of Titanium
 * Copyright (C) 2021, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
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

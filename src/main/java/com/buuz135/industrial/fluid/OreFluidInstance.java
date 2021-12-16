/*
 * This file is part of Titanium
 * Copyright (C) 2021, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.buuz135.industrial.fluid;

import java.util.function.Supplier;

import com.buuz135.industrial.item.OreBucketItem;
import com.hrznstudio.titanium.module.DeferredRegistryHelper;
import com.hrznstudio.titanium.module.api.IAlternativeEntries;
import com.hrznstudio.titanium.module.api.RegistryManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
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
    private String fluid;

    public OreFluidInstance(String modid, String fluid, FluidAttributes.Builder attributes, boolean hasBucket, CreativeModeTab group) {
        this.fluid = fluid;
        this.sourceFluid = (OreFluid) new OreFluid.Source(attributes);
        this.flowingFluid = (OreFluid) new OreFluid.Flowing(attributes);
        this.sourceFluid = this.sourceFluid.setSourceFluid(sourceFluid).setFlowingFluid(flowingFluid);
        this.flowingFluid = this.flowingFluid.setSourceFluid(sourceFluid).setFlowingFluid(flowingFluid);
        if (hasBucket)
            this.bucketFluid = new OreBucketItem(this::getSourceFluid, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(group));
        this.blockFluid = new LiquidBlock(() -> sourceFluid, Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops()) {
        };
        this.sourceFluid.setBlockFluid(blockFluid).setBucketFluid(bucketFluid);
        this.flowingFluid.setBlockFluid(blockFluid).setBucketFluid(bucketFluid);
    }

    @Override
    public void addAlternatives(DeferredRegistryHelper registry) {
        registry.register(Fluid.class, fluid + "_fluid", () -> flowingFluid);
        registry.register(Fluid.class, fluid, () -> sourceFluid);
        registry.register(Block.class, fluid, () -> blockFluid);
        if (bucketFluid != null) registry.register(Item.class, fluid + "_bucket", () -> bucketFluid);
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

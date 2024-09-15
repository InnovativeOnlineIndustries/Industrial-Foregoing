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

import com.hrznstudio.titanium.module.DeferredRegistryHelper;
import com.hrznstudio.titanium.tab.TitaniumTab;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class OreFluidInstance {

    private DeferredHolder<FluidType, FluidType> fluidType;
    private DeferredHolder<Fluid, Fluid> flowingFluid;
    private DeferredHolder<Fluid, Fluid> sourceFluid;
    private DeferredHolder<Item, Item> bucketFluid;
    private DeferredHolder<Block, Block> blockFluid;
    private String fluid;

    public OreFluidInstance(DeferredRegistryHelper helper, String fluid, FluidType.Properties properties, IClientFluidTypeExtensions renderProperties, @Nullable TitaniumTab group) {
        this.fluid = fluid;
        this.sourceFluid = helper.registerGeneric(Registries.FLUID, fluid, () -> new OreFluid.Source(this));
        this.flowingFluid = helper.registerGeneric(Registries.FLUID, fluid + "_flowing", () -> new OreFluid.Flowing(this));
        this.bucketFluid = helper.registerGeneric(Registries.ITEM, fluid + "_bucket", () -> {
            var item = new BucketItem(this.sourceFluid.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
            if (group != null) group.getTabList().add(item);
            return item;
        });
        this.blockFluid = helper.registerGeneric(Registries.BLOCK, fluid, () -> new LiquidBlock((FlowingFluid) sourceFluid.get(), Block.Properties.ofFullCopy(Blocks.WATER).noCollission().strength(100.0F)));
        this.fluidType = helper.registerGeneric(NeoForgeRegistries.FLUID_TYPES.key(), fluid, () -> new OreTitaniumFluidType(properties) {
            @Override
            public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(renderProperties);
            }


        });
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

    public DeferredHolder<FluidType, FluidType> getFluidType() {
        return fluidType;
    }
}

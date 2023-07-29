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

import com.hrznstudio.titanium.Titanium;
import com.hrznstudio.titanium.module.DeferredRegistryHelper;
import com.hrznstudio.titanium.tab.TitaniumTab;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class OreFluidInstance {

    private RegistryObject<FluidType> fluidType;
    private RegistryObject<Fluid> flowingFluid;
    private RegistryObject<Fluid> sourceFluid;
    private RegistryObject<Item> bucketFluid;
    private RegistryObject<Block> blockFluid;
    private String fluid;

    public OreFluidInstance(DeferredRegistryHelper helper, String fluid, FluidType.Properties properties, IClientFluidTypeExtensions renderProperties, @Nullable TitaniumTab group) {
        this.fluid = fluid;
        this.sourceFluid = helper.registerGeneric(ForgeRegistries.FLUIDS.getRegistryKey(), fluid, () -> new OreFluid.Source(this));
        this.flowingFluid = helper.registerGeneric(ForgeRegistries.FLUIDS.getRegistryKey(), fluid + "_flowing", () -> new OreFluid.Flowing(this));
        this.bucketFluid = helper.registerGeneric(ForgeRegistries.ITEMS.getRegistryKey(), fluid + "_bucket", () -> {
            var item = new BucketItem(this.sourceFluid, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
            if (group != null) group.getTabList().add(item);
            return item;
        });
        this.blockFluid = helper.registerGeneric(ForgeRegistries.BLOCKS.getRegistryKey(), fluid, () -> new LiquidBlock(() -> (FlowingFluid) sourceFluid.get(), Block.Properties.copy(Blocks.WATER).noCollission().strength(100.0F)));
        this.fluidType = helper.registerGeneric(ForgeRegistries.Keys.FLUID_TYPES, fluid, () -> new OreTitaniumFluidType(properties) {
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

    public RegistryObject<FluidType> getFluidType() {
        return fluidType;
    }
}

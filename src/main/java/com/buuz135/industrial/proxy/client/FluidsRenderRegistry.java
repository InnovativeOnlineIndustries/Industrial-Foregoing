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
package com.buuz135.industrial.proxy.client;

import com.buuz135.industrial.fluid.IFCustomFluidBlock;
import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;

public class FluidsRenderRegistry {

    public static void registerRender() {
        register(BlockRegistry.BLOCK_ESSENCE);
        register(BlockRegistry.BLOCK_MILK);
        register(BlockRegistry.BLOCK_MEAT);
        register(BlockRegistry.BLOCK_LATEX);
        register(BlockRegistry.BLOCK_SEWAGE);
        register(BlockRegistry.BLOCK_SLUDGE);
        register(BlockRegistry.BLOCK_BIOFUEL);
        register(BlockRegistry.BLOCK_PINK_SLIME);
        register(BlockRegistry.BLOCK_PROTEIN);
    }

    public static void register(IFCustomFluidBlock base) {
        Item fluid = Item.getItemFromBlock(base);

        ModelBakery.registerItemVariants(fluid);
        FluidStateMapper mapper = new FluidStateMapper(base.getName());
        ModelLoader.setCustomMeshDefinition(fluid, mapper);
        ModelLoader.setCustomStateMapper(base, mapper);
    }

    public static class FluidStateMapper extends StateMapperBase implements ItemMeshDefinition {
        public final ModelResourceLocation location;

        public FluidStateMapper(String name) {
            location = new ModelResourceLocation(Reference.MOD_ID + ":fluids", name);
        }

        protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
            return location;
        }

        public ModelResourceLocation getModelLocation(ItemStack stack) {
            return location;
        }
    }
}

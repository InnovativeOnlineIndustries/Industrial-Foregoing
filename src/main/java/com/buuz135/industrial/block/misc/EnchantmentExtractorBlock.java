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

package com.buuz135.industrial.block.misc;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.misc.tile.EnchantmentExtractorTile;
import com.buuz135.industrial.module.ModuleMisc;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import com.hrznstudio.titanium.util.TagUtil;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class EnchantmentExtractorBlock extends IndustrialBlock<EnchantmentExtractorTile> {

    public EnchantmentExtractorBlock() {
        super("enchantment_extractor", Properties.ofFullCopy(Blocks.IRON_BLOCK), EnchantmentExtractorTile.class, ModuleMisc.TAB_MISC);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<EnchantmentExtractorTile> getTileEntityFactory() {
        return EnchantmentExtractorTile::new;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public void registerRecipe(RecipeOutput consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .pattern("PSP").pattern("BMB").pattern("DGD")
                .define('P', IndustrialTags.Items.PLASTIC)
                .define('S', Blocks.NETHER_BRICKS)
                .define('B', Items.BOOK)
                .define('M', IndustrialTags.Items.MACHINE_FRAME_ADVANCED)
                .define('G', TagUtil.getItemTag(ResourceLocation.fromNamespaceAndPath("c", "gears/gold")))
                .define('D', Items.DIAMOND)
                .save(consumer);
    }

}

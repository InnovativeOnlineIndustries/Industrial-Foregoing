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
package com.buuz135.industrial.item;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.utils.RecipeUtils;
import lombok.Getter;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

public class LaserLensItem extends IFCustomItem {

    private static String[] dyes = {"Black", "Red", "Green", "Brown", "Blue", "Purple", "Cyan", "LightGray", "Gray", "Pink", "Lime", "Yellow", "LightBlue", "Magenta", "Orange", "White"};

    @Getter
    private boolean inverted;

    public LaserLensItem(boolean inverted) {
        super("laser_lens" + (inverted ? "_inverted" : ""));
        setMaxStackSize(1);
        setHasSubtypes(true);
        this.inverted = inverted;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (isInCreativeTab(tab))
            for (int i = 0; i < 16; ++i) subItems.add(new ItemStack(this, 1, i));
    }

    @Override
    public void register(IForgeRegistry<Item> items) {
        super.register(items);
    }

    public void createRecipe() {
        for (int i = 0; i < 16; ++i) {
            RecipeUtils.addShapedRecipe(new ItemStack(this, 1, i), " i ", "ipi", " i ", 'i', inverted ? new ItemStack(Items.IRON_INGOT) : new ItemStack(ItemRegistry.pinkSlime),
                    'p', new ItemStack(Blocks.STAINED_GLASS_PANE, 1, i));
            RecipeUtils.addShapelessRecipe("recolor", new ItemStack(this, 1, i), new ItemStack(this, 1, OreDictionary.WILDCARD_VALUE), "dye" + dyes[15 - i]);
        }

    }

    @Override
    public void registerRender() {
        for (int i = 0; i < 16; ++i)
            ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(this.getRegistryName().toString() + i, "inventory"));
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return new TextComponentTranslation("item.fireworksCharge." + EnumDyeColor.byMetadata(stack.getMetadata()).getTranslationKey().replaceAll("_", "")).getFormattedText() + " " + super.getItemStackDisplayName(stack);
    }
}

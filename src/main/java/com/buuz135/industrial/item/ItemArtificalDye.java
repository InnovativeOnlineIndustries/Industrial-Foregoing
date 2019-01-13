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

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.model.ModelLoader;

public class ItemArtificalDye extends IFCustomItem {
    public ItemArtificalDye() {
        super("artificial_dye");
        setHasSubtypes(true);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (isInCreativeTab(tab))
            for (int i = 0; i < 16; ++i) subItems.add(new ItemStack(this, 1, i));
    }

    @Override
    public void registerRender() {
        for (int i = 0; i < 16; ++i)
            ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(this.getRegistryName().toString(), "inventory"));
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return new TextComponentTranslation("item.fireworksCharge." + EnumDyeColor.byMetadata(stack.getMetadata()).getTranslationKey().replaceAll("_", "")).getFormattedText() + " " + super.getItemStackDisplayName(stack);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
        if (target instanceof EntitySheep) {
            EntitySheep sheepboi = (EntitySheep) target;
            EnumDyeColor dyeboi = EnumDyeColor.byMetadata(stack.getMetadata());
            if (!sheepboi.getSheared() && sheepboi.getFleeceColor() != dyeboi) {
                sheepboi.setFleeceColor(dyeboi);
                stack.shrink(1);
            }
            return true;
        } else {
            return false;
        }
    }
}
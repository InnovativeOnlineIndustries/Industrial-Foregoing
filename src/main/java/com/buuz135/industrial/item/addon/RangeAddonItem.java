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
package com.buuz135.industrial.item.addon;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.ndrei.teslacorelib.tileentities.SidedTileEntity;

import javax.annotation.Nullable;
import java.util.List;

public class RangeAddonItem extends CustomAddon {

    public RangeAddonItem() {
        super("range_addon");
        setMaxStackSize(1);
        setHasSubtypes(true);
    }

    @Override
    public boolean canBeAddedTo(SidedTileEntity machine) {
        return !machine.getAddons().contains(this) && machine instanceof WorkingAreaElectricMachine && ((WorkingAreaElectricMachine) machine).canAcceptRangeUpgrades();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) for (int i = 0; i < 12; ++i) items.add(new ItemStack(this, 1, i));
    }


    @Override
    public void registerRenderer() {
        for (int i = 0; i < 12; ++i)
            ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(this.getRegistryName().toString() + i, "inventory"));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add("Range: +" + (stack.getMetadata() + 1));
    }

    public void createRecipe() {
        Object[] items = new Object[]{new ItemStack(Blocks.COBBLESTONE), new ItemStack(Items.DYE, 1, 4), "ingotIron", "TIER3", "TIER4", "TIER5", "TIER6", "ingotGold", new ItemStack(Items.QUARTZ), new ItemStack(Items.DIAMOND), "TIER10", new ItemStack(Items.EMERALD)};
        for (int i = 0; i < 12; ++i) {
            RecipeUtils.addShapedRecipe(new ItemStack(this, 1, i), "ipi", "igi", "ipi",
                    'i', items[i],
                    'p', ItemRegistry.plastic,
                    'g', "paneGlass");
        }

    }
}

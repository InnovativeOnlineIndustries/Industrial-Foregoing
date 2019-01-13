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
package com.buuz135.industrial.fluid;

import com.buuz135.industrial.utils.ItemStackUtils;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;

public class IFOreFluid extends IFCustomFluid {

    public static HashMap<String, Integer> CACHED_COLORS = new HashMap<>();

    public IFOreFluid(String type) {
        super("if.ore_fluid_" + type, 300, new ResourceLocation(Reference.MOD_ID, "blocks/fluids/ore_fluid_" + type + "_still"), new ResourceLocation(Reference.MOD_ID, "blocks/fluids/ore_fluid_" + type + "_flow"));
    }

    public static String getOre(FluidStack stack) {
        return stack.tag != null && stack.tag.hasKey("Ore") ? stack.tag.getString("Ore") : null;
    }

    @Override
    public int getColor(FluidStack stack) {
        String ore = getOre(stack);
        if (ore != null) {
            if (CACHED_COLORS.containsKey(ore)) return CACHED_COLORS.get(ore);
            String dust = "dust" + ore.replaceAll("ore", "");
            if (OreDictionary.doesOreNameExist(dust)) {
                int color = ItemStackUtils.getColor(OreDictionary.getOres(ore).get(0));
                CACHED_COLORS.put(ore, color);
                return color;
            }
        }
        return super.getColor(stack);
    }

    public FluidStack getWithOre(String ore, int amount) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("Ore", ore);
        return new FluidStack(this, amount, compound);
    }

    @Override
    public String getLocalizedName(FluidStack stack) {
        String ore = getOre(stack);
        if (ore != null && OreDictionary.getOres(ore).size() > 0) {
            return super.getLocalizedName(stack) + " (" + OreDictionary.getOres(ore).get(0).getDisplayName() + ")";
        }
        return super.getLocalizedName(stack);
    }
}

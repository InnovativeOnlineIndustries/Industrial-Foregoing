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

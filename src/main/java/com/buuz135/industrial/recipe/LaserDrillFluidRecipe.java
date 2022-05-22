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
package com.buuz135.industrial.recipe;

import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.recipe.serializer.GenericSerializer;
import com.hrznstudio.titanium.recipe.serializer.SerializableRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LaserDrillFluidRecipe extends SerializableRecipe {

    public static GenericSerializer<LaserDrillFluidRecipe> SERIALIZER = new GenericSerializer<>(new ResourceLocation(Reference.MOD_ID, "laser_drill_fluid"), LaserDrillFluidRecipe.class);
    public static List<LaserDrillFluidRecipe> RECIPES = new ArrayList<>();

    public static ResourceLocation EMPTY = new ResourceLocation("minecraft", "empty");

    public static void init() {
        new LaserDrillFluidRecipe(new FluidStack(Fluids.LAVA, 100),  1, EMPTY, new LaserDrillRarity(LaserDrillRarity.NETHER, new ResourceKey[0], 5, 20, 8));
        new LaserDrillFluidRecipe(new FluidStack(ModuleCore.ETHER.getSourceFluid().get(), 10),  10, new ResourceLocation("minecraft", "wither"), new LaserDrillRarity(new ResourceKey[0], new ResourceKey[0], -64, 256, 8));
        new LaserDrillFluidRecipe("oil", createNBT("pneumaticcraft:oil", 50),  15, EMPTY, new LaserDrillRarity( LaserDrillRarity.OIL, new ResourceKey[0], 20, 60, 8)).setModIdCondition("pneumaticcraft");
    }

    public CompoundTag output;
    public LaserDrillRarity[] rarity;
    public int pointer = 0;
    public Ingredient catalyst;
    public ResourceLocation entity;
    private String modIdCondition;

    public LaserDrillFluidRecipe(String name, CompoundTag output, Ingredient catalyst, ResourceLocation entity, LaserDrillRarity... rarity) {
        super(new ResourceLocation(Reference.MOD_ID, name));
        this.output = output;
        this.rarity = rarity;
        this.catalyst = catalyst;
        this.entity = entity;
        System.out.println(this.output);
        RECIPES.add(this);
    }

    public LaserDrillFluidRecipe(String name, CompoundTag output, int color, ResourceLocation entity, LaserDrillRarity... rarity) {
        this(name, output, Ingredient.of(ModuleCore.LASER_LENS[color].get()),entity,  rarity);
    }

    public LaserDrillFluidRecipe(FluidStack output, int color, ResourceLocation entity, LaserDrillRarity... rarity) {
        this(output.getFluid().getRegistryName().getPath(), output.writeToNBT(new CompoundTag()), color,entity,  rarity);
    }

    public LaserDrillFluidRecipe(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    @Override
    public boolean matches(Container inv, Level worldIn) {
        return false;
    }

    @Override
    public ItemStack assemble(Container inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public GenericSerializer<? extends SerializableRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return SERIALIZER.getRecipeType();
    }

    @Override
    public Pair<ICondition, IConditionSerializer> getOutputCondition() {
        if (modIdCondition != null){
            return Pair.of(new ModLoadedCondition(modIdCondition), ModLoadedCondition.Serializer.INSTANCE);
        }
        return null;
    }

    public void setModIdCondition(String modIdCondition) {
        this.modIdCondition = modIdCondition;
    }

    public static CompoundTag createNBT(String name, int amount)
    {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("FluidName", name);
        nbt.putInt("Amount", amount);
        return nbt;
    }

    @Nullable
    public LaserDrillRarity getValidRarity(ResourceLocation biome, int height){
        for (LaserDrillRarity laserDrillRarity : rarity) {
            if (laserDrillRarity.depth_max >= height && laserDrillRarity.depth_min <= height){
                if (laserDrillRarity.whitelist.length == 0 ? Arrays.stream(laserDrillRarity.blacklist).noneMatch(registryKey -> registryKey.location().equals(biome)) : Arrays.stream(laserDrillRarity.whitelist).anyMatch(registryKey -> registryKey.location().equals(biome))) return laserDrillRarity;
            }
        }
        return null;
    }

}

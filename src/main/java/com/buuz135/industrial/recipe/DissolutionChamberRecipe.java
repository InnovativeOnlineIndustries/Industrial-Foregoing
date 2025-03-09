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
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ItemExistsCondition;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DissolutionChamberRecipe implements Recipe<CraftingInput> {

    public static final MapCodec<DissolutionChamberRecipe> CODEC = RecordCodecBuilder.mapCodec(in -> in.group(
            Ingredient.CODEC.listOf(0, 8).fieldOf("input").forGetter(o -> o.input),
            SizedFluidIngredient.FLAT_CODEC.fieldOf("inputFluid").forGetter(o -> o.inputFluid),
            Codec.INT.fieldOf("processingTime").forGetter(o -> o.processingTime),
            ItemStack.CODEC.optionalFieldOf("output").forGetter(o -> o.output),
            FluidStack.CODEC.optionalFieldOf("outputFluid").forGetter(o -> o.outputFluid)
    ).apply(in, DissolutionChamberRecipe::new));

    public List<Ingredient> input;
    public SizedFluidIngredient inputFluid;
    public int processingTime;
    public Optional<ItemStack> output;
    public Optional<FluidStack> outputFluid;

    public DissolutionChamberRecipe(List<Ingredient> input, SizedFluidIngredient inputFluid, int processingTime, Optional<ItemStack> output, Optional<FluidStack> outputFluid) {
        this.input = input;
        this.inputFluid = inputFluid;
        this.processingTime = processingTime;
        this.output = output;
        this.outputFluid = outputFluid;
    }

    public DissolutionChamberRecipe(List<Ingredient> input, FluidStack inputFluid, int processingTime, Optional<ItemStack> output, Optional<FluidStack> outputFluid) {
        this.input = input;
        this.inputFluid = SizedFluidIngredient.of(inputFluid);
        this.processingTime = processingTime;
        this.output = output;
        this.outputFluid = outputFluid;
    }

    public DissolutionChamberRecipe() {

    }

    public static void createRecipe(RecipeOutput recipeOutput, String name, DissolutionChamberRecipe recipe) {
        var rl = generateRL(name);
        var advancementHolder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(rl))
                .rewards(AdvancementRewards.Builder.recipe(rl))
                .requirements(AdvancementRequirements.Strategy.OR).build(rl);
        List<ICondition> conditions = new ArrayList<>();
        if (recipe.output.isPresent())
            conditions.add(new ItemExistsCondition(BuiltInRegistries.ITEM.getKey(recipe.output.get().getItem())));
        recipeOutput.accept(rl, recipe, advancementHolder, conditions.toArray(new ICondition[conditions.size()]));
    }

    public static ResourceLocation generateRL(String key) {
        return ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "dissolution_chamber/" + key);
    }

    public boolean matches(IItemHandler handler, FluidTankComponent tank) {
        if (input == null || tank == null || inputFluid == null) return false;
        List<ItemStack> handlerItems = new ArrayList<>();
        for (int i = 0; i < handler.getSlots(); i++) {
            if (!handler.getStackInSlot(i).isEmpty()) handlerItems.add(handler.getStackInSlot(i).copy());
        }
        for (Ingredient ingredient : input) {
            boolean found = false;
            for (ItemStack stack : ingredient.getItems()) {
                int i = 0;
                for (; i < handlerItems.size(); i++) {
                    if (ItemStack.isSameItem(handlerItems.get(i), stack)) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    handlerItems.remove(i);
                    break;
                }
            }
            if (!found) return false;
        }

        Optional<FluidStack> optionalInputFluid = Arrays.stream(inputFluid.getFluids()).findFirst();

        return handlerItems.isEmpty() && optionalInputFluid.isPresent() && tank.drainForced(optionalInputFluid.get(), IFluidHandler.FluidAction.SIMULATE).getAmount() == optionalInputFluid.get().getAmount();
    }

    @Override
    public boolean matches(CraftingInput craftingInput, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return this.output.orElse(ItemStack.EMPTY).copy();
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ModuleCore.DISSOLUTION_CHAMBER.getBlock());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModuleCore.DISSOLUTION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModuleCore.DISSOLUTION_TYPE.get();
    }
}

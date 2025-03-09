package com.buuz135.industrial.plugin.emi.recipe;

import dev.emi.emi.api.neoforge.NeoForgeEmiStack;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class CustomEmiRecipe implements EmiRecipe {

    private final ResourceLocation id;
    private final List<EmiIngredient> ingredients;
    private final EmiRecipeCategory category;
    private final List<EmiStack> output;

    protected CustomEmiRecipe(ResourceLocation id, EmiRecipeCategory category, List<EmiIngredient> ingredients, List<EmiStack> output) {
        this.id = id;
        this.ingredients = ingredients;
        this.category = category;
        this.output = output;
    }

    public static List<EmiIngredient> combineIng(List<EmiIngredient>... inputs) {
        return Arrays.stream(inputs).flatMap(Collection::stream).toList();
    }

    public static List<EmiStack> combineStack(List<EmiStack>... inputs) {
        return Arrays.stream(inputs).flatMap(Collection::stream).toList();
    }

    public static List<EmiIngredient> fromInput(List<Ingredient> ingredients) {
        return ingredients.stream().map(EmiIngredient::of).toList();
    }

    public static List<EmiIngredient> fromInput(EmiIngredient ingredients) {
        return List.of(ingredients);
    }

    public static List<EmiIngredient> fromInput(FluidStack fluidStack) {
        return fromInput(EmiIngredient.of(Collections.singletonList(NeoForgeEmiStack.of(fluidStack))));
    }

    public static List<EmiIngredient> fromInput(SizedFluidIngredient fluidStack) {
        Optional<FluidStack> optionalInputFluid = Arrays.stream(fluidStack.getFluids()).findFirst();
        return optionalInputFluid.map(stack -> fromInput(EmiIngredient.of(Collections.singletonList(NeoForgeEmiStack.of(stack))))).orElseGet(ArrayList::new);
    }

    public static List<EmiStack> fromOutput(ItemStack output, FluidStack fluidStack) {
        var list = new ArrayList<EmiStack>();
        list.add(EmiStack.of(output));
        list.add(EmiStack.of(fluidStack.getFluid(), fluidStack.getComponentsPatch(), fluidStack.getAmount()));
        return list;
    }

    public static List<EmiStack> fromOutput(EmiStack output) {
        return List.of(output);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return category;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return ingredients;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return output;
    }
}

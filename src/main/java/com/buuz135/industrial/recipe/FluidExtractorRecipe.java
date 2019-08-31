package com.buuz135.industrial.recipe;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.recipe.serializer.GenericSerializer;
import com.hrznstudio.titanium.recipe.serializer.SerializableRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class FluidExtractorRecipe extends SerializableRecipe {

    public static GenericSerializer<FluidExtractorRecipe> SERIALIZER = new GenericSerializer<>(new ResourceLocation(Reference.MOD_ID, "fluid_extractor"), FluidExtractorRecipe.class);

    static {
        new FluidExtractorRecipe(new ResourceLocation(Reference.MOD_ID, "acacia"), new Ingredient.SingleItemList(new ItemStack(Blocks.ACACIA_LOG)), Blocks.STRIPPED_ACACIA_LOG, 0.005f, new FluidStack(ModuleCore.LATEX.getSourceFluid(), 3), false);
        new FluidExtractorRecipe(new ResourceLocation(Reference.MOD_ID, "dark_oak"), new Ingredient.SingleItemList(new ItemStack(Blocks.DARK_OAK_LOG)), Blocks.STRIPPED_DARK_OAK_LOG, 0.005f, new FluidStack(ModuleCore.LATEX.getSourceFluid(), 2), false);
        new FluidExtractorRecipe(new ResourceLocation(Reference.MOD_ID, "default"), new Ingredient.TagList(ItemTags.LOGS), Blocks.AIR, 0.005f, new FluidStack(ModuleCore.LATEX.getSourceFluid(), 1), true);
    }

    public Ingredient.IItemList input;
    public Block result;
    public float breakChance;
    public FluidStack output;
    public boolean defaultRecipe;

    public FluidExtractorRecipe(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    public FluidExtractorRecipe(ResourceLocation resourceLocation, Ingredient.IItemList input, Block result, float breakChance, FluidStack output, boolean defaultRecipe) {
        super(resourceLocation);
        this.input = input;
        this.result = result;
        this.breakChance = breakChance;
        this.output = output;
        this.defaultRecipe = defaultRecipe;
        IndustrialForegoing.RECIPES.addRecipe(this);
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return false;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(result);
    }

    @Override
    public GenericSerializer<? extends SerializableRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType() {
        return SERIALIZER.getRecipeType();
    }
}

package com.buuz135.industrial.recipe;

import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.recipe.serializer.GenericSerializer;
import com.hrznstudio.titanium.recipe.serializer.SerializableRecipe;
import com.hrznstudio.titanium.util.TagUtil;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class CrusherRecipe extends SerializableRecipe {

    public static GenericSerializer<CrusherRecipe> SERIALIZER = new GenericSerializer<>(new ResourceLocation(Reference.MOD_ID, "crusher"), CrusherRecipe.class);
    public static List<CrusherRecipe> RECIPES = new ArrayList<>();

    public static void init() {
        new CrusherRecipe(new ResourceLocation(Reference.MOD_ID, "cobble_gravel"), Ingredient.fromTag(TagUtil.getItemTag(new ResourceLocation("forge:cobblestone"))), Ingredient.fromTag(TagUtil.getItemTag(new ResourceLocation("forge:gravel"))));
        new CrusherRecipe(new ResourceLocation(Reference.MOD_ID, "gravel_sand"), Ingredient.fromTag(TagUtil.getItemTag(new ResourceLocation("forge:gravel"))), Ingredient.fromTag(TagUtil.getItemTag(new ResourceLocation("forge:sand"))));
        new CrusherRecipe(new ResourceLocation(Reference.MOD_ID, "sand_silicon"), Ingredient.fromTag(TagUtil.getItemTag(new ResourceLocation("forge:sand"))), Ingredient.fromTag(TagUtil.getItemTag(new ResourceLocation("forge:silicon"))), new ResourceLocation("forge:silicon"));
    }

    public Ingredient input;
    public Ingredient output;
    private ResourceLocation isTag;

    public CrusherRecipe(ResourceLocation resourceLocation, Ingredient input, Ingredient output) {
       this(resourceLocation, input,output, null);
    }

    public CrusherRecipe(ResourceLocation resourceLocation, Ingredient input, Ingredient output, ResourceLocation isTag) {
        super(resourceLocation);
        this.input = input;
        this.output = output;
        this.isTag = isTag;
        RECIPES.add(this);
    }

    public CrusherRecipe(ResourceLocation resourceLocation) {
        super(resourceLocation);
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
        return ItemStack.EMPTY;
    }

    @Override
    public GenericSerializer<? extends SerializableRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType() {
        return SERIALIZER.getRecipeType();
    }

    @Override
    public Pair<ICondition, IConditionSerializer> getOutputCondition() {
        if (isTag != null){
            return Pair.of(new NotCondition(new TagEmptyCondition(isTag)), NotCondition.Serializer.INSTANCE);
        }
        return null;
    }

}

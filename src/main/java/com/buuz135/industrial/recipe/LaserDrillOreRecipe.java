package com.buuz135.industrial.recipe;

import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.recipe.serializer.GenericSerializer;
import com.hrznstudio.titanium.recipe.serializer.SerializableRecipe;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.common.Tags;

import java.util.ArrayList;
import java.util.List;

public class LaserDrillOreRecipe extends SerializableRecipe {

    public static GenericSerializer<LaserDrillOreRecipe> SERIALIZER = new GenericSerializer<>(new ResourceLocation(Reference.MOD_ID, "laser_drill_ore"), LaserDrillOreRecipe.class);
    public static List<LaserDrillOreRecipe> RECIPES = new ArrayList<>();

    static {
        new LaserDrillOreRecipe(new ItemStack(Blocks.IRON_ORE), 0, new LaserDrillRarity(new RegistryKey[0], new RegistryKey[]{Biomes.THE_END, Biomes.SMALL_END_ISLANDS}, 5, 68, 20));
    }

    public Ingredient output;
    public LaserDrillRarity[] rarity;
    public int pointer = 0;
    public Ingredient catalyst;

    public LaserDrillOreRecipe(ItemStack output, Ingredient catalyst, LaserDrillRarity... rarity) {
        super(output.getItem().getRegistryName());
        this.output = Ingredient.fromStacks(output);
        this.catalyst = catalyst;
        this.rarity = rarity;
        RECIPES.add(this);
    }

    public LaserDrillOreRecipe(ItemStack output, int color, LaserDrillRarity... rarity) { //TODO Add lenses
        this(output, Ingredient.fromTag(Tags.Items.STONE), rarity);
    }

    public LaserDrillOreRecipe(ResourceLocation resourceLocation) {
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
}

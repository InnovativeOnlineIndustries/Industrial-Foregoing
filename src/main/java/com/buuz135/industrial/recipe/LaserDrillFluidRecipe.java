package com.buuz135.industrial.recipe;

import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.recipe.serializer.GenericSerializer;
import com.hrznstudio.titanium.recipe.serializer.SerializableRecipe;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class LaserDrillFluidRecipe extends SerializableRecipe {

    public static GenericSerializer<LaserDrillFluidRecipe> SERIALIZER = new GenericSerializer<>(new ResourceLocation(Reference.MOD_ID, "laser_drill_fluid"), LaserDrillFluidRecipe.class);
    public static List<LaserDrillFluidRecipe> RECIPES = new ArrayList<>();

    static {
        new LaserDrillFluidRecipe(new FluidStack(Fluids.LAVA, 10), 0, new LaserDrillRarity(new RegistryKey[]{Biomes.NETHER_WASTES, Biomes.BASALT_DELTAS, Biomes.WARPED_FOREST, Biomes.CRIMSON_FOREST, Biomes.SOUL_SAND_VALLEY}, new RegistryKey[0], 5, 20, 8));
    }

    public FluidStack output;
    public LaserDrillRarity[] rarity;
    public int pointer = 0;
    public Ingredient catalyst;

    public LaserDrillFluidRecipe(FluidStack output, Ingredient catalyst, LaserDrillRarity... rarity) {
        super(output.getFluid().getRegistryName());
        this.output = output;
        this.rarity = rarity;
        this.catalyst = catalyst;
        RECIPES.add(this);
    }

    public LaserDrillFluidRecipe(FluidStack output, int color, LaserDrillRarity... rarity) { //TODO Add lenses
        this(output, Ingredient.fromTag(Tags.Items.CROPS), rarity);
    }

    public LaserDrillFluidRecipe(ResourceLocation resourceLocation) {
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

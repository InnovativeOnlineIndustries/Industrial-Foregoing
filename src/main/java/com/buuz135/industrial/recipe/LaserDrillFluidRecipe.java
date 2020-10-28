package com.buuz135.industrial.recipe;

import com.buuz135.industrial.module.ModuleCore;
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
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class LaserDrillFluidRecipe extends SerializableRecipe {

    public static GenericSerializer<LaserDrillFluidRecipe> SERIALIZER = new GenericSerializer<>(new ResourceLocation(Reference.MOD_ID, "laser_drill_fluid"), LaserDrillFluidRecipe.class);
    public static List<LaserDrillFluidRecipe> RECIPES = new ArrayList<>();

    public static ResourceLocation EMPTY = new ResourceLocation("minecraft", "empty");

    public static void init() {
        new LaserDrillFluidRecipe(new FluidStack(Fluids.LAVA, 100),  1, EMPTY, new LaserDrillRarity(LaserDrillRarity.NETHER, new RegistryKey[0], 5, 20, 8));
        new LaserDrillFluidRecipe(new FluidStack(ModuleCore.ETHER.getSourceFluid(), 10),  10, new ResourceLocation("minecraft", "wither"), new LaserDrillRarity(new RegistryKey[0], new RegistryKey[0], 0, 256, 8));
    }

    public FluidStack output;
    public LaserDrillRarity[] rarity;
    public int pointer = 0;
    public Ingredient catalyst;
    public ResourceLocation entity;

    public LaserDrillFluidRecipe(FluidStack output, Ingredient catalyst, ResourceLocation entity, LaserDrillRarity... rarity) {
        super(output.getFluid().getRegistryName());
        this.output = output;
        this.rarity = rarity;
        this.catalyst = catalyst;
        this.entity = entity;
        RECIPES.add(this);
    }

    public LaserDrillFluidRecipe(FluidStack output, int color, ResourceLocation entity, LaserDrillRarity... rarity) {
        this(output, Ingredient.fromItems(ModuleCore.LASER_LENS[color]),entity,  rarity);
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

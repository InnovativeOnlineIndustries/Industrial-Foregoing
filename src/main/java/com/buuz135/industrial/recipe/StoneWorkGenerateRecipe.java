package com.buuz135.industrial.recipe;

import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.recipe.serializer.GenericSerializer;
import com.hrznstudio.titanium.recipe.serializer.SerializableRecipe;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.ArrayList;
import java.util.List;

public class StoneWorkGenerateRecipe extends SerializableRecipe {

    public static GenericSerializer<StoneWorkGenerateRecipe> SERIALIZER = new GenericSerializer<>(new ResourceLocation(Reference.MOD_ID, "stonework_generate"), StoneWorkGenerateRecipe.class);
    public static List<StoneWorkGenerateRecipe> RECIPES = new ArrayList<>();

    static {
        new StoneWorkGenerateRecipe(new ResourceLocation(Reference.MOD_ID, "cobblestone"),new ItemStack(Blocks.COBBLESTONE), 1000, 1000, 0, 0);
        new StoneWorkGenerateRecipe(new ResourceLocation(Reference.MOD_ID, "netherrack"),new ItemStack(Blocks.NETHERRACK), 250, 400, 250, 200);
        new StoneWorkGenerateRecipe(new ResourceLocation(Reference.MOD_ID, "obsidian"),new ItemStack(Blocks.OBSIDIAN), 1000, 1000, 0, 1000);
        new StoneWorkGenerateRecipe(new ResourceLocation(Reference.MOD_ID, "granite"),new ItemStack(Blocks.GRANITE), 200, 200, 200, 200);
        new StoneWorkGenerateRecipe(new ResourceLocation(Reference.MOD_ID, "diorite"),new ItemStack(Blocks.DIORITE), 200, 250, 200, 250);
        new StoneWorkGenerateRecipe(new ResourceLocation(Reference.MOD_ID, "andesite"),new ItemStack(Blocks.ANDESITE), 300, 300, 300, 300);
    }

    public ItemStack output;
    public int waterNeed;
    public int lavaNeed;
    public int waterConsume;
    public int lavaConsume;

    public StoneWorkGenerateRecipe(ResourceLocation resourceLocation, ItemStack output, int waterNeed, int lavaNeed, int waterConsume, int lavaConsume) {
        super(resourceLocation);
        this.output = output;
        this.waterNeed = waterNeed;
        this.lavaNeed = lavaNeed;
        this.waterConsume = waterConsume;
        this.lavaConsume = lavaConsume;
        RECIPES.add(this);
    }

    public StoneWorkGenerateRecipe(ResourceLocation resourceLocation) {
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

    public boolean canIncrease(FluidTank fluidTank, FluidTank fluidTank2){
        return fluidTank.getFluidAmount() >= waterNeed && fluidTank2.getFluidAmount() >= lavaNeed;
    }

    public void consume(FluidTankComponent fluidTank, FluidTankComponent fluidTank2){
        fluidTank.drainForced(waterConsume, IFluidHandler.FluidAction.EXECUTE);
        fluidTank2.drainForced(lavaConsume, IFluidHandler.FluidAction.EXECUTE);
    }

}

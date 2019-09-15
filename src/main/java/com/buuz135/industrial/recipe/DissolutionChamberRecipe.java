package com.buuz135.industrial.recipe;

import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.block.tile.fluid.PosFluidTank;
import com.hrznstudio.titanium.recipe.serializer.GenericSerializer;
import com.hrznstudio.titanium.recipe.serializer.SerializableRecipe;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public class DissolutionChamberRecipe extends SerializableRecipe {

    public static GenericSerializer<DissolutionChamberRecipe> SERIALIZER = new GenericSerializer<>(new ResourceLocation(Reference.MOD_ID, "dissolution_chamber"), DissolutionChamberRecipe.class);
    public static List<DissolutionChamberRecipe> RECIPES = new ArrayList<>();

    static {
        new DissolutionChamberRecipe(new ResourceLocation(Reference.MOD_ID, "test"),
                new Ingredient.IItemList[]{
                        new Ingredient.TagList(ItemTags.LOGS),
                        new Ingredient.SingleItemList(new ItemStack(Blocks.DIRT)),
                        new Ingredient.SingleItemList(new ItemStack(Blocks.DISPENSER)),
                        new Ingredient.SingleItemList(new ItemStack(Blocks.IRON_BARS)),
                        new Ingredient.SingleItemList(new ItemStack(Blocks.HOPPER)),
                        new Ingredient.SingleItemList(new ItemStack(Blocks.SLIME_BLOCK)),
                        new Ingredient.SingleItemList(new ItemStack(Blocks.PISTON)),
                        new Ingredient.SingleItemList(new ItemStack(Blocks.DIRT))
                }, new FluidStack(ModuleCore.LATEX.getSourceFluid(), 100), 500, new ItemStack(Items.DIAMOND, 2), new FluidStack(Fluids.WATER, 10));
    }

    public Ingredient.IItemList[] input;
    public FluidStack inputFluid;
    public int processingTime;
    public ItemStack output;
    public FluidStack outputFluid;

    public DissolutionChamberRecipe(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    public DissolutionChamberRecipe(ResourceLocation resourceLocation, Ingredient.IItemList[] input, FluidStack inputFluid, int processingTime, ItemStack output, FluidStack outputFluid) {
        super(resourceLocation);
        this.input = input;
        this.inputFluid = inputFluid;
        this.processingTime = processingTime;
        this.output = output;
        this.outputFluid = outputFluid;
        RECIPES.add(this);
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return false;
    }

    public boolean matches(IItemHandler handler, PosFluidTank tank) {
        List<ItemStack> handlerItems = new ArrayList<>();
        for (int i = 0; i < handler.getSlots(); i++) {
            handlerItems.add(handler.getStackInSlot(i).copy());
        }
        for (Ingredient.IItemList iItemList : input) {
            boolean found = false;
            for (ItemStack stack : iItemList.getStacks()) {
                int i = 0;
                for (; i < handlerItems.size(); i++) {
                    if (handlerItems.get(i).isItemEqual(stack)) {
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
        return handlerItems.size() == 0 && tank.drainForced(inputFluid, IFluidHandler.FluidAction.SIMULATE).getAmount() == inputFluid.getAmount();
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
        return output;
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

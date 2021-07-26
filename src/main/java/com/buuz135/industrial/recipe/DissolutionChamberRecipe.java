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
import com.buuz135.industrial.module.ModuleGenerator;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.IndustrialTags;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.recipe.serializer.GenericSerializer;
import com.hrznstudio.titanium.recipe.serializer.SerializableRecipe;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public class DissolutionChamberRecipe extends SerializableRecipe {

    public static GenericSerializer<DissolutionChamberRecipe> SERIALIZER = new GenericSerializer<>(new ResourceLocation(Reference.MOD_ID, "dissolution_chamber"), DissolutionChamberRecipe.class);
    public static List<DissolutionChamberRecipe> RECIPES = new ArrayList<>();

    static {
        new DissolutionChamberRecipe(new ResourceLocation(Reference.MOD_ID, "pink_slime_ball"), new Ingredient.Value[]{new Ingredient.ItemValue(new ItemStack(Items.GLASS_PANE))}, new FluidStack(ModuleCore.PINK_SLIME.getSourceFluid(), 300), 200, new ItemStack(ModuleCore.PINK_SLIME_ITEM), new FluidStack(Fluids.WATER, 150));
        new DissolutionChamberRecipe(new ResourceLocation(Reference.MOD_ID, "pink_slime_ingot"),
                new Ingredient.Value[]{
                        new Ingredient.TagValue(Tags.Items.INGOTS_IRON),
                        new Ingredient.TagValue(Tags.Items.INGOTS_IRON),
                        new Ingredient.TagValue(Tags.Items.INGOTS_GOLD),
                        new Ingredient.TagValue(Tags.Items.INGOTS_GOLD),
                }, new FluidStack(ModuleCore.PINK_SLIME.getSourceFluid(), 1000), 300, new ItemStack(ModuleCore.PINK_SLIME_INGOT), FluidStack.EMPTY);
        new DissolutionChamberRecipe(new ResourceLocation(Reference.MOD_ID, "mechanical_dirt"),
                new Ingredient.Value[]{
                        new Ingredient.ItemValue(new ItemStack(Blocks.DIRT)),
                        new Ingredient.ItemValue(new ItemStack(Blocks.DIRT)),
                        new Ingredient.ItemValue(new ItemStack(Items.ROTTEN_FLESH)),
                        new Ingredient.ItemValue(new ItemStack(Items.ROTTEN_FLESH)),
                        new Ingredient.TagValue(IndustrialTags.Items.MACHINE_FRAME_PITY),
                }, new FluidStack(ModuleCore.MEAT.getSourceFluid(), 1000), 100, new ItemStack(ModuleResourceProduction.MECHANICAL_DIRT), FluidStack.EMPTY);
        new DissolutionChamberRecipe(new ResourceLocation(Reference.MOD_ID, "simple_machine_frame"),
                new Ingredient.Value[]{
                        new Ingredient.TagValue(IndustrialTags.Items.PLASTIC),
                        new Ingredient.TagValue(IndustrialTags.Items.MACHINE_FRAME_PITY),
                        new Ingredient.TagValue(IndustrialTags.Items.PLASTIC),
                        new Ingredient.ItemValue(new ItemStack(Items.NETHER_BRICK)),
                        new Ingredient.ItemValue(new ItemStack(Items.NETHER_BRICK)),
                        new Ingredient.TagValue(Tags.Items.INGOTS_IRON),
                        new Ingredient.TagValue(IndustrialTags.Items.GEAR_GOLD),
                        new Ingredient.TagValue(Tags.Items.INGOTS_IRON)
                },
                new FluidStack(ModuleCore.LATEX.getSourceFluid(), 250), 300, new ItemStack(ModuleCore.SIMPLE), FluidStack.EMPTY);
        new DissolutionChamberRecipe(new ResourceLocation(Reference.MOD_ID, "advanced_machine_frame"),
                new Ingredient.Value[]{
                        new Ingredient.TagValue(IndustrialTags.Items.PLASTIC),
                        new Ingredient.TagValue(IndustrialTags.Items.MACHINE_FRAME_SIMPLE),
                        new Ingredient.TagValue(IndustrialTags.Items.PLASTIC),
                        new Ingredient.ItemValue(new ItemStack(Items.NETHERITE_SCRAP)),
                        new Ingredient.ItemValue(new ItemStack(Items.NETHERITE_SCRAP)),
                        new Ingredient.TagValue(Tags.Items.INGOTS_GOLD),
                        new Ingredient.TagValue(IndustrialTags.Items.GEAR_DIAMOND),
                        new Ingredient.TagValue(Tags.Items.INGOTS_GOLD)
                },
                new FluidStack(ModuleCore.PINK_SLIME.getSourceFluid(), 500), 300, new ItemStack(ModuleCore.ADVANCED), FluidStack.EMPTY);
        new DissolutionChamberRecipe(new ResourceLocation(Reference.MOD_ID, "dark_glass"),
                new Ingredient.Value[]{
                        new Ingredient.ItemValue(new ItemStack(Blocks.SOUL_SAND)),
                        new Ingredient.ItemValue(new ItemStack(Blocks.SOUL_SAND)),
                        new Ingredient.ItemValue(new ItemStack(Blocks.SOUL_SAND)),
                        new Ingredient.ItemValue(new ItemStack(Blocks.SOUL_SAND)),
                        new Ingredient.ItemValue(new ItemStack(Blocks.SOUL_SAND)),
                        new Ingredient.ItemValue(new ItemStack(Blocks.SOUL_SAND)),
                        new Ingredient.ItemValue(new ItemStack(Blocks.SOUL_SAND)),
                        new Ingredient.ItemValue(new ItemStack(Blocks.SOUL_SAND)),
                },
                new FluidStack(ModuleCore.LATEX.getSourceFluid(), 100), 100, new ItemStack(ModuleCore.DARK_GLASS, 8), FluidStack.EMPTY);
        new DissolutionChamberRecipe(new ResourceLocation(Reference.MOD_ID, "supreme_machine_frame"),
                new Ingredient.Value[]{
                        new Ingredient.TagValue(IndustrialTags.Items.PLASTIC),
                        new Ingredient.TagValue(IndustrialTags.Items.MACHINE_FRAME_ADVANCED),
                        new Ingredient.TagValue(IndustrialTags.Items.PLASTIC),
                        new Ingredient.ItemValue(new ItemStack(Items.NETHERITE_INGOT)),
                        new Ingredient.ItemValue(new ItemStack(Items.NETHERITE_INGOT)),
                        new Ingredient.TagValue(Tags.Items.GEMS_DIAMOND),
                        new Ingredient.TagValue(IndustrialTags.Items.GEAR_DIAMOND),
                        new Ingredient.TagValue(Tags.Items.GEMS_DIAMOND)
                },
                new FluidStack(ModuleCore.ETHER.getSourceFluid(), 135), 300, new ItemStack(ModuleCore.SUPREME), FluidStack.EMPTY);
        new DissolutionChamberRecipe(new ResourceLocation(Reference.MOD_ID, "mycelial_reactor"),
                new Ingredient.Value[]{
                        new Ingredient.TagValue(IndustrialTags.Items.PLASTIC),
                        new Ingredient.TagValue(IndustrialTags.Items.MACHINE_FRAME_SUPREME),
                        new Ingredient.TagValue(IndustrialTags.Items.PLASTIC),
                        new Ingredient.ItemValue(new ItemStack(Items.NETHERITE_INGOT)),
                        new Ingredient.ItemValue(new ItemStack(Items.NETHERITE_INGOT)),
                        new Ingredient.TagValue(IndustrialTags.Items.GEAR_DIAMOND),
                        new Ingredient.ItemValue(new ItemStack(Items.NETHER_STAR)),
                        new Ingredient.TagValue(IndustrialTags.Items.GEAR_DIAMOND)
                },
                new FluidStack(ModuleCore.ETHER.getSourceFluid(), 500), 600, new ItemStack(ModuleGenerator.MYCELIAL_REACTOR), FluidStack.EMPTY);
        new DissolutionChamberRecipe(new ResourceLocation(Reference.MOD_ID, "xp_bottles"),
                new Ingredient.Value[]{},
                new FluidStack(ModuleCore.ESSENCE.getSourceFluid(), 250), 5, new ItemStack(Items.EXPERIENCE_BOTTLE), FluidStack.EMPTY);
    }

    public Ingredient.Value[] input;
    public FluidStack inputFluid;
    public int processingTime;
    public ItemStack output;
    public FluidStack outputFluid;

    public DissolutionChamberRecipe(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    public DissolutionChamberRecipe(ResourceLocation resourceLocation, Ingredient.Value[] input, FluidStack inputFluid, int processingTime, ItemStack output, FluidStack outputFluid) {
        super(resourceLocation);
        this.input = input;
        this.inputFluid = inputFluid;
        this.processingTime = processingTime;
        this.output = output;
        this.output.getItem().onCraftedBy(this.output, null, null);
        this.outputFluid = outputFluid;
        RECIPES.add(this);
    }

    @Override
    public boolean matches(Container inv, Level worldIn) {
        return false;
    }

    public boolean matches(IItemHandler handler, FluidTankComponent tank) {
        if (input == null || tank == null || inputFluid == null) return false;
        List<ItemStack> handlerItems = new ArrayList<>();
        for (int i = 0; i < handler.getSlots(); i++) {
            if (!handler.getStackInSlot(i).isEmpty()) handlerItems.add(handler.getStackInSlot(i).copy());
        }
        for (Ingredient.Value iItemList : input) {
            boolean found = false;
            for (ItemStack stack : iItemList.getItems()) {
                int i = 0;
                for (; i < handlerItems.size(); i++) {
                    if (handlerItems.get(i).sameItem(stack)) {
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
    public ItemStack assemble(Container inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem() {
        return output;
    }

    @Override
    public GenericSerializer<? extends SerializableRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return SERIALIZER.getRecipeType();
    }
}

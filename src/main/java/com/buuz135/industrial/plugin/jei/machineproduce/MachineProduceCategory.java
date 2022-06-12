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

package com.buuz135.industrial.plugin.jei.machineproduce;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;

import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.client.screen.addon.SlotsScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.DefaultAssetProvider;
import com.hrznstudio.titanium.util.AssetUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class MachineProduceCategory implements IRecipeCategory<MachineProduceWrapper> {

    public static ResourceLocation MACHINE_PRODUCE = new ResourceLocation(Reference.MOD_ID, "machine_produce");

    private IGuiHelper guiHelper;
    private final IDrawable smallTank;

    public MachineProduceCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.smallTank = guiHelper.createDrawable(DefaultAssetProvider.DEFAULT_LOCATION, 235 + 3, 1 + 3, 12, 13);
    }

    @Override
    public ResourceLocation getUid() {
        return MACHINE_PRODUCE;
    }

    @Override
    public Class<? extends MachineProduceWrapper> getRecipeClass() {
        return MachineProduceWrapper.class;
    }

    @Override
    public Component getTitle() {
        return new TextComponent( "Machine Outputs");
    }

    @Override
    public IDrawable getBackground() {
        return guiHelper.drawableBuilder(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 0, 0, 54, 26).addPadding(   0,0,0,26).build();
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setIngredients(MachineProduceWrapper machineProduceWrapper, IIngredients iIngredients) {
        iIngredients.setInput(VanillaTypes.ITEM, new ItemStack(machineProduceWrapper.getBlock()));
        if (machineProduceWrapper.getOutputItem() == null){
            iIngredients.setOutput(VanillaTypes.FLUID, machineProduceWrapper.getOutputFluid());
        } else {
            iIngredients.setOutputLists(VanillaTypes.ITEM, Collections.singletonList(Arrays.asList(machineProduceWrapper.getOutputItem().getItems().clone())));
        }
    }


    @Override
    public void setRecipe(IRecipeLayout recipeLayout, MachineProduceWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStackGroup = recipeLayout.getItemStacks();
        guiItemStackGroup.init(0, true, 0, 4);
        guiItemStackGroup.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        if (recipeWrapper.getOutputItem() == null){
            IGuiFluidStackGroup guiFluidStackGroup = recipeLayout.getFluidStacks();
            guiFluidStackGroup.init(1, true,56 + 3, 4 + 3, 12, 13, 1000, false, smallTank);
            guiFluidStackGroup.set(1, ingredients.getOutputs(VanillaTypes.FLUID).get(0));
        } else {
            guiItemStackGroup.init(1, false, 56, 4);
            guiItemStackGroup.set(1, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
        }
    }

    @Override
    public void draw(MachineProduceWrapper recipe, PoseStack stack, double mouseX, double mouseY) {
        if (recipe.getOutputItem() == null){
            AssetUtil.drawAsset(stack, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.TANK_SMALL), 56 , Minecraft.getInstance().font.lineHeight / 2);
        } else {
            SlotsScreenAddon.drawAsset(stack, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 56 , Minecraft.getInstance().font.lineHeight / 2, 0, 0, 1, integer -> Pair.of(1,1), integer -> ItemStack.EMPTY, true, integer -> new Color(DyeColor.ORANGE.getFireworkColor()), integer -> true);
        }
    }

}

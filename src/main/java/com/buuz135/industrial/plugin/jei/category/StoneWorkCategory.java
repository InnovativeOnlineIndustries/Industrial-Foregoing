package com.buuz135.industrial.plugin.jei.category;

import java.util.List;

import com.buuz135.industrial.block.resourceproduction.tile.MaterialStoneWorkFactoryTile;
import com.buuz135.industrial.utils.Reference;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class StoneWorkCategory implements IRecipeCategory<StoneWorkCategory.Wrapper> {

    public static ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "stonework");

    private final IGuiHelper helper;

    public StoneWorkCategory(IGuiHelper helper) {
        this.helper = helper;
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public Class<? extends Wrapper> getRecipeClass() {
        return Wrapper.class;
    }

    @Override
    public Component getTitle() {
        // TODO: 21/08/2021 Make this translatable.
        return new TextComponent("Material StoneWork Factory");
    }

    @Override
    public IDrawable getBackground() {
        return helper.createDrawable(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 94, 0, 160, 26);
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setIngredients(Wrapper wrapper, IIngredients iIngredients) {
        iIngredients.setInput(VanillaTypes.ITEM, wrapper.getInput());
        iIngredients.setOutput(VanillaTypes.ITEM, wrapper.getOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, Wrapper wrapper, IIngredients iIngredients) {
        iRecipeLayout.getItemStacks().init(0, true, 0, 4);
        iRecipeLayout.getItemStacks().set(0, wrapper.getInput());

        iRecipeLayout.getItemStacks().init(10, false, 138, 4);
        iRecipeLayout.getItemStacks().set(10, wrapper.getOutput());

        for (int i = 0; i < wrapper.getModes().size(); i++) {
            iRecipeLayout.getItemStacks().init(i +1, true, 28 + i * 24, 4);
            iRecipeLayout.getItemStacks().set(1+ i, wrapper.getModes().get(i).getIcon());
            //ItemStackUtils.renderItemIntoGUI(matrixStack, recipe.getModes().get(i).getIcon(), 29 + i * 24, 5);
        }
    }

    @Override
    public void draw(Wrapper recipe, PoseStack stack, double mouseX, double mouseY) {


    }

    public static class Wrapper {

        private final ItemStack input;
        private final List<MaterialStoneWorkFactoryTile.StoneWorkAction> modes;
        private final ItemStack output;

        public Wrapper(ItemStack input, List<MaterialStoneWorkFactoryTile.StoneWorkAction> modes, ItemStack output) {
            this.input = input;
            this.modes = modes;
            this.output = output;
            while (this.modes.size() < 4) {
                this.modes.add(MaterialStoneWorkFactoryTile.ACTION_RECIPES[4]);
            }
        }

        public ItemStack getInput() {
            return input;
        }

        public List<MaterialStoneWorkFactoryTile.StoneWorkAction> getModes() {
            return modes;
        }

        public ItemStack getOutput() {
            return output;
        }
    }
}

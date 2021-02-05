package com.buuz135.industrial.plugin.jei.category;

import com.buuz135.industrial.recipe.StoneWorkGenerateRecipe;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.client.screen.addon.SlotsScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.DefaultAssetProvider;
import com.hrznstudio.titanium.util.LangUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StoneWorkGeneratorCategory implements IRecipeCategory<StoneWorkGenerateRecipe> {

    public static ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "stonework_generate");

    private final IGuiHelper helper;

    public StoneWorkGeneratorCategory(IGuiHelper helper) {
        this.helper = helper;
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public Class<? extends StoneWorkGenerateRecipe> getRecipeClass() {
        return StoneWorkGenerateRecipe.class;
    }

    @Override
    public String getTitle() {
        return "StoneWork Generation";
    }

    @Override
    public IDrawable getBackground() {
        return helper.createBlankDrawable(130, 9*6);
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setIngredients(StoneWorkGenerateRecipe wrapper, IIngredients iIngredients) {
        iIngredients.setOutput(VanillaTypes.ITEM, wrapper.output);
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, StoneWorkGenerateRecipe wrapper, IIngredients iIngredients) {
        iRecipeLayout.getItemStacks().init(10, false, 8, 9*2);
        iRecipeLayout.getItemStacks().set(10, wrapper.output);
    }

    @Override
    public void draw(StoneWorkGenerateRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        List<ITextComponent> lines = new ArrayList<>();
        SlotsScreenAddon.drawAsset(matrixStack, Minecraft.getInstance().currentScreen, DefaultAssetProvider.DEFAULT_PROVIDER, 8, 9*2, 0, 0, 1, integer -> Pair.of(1,1), integer -> ItemStack.EMPTY, true, integer -> new Color(DyeColor.ORANGE.getFireworkColor()), integer -> true);
        lines.add(new StringTextComponent(TextFormatting.GOLD + LangUtil.getString("tooltip.industrialforegoing.needs")));
        lines.add(new StringTextComponent(TextFormatting.YELLOW + " - " + TextFormatting.DARK_GRAY + recipe.waterNeed + TextFormatting.DARK_AQUA + LangUtil.getString("tooltip.industrialforegoing.mb_of", LangUtil.getString("block.minecraft.water"))));
        lines.add(new StringTextComponent(TextFormatting.YELLOW + " - " + TextFormatting.DARK_GRAY + recipe.lavaNeed + TextFormatting.DARK_AQUA + LangUtil.getString("tooltip.industrialforegoing.mb_of", LangUtil.getString("block.minecraft.lava"))));
        lines.add(new StringTextComponent(TextFormatting.GOLD + LangUtil.getString("tooltip.industrialforegoing.consumes")));
        lines.add(new StringTextComponent(TextFormatting.YELLOW + " - " + TextFormatting.DARK_GRAY + recipe.waterConsume + TextFormatting.DARK_AQUA + LangUtil.getString("tooltip.industrialforegoing.mb_of", LangUtil.getString("block.minecraft.water"))));
        lines.add(new StringTextComponent(TextFormatting.YELLOW + " - " + TextFormatting.DARK_GRAY + recipe.lavaConsume + TextFormatting.DARK_AQUA + LangUtil.getString("tooltip.industrialforegoing.mb_of", LangUtil.getString("block.minecraft.lava"))));
        int y = 0;
        for (ITextComponent line : lines) {
            Minecraft.getInstance().fontRenderer.drawString(matrixStack, line.getString(), 36, y * Minecraft.getInstance().fontRenderer.FONT_HEIGHT, 0xFFFFFFFF);
            ++y;
        }
    }

}

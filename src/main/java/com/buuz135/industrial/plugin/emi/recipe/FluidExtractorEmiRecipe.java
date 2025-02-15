package com.buuz135.industrial.plugin.emi.recipe;

import com.buuz135.industrial.plugin.emi.IFEmiPlugin;
import com.buuz135.industrial.recipe.FluidExtractorRecipe;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.client.screen.asset.DefaultAssetProvider;
import com.hrznstudio.titanium.util.AssetUtil;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class FluidExtractorEmiRecipe extends CustomEmiRecipe {

    private final RecipeHolder<FluidExtractorRecipe> recipe;

    public FluidExtractorEmiRecipe(RecipeHolder<FluidExtractorRecipe> recipe) {
        super(recipe.id(), IFEmiPlugin.FLUID_EXTRACTOR_EMI_CATEGORY,
                combineIng(
                        fromInput(EmiIngredient.of(recipe.value().input))
                ),
                fromOutput(new ItemStack(recipe.value().result.getBlock()), recipe.value().output));
        this.recipe = recipe;
    }


    @Override
    public int getDisplayWidth() {
        return 160;
    }

    @Override
    public int getDisplayHeight() {
        return 58;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(this.getInputs().get(0), 1, 17);

        widgets.addSlot(EmiIngredient.of(List.of(this.getOutputs().get(0))), 27, 34).recipeContext(this);
        widgets.addTank(this.getOutputs().get(1), 59, 3, 14, 52, 10).backgroundTexture(DefaultAssetProvider.DEFAULT_LOCATION, 177 + 3, 1 + 3).drawBack(false).recipeContext(this);

        widgets.addFillingArrow(26, 12, 5000);

        widgets.addDrawable(0, 0, 0, 0, (draw, mouseX, mouseY, delta) -> {
            draw.drawString(Minecraft.getInstance().font, ChatFormatting.DARK_GRAY + Component.translatable("text.industrialforegoing.jei.recipe.production").getString(), 80, 6, 0xFFFFFF, false);
            draw.drawString(Minecraft.getInstance().font, ChatFormatting.DARK_GRAY + "" + recipe.value().output.getAmount() + Component.translatable("text.industrialforegoing.jei.recipe.mb_work").getString(), 80, 6 + (Minecraft.getInstance().font.lineHeight + 2) * 1, 0xFFFFFF, false);
            if (recipe.value().outputsLatex()) {
                draw.drawString(Minecraft.getInstance().font, ChatFormatting.DARK_AQUA + "" + Component.translatable("text.industrialforegoing.jei.recipe.tripled_when").getString(), 80, 6 + (Minecraft.getInstance().font.lineHeight + 2) * 2, 0xFFFFFF, false);
                draw.drawString(Minecraft.getInstance().font, ChatFormatting.DARK_AQUA + "" + Component.translatable("text.industrialforegoing.jei.recipe.powered").getString(), 80, 6 + (Minecraft.getInstance().font.lineHeight + 2) * 3, 0xFFFFFF, false);
            }
            AssetUtil.drawAsset(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.TANK_NORMAL), 57, 1);

        });

        widgets.addTooltipText(List.of(Component.literal(Component.translatable("text.industrialforegoing.jei.recipe.production_rate").getString())), 78, 5, 140 - 78, 20);
    }

    public RecipeHolder<FluidExtractorRecipe> getRecipe() {
        return recipe;
    }
}

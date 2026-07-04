package com.buuz135.industrial.plugin.emi.recipe;

import com.buuz135.industrial.api.recipe.ore.OreFluidEntryFermenter;
import com.buuz135.industrial.fluid.OreTitaniumFluidType;
import com.buuz135.industrial.plugin.emi.IFEmiPlugin;
import com.buuz135.industrial.plugin.emi.widget.NormalTankEmiWidget;
import dev.emi.emi.api.neoforge.NeoForgeEmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class FermentationStationEmiRecipe extends CustomEmiRecipe {

    private final OreFluidEntryFermenter recipe;

    public FermentationStationEmiRecipe(OreFluidEntryFermenter recipe) {
        super(ResourceLocation.parse(OreTitaniumFluidType.getFluidTag(recipe.getOutput())), IFEmiPlugin.FERMENTATION_STATION_EMI_CATEGORY,
                fromInput(recipe.getInput()),
                fromOutput(NeoForgeEmiStack.of(recipe.getOutput())));
        this.recipe = recipe;
    }

    @Override
    public int getDisplayWidth() {
        return 74;
    }

    @Override
    public int getDisplayHeight() {
        return 68;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.add(new NormalTankEmiWidget(this.getInputs().get(0), 1000, 2, 1));
        widgets.add(new NormalTankEmiWidget(this.getOutputs().get(0), 1000, 99 - 45, 1)).recipeContext(this);

        widgets.addFillingArrow(26, 21, 2000);

        widgets.addDrawable(0, 0, 0, 0, (draw, mouseX, mouseY, delta) -> {
            draw.drawString(Minecraft.getInstance().font, ChatFormatting.DARK_AQUA + Component.translatable("text.industrialforegoing.jei.recipe.up_to_500mb").getString(), 8, 59, 0xFFFFFF, false);
        });
    }
}

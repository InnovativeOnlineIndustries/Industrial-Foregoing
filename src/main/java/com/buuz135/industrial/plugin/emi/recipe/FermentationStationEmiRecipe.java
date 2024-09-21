package com.buuz135.industrial.plugin.emi.recipe;

import com.buuz135.industrial.api.recipe.ore.OreFluidEntryFermenter;
import com.buuz135.industrial.fluid.OreTitaniumFluidType;
import com.buuz135.industrial.plugin.emi.IFEmiPlugin;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.client.screen.asset.DefaultAssetProvider;
import com.hrznstudio.titanium.util.AssetUtil;
import dev.emi.emi.api.neoforge.NeoForgeEmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
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

        widgets.addTank(this.getInputs().get(0), 4, 3, 14, 52, 1000).drawBack(false);

        widgets.addTank(this.getOutputs().get(0), 99 - 45 + 2, 3, 14, 52, 1000).drawBack(false).recipeContext(this);

        widgets.addFillingArrow(26, 21, 2000);

        widgets.addDrawable(0, 0, 0, 0, (draw, mouseX, mouseY, delta) -> {
            AssetUtil.drawAsset(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.TANK_NORMAL), 2, 1);
            AssetUtil.drawAsset(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.TANK_NORMAL), 99 - 45, 1);
            draw.drawString(Minecraft.getInstance().font, ChatFormatting.DARK_AQUA + "Up to 500mb", 8, 59, 0xFFFFFF, false);
        });

    }


}

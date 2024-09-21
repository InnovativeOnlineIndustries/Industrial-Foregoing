package com.buuz135.industrial.plugin.emi.recipe;

import com.buuz135.industrial.api.recipe.ore.OreFluidEntrySieve;
import com.buuz135.industrial.fluid.OreTitaniumFluidType;
import com.buuz135.industrial.plugin.emi.IFEmiPlugin;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.client.screen.asset.DefaultAssetProvider;
import com.hrznstudio.titanium.util.AssetUtil;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class FluidSieveEmiRecipe extends CustomEmiRecipe {

    private final OreFluidEntrySieve recipe;

    public FluidSieveEmiRecipe(OreFluidEntrySieve recipe) {
        super(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "fluid_sieve_" + ResourceLocation.parse(OreTitaniumFluidType.getFluidTag(recipe.getInput())).getPath()), IFEmiPlugin.FLUID_SIEVE_EMI_CATEGORY,
                combineIng(fromInput(recipe.getInput()), fromInput(EmiIngredient.of(recipe.getSieveItem()))),
                fromOutput(EmiStack.of(recipe.getOutput())));
        this.recipe = recipe;
    }

    @Override
    public int getDisplayWidth() {
        return 74;
    }

    @Override
    public int getDisplayHeight() {
        return 58;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {

        widgets.addTank(this.getInputs().get(0), 3, 3, 14, 52, 200).drawBack(false);
        widgets.addSlot(this.getInputs().get(1), 24, 36);

        widgets.addSlot(this.getOutputs().get(0), 50, 17).recipeContext(this);

        widgets.addFillingArrow(24, 18, 2000);

        widgets.addDrawable(0, 0, 0, 0, (draw, mouseX, mouseY, delta) -> {
            AssetUtil.drawAsset(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.TANK_NORMAL), 1, 1);
        });

    }


}

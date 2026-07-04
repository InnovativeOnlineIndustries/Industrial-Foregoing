package com.buuz135.industrial.plugin.emi.recipe;

import com.buuz135.industrial.api.recipe.ore.OreFluidEntryRaw;
import com.buuz135.industrial.fluid.OreTitaniumFluidType;
import com.buuz135.industrial.plugin.emi.IFEmiPlugin;
import com.buuz135.industrial.plugin.emi.widget.NormalTankEmiWidget;
import com.buuz135.industrial.plugin.emi.widget.SmallTankEmiWidget;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.client.screen.addon.SlotsScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.DefaultAssetProvider;
import dev.emi.emi.api.neoforge.NeoForgeEmiStack;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;

public class OreWasherEmiRecipe extends CustomEmiRecipe {

    private final OreFluidEntryRaw recipe;

    public OreWasherEmiRecipe(OreFluidEntryRaw recipe) {
        super(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "washing_" + ResourceLocation.parse(OreTitaniumFluidType.getFluidTag(recipe.getOutput())).getPath()), IFEmiPlugin.ORE_WASHER_EMI_CATEGORY,
                combineIng(fromInput(recipe.getInput()), fromInput(EmiIngredient.of(recipe.getOre()))),
                fromOutput(NeoForgeEmiStack.of(recipe.getOutput())));
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
        widgets.add(new SmallTankEmiWidget(this.getInputs().get(0), 200, 2, 30)).recipeContext(this);
        widgets.addSlot(this.getInputs().get(1), 2, 7).drawBack(false);

        widgets.add(new NormalTankEmiWidget(this.getOutputs().get(0), 1000, 54, 1)).recipeContext(this);

        widgets.addFillingArrow(26, 19, 2000);

        widgets.addDrawable(0, 0, 0, 0, (draw, mouseX, mouseY, delta) -> {
            SlotsScreenAddon.drawAsset(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 3, 8, 0, 0, 1, integer -> Pair.of(18 * (integer % 1), 18 * (integer / 1)), integer -> ItemStack.EMPTY, true, integer -> new Color(DyeColor.BLUE.getFireworkColor()), integer -> true, 1);
        });
    }
}

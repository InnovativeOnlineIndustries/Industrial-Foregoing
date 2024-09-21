package com.buuz135.industrial.plugin.emi.recipe;

import com.buuz135.industrial.api.recipe.ore.OreFluidEntryRaw;
import com.buuz135.industrial.fluid.OreTitaniumFluidType;
import com.buuz135.industrial.plugin.emi.IFEmiPlugin;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.client.screen.addon.SlotsScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.DefaultAssetProvider;
import com.hrznstudio.titanium.util.AssetUtil;
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

        widgets.addTank(this.getInputs().get(0), 4, 32, 14, 15, 200).drawBack(false);
        widgets.addSlot(this.getInputs().get(1), 2, 7).drawBack(false);

        widgets.addTank(this.getOutputs().get(0), 56, 3, 14, 52, 200).drawBack(false).recipeContext(this);

        widgets.addFillingArrow(26, 19, 2000);

        widgets.addDrawable(0, 0, 0, 0, (draw, mouseX, mouseY, delta) -> {
            SlotsScreenAddon.drawAsset(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 3, 8, 0, 0, 1, integer -> Pair.of(18 * (integer % 1), 18 * (integer / 1)), integer -> ItemStack.EMPTY, true, integer -> new Color(DyeColor.BLUE.getFireworkColor()), integer -> true, 1);

            AssetUtil.drawAsset(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.TANK_SMALL), 2, 30);
            AssetUtil.drawAsset(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.TANK_NORMAL), 54, 1);
        });

    }


}

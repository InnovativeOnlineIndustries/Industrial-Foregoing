package com.buuz135.industrial.plugin.emi.recipe;

import com.buuz135.industrial.config.machine.core.LatexProcessingUnitConfig;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.plugin.emi.IFEmiPlugin;
import com.buuz135.industrial.plugin.emi.widget.EnergyBarEmiWidget;
import com.buuz135.industrial.plugin.emi.widget.NormalTankEmiWidget;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.client.screen.addon.SlotsScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.DefaultAssetProvider;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.List;

public class LatexProcessingUnitEmiRecipe extends CustomEmiRecipe {
    public static final EmiStack LATEX = EmiStack.of(ModuleCore.LATEX.getFlowingFluid().get(), 750);
    public static final EmiStack WATER = EmiStack.of(Fluids.WATER.getSource(), 500);

    public LatexProcessingUnitEmiRecipe() {
        super(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "/emi/latex_processing_unit"), IFEmiPlugin.LATEX_PROCESSING_UNIT_EMI_CATEGORY,
                List.of(LATEX, WATER), List.of(EmiStack.of(ModuleCore.DRY_RUBBER.get())));
    }

    @Override
    public int getDisplayWidth() {
        return 112;
    }

    @Override
    public int getDisplayHeight() {
        return 56;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addFillingArrow(62, 19, LatexProcessingUnitConfig.maxProgress * 50);
        widgets.addSlot(this.getOutputs().getFirst(), 91, 1).recipeContext(this);

        widgets.add(new EnergyBarEmiWidget(0, 0, LatexProcessingUnitConfig.powerPerTick * LatexProcessingUnitConfig.maxProgress, LatexProcessingUnitConfig.maxStoredPower));
        widgets.add(new NormalTankEmiWidget(24, 0, LATEX, LatexProcessingUnitConfig.maxLatexTankSize, null));
        widgets.add(new NormalTankEmiWidget(48, 0, WATER, LatexProcessingUnitConfig.maxWaterTankSize, null));

        widgets.addDrawable(0, 0, 0, 0, (draw, mouseX, mouseY, delta) -> {
            SlotsScreenAddon.drawAsset(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 92, 2, 0, 0, 3, integer -> Pair.of(18 * (integer % 1), 18 * (integer / 1)), integer -> ItemStack.EMPTY, true, integer -> new Color(DyeColor.ORANGE.getFireworkColor()), integer -> true, 1);
        });
    }
}

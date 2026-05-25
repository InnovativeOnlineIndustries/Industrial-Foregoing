package com.buuz135.industrial.plugin.emi.recipe;

import com.buuz135.industrial.config.machine.agriculturehusbandry.SewageComposterConfig;
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
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.List;

public class SewageComposterEmiRecipe extends CustomEmiRecipe {
    public static final EmiStack SEWAGE = EmiStack.of(ModuleCore.SEWAGE.getSourceFluid().get(), 1000);

    public SewageComposterEmiRecipe() {
        super(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "/emi/sewage_composter"), IFEmiPlugin.SEWAGE_COMPOSTER_EMI_CATEGORY,
                List.of(SEWAGE), List.of(EmiStack.of(ModuleCore.FERTILIZER.get())));
    }

    @Override
    public int getDisplayWidth() {
        return 100;
    }

    @Override
    public int getDisplayHeight() {
        return 56;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addFillingArrow(48, 19, LatexProcessingUnitConfig.maxProgress * 50);
        widgets.addSlot(this.getOutputs().getFirst(), 79, 1).recipeContext(this);

        int consumed = SewageComposterConfig.powerPerTick * SewageComposterConfig.maxProgress;
        widgets.add(new EnergyBarEmiWidget(0, 0, consumed, Math.max(SewageComposterConfig.maxStoredPower, consumed)));
        widgets.add(new NormalTankEmiWidget(SEWAGE, 1000, 24, 0));

        widgets.addDrawable(0, 0, 0, 0, (draw, mouseX, mouseY, delta) -> {
            SlotsScreenAddon.drawAsset(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 80, 2, 0, 0, 3, integer -> Pair.of(18 * (integer % 1), 18 * (integer / 1)), integer -> ItemStack.EMPTY, true, integer -> new Color(DyeColor.ORANGE.getFireworkColor()), integer -> true, 1);
        });
    }
}

package com.buuz135.industrial.plugin.emi.recipe;

import com.buuz135.industrial.config.machine.resourceproduction.SludgeRefinerConfig;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.plugin.emi.IFEmiPlugin;
import com.buuz135.industrial.plugin.emi.widget.EnergyBarEmiWidget;
import com.buuz135.industrial.plugin.emi.widget.NormalTankEmiWidget;
import com.buuz135.industrial.utils.IndustrialTags;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.client.screen.addon.SlotsScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.DefaultAssetProvider;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.List;

public class SludgeRefinerEmiRecipe extends CustomEmiRecipe {
    public static final EmiStack SLUDGE = EmiStack.of(ModuleCore.SLUDGE.getSourceFluid().get(), 500);
    private static List<EmiStack> POSSIBLE_OUTPUTS = null;

    public SludgeRefinerEmiRecipe(EmiStack output) {
        super(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "/emi/sludge_refiner/" + output.getItemStack().getItem().builtInRegistryHolder().getKey().location().toString().replace(':', '/')), IFEmiPlugin.SLUDGE_REFINER_EMI_CATEGORY,
                List.of(SLUDGE), List.of(output));
    }

    public static List<EmiStack> getPossibleOutputs() {
        if (POSSIBLE_OUTPUTS == null) {
            POSSIBLE_OUTPUTS = EmiIngredient.of(IndustrialTags.Items.SLUDGE_OUTPUT).getEmiStacks();
            for (var output : POSSIBLE_OUTPUTS) {
                output.setChance(1.0F / POSSIBLE_OUTPUTS.size());
            }
        }

        return POSSIBLE_OUTPUTS;
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
        widgets.addFillingArrow(48, 19, 100 * 50);
        widgets.addSlot(this.getOutputs().getFirst(), 79, 1).recipeContext(this);

        int consumed = SludgeRefinerConfig.powerPerTick * 100;
        widgets.add(new EnergyBarEmiWidget(0, 0, consumed, Math.max(SludgeRefinerConfig.maxStoredPower, consumed)));
        widgets.add(new NormalTankEmiWidget(SLUDGE, SludgeRefinerConfig.maxSludgeTankSize, 24, 0));

        widgets.addDrawable(0, 0, 0, 0, (draw, mouseX, mouseY, delta) -> {
            SlotsScreenAddon.drawAsset(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 80, 2, 0, 0, 3, integer -> Pair.of(18 * (integer % 1), 18 * (integer / 1)), integer -> ItemStack.EMPTY, true, integer -> new Color(DyeColor.ORANGE.getFireworkColor()), integer -> true, 1);
        });
    }
}

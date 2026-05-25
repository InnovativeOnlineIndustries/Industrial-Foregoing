package com.buuz135.industrial.plugin.emi.recipe;

import com.buuz135.industrial.config.machine.resourceproduction.SporeRecreatorConfig;
import com.buuz135.industrial.plugin.emi.IFEmiPlugin;
import com.buuz135.industrial.plugin.emi.widget.EnergyBarEmiWidget;
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

public class SporesRecreatorEmiRecipe extends CustomEmiRecipe {
    public SporesRecreatorEmiRecipe(EmiStack input) {
        super(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "/emi/spores_recreator/" + input.getItemStack().getItem().builtInRegistryHolder().getKey().location().toString().replace(':', '/')), IFEmiPlugin.SPORES_RECREATOR_EMI_CATEGORY,
                List.of(input), List.of(input.copy().setAmount(2)));
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
        widgets.addSlot(this.getInputs().getFirst(), 24, 19);
        widgets.addSlot(this.getOutputs().getFirst(), 79, 1).recipeContext(this);

        int consumed = SporeRecreatorConfig.powerPerTick * 100;
        widgets.add(new EnergyBarEmiWidget(0, 0, consumed, Math.max(SporeRecreatorConfig.maxStoredPower, consumed)));

        widgets.addDrawable(0, 0, 0, 0, (draw, mouseX, mouseY, delta) -> {
            SlotsScreenAddon.drawAsset(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 25, 20, 0, 0, 1, integer -> Pair.of(18 * (integer % 1), 18 * (integer / 1)), integer -> ItemStack.EMPTY, true, integer -> new Color(DyeColor.LIGHT_BLUE.getFireworkColor()), integer -> true, 1);
            SlotsScreenAddon.drawAsset(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 80, 2, 0, 0, 3, integer -> Pair.of(18 * (integer % 1), 18 * (integer / 1)), integer -> ItemStack.EMPTY, true, integer -> new Color(DyeColor.ORANGE.getFireworkColor()), integer -> true, 1);
        });
    }
}

package com.buuz135.industrial.plugin.emi.recipe;

import com.buuz135.industrial.config.machine.resourceproduction.DyeMixerConfig;
import com.buuz135.industrial.plugin.emi.IFEmiPlugin;
import com.buuz135.industrial.plugin.emi.widget.EnergyBarEmiWidget;
import com.buuz135.industrial.plugin.emi.widget.ProgressBarEmiWidget;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.client.screen.addon.ProgressBarScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.SlotsScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.DefaultAssetProvider;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class DyeMixerEmiRecipe extends CustomEmiRecipe {
    private final ProgressBarEmiWidget redWidget;
    private final ProgressBarEmiWidget greenWidget;
    private final ProgressBarEmiWidget blueWidget;

    public DyeMixerEmiRecipe(int red, int green, int blue, int dye) {
        super(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "/emi/dye_mixer_" + dye), IFEmiPlugin.DYE_MIXER_EMI_CATEGORY,
                List.of(EmiStack.EMPTY), List.of(EmiStack.of(DyeItem.byColor(DyeColor.byId(dye)))));

        var redProgress = new ProgressBarScreenAddon<>(20, 0, new ProgressBarComponent<>(0, 0, 3)) {
            @Override
            public List<Component> getTooltipLines() {
                return List.of(Component.literal(ChatFormatting.GOLD + net.minecraft.network.chat.Component.translatable("text.industrialforegoing.display.amount_2").getString() + ChatFormatting.WHITE + NumberFormat.getNumberInstance(Locale.ROOT).format(red) + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE + NumberFormat.getNumberInstance(Locale.ROOT).format(300)));
            }
        };
        redProgress.getProgressBar().setColor(DyeColor.RED);
        redProgress.getProgressBar().setProgress(red);
        var greenProgress = new ProgressBarScreenAddon<>(20 + 13, 0, new ProgressBarComponent<>(0, 0, 3)) {
            @Override
            public List<Component> getTooltipLines() {
                return List.of(Component.literal(ChatFormatting.GOLD + net.minecraft.network.chat.Component.translatable("text.industrialforegoing.display.amount_2").getString() + ChatFormatting.WHITE + NumberFormat.getNumberInstance(Locale.ROOT).format(green) + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE + NumberFormat.getNumberInstance(Locale.ROOT).format(300)));
            }
        };
        greenProgress.getProgressBar().setColor(DyeColor.GREEN);
        greenProgress.getProgressBar().setProgress(green);
        var blueProgress = new ProgressBarScreenAddon<>(20 + 13 + 13, 0, new ProgressBarComponent<>(0, 0, 3)) {
            @Override
            public List<Component> getTooltipLines() {
                return List.of(Component.literal(ChatFormatting.GOLD + Component.translatable("text.industrialforegoing.display.amount_2").getString() + ChatFormatting.WHITE + NumberFormat.getNumberInstance(Locale.ROOT).format(blue) + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE + NumberFormat.getNumberInstance(Locale.ROOT).format(300)));
            }
        };
        blueProgress.getProgressBar().setColor(DyeColor.BLUE);
        blueProgress.getProgressBar().setProgress(blue);

        this.redWidget = new ProgressBarEmiWidget(redProgress, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.PROGRESS_BAR_BORDER_VERTICAL).getArea());
        this.greenWidget = new ProgressBarEmiWidget(greenProgress, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.PROGRESS_BAR_BORDER_VERTICAL).getArea());
        this.blueWidget = new ProgressBarEmiWidget(blueProgress, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.PROGRESS_BAR_BORDER_VERTICAL).getArea());
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
        widgets.addFillingArrow(62, 19, 100 * 50);
        widgets.addSlot(this.getOutputs().getFirst(), 91, 1).recipeContext(this);

        widgets.add(this.redWidget);
        widgets.add(this.greenWidget);
        widgets.add(this.blueWidget);
        widgets.add(new EnergyBarEmiWidget(0, 0, DyeMixerConfig.powerPerTick * 100, DyeMixerConfig.maxStoredPower));

        widgets.addDrawable(0, 0, 0, 0, (draw, mouseX, mouseY, delta) -> {
            SlotsScreenAddon.drawAsset(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 92, 2, 0, 0, 3, integer -> Pair.of(18 * (integer % 1), 18 * (integer / 1)), integer -> ItemStack.EMPTY, true, integer -> new Color(DyeColor.ORANGE.getFireworkColor()), integer -> true, 1);
        });
    }
}

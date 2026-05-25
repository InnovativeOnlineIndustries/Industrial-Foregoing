package com.buuz135.industrial.plugin.emi.recipe;

import com.buuz135.industrial.block.generator.mycelial.IMycelialGeneratorType;
import com.buuz135.industrial.plugin.emi.widget.SmallTankEmiWidget;
import com.buuz135.industrial.plugin.jei.generator.MycelialGeneratorRecipe;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.client.screen.addon.SlotsScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.DefaultAssetProvider;
import dev.emi.emi.api.neoforge.NeoForgeEmiStack;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MycelialGeneratorEmiRecipe extends CustomEmiRecipe {

    private final MycelialGeneratorRecipe recipe;
    private final IMycelialGeneratorType type;

    public MycelialGeneratorEmiRecipe(IMycelialGeneratorType type, MycelialGeneratorRecipe recipe, EmiRecipeCategory category) {
        super(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "emi_" + type.getName().toLowerCase(Locale.ROOT) + "_" + UUID.randomUUID().toString()), category,
                transformRecipe(recipe),
                fromOutput(EmiStack.EMPTY));
        this.type = type;
        this.recipe = recipe;
    }

    private static List<EmiIngredient> transformRecipe(MycelialGeneratorRecipe recipe) {
        var items = recipe.getInputItems();
        var fluids = recipe.getFluidItems();

        var size = Math.max(items.size(), fluids.size());
        var list = new ArrayList<EmiIngredient>(size);

        for (int i = 0; i < size; i++) {
            list.add(EmiStack.EMPTY);
        }

        for (int i = 0; i < items.size(); i++) {
            list.set(i, EmiIngredient.of(items.get(i).stream().map(EmiIngredient::of).toList()));
        }

        for (int i = 0; i < fluids.size(); i++) {
            var fluidStacks = fluids.get(i);
            if (!fluidStacks.isEmpty()) {
                list.set(i, NeoForgeEmiStack.of(fluidStacks.get(0)));
            }
        }

        return list;
    }

    @Override
    public int getDisplayWidth() {
        return 20 * type.getInputs().length + 110;
    }

    @Override
    public int getDisplayHeight() {
        return Minecraft.getInstance().font.lineHeight * 3;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        for (int i = 0; i < type.getInputs().length; i++) {
            IMycelialGeneratorType.Input input = type.getInputs()[i];
            if (input == IMycelialGeneratorType.Input.SLOT) {
                widgets.addSlot(this.getInputs().get(i), 20 * i, Minecraft.getInstance().font.lineHeight / 2);
            } else if (input == IMycelialGeneratorType.Input.TANK) {
                widgets.add(new SmallTankEmiWidget(this.getInputs().get(i), 1000, 20 * i, Minecraft.getInstance().font.lineHeight / 2));
            }
        }

        widgets.addDrawable(0, 0, 0, 0, (draw, mouseX, mouseY, delta) -> {
            for (int i = 0; i < type.getInputs().length; i++) {
                if (type.getInputs()[i] == IMycelialGeneratorType.Input.SLOT) {
                    int finalI = i;
                    SlotsScreenAddon.drawAsset(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 20 * i, Minecraft.getInstance().font.lineHeight / 2, 0, 0, 1, integer -> Pair.of(1, 1), integer -> ItemStack.EMPTY, true, integer -> new Color(type.getInputColors()[finalI].getFireworkColor()), integer -> true, 1);
                }
            }
            int x = 20 * type.getInputs().length + 3;
            draw.drawString(Minecraft.getInstance().font, ChatFormatting.DARK_GRAY + Component.translatable("text.industrialforegoing.jei.recipe.time").getString() + ChatFormatting.DARK_AQUA + new DecimalFormat().format(recipe.getTicks() / 20D) + ChatFormatting.DARK_GRAY + Component.translatable("tooltip.industrialforegoing.sec_short").getString(), x, Minecraft.getInstance().font.lineHeight * 0, 0xFFFFFFFF, false);
            draw.drawString(Minecraft.getInstance().font, ChatFormatting.DARK_GRAY + Component.translatable("text.industrialforegoing.jei.recipe.production").getString() + ChatFormatting.DARK_AQUA + recipe.getPowerTick() + ChatFormatting.DARK_GRAY + Component.translatable("tooltip.industrialforegoing.fe_t").getString(), x, Minecraft.getInstance().font.lineHeight * 1, 0xFFFFFFFF, false);
            draw.drawString(Minecraft.getInstance().font, ChatFormatting.DARK_GRAY + Component.translatable("text.industrialforegoing.jei.recipe.total").getString() + ChatFormatting.DARK_AQUA + new DecimalFormat().format(recipe.getTicks() * recipe.getPowerTick()) + ChatFormatting.DARK_GRAY + " FE", x, Minecraft.getInstance().font.lineHeight * 2, 0xFFFFFFFF, false);
        });


    }

    @Override
    public boolean supportsRecipeTree() {
        return false;
    }
}

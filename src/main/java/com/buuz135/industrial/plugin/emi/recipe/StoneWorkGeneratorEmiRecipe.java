package com.buuz135.industrial.plugin.emi.recipe;

import com.buuz135.industrial.plugin.emi.IFEmiPlugin;
import com.buuz135.industrial.recipe.StoneWorkGenerateRecipe;
import com.hrznstudio.titanium.client.screen.addon.SlotsScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.DefaultAssetProvider;
import com.hrznstudio.titanium.util.LangUtil;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StoneWorkGeneratorEmiRecipe extends CustomEmiRecipe {

    private final RecipeHolder<StoneWorkGenerateRecipe> recipe;

    public StoneWorkGeneratorEmiRecipe(RecipeHolder<StoneWorkGenerateRecipe> recipe) {
        super(recipe.id(), IFEmiPlugin.STONE_WORK_GENERATOR,
                List.of(),
                fromOutput(EmiStack.of(recipe.value().output)));
        this.recipe = recipe;
    }


    @Override
    public int getDisplayWidth() {
        return 130;
    }

    @Override
    public int getDisplayHeight() {
        return 9 * 6;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {


        widgets.addSlot(EmiIngredient.of(List.of(this.getOutputs().get(0))), 8, 9 * 2).drawBack(false).recipeContext(this);


        widgets.addDrawable(0, 0, 0, 0, (draw, mouseX, mouseY, delta) -> {
            List<Component> lines = new ArrayList<>();
            SlotsScreenAddon.drawAsset(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 8, 9 * 2, 0, 0, 1, integer -> Pair.of(1, 1), integer -> ItemStack.EMPTY, true, integer -> new Color(DyeColor.ORANGE.getFireworkColor()), integer -> true, 1);
            lines.add(Component.literal(ChatFormatting.GOLD + LangUtil.getString("tooltip.industrialforegoing.needs")));
            lines.add(Component.literal(ChatFormatting.YELLOW + " - " + ChatFormatting.DARK_GRAY + recipe.value().waterNeed + ChatFormatting.DARK_AQUA + LangUtil.getString("tooltip.industrialforegoing.mb_of", LangUtil.getString("block.minecraft.water"))));
            lines.add(Component.literal(ChatFormatting.YELLOW + " - " + ChatFormatting.DARK_GRAY + recipe.value().lavaNeed + ChatFormatting.DARK_AQUA + LangUtil.getString("tooltip.industrialforegoing.mb_of", LangUtil.getString("block.minecraft.lava"))));
            lines.add(Component.literal(ChatFormatting.GOLD + LangUtil.getString("tooltip.industrialforegoing.consumes")));
            lines.add(Component.literal(ChatFormatting.YELLOW + " - " + ChatFormatting.DARK_GRAY + recipe.value().waterConsume + ChatFormatting.DARK_AQUA + LangUtil.getString("tooltip.industrialforegoing.mb_of", LangUtil.getString("block.minecraft.water"))));
            lines.add(Component.literal(ChatFormatting.YELLOW + " - " + ChatFormatting.DARK_GRAY + recipe.value().lavaConsume + ChatFormatting.DARK_AQUA + LangUtil.getString("tooltip.industrialforegoing.mb_of", LangUtil.getString("block.minecraft.lava"))));
            int y = 0;
            for (Component line : lines) {
                draw.drawString(Minecraft.getInstance().font, line.getString(), 36, y * Minecraft.getInstance().font.lineHeight, 0xFFFFFFFF, false);
                ++y;
            }

        });

    }

}

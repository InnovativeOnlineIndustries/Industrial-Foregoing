package com.buuz135.industrial.plugin.emi.recipe;

import com.buuz135.industrial.plugin.emi.IFEmiPlugin;
import com.buuz135.industrial.plugin.emi.widget.SmallTankEmiWidget;
import com.buuz135.industrial.recipe.LaserDrillFluidRecipe;
import com.buuz135.industrial.recipe.data.EntityIngredient;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.api.neoforge.NeoForgeEmiStack;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LaserDrillFluidEmiRecipe extends CustomEmiRecipe {

    private final RecipeHolder<LaserDrillFluidRecipe> recipe;

    public LaserDrillFluidEmiRecipe(RecipeHolder<LaserDrillFluidRecipe> recipe) {
        super(recipe.id(), IFEmiPlugin.LASER_DRILL_FLUID_EMI_CATEGORY,
                List.of(),
                Arrays.stream(recipe.value().output.getFluids()).map(NeoForgeEmiStack::of).toList());
        this.recipe = recipe;
    }

    @Override
    public int getDisplayWidth() {
        return 82 + 70;
    }

    @Override
    public int getDisplayHeight() {
        return 26 + 60;
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        return List.of(EmiIngredient.of(this.recipe.value().catalyst));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {

        widgets.addSlot(this.getCatalysts().get(0), 36, 5).drawBack(false);

        widgets.add(new SmallTankEmiWidget(this.getOutputs().get(0), 1000, 60 + 35 + 3, 3)).recipeContext(this);

        widgets.addDrawable(0, 0, 0, 0, (draw, mouseX, mouseY, delta) -> {
            int recipeWidth = 82 + 35 + 35;
            RenderSystem.enableBlend();
            var toasts = ResourceLocation.fromNamespaceAndPath("minecraft", "toast/tree");
            draw.blitSprite(toasts, recipeWidth / 10 * 2, 30 + (Minecraft.getInstance().font.lineHeight + 2) * 3, 20, 20);
            draw.blitSprite(toasts, recipeWidth / 10 * 7, 30 + (Minecraft.getInstance().font.lineHeight + 2) * 3, 20, 20);
            var icons = ResourceLocation.fromNamespaceAndPath("neoforge", "textures/gui/icons.png");
            draw.blit(icons, recipeWidth / 10 * 7 + 1, 30 + (Minecraft.getInstance().font.lineHeight + 2) * 3 + 3, 0, 16, 16, 16);

            String minY = Component.translatable("text.industrialforegoing.miny").getString() + " " + recipe.value().rarity.get(recipe.value().pointer).depth_min();
            String maxY = Component.translatable("text.industrialforegoing.maxy").getString() + " " + recipe.value().rarity.get(recipe.value().pointer).depth_max();
            String biomes = Component.translatable("text.industrialforegoing.requirements").getString();

            draw.drawString(Minecraft.getInstance().font, ChatFormatting.DARK_GRAY + minY, recipeWidth / 10, 30, 0, false);
            draw.drawString(Minecraft.getInstance().font, ChatFormatting.DARK_GRAY + maxY, recipeWidth / 10 * 6, 30, 0, false);
            if (recipe.value().entityData.isPresent()) {
                EntityIngredient entityIngredient = recipe.value().entityData.get().getEntity();
                String name = entityIngredient.isTag() ? "#" + entityIngredient.tag().location() : entityIngredient.getType().getDescription().getString();
                String wight = Component.translatable("text.industrialforegoing.jei.recipe.over").getString() + name;
                draw.drawString(Minecraft.getInstance().font, ChatFormatting.DARK_GRAY + wight, recipeWidth / 10, 30 + (Minecraft.getInstance().font.lineHeight + 2), 0, false);
            }
            draw.drawString(Minecraft.getInstance().font, ChatFormatting.DARK_GRAY + "" + ChatFormatting.UNDERLINE + biomes, recipeWidth / 2 - Minecraft.getInstance().font.width(biomes) / 2, 30 + (Minecraft.getInstance().font.lineHeight + 2) * 2, 0, false);


            List<Component> components = new ArrayList<>();
            getTooltip(components, this.recipe.value(), mouseX, mouseY);
            draw.renderComponentTooltip(Minecraft.getInstance().font, components, mouseX, mouseY);
        });

        widgets.addButton(0, 70, 12, 12, 0, 0, () -> recipe.value().pointer > 0, (mouseX, mouseY, button) -> {
            if (recipe.value().pointer > 0) recipe.value().pointer--;
        });
        widgets.addButton(137, 70, 12, 12, 12, 0, () -> recipe.value().pointer < recipe.value().rarity.size() - 1, (mouseX, mouseY, button) -> {
            if (recipe.value().pointer < recipe.value().rarity.size() - 1) recipe.value().pointer++;
        });
    }


    public void getTooltip(List<Component> tooltip, LaserDrillFluidRecipe recipe, double mouseX, double mouseY) {
        if (mouseX > 0 && mouseX < 15 && mouseY > 70 && mouseY < 85 && recipe.pointer > 0) { // Inside the back button
            tooltip.add(Component.translatable("text.industrialforegoing.button.jei.prev_rarity"));
        }
        if (mouseX > 137 && mouseX < (137 + 15) && mouseY > 70 && mouseY < 85 && recipe.pointer < recipe.rarity.size() - 1) { //Inside the next button
            tooltip.add(Component.translatable("text.industrialforegoing.button.jei.next_rarity"));
        }
        if (recipe.entityData.isPresent()) {
            EntityIngredient entityIngredient = recipe.entityData.get().getEntity();
            String name = entityIngredient.isTag() ? "#" + entityIngredient.tag().location() : entityIngredient.getType().getDescription().getString();
            String text = Component.translatable("text.industrialforegoing.jei.recipe.over").getString() + name;
            int width = Minecraft.getInstance().font.width(text);
            if (mouseX > 12 && mouseX < 12 + width + 4 && mouseY > 28 + (Minecraft.getInstance().font.lineHeight + 2) && mouseY < 28 + (Minecraft.getInstance().font.lineHeight + 2) * 2) {
                if (!recipe.entityData.get().getData().isEmpty()) {
                    tooltip.add(Component.translatable("text.industrialforegoing.tooltip.data").withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.GOLD));
                    tooltip.add(recipe.entityData.get().getDisplay());
                }
            }
        }
        if (mouseX > 13 * 2 && mouseX < 13 * 2 + 20 && mouseY > 30 + (Minecraft.getInstance().font.lineHeight + 2) * 3 && mouseY < 30 + (Minecraft.getInstance().font.lineHeight + 2) * 3 + 20) { //Inside the whitelisted biomes
            tooltip.add(Component.translatable("text.industrialforegoing.tooltip.whitelisted_dimensions").withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.GOLD));
            if (recipe.rarity.get(recipe.pointer).dimensionRarity().whitelist().isEmpty())
                tooltip.add(Component.literal(Component.translatable("text.industrialforegoing.jei.recipe.any").getString()));
            else {
                for (ResourceKey<DimensionType> registryKey : recipe.rarity.get(recipe.pointer).dimensionRarity().whitelist()) {
                    tooltip.add(Component.literal("- " + WordUtils.capitalize(Arrays.stream(registryKey.location().getPath().split("_")).reduce((string, string2) -> string + " " + string2).get())));
                }
            }
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("text.industrialforegoing.tooltip.whitelisted_biomes").withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.GOLD));
            if (recipe.rarity.get(recipe.pointer).biomeRarity().whitelist().isEmpty())
                tooltip.add(Component.literal(Component.translatable("text.industrialforegoing.jei.recipe.any").getString()));
            else {
                for (TagKey<Biome> registryKey : recipe.rarity.get(recipe.pointer).biomeRarity().whitelist()) {
                    for (Holder<Biome> biomeHolder : Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.BIOME).getTagOrEmpty(registryKey)) {
                        tooltip.add(Component.literal("- ").append(Component.translatable("biome." + biomeHolder.getKey().location().getNamespace() + "." + biomeHolder.getKey().location().getPath())));
                    }
                }
            }
        }
        if (mouseX > 13 * 8 && mouseX < 13 * 8 + 20 && mouseY > 30 + (Minecraft.getInstance().font.lineHeight + 2) * 3 && mouseY < 30 + (Minecraft.getInstance().font.lineHeight + 2) * 3 + 20) { //Inside the whitelisted biomes
            tooltip.add(Component.translatable("text.industrialforegoing.tooltip.blacklisted_dimensions").withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.GOLD));
            if (recipe.rarity.get(recipe.pointer).dimensionRarity().blacklist().isEmpty())
                tooltip.add(Component.literal(Component.translatable("text.industrialforegoing.jei.recipe.none").getString()));
            else {
                for (ResourceKey<DimensionType> registryKey : recipe.rarity.get(recipe.pointer).dimensionRarity().blacklist()) {
                    tooltip.add(Component.literal("- " + WordUtils.capitalize(Arrays.stream(registryKey.location().getPath().split("_")).reduce((string, string2) -> string + " " + string2).get())));
                }
            }
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("text.industrialforegoing.tooltip.blacklisted_biomes").withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.GOLD));
            if (recipe.rarity.get(recipe.pointer).biomeRarity().blacklist().isEmpty())
                tooltip.add(Component.literal(Component.translatable("text.industrialforegoing.jei.recipe.none").getString()));
            else {
                for (TagKey<Biome> registryKey : recipe.rarity.get(recipe.pointer).biomeRarity().blacklist()) {
                    for (Holder<Biome> biomeHolder : Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.BIOME).getTagOrEmpty(registryKey)) {
                        tooltip.add(Component.literal("- ").append(Component.translatable("biome." + biomeHolder.getKey().location().getNamespace() + "." + biomeHolder.getKey().location().getPath())));
                    }
                }
            }
        }
    }

}

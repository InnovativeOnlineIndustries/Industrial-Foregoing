package com.buuz135.industrial.plugin.patchouli;

import com.buuz135.industrial.block.core.tile.DissolutionChamberTile;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import com.buuz135.industrial.utils.Reference;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageDoubleRecipeRegistry;

import java.util.List;

public class PageDissolution extends PageDoubleRecipeRegistry<DissolutionChamberRecipe> {
    public final ResourceLocation patchouliTexture = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "textures/gui/patchouli.png");

    @SuppressWarnings("unchecked")
    public PageDissolution() {
        super((RecipeType<? extends DissolutionChamberRecipe>) ModuleCore.DISSOLUTION_TYPE.get());
    }

    @Override
    protected void drawRecipe(GuiGraphics graphics, DissolutionChamberRecipe recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }

        RenderSystem.enableBlend();
        graphics.blit(patchouliTexture, recipeX - 6, recipeY, 0, 0, 128, 70, 128, 128);

        parent.drawCenteredStringNoShadow(graphics, getTitle(second).getVisualOrderText(), GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);

        for (int i = 0; i < recipe.input.size(); i++) {
            parent.renderIngredient(graphics,
                    recipeX + DissolutionChamberTile.getSlotPos(i).getLeft(),
                    recipeY + DissolutionChamberTile.getSlotPos(i).getRight() + 6,
                    mouseX, mouseY,
                    recipe.input.get(i));
        }
        if (recipe.inputFluid != null && !recipe.inputFluid.isEmpty()) {
            renderFluid(graphics, recipeX + 22, recipeY + 28, mouseX, mouseY, recipe.inputFluid);
        }
        if (!recipe.output.isEmpty()) {
            ItemStack stack = recipe.output.get();
            stack.getItem().onCraftedBy(stack, null, null);
            parent.renderItemStack(graphics, recipeX + 79, recipeY + 28, mouseX, mouseY, stack);
        }

        parent.renderItemStack(graphics, recipeX + 79, recipeY + 49, mouseX, mouseY, recipe.getToastSymbol());
    }

    @Override
    protected ItemStack getRecipeOutput(Level level, DissolutionChamberRecipe recipe) {
        if (recipe == null || level == null) {
            return ItemStack.EMPTY;
        }

        return recipe.getResultItem(level.registryAccess());
    }

    @Override
    protected int getRecipeHeight() {
        return 70;
    }

    private void renderFluid(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, FluidStack fluid) {
        if (fluid == null || fluid.isEmpty())
            return;

        final IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluid.getFluid());
        ResourceLocation flowing = fluidTypeExtensions.getStillTexture(fluid);
        AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(InventoryMenu.BLOCK_ATLAS);
        if (texture instanceof TextureAtlas atlas) {
            TextureAtlasSprite sprite = atlas.getSprite(flowing);
            if (sprite != null) {
                int color = fluidTypeExtensions.getTintColor(fluid);
                int red = FastColor.ARGB32.red(color);
                int green = FastColor.ARGB32.green(color);
                int blue = FastColor.ARGB32.blue(color);
                int alpha = FastColor.ARGB32.alpha(color);

                graphics.blit(x, y, 0, 16, 16, sprite, (float) red / 255.0F,
                        (float) green / 255.0F,
                        (float) blue / 255.0F,
                        (float) alpha / 255.0F);

                if (parent.isMouseInRelativeRange(mouseX, mouseY, x, y, 16, 16)) {
                    parent.setTooltip(List.of(fluid.getHoverName(), Component.literal(fluid.getAmount() + " mB").withStyle(ChatFormatting.GRAY)));
                }
            }
        }
    }
}

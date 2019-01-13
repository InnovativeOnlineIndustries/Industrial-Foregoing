/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.industrial.tile.ore;

import com.buuz135.industrial.api.recipe.ore.OreFluidEntryRaw;
import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.tile.CustomElectricMachine;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.IFluidTank;
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.LockedInventoryTogglePiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.FluidTankType;
import net.ndrei.teslacorelib.inventory.LockableItemHandler;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

public class OreWasherTile extends CustomElectricMachine {

    private IFluidTank input;
    private IFluidTank output;
    private LockableItemHandler items;
    private OreFluidEntryRaw recipe;

    public OreWasherTile() {
        super(OreWasherTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        input = this.addSimpleFluidTank(8000, "input", EnumDyeColor.BLUE, 48 + 12, 25, FluidTankType.INPUT, stack -> true, stack -> false);
        output = this.addSimpleFluidTank(8000, "output", EnumDyeColor.ORANGE, 129 + 7, 25, FluidTankType.OUTPUT, stack -> false, stack -> true);
        items = (LockableItemHandler) this.addSimpleInventory(1, "input", EnumDyeColor.GREEN, "input", new BoundingRectangle(18 * 5 + 8, 26, 18, 18), (stack, integer) -> true, (stack, integer) -> false, true, null);
    }


    @Override
    public void protectedUpdate() {
        super.protectedUpdate();
        if (this.world.getTotalWorldTime() % 5 == 0) {
            if (recipe != null && !recipe.matches(getFirstStack(), input.getFluid())) {
                recipe = null;
            }
            if (recipe == null) {
                for (OreFluidEntryRaw oreRawEntry : OreFluidEntryRaw.ORE_RAW_ENTRIES) {
                    if (oreRawEntry.matches(getFirstStack(), input.getFluid())) {
                        recipe = oreRawEntry;
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected float performWork() {
        if (recipe != null && input.drain(recipe.getInput().amount, false) != null && input.drain(recipe.getInput().amount, false).amount == recipe.getInput().amount && output.fill(recipe.getOutput(), false) == recipe.getOutput().amount) {
            getFirstStack().shrink(1);
            input.drain(recipe.getInput().amount, true);
            output.fill(recipe.getOutput().copy(), true);
            return 1;
        }
        return 0;
    }

    @Override
    public @NotNull List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer<?> container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
        pieces.add(new LockedInventoryTogglePiece(18 * 6 + 10, 25 + 3, this, EnumDyeColor.GREEN));
        pieces.add(new BasicRenderedGuiPiece(18 * 5 + 8, 52 + 6, 18, 18, new ResourceLocation(Reference.MOD_ID, "textures/gui/machines.png"), 1, 168) {
            @Override
            public void drawBackgroundLayer(@NotNull BasicTeslaGuiContainer<?> container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
                super.drawBackgroundLayer(container, guiX, guiY, partialTicks, mouseX, mouseY);
                if (recipe != null) {
                    GlStateManager.pushMatrix();
                    TextureAtlasSprite sprite = container.mc.getTextureMapBlocks().getTextureExtry(FluidsRegistry.MEAT.getFlowing().toString());
                    if (sprite != null) {
                        container.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                        GlStateManager.enableBlend();
                        Color color = new Color(FluidsRegistry.MEAT.getColor());
                        GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
                        container.drawTexturedModalRect(guiX + this.getLeft() + 1, guiY + this.getTop() + 1, sprite, 16, 16);
                        GlStateManager.color(1, 1, 1, 1);
                        GlStateManager.disableBlend();
                    }
                    RenderHelper.enableGUIStandardItemLighting();
                    container.mc.getRenderItem().renderItemIntoGUI(getFirstStack(), guiX + this.getLeft() + 1, guiY + this.getTop() + 1);
                    sprite = container.mc.getTextureMapBlocks().getTextureExtry(FluidsRegistry.ORE_FLUID_RAW.getFlowing().toString());
                    if (sprite != null) {
                        GlStateManager.translate(0, 0, 120);
                        container.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                        Color color = new Color(FluidsRegistry.ORE_FLUID_RAW.getColor(recipe.getOutput()));
                        GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, OreWasherTile.this.getWorkEnergyStored() / (float) OreWasherTile.this.getWorkEnergyCapacity());
                        container.drawTexturedModalRect(guiX + this.getLeft() + 1, guiY + this.getTop() + 1, sprite, 16, 16);
                        GlStateManager.color(1, 1, 1, 1);
                    }
                    GlStateManager.enableAlpha();
                    GlStateManager.popMatrix();
                }
            }

            @Override
            public void drawForegroundTopLayer(@NotNull BasicTeslaGuiContainer<?> container, int guiX, int guiY, int mouseX, int mouseY) {
                super.drawForegroundTopLayer(container, guiX, guiY, mouseX, mouseY);

            }
        });
        return pieces;
    }

    public ItemStack getFirstStack() {
        return items.getStackInSlot(0);
    }

    @Override
    protected boolean shouldAddFluidItemsInventory() {
        return false;
    }
}

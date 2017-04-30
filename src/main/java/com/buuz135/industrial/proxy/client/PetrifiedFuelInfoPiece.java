package com.buuz135.industrial.proxy.client;

import com.buuz135.industrial.tile.PetrifiedFuelGeneratorTile;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;

import java.util.concurrent.TimeUnit;

public class PetrifiedFuelInfoPiece extends BasicRenderedGuiPiece {

    private PetrifiedFuelGeneratorTile tile;

    public PetrifiedFuelInfoPiece(PetrifiedFuelGeneratorTile tile, int left, int top) {
        super(left, top, 76, 60, new ResourceLocation(Reference.MOD_ID, "textures/gui/machines.png"), 1, 1);
        this.tile = tile;
    }

    @Override
    public void drawForegroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, int mouseX, int mouseY) {
        super.drawForegroundLayer(container, guiX, guiY, mouseX, mouseY);
        if (this.tile != null) {
            long timeLeft = 0;
            long generatingRate = 0;
            if (this.tile.getGeneratedPowerStored() > 0) {
                timeLeft = ((this.tile.getGeneratedPowerStored() / 2) / this.tile.getGeneratedPowerReleaseRate()) / 20;
                generatingRate = this.tile.getGeneratedPowerReleaseRate();
            }
            FontRenderer renderer = Minecraft.getMinecraft().fontRenderer;
            GlStateManager.pushMatrix();
            GlStateManager.translate(this.getLeft() + 2, this.getTop() + 2, 0);
            GlStateManager.scale(1, 1, 1);
            renderer.drawString(TextFormatting.DARK_GRAY + new TextComponentTranslation("text.display.burning").getFormattedText(), 4, 4, 0xFFFFFF);
            renderer.drawString(TextFormatting.DARK_GRAY + getFormatedTime(timeLeft * 1000), 8, (renderer.FONT_HEIGHT) + 5, 0xFFFFFF);
            renderer.drawString(TextFormatting.DARK_GRAY + new TextComponentTranslation("text.display.producing").getFormattedText(), 4, 2 * (renderer.FONT_HEIGHT) + 9, 0xFFFFFF);
            renderer.drawString(TextFormatting.DARK_GRAY + "" + generatingRate + " T/tick", 8, 3 * (renderer.FONT_HEIGHT) + 10, 0xFFFFFF);
            GlStateManager.popMatrix();
        }
    }

    private String getFormatedTime(long time) {
        return String.format("%02dm %02ds", TimeUnit.MILLISECONDS.toMinutes(time) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)), TimeUnit.MILLISECONDS.toSeconds(time) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
    }
}

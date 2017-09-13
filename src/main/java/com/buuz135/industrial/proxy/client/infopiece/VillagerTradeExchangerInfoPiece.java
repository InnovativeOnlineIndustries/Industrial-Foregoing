package com.buuz135.industrial.proxy.client.infopiece;

import com.buuz135.industrial.tile.mob.VillagerTradeExchangerTile;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;

import java.util.Collections;

public class VillagerTradeExchangerInfoPiece extends BasicRenderedGuiPiece {

    private VillagerTradeExchangerTile tile;

    public VillagerTradeExchangerInfoPiece(VillagerTradeExchangerTile tile) {
        super(52, 22, 82, 26, new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 0, 0);
        this.tile = tile;
    }

    @Override
    public void drawBackgroundLayer(BasicTeslaGuiContainer<?> container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
        super.drawBackgroundLayer(container, guiX, guiY, partialTicks, mouseX, mouseY);
        if (tile.getMerchantRecipes() != null) {
            MerchantRecipe currentRecipe = tile.getMerchantRecipes().get(tile.getCurrent());
            ItemStackUtils.renderItemIntoGUI(currentRecipe.getItemToBuy(), guiX + this.getLeft() + 1, guiY + this.getHeight() + 1, 9);
            ItemStackUtils.renderItemIntoGUI(currentRecipe.getItemToSell(), guiX + this.getLeft() + 1 + 59, guiY + this.getHeight() + 1, 9);
        }
    }

    @Override
    public void drawForegroundTopLayer(BasicTeslaGuiContainer<?> container, int guiX, int guiY, int mouseX, int mouseY) {
        super.drawForegroundTopLayer(container, guiX, guiY, mouseX, mouseY);
        if (tile.getMerchantRecipes() != null) {
            MerchantRecipe currentRecipe = tile.getMerchantRecipes().get(tile.getCurrent());
            if (mouseX > guiX + this.getLeft() && mouseY > guiY + this.getHeight() && mouseX < guiX + this.getLeft() + 18 && mouseY < guiY + this.getHeight() + 18) {
                container.drawTooltip(Collections.singletonList(currentRecipe.getItemToBuy().getCount() + "x " + currentRecipe.getItemToBuy().getDisplayName()), mouseX - guiX, mouseY - guiY);
            }
            if (mouseX > guiX + this.getLeft() + 1 + 59 && mouseY > guiY + this.getHeight() && mouseX < guiX + this.getLeft() + 1 + 59 + 18 && mouseY < guiY + this.getHeight() + 18) {
                container.drawTooltip(Collections.singletonList(currentRecipe.getItemToSell().getCount() + "x " + currentRecipe.getItemToSell().getDisplayName()), mouseX - guiX, mouseY - guiY);
            }
        }
    }
}

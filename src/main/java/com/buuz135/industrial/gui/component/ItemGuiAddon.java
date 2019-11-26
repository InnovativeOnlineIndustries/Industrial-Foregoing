package com.buuz135.industrial.gui.component;

import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.client.gui.addon.BasicGuiAddon;
import com.hrznstudio.titanium.client.gui.asset.IAssetProvider;
import com.hrznstudio.titanium.util.AssetUtil;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import java.util.List;

public abstract class ItemGuiAddon extends BasicGuiAddon {

    protected ItemGuiAddon(int posX, int posY) {
        super(posX, posY);
    }

    @Override
    public int getXSize() {
        return 18;
    }

    @Override
    public int getYSize() {
        return 18;
    }

    @Override
    public void drawGuiContainerBackgroundLayer(Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        AssetUtil.drawAsset(screen, provider.getAsset(AssetTypes.ITEM_BACKGROUND), guiX + getPosX(), guiY + getPosY());
        RenderHelper.enableGUIStandardItemLighting();
        Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(getItemStack(), guiX + getPosX() + 1, guiY + getPosY() + 1);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlphaTest();
    }

    @Override
    public void drawGuiContainerForegroundLayer(Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY) {

    }

    @Override
    public List<String> getTooltipLines() {
        return Minecraft.getInstance().currentScreen.getTooltipFromItem(getItemStack());
    }

    public abstract ItemStack getItemStack();
}

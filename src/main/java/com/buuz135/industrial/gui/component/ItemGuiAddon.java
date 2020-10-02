package com.buuz135.industrial.gui.component;

import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.client.screen.addon.BasicScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.util.AssetUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;

public abstract class ItemGuiAddon extends BasicScreenAddon {

    private boolean tooltip;

    protected ItemGuiAddon(int posX, int posY) {
        super(posX, posY);
        this.tooltip = true;
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
    public void drawBackgroundLayer(MatrixStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        AssetUtil.drawAsset(stack, screen, provider.getAsset(AssetTypes.ITEM_BACKGROUND), guiX + getPosX(), guiY + getPosY());
        //RenderSystem.setupGui3DDiffuseLighting();
        Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(getItemStack(), guiX + getPosX() + 1, guiY + getPosY() + 1);
        RenderHelper.disableStandardItemLighting();
        RenderSystem.enableAlphaTest();
    }

    @Override
    public void drawForegroundLayer(MatrixStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY) {

    }

    public ItemGuiAddon withoutTooltip() {
        this.tooltip = false;
        return this;
    }

    @Override
    public List<ITextComponent> getTooltipLines() {
        return this.tooltip ? Minecraft.getInstance().currentScreen.getTooltipFromItem(getItemStack()) : new ArrayList<>();
    }

    public abstract ItemStack getItemStack();
}

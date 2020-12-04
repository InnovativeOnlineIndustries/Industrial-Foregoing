package com.buuz135.industrial.gui.component;

import com.buuz135.industrial.block.generator.mycelial.IMycelialGeneratorType;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.hrznstudio.titanium.client.screen.addon.BasicScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;

public class GeneratorBackgroundScreenAddon extends BasicScreenAddon {

    private final IMycelialGeneratorType type;

    public GeneratorBackgroundScreenAddon(int posX, int posY, IMycelialGeneratorType type) {
        super(posX, posY);
        this.type = type;
    }

    @Override
    public int getXSize() {
        return 0;
    }

    @Override
    public int getYSize() {
        return 0;
    }

    @Override
    public void drawBackgroundLayer(MatrixStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        float scale = 4;
        stack.push();
        stack.scale(scale, scale, scale);
        ItemStackUtils.renderItemIntoGUI(stack, new ItemStack(type.getDisplay()), guiX + getPosX(),guiY + getPosY());
        stack.scale(1/scale, 1/scale, 1/scale);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1,1,1, 0.6f);
        Minecraft.getInstance().getTextureManager().bindTexture(IAssetProvider.DEFAULT_LOCATION);
        stack.translate(0,0, 200);
        screen.blit(stack, guiX+ getPosX() - 24 ,guiY + this.getPosY() - 24 , 4, 4, 64, 64);
        stack.pop();
    }

    @Override
    public void drawForegroundLayer(MatrixStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY) {

    }
}

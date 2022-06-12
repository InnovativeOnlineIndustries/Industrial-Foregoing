/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
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

package com.buuz135.industrial.gui.component;

import com.buuz135.industrial.block.generator.mycelial.IMycelialGeneratorType;
import com.hrznstudio.titanium.client.screen.addon.BasicScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;

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
    public void drawBackgroundLayer(PoseStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
//        float scale = 4;
//        stack.pushPose();
//        stack.scale(scale, scale, scale);
//        ItemStackUtils.renderItemIntoGUI(stack, new ItemStack(type.getDisplay()), guiX + getPosX(),guiY + getPosY());
//        stack.scale(1/scale, 1/scale, 1/scale);
//        RenderSystem.enableBlend();
//        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//        RenderSystem.color4f(1,1,1, 0.6f);
//        Minecraft.getInstance().getTextureManager().bind(IAssetProvider.DEFAULT_LOCATION);
//        stack.translate(0,0, 200);
//        screen.blit(stack, guiX+ getPosX() - 24 ,guiY + this.getPosY() - 24 , 4, 4, 64, 64);
//        stack.popPose();
    }

    @Override
    public void drawForegroundLayer(PoseStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {

    }
}

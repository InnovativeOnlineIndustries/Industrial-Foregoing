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
package com.buuz135.industrial.proxy.client.entity;

import com.buuz135.industrial.entity.EntityPinkSlime;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.SlimeModel;

import java.awt.*;

import static com.buuz135.industrial.proxy.client.entity.RenderPinkSlime.NAMES;

public class LayerPinkGel extends LayerRenderer<EntityPinkSlime, SlimeModel<EntityPinkSlime>> {

    private final RenderPinkSlime slimeRenderer;
    private final SlimeModel<EntityPinkSlime> slimeModel = new SlimeModel<EntityPinkSlime>(0);

    public LayerPinkGel(RenderPinkSlime slimeRenderer) {
        super(slimeRenderer);
        this.slimeRenderer = slimeRenderer;
    }

    @Override
    public void func_212842_a_(EntityPinkSlime entityPinkSlime, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!entityPinkSlime.isInvisible()) {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableNormalize();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            //this.slimeModel.setModelAttributes(this.slimeRenderer.getMainModel()); TODO
            if (entityPinkSlime.hasCustomName() && NAMES.contains(entityPinkSlime.getDisplayName().getUnformattedComponentText().toLowerCase())) {
                float speed = 360 * 0.2f;
                int hsb = (int) (entityPinkSlime.world.getGameTime() % speed);
                Color color = Color.getHSBColor(hsb / speed, 0.75f, 0.75f);
                GlStateManager.color4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1.0f);
            }
            this.slimeModel.render(entityPinkSlime, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.disableBlend();
            GlStateManager.disableNormalize();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}

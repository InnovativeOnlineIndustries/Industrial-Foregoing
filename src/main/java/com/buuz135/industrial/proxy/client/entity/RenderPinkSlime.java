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
import com.buuz135.industrial.utils.Reference;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

@SideOnly(Side.CLIENT)
public class RenderPinkSlime extends RenderLiving<EntityPinkSlime> {

    public static final List<String> NAMES = Arrays.asList("buuz135", "the_codedone");
    private static final ResourceLocation PINK_SLIME_TEXTURES = new ResourceLocation(Reference.MOD_ID, "textures/entity/pink_slime.png");
    private static final ResourceLocation PINK_SLIME_TEXTURES_RGB = new ResourceLocation(Reference.MOD_ID, "textures/entity/pink_slime_white.png");

    public RenderPinkSlime(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelSlime(16), 0.25f);
        this.addLayer(new LayerPinkGel(this));
    }

    public void doRender(EntityPinkSlime entity, double x, double y, double z, float entityYaw, float partialTicks) {
        this.shadowSize = 0.25F * (float) entity.getSlimeSize();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    protected void preRenderCallback(EntityPinkSlime entitylivingbaseIn, float partialTickTime) {
        float f = 0.999F;
        GlStateManager.scale(0.999F, 0.999F, 0.999F);
        float f1 = (float) entitylivingbaseIn.getSlimeSize();
        float f2 = (entitylivingbaseIn.prevSquishFactor + (entitylivingbaseIn.squishFactor - entitylivingbaseIn.prevSquishFactor) * partialTickTime) / (f1 * 0.5F + 1.0F);
        float f3 = 1.0F / (f2 + 1.0F);
        GlStateManager.scale(f3 * f1, 1.0F / f3 * f1, f3 * f1);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityPinkSlime entity) {
        return entity.hasCustomName() && NAMES.contains(entity.getDisplayName().getUnformattedText().toLowerCase()) ? PINK_SLIME_TEXTURES_RGB : PINK_SLIME_TEXTURES;
    }
}

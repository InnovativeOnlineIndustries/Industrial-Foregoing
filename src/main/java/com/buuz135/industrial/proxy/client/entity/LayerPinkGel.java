package com.buuz135.industrial.proxy.client.entity;

import com.buuz135.industrial.entity.EntityPinkSlime;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

import java.awt.*;

import static com.buuz135.industrial.proxy.client.entity.RenderPinkSlime.NAMES;

public class LayerPinkGel implements LayerRenderer<EntityPinkSlime> {

    private final RenderPinkSlime slimeRenderer;
    private final ModelBase slimeModel = new ModelSlime(0);

    public LayerPinkGel(RenderPinkSlime slimeRenderer) {
        this.slimeRenderer = slimeRenderer;
    }

    @Override
    public void doRenderLayer(EntityPinkSlime entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!entitylivingbaseIn.isInvisible()) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableNormalize();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.slimeModel.setModelAttributes(this.slimeRenderer.getMainModel());
            if (entitylivingbaseIn.hasCustomName() && NAMES.contains(entitylivingbaseIn.getDisplayName().getUnformattedText().toLowerCase())) {
                float speed = 300 * 0.2f;
                int hsb = (int) (entitylivingbaseIn.world.getTotalWorldTime() % speed);
                Color color = Color.getHSBColor(hsb / speed, 0.75f, 0.75f);
                GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1.0f);
            }
            this.slimeModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.disableBlend();
            GlStateManager.disableNormalize();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}

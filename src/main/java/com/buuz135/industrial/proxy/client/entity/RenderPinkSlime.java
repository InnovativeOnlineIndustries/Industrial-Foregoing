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

@SideOnly(Side.CLIENT)
public class RenderPinkSlime extends RenderLiving<EntityPinkSlime> {

    private static final ResourceLocation PINK_SLIME_TEXTURES = new ResourceLocation(Reference.MOD_ID, "textures/entity/pink_slime.png");

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
        return PINK_SLIME_TEXTURES;
    }
}

package com.buuz135.industrial.proxy.client.event;

import com.buuz135.industrial.api.conveyor.ConveyorUpgradeFactory;
import com.buuz135.industrial.proxy.block.Cuboid;
import com.buuz135.industrial.proxy.block.DistanceRayTraceResult;
import com.buuz135.industrial.proxy.client.model.ConveyorBlockModel;
import com.buuz135.industrial.registry.IFRegistries;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class IFClientEvents {

    @SubscribeEvent
    public void textureStich(TextureStitchEvent.Pre pre) {
        pre.getMap().registerSprite(new ResourceLocation(Reference.MOD_ID, "blocks/catears"));
        for (ConveyorUpgradeFactory factory : IFRegistries.CONVEYOR_UPGRADE_REGISTRY.getValuesCollection()) {
            factory.getTextures().forEach(pre.getMap()::registerSprite);
        }
    }

    @SubscribeEvent
    public void modelBake(ModelBakeEvent event) {
        for (ModelResourceLocation resourceLocation : event.getModelRegistry().getKeys()) {
            if (resourceLocation.getResourceDomain().equals(Reference.MOD_ID)) {
                if (resourceLocation.getResourcePath().contains("conveyor"))
                    event.getModelRegistry().putObject(resourceLocation, new ConveyorBlockModel(event.getModelRegistry().getObject(resourceLocation)));
            }
        }
    }

    @SubscribeEvent
    public void blockOverlayEvent(DrawBlockHighlightEvent event) {
        RayTraceResult hit = event.getTarget();
        if (hit.typeOfHit == RayTraceResult.Type.BLOCK && hit instanceof DistanceRayTraceResult) {
            BlockPos pos = event.getTarget().getBlockPos();
            event.setCanceled(true);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.glLineWidth(2.0F);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);

            EntityPlayer player = event.getPlayer();
            double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
            double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
            double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();
            RenderGlobal.drawSelectionBoundingBox(((Cuboid) ((DistanceRayTraceResult) event.getTarget()).hitInfo).aabb().offset(-x, -y, -z).offset(pos).grow(0.002),
                    0.0F, 0.0F, 0.0F, 0.4F
            );

            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }
    }
}
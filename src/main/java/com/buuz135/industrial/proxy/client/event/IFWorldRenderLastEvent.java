package com.buuz135.industrial.proxy.client.event;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class IFWorldRenderLastEvent {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        EntityPlayerSP playerIn = Minecraft.getMinecraft().player;
        if (!playerIn.getHeldItemMainhand().getItem().equals(ItemRegistry.bookManualItem) || Minecraft.getMinecraft().currentScreen != null)
            return;
        float f = playerIn.rotationPitch;
        float f1 = playerIn.rotationYaw;
        double d0 = playerIn.posX;
        double d1 = playerIn.posY + (double) playerIn.getEyeHeight();
        double d2 = playerIn.posZ;
        Vec3d vec3d = new Vec3d(d0, d1, d2);
        float f2 = MathHelper.cos(-f1 * 0.017453292F - (float) Math.PI);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - (float) Math.PI);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d3 = 5.0D;
        Vec3d vec3d1 = vec3d.addVector((double) f6 * d3, (double) f5 * d3, (double) f7 * d3);
        RayTraceResult result = Minecraft.getMinecraft().world.rayTraceBlocks(vec3d, vec3d1, false, true, false);
        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = result.getBlockPos();
            if (Minecraft.getMinecraft().world.getBlockState(pos).getBlock().getRegistryName().getResourceDomain().equals(Reference.MOD_ID)) {
                Minecraft.getMinecraft().fontRenderer.drawString(TextFormatting.GOLD + "SNEAK" + TextFormatting.WHITE + "+" + TextFormatting.GOLD + "Right Click", event.getResolution().getScaledWidth() / 2 + 10, event.getResolution().getScaledHeight() / 2 - 5, 0xFFFFFF, true);
                Minecraft.getMinecraft().fontRenderer.drawString(TextFormatting.YELLOW + "Open Block Description", event.getResolution().getScaledWidth() / 2 + 10, event.getResolution().getScaledHeight() / 2 - 5 + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 2, 0xFFFFFF, true);

            }
        }
    }
}

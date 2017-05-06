package com.buuz135.industrial.proxy.client;

import com.buuz135.industrial.item.MobImprisonmentToolItem;
import com.buuz135.industrial.utils.ItemStackUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MobRenderInPrisonHandler {

    @SubscribeEvent
    public void onTooltip(RenderTooltipEvent.PostText event) {
        if (event.getStack().isEmpty()) return;
        if (event.getStack().getItem() instanceof MobImprisonmentToolItem && ((MobImprisonmentToolItem) event.getStack().getItem()).containsEntity(event.getStack())) {
            try {
                Entity entity = EntityList.createEntityByID(event.getStack().getTagCompound().getInteger("id"), Minecraft.getMinecraft().world);
                entity.readFromNBT(event.getStack().getTagCompound());
                ItemStackUtils.renderEntity((int) (event.getX() + 15 + entity.width), (int) (event.getY() + 58 + entity.height), 15, 0, 0, (EntityLivingBase) entity);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

    }
}

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
package com.buuz135.industrial.proxy.client.render;

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
        if (event.getStack() == null || event.getStack().isEmpty()) return;
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

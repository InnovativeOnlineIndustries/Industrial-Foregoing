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
package com.buuz135.industrial.plugin.curios;

import com.buuz135.industrial.item.infinity.item.ItemInfinityBackpack;
import com.buuz135.industrial.module.ModuleTool;
import com.buuz135.industrial.plugin.CuriosPlugin;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.client.ICurioRenderer;
import top.theillusivec4.curios.api.type.capability.ICurio;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class InfinityBackpackCurios implements ICurio, ICurioRenderer {

    @Override
    public boolean canEquip(String identifier, LivingEntity livingEntity) {
        return identifier.equals(SlotTypePreset.BACK.getIdentifier());
    }

    @Override
    public ItemStack getStack() {
        return null;
    }

    @Override
    public void curioTick(SlotContext slotContext) {
        LivingEntity livingEntity = slotContext.entity();
        ItemStack stack = CuriosPlugin.getStack(livingEntity, SlotTypePreset.BACK, 0);
        if (stack.getItem() instanceof ItemInfinityBackpack){
            ModuleTool.INFINITY_BACKPACK.inventoryTick(stack, livingEntity.level, livingEntity, 0, false);
        }
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        LivingEntity entity = slotContext.entity();
        ItemStack itemStack = CuriosPlugin.getStack(entity, SlotTypePreset.BACK, 0);
        if (itemStack.getItem() instanceof ItemInfinityBackpack) {
            matrixStack.pushPose();
            if (entity.isCrouching()) {
                matrixStack.translate(0D, 0.2D, 0D);
                matrixStack.mulPose(Vector3f.XP.rotationDegrees((float) (90F / Math.PI)));
            }
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(180));
            //matrixStack.translate(0,0.25,0.265);
            matrixStack.translate(0, -0.4, -0.2);
            matrixStack.scale(0.65f, 0.65f, 0.65f);

            Minecraft.getInstance().getItemRenderer().render(itemStack, ItemTransforms.TransformType.FIXED, true, matrixStack, renderTypeBuffer, light, OverlayTexture.NO_OVERLAY, (BakedModel) renderLayerParent.getModel());
            matrixStack.popPose();
        }
    }
}

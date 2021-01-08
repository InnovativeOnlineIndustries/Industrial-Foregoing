package com.buuz135.industrial.plugin.curios;

import com.buuz135.industrial.item.MeatFeederItem;
import com.buuz135.industrial.module.ModuleTool;
import com.buuz135.industrial.plugin.CuriosPlugin;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class MeatFeedCurios implements ICurio {

    @Override
    public boolean canEquip(String identifier, LivingEntity livingEntity) {
        return identifier.equals(SlotTypePreset.HEAD.getIdentifier());
    }

    @Override
    public boolean canRender(String identifier, int index, LivingEntity livingEntity) {
        return true;
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity) {
        ItemStack stack = CuriosPlugin.getStack(livingEntity, SlotTypePreset.HEAD, 0);
        if (stack.getItem() instanceof MeatFeederItem){
            ModuleTool.MEAT_FEEDER.inventoryTick(stack, livingEntity.world, livingEntity, 0, false);
        }
    }

    @Override
    public void render(String identifier, int index, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack stack = CuriosPlugin.getStack(livingEntity, SlotTypePreset.HEAD, 0);
        if (stack.getItem() instanceof MeatFeederItem){
            matrixStack.push();
            matrixStack.rotate(Vector3f.YP.rotationDegrees(90));
            matrixStack.rotate(Vector3f.XP.rotationDegrees(180));
            matrixStack.rotate(Vector3f.YN.rotationDegrees(netHeadYaw));
            matrixStack.rotate(Vector3f.ZN.rotationDegrees(headPitch));
            matrixStack.translate(0,0.25,0.265);
            matrixStack.scale(0.5f, 0.5f, 0.5f);
            Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.NONE, light, OverlayTexture.NO_OVERLAY, matrixStack, renderTypeBuffer);
            matrixStack.pop();
        }

    }

}

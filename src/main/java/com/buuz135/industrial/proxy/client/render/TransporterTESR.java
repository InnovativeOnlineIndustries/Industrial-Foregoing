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
package com.buuz135.industrial.proxy.client.render;

import com.buuz135.industrial.api.transporter.TransporterType;
import com.buuz135.industrial.api.transporter.TransporterTypeFactory;
import com.buuz135.industrial.block.transportstorage.tile.TransporterTile;
import com.buuz135.industrial.block.transportstorage.transporter.TransporterItemType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Vector3f;

import java.util.Map;

public class TransporterTESR implements BlockEntityRenderer<TransporterTile> {


//    public static RenderType TYPE = createRenderType();

//    public TransporterTESR(BlockEntityRenderDispatcher rendererDispatcherIn) {
//        super(rendererDispatcherIn);
//    }

//    public static RenderType createRenderType() {
//        RenderType.CompositeState state = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation("industrialforegoing", "textures/blocks/transporters/particle.png"), false, false)).setTransparencyState(new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
//            RenderSystem.depthMask(true);
//            RenderSystem.enableBlend();
//            RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
//        }, () -> {
//            RenderSystem.disableBlend();
//            RenderSystem.defaultBlendFunc();
//        })).createCompositeState(true);
//        return RenderType.create("transporter_render", DefaultVertexFormat.POSITION_TEX_COLOR, 7, 262144, false, true, state);
//    }

    public static Vector3f getPath(Direction from, Direction to, double step) {
        float totalSteps = 6.15f;
        if (from.getOpposite() == to) {
            totalSteps = 9f;
            Vec3 vector3d = new Vec3(to.step().x() / totalSteps * step, to.step().y() / totalSteps * step, to.step().z() / totalSteps * step);
            if (from.getAxis() == Direction.Axis.X) {
                vector3d = vector3d.add(0, 0.5, 0.5);
                if (from.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                    vector3d = vector3d.add(1, 0, 0);
                }
            }
            if (from.getAxis() == Direction.Axis.Z) {
                vector3d = vector3d.add(0.5, 0.5, 0);
                if (from.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                    vector3d = vector3d.add(0, 0, 1);
                }
            }
            if (from.getAxis() == Direction.Axis.Y) {
                vector3d = vector3d.add(0.5, 0, 0.5);
                if (from.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                    vector3d = vector3d.add(0, 1, 0);
                }
            }
            float divideSecond = -0.22f;
            vector3d = vector3d.add(from.step().x() * divideSecond, from.step().y() * divideSecond, from.step().z() * divideSecond);
            return new Vector3f(vector3d);
        }
        Vector3f vsrc = from.step();
        Vector3f vdst = to.step();
        Vector3f a = vsrc.copy();
        a.mul(5 / 16f);
        Vector3f b = vdst.copy();
        b.mul(6 / 16f);
        Vector3f c = vsrc.copy();
        c.mul(3 / 16f);
        float sind = -Mth.sin((float) ((step / totalSteps) * Math.PI / 2f));
        float cosd = -Mth.cos((float) ((step / totalSteps) * Math.PI / 2f));
        a.mul(sind);
        b.mul(cosd);
        a.add(b);
        a.add(c);
        float divide = 2.5f;
        a.add(vdst.x() / divide, vdst.y() / divide, vdst.z() / divide);
        a.add(0.5f, 0.5f, 0.5f);
        float divideSecond = 0.15f;
        a.add(vsrc.x() * divideSecond, vsrc.y() * divideSecond, vsrc.z() * divideSecond);
        return a;
    }

    @Override
    public void render(TransporterTile tile, float partialTicks, PoseStack stack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        Map<Direction, TransporterType> transporters = tile.getTransporterTypeMap();
        for (Direction direction : transporters.keySet()) {
            if (transporters.get(direction).getAction() == TransporterTypeFactory.TransporterAction.EXTRACT) {
                for (Direction other : transporters.keySet()) {
                    if (direction == other || !transporters.get(direction).getFactory().getRegistryName().equals(transporters.get(other).getFactory().getRegistryName()) || transporters.get(other).getAction() == TransporterTypeFactory.TransporterAction.EXTRACT)
                        continue;
                    for (int i = -1; i < TransporterItemType.QUEUE_SIZE; ++i) {
                        stack.pushPose();
                        Vector3f pos = getPath(direction, other, i + (tile.getLevel().getGameTime() % 2) + (tile.getLevel().getGameTime() % 3) / 3D);
                        stack.translate(pos.x(), pos.y(), pos.z());
                        transporters.get(other).renderTransfer(pos, direction, i + 1, stack, combinedOverlayIn, buffer);
                        stack.popPose();
                    }
                }
            }
        }
    }
}

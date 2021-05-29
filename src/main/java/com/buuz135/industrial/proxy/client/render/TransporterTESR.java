package com.buuz135.industrial.proxy.client.render;

import com.buuz135.industrial.api.transporter.TransporterType;
import com.buuz135.industrial.api.transporter.TransporterTypeFactory;
import com.buuz135.industrial.block.transportstorage.tile.TransporterTile;
import com.buuz135.industrial.block.transportstorage.transporter.TransporterItemType;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.Map;

public class TransporterTESR extends TileEntityRenderer<TransporterTile> {


    public static RenderType TYPE = createRenderType();

    public TransporterTESR(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    public static RenderType createRenderType() {
        RenderType.State state = RenderType.State.getBuilder().texture(new RenderState.TextureState(new ResourceLocation("industrialforegoing", "textures/blocks/transporters/particle.png"), false, false)).transparency(new RenderState.TransparencyState("translucent_transparency", () -> {
            RenderSystem.depthMask(true);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        }, () -> {
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
        })).build(true);
        return RenderType.makeType("transporter_render", DefaultVertexFormats.POSITION_TEX_COLOR, 7, 262144, false, true, state);
    }

    public static Vector3f getPath(Direction from, Direction to, double step) {
        float totalSteps = 6.15f;
        if (from.getOpposite() == to) {
            totalSteps = 9f;
            Vector3d vector3d = new Vector3d(to.toVector3f().getX() / totalSteps * step, to.toVector3f().getY() / totalSteps * step, to.toVector3f().getZ() / totalSteps * step);
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
            vector3d = vector3d.add(from.toVector3f().getX() * divideSecond, from.toVector3f().getY() * divideSecond, from.toVector3f().getZ() * divideSecond);
            return new Vector3f(vector3d);
        }
        Vector3f vsrc = from.toVector3f();
        Vector3f vdst = to.toVector3f();
        Vector3f a = vsrc.copy();
        a.mul(5 / 16f);
        Vector3f b = vdst.copy();
        b.mul(6 / 16f);
        Vector3f c = vsrc.copy();
        c.mul(3 / 16f);
        float sind = -MathHelper.sin((float) ((step / totalSteps) * Math.PI / 2f));
        float cosd = -MathHelper.cos((float) ((step / totalSteps) * Math.PI / 2f));
        a.mul(sind);
        b.mul(cosd);
        a.add(b);
        a.add(c);
        float divide = 2.5f;
        a.add(vdst.getX() / divide, vdst.getY() / divide, vdst.getZ() / divide);
        a.add(0.5f, 0.5f, 0.5f);
        float divideSecond = 0.15f;
        a.add(vsrc.getX() * divideSecond, vsrc.getY() * divideSecond, vsrc.getZ() * divideSecond);
        return a;
    }

    @Override
    public void render(TransporterTile tile, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        Map<Direction, TransporterType> transporters = tile.getTransporterTypeMap();
        for (Direction direction : transporters.keySet()) {
            if (transporters.get(direction).getAction() == TransporterTypeFactory.TransporterAction.EXTRACT) {
                for (Direction other : transporters.keySet()) {
                    if (direction == other || !transporters.get(direction).getFactory().getRegistryName().equals(transporters.get(other).getFactory().getRegistryName()) || transporters.get(other).getAction() == TransporterTypeFactory.TransporterAction.EXTRACT)
                        continue;
                    for (int i = -1; i < TransporterItemType.QUEUE_SIZE; ++i) {
                        stack.push();
                        Vector3f pos = getPath(direction, other, i + (tile.getWorld().getGameTime() % 2) + (tile.getWorld().getGameTime() % 3) / 3D);
                        stack.translate(pos.getX(), pos.getY(), pos.getZ());
                        transporters.get(other).renderTransfer(pos, direction, i + 1, stack, combinedOverlayIn, buffer);
                        stack.pop();
                    }
                }
            }
        }
    }
}

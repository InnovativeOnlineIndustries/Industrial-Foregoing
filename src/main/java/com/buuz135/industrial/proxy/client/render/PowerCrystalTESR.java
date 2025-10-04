package com.buuz135.industrial.proxy.client.render;

import com.buuz135.industrial.block.transportstorage.tile.PowerCrystalTile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PowerCrystalTESR implements BlockEntityRenderer<PowerCrystalTile> {

    private static final List<ResourceLocation> CRYSTAL_TEXTURES = new ArrayList<>();
    private static final int COLOR_WHITE = Color.WHITE.getRGB();
    private static final int COLOR_R = FastColor.ARGB32.red(COLOR_WHITE);
    private static final int COLOR_G = FastColor.ARGB32.green(COLOR_WHITE);
    private static final int COLOR_B = FastColor.ARGB32.blue(COLOR_WHITE);
    private static final int COLOR_A = 230; // Slightly transparent
    private static final double MAX_RENDER_DISTANCE_SQR = 40 * 40; // cull far connections
    private static final int MAX_SEGMENTS_PER_CONNECTION = 24; // hard cap on quads per connection
    private static final HashMap<ResourceLocation, RenderType> RENDER_TYPES = new HashMap<>();
    private static float TEXTURE_SIZE = 0.15f;// Half a block size

    static {
        // Initialize the list of textures
        CRYSTAL_TEXTURES.add(ResourceLocation.fromNamespaceAndPath("industrialforegoing", "textures/gui/power_crystal/particle_1.png"));
        CRYSTAL_TEXTURES.add(ResourceLocation.fromNamespaceAndPath("industrialforegoing", "textures/gui/power_crystal/particle_2.png"));
        CRYSTAL_TEXTURES.add(ResourceLocation.fromNamespaceAndPath("industrialforegoing", "textures/gui/power_crystal/particle_3.png"));
        //CRYSTAL_TEXTURES.add(ResourceLocation.fromNamespaceAndPath("industrialforegoing", "textures/gui/power_crystal/particle_4.png"));
        //CRYSTAL_TEXTURES.add(ResourceLocation.fromNamespaceAndPath("industrialforegoing", "textures/gui/power_crystal/particle_5.png"));
        CRYSTAL_TEXTURES.add(ResourceLocation.fromNamespaceAndPath("industrialforegoing", "textures/gui/power_crystal/particle_6.png"));
        //CRYSTAL_TEXTURES.add(ResourceLocation.fromNamespaceAndPath("industrialforegoing", "textures/gui/power_crystal/particle_1.png"));
        //CRYSTAL_TEXTURES.add(ResourceLocation.fromNamespaceAndPath("industrialforegoing", "textures/gui/power_crystal/particle_2.png"));
        //CRYSTAL_TEXTURES.add(ResourceLocation.fromNamespaceAndPath("industrialforegoing", "textures/gui/power_crystal/particle_3.png"));
    }

    public PowerCrystalTESR(BlockEntityRendererProvider.Context power) {
    }

    public static RenderType createRenderType(ResourceLocation texture) {
        return RENDER_TYPES.computeIfAbsent(texture, resourceLocation -> {
            RenderType.CompositeState state = RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionTexColorShader))
                    .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                    .setTransparencyState(new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
                        RenderSystem.enableBlend();
                        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        RenderSystem.disableCull();
                    }, () -> {
                        RenderSystem.disableBlend();
                    })).createCompositeState(true);
            return RenderType.create("power_crystal", DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 32, false, true, state);
        });

    }

    @Override
    public void render(PowerCrystalTile powerCrystalTile, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        Vec3 startPos = powerCrystalTile.getStartPosition();
        var stepSize = 0.33d; //TODO CONFIG
        var totalConnections = Math.max(1, powerCrystalTile.getConnections().size() / 16);
        var maxSegmentCount = Math.max(6, MAX_SEGMENTS_PER_CONNECTION / totalConnections);
        // Render only neighbors that are on shortest paths to this crystal's connection targets
        for (BlockPos pos : powerCrystalTile.getDirectConnections()) {
            Vec3 endPos = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

            double distance = startPos.distanceTo(endPos);
            if (distance <= 0.0001) continue;

            int desiredSegments = (int) Math.ceil(distance / stepSize);
            int textureCount = desiredSegments;
            if (textureCount > maxSegmentCount) textureCount = maxSegmentCount; //TODO CONFIG

            // Determine if we should fade only the tail (degraded) as we approach the cap
            float fractionOfMax = Mth.clamp((float) desiredSegments / (float) maxSegmentCount, 0.0f, 1.0f);
            boolean doFade = fractionOfMax >= 0.65f;
            int fadeStartIndex = (int) Math.floor(maxSegmentCount * 0.65f);
            // How strong the fade should be overall (0 just above 75%, 1 at the cap or beyond)
            float fadeStrength = doFade ? Mth.clamp((fractionOfMax - 0.65f) / 0.25f, 0.0f, 1.0f) : 0.0f;

            for (double i = 1; i < textureCount; i += 1) {
                double progress = i * stepSize / distance;
                Vec3 position = startPos.lerp(endPos, progress);

                int textureIndex = (int) (powerCrystalTile.getBlockPos().getX() * 7.5
                        + powerCrystalTile.getBlockPos().getY() * -8
                        + powerCrystalTile.getBlockPos().getZ() * 22
                        + position.x * 3.3 + position.y * 7 + position.z * 41) % CRYSTAL_TEXTURES.size();
                if (textureIndex < 0) textureIndex += CRYSTAL_TEXTURES.size();
                ResourceLocation texture = CRYSTAL_TEXTURES.get(textureIndex);
                VertexConsumer buffer = bufferSource.getBuffer(createRenderType(texture));

                // Compute per-segment alpha: keep head at full alpha, fade only the tail near the cap
                int idx = (int) i;
                int segAlpha;
                if (doFade && idx >= fadeStartIndex) {
                    int denom = Math.max(1, textureCount - fadeStartIndex - 1);
                    float tTail = Mth.clamp((float) (idx - fadeStartIndex) / (float) denom, 0.0f, 1.0f);
                    float tailFade = tTail * fadeStrength; // scale by closeness to the cap
                    float alphaScale = Mth.lerp(tailFade, 1.0f, 0.35f);
                    segAlpha = Mth.clamp((int) (COLOR_A * alphaScale), 0, 255);
                } else {
                    segAlpha = COLOR_A;
                }

                renderTextureAtPosition(position, buffer, poseStack, powerCrystalTile, endPos, segAlpha);
            }
        }
    }

    private void renderTextureAtPosition(Vec3 position, VertexConsumer buffer, PoseStack poseStack, PowerCrystalTile tile, Vec3 endPos, int alpha) {
        poseStack.pushPose();

        float time = (float) ((tile.getLevel().getGameTime()
                + tile.getBlockPos().getX() * 27 + tile.getBlockPos().getY() * 27 + tile.getBlockPos().getZ() * 27
                + position.x * 15 + position.y * 30 + position.z * 24) / 20.0f);
        var distance = tile.getBlockPos().distToCenterSqr(position);
        var distanceEnd = endPos.distanceToSqr(position);
        var multiplier = 1D;
        if (distance <= Math.sqrt(0.80)) {
            multiplier = distance / 0.99D;
        } else if (distanceEnd <= Math.sqrt(0.80)) {
            multiplier = distanceEnd / 0.99D;
        }

        // Move to the position
        poseStack.translate(
                position.x - tile.getBlockPos().getX() + (tile.getBlockPos().getX() % 2 == 0 ? -Mth.sin(time) : Mth.sin(time)) * 0.03f * multiplier,
                position.y - tile.getBlockPos().getY() + (tile.getBlockPos().getY() % 2 == 0 ? -Mth.cos(time) : Mth.cos(time)) * 0.05f * multiplier,
                position.z - tile.getBlockPos().getZ() + (tile.getBlockPos().getZ() % 2 == 0 ? -Mth.cos(time) : Mth.cos(time)) * 0.04f * multiplier
        );

        // Make the texture face the camera
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());

        float scale = TEXTURE_SIZE / 4f;
        poseStack.scale(scale, scale, scale);

        Matrix4f matrix = poseStack.last().pose();

        // Front face quad
        buffer.addVertex(matrix, -0.5f, -0.5f, 0).setUv(0, 0).setColor(COLOR_R, COLOR_G, COLOR_B, alpha);
        buffer.addVertex(matrix, 0.5f, -0.5f, 0).setUv(1, 0).setColor(COLOR_R, COLOR_G, COLOR_B, alpha);
        buffer.addVertex(matrix, 0.5f, 0.5f, 0).setUv(1, 1).setColor(COLOR_R, COLOR_G, COLOR_B, alpha);
        buffer.addVertex(matrix, -0.5f, 0.5f, 0).setUv(0, 1).setColor(COLOR_R, COLOR_G, COLOR_B, alpha);

        poseStack.popPose();
    }

    @Override
    public AABB getRenderBoundingBox(PowerCrystalTile blockEntity) {
        return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity).inflate(4);
    }
}

package com.buuz135.industrial.proxy.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.MatrixUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.client.renderer.entity.ItemRenderer.*;

public abstract class IndustrialForegoingISTER extends BlockEntityWithoutLevelRenderer {

    public IndustrialForegoingISTER() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    private static boolean isLeftHand(ItemDisplayContext type) {
        return type == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || type == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
    }

    protected EntityModelSet getEntityModels() {
        //Just have this method as a helper for what we pass as entity models rather than bothering to
        // use an AT to access it directly
        return Minecraft.getInstance().getEntityModels();
    }

    protected BlockEntityRenderDispatcher getBlockEntityRenderDispatcher() {
        //Just have this method as a helper for what we pass as the block entity render dispatcher
        // rather than bothering to use an AT to access it directly
        return Minecraft.getInstance().getBlockEntityRenderDispatcher();
    }

    protected Camera getCamera() {
        return getBlockEntityRenderDispatcher().camera;
    }

    @Override
    public abstract void onResourceManagerReload(@NotNull ResourceManager resourceManager);

    public abstract void renderByItem(HolderLookup.Provider access, @NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer,
                                      int light, int overlayLight);

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        renderByItem(Minecraft.getInstance().level.registryAccess(), stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
    }

    /**
     * @implNote Heavily based on/from vanilla's ItemRenderer#render code that calls the renderByItem method on the ISBER
     */
    protected void renderBlockItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer,
                                   int light, int overlayLight, ModelData modelData) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return;
        }
        Block block = blockItem.getBlock();
        boolean fabulous;
        if (displayContext != ItemDisplayContext.GUI && !displayContext.firstPerson()) {
            fabulous = !(block instanceof HalfTransparentBlock) && !(block instanceof StainedGlassPaneBlock);
        } else {
            fabulous = true;
        }
        //ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();

        // Pop off the transformations applied by ItemRenderer before calling this
        matrix.popPose();
        matrix.pushPose();

        Minecraft minecraft = Minecraft.getInstance();
        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        BlockState defaultState = block.defaultBlockState();

        BakedModel mainModel = minecraft.getModelManager().getBlockModelShaper().getBlockModel(defaultState);
        mainModel = mainModel.applyTransform(displayContext, matrix, isLeftHand(displayContext));
        matrix.translate(-.5, -.5, -.5); // Replicate ItemRenderer's translation
        long seed = 42;
        RandomSource random = RandomSource.create();
        boolean glint = stack.hasFoil();
        for (BakedModel model : mainModel.getRenderPasses(stack, fabulous)) {
            for (RenderType renderType : model.getRenderTypes(stack, fabulous)) {
                VertexConsumer consumer = null;
                if (fabulous) {
                    consumer = getFoilBufferDirect(renderer, renderType, true, glint);
                } else {
                    consumer = getFoilBuffer(renderer, renderType, true, glint);
                }
                for (Direction direction : Direction.values()) {
                    random.setSeed(seed);
                    itemRenderer.renderQuadList(matrix, consumer, model.getQuads(defaultState, direction, random, modelData, null), stack, light, overlayLight);
                }
                random.setSeed(seed);
                itemRenderer.renderQuadList(matrix, consumer, model.getQuads(defaultState, null, random, modelData, null), stack, light, overlayLight);
            }
        }
    }

    public void renderItem(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel p_model) {
        if (!itemStack.isEmpty()) {
            poseStack.pushPose();
            boolean flag = displayContext == ItemDisplayContext.GUI || displayContext == ItemDisplayContext.GROUND || displayContext == ItemDisplayContext.FIXED;

            p_model = net.neoforged.neoforge.client.ClientHooks.handleCameraTransforms(poseStack, p_model, displayContext, leftHand);
            poseStack.translate(-0.5F, -0.5F, -0.5F);
            if (flag || true) {
                boolean flag1;
                if (displayContext != ItemDisplayContext.GUI && !displayContext.firstPerson() && itemStack.getItem() instanceof BlockItem blockitem) {
                    Block block = blockitem.getBlock();
                    flag1 = !(block instanceof HalfTransparentBlock) && !(block instanceof StainedGlassPaneBlock);
                } else {
                    flag1 = true;
                }

                for (var model : p_model.getRenderPasses(itemStack, flag1)) {
                    for (var rendertype : model.getRenderTypes(itemStack, flag1)) {
                        VertexConsumer vertexconsumer;
                        if (itemStack.hasFoil()) {
                            PoseStack.Pose posestack$pose = poseStack.last().copy();
                            if (displayContext == ItemDisplayContext.GUI) {
                                MatrixUtil.mulComponentWise(posestack$pose.pose(), 0.5F);
                            } else if (displayContext.firstPerson()) {
                                MatrixUtil.mulComponentWise(posestack$pose.pose(), 0.75F);
                            }

                            vertexconsumer = getCompassFoilBuffer(bufferSource, rendertype, posestack$pose);
                        } else if (flag1) {
                            vertexconsumer = getFoilBufferDirect(bufferSource, rendertype, true, itemStack.hasFoil());
                        } else {
                            vertexconsumer = getFoilBuffer(bufferSource, rendertype, true, itemStack.hasFoil());
                        }

                        Minecraft.getInstance().getItemRenderer().renderModelLists(model, itemStack, combinedLight, combinedOverlay, poseStack, vertexconsumer);
                    }
                }
            }

            poseStack.popPose();
        }
    }
}

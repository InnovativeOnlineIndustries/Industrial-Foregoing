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
package com.buuz135.industrial.block.transportstorage.transporter;

import com.buuz135.industrial.api.IBlockContainer;
import com.buuz135.industrial.api.transporter.FilteredTransporterType;
import com.buuz135.industrial.api.transporter.TransporterType;
import com.buuz135.industrial.api.transporter.TransporterTypeFactory;
import com.buuz135.industrial.block.transportstorage.tile.TransporterTile;
import com.buuz135.industrial.block.transportstorage.transporter.filter.FluidFilter;
import com.buuz135.industrial.proxy.block.filter.RegulatorFilter;
import com.buuz135.industrial.proxy.client.render.TransporterTESR;
import com.buuz135.industrial.utils.FluidUtils;
import com.buuz135.industrial.utils.IndustrialTags;
import com.buuz135.industrial.utils.Reference;
import com.google.common.collect.Sets;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class TransporterFluidType extends FilteredTransporterType<FluidStack, IFluidHandler> {

    public static final int QUEUE_SIZE = 6;

    private final HashMap<Direction, List<FluidStack>> queue;

    public TransporterFluidType(IBlockContainer<?> container, TransporterTypeFactory factory, Direction side, TransporterTypeFactory.TransporterAction action) {
        super(container, factory, side, action);
        this.queue = new HashMap<>();
        for (Direction value : Direction.values()) {
            while (this.queue.computeIfAbsent(value, direction -> new ArrayList<>()).size() < QUEUE_SIZE)
                this.queue.get(value).addFirst(FluidStack.EMPTY);

            if (this.queue.size() > QUEUE_SIZE)
                this.queue.get(value).removeLast();
        }
    }

    @Override
    public RegulatorFilter<FluidStack, IFluidHandler> createFilter() {
        // Max 8192 Buckets - 8 192 000 mb.
        return new FluidFilter(20, 20, 5, 3, 50, 200, 8192000, "mb");
    }

    @Override
    public void update() {
        super.update();
        float speed = getSpeed();

        if (getLevel().isClientSide || getLevel().getGameTime() % (Math.max(1, 4 - speed)) != 0) return;

        IBlockContainer<?> container = getContainer();
        if (getAction() != TransporterTypeFactory.TransporterAction.EXTRACT || !(container instanceof TransporterTile transporterTile)) return;

        for (Direction direction : transporterTile.getTransporterTypeMap().keySet()) {
            TransporterType transporterType = transporterTile.getTransporterTypeMap().get(direction);
            if (transporterType instanceof TransporterFluidType fluidTransporter && transporterType.getAction() == TransporterTypeFactory.TransporterAction.INSERT) {
                var origin = getLevel().getCapability(Capabilities.FluidHandler.BLOCK, getPos().relative(this.getSide()), getSide().getOpposite());
                if (origin == null) continue;

                var destination = getLevel().getCapability(Capabilities.FluidHandler.BLOCK, getPos().relative(direction), direction.getOpposite());
                if (destination == null) continue;

                int possibleAmount = (int) Math.ceil(1000 * getEfficiency());
                FluidStack simulatedStack = origin.drain(possibleAmount, IFluidHandler.FluidAction.SIMULATE);
                int filteredAmount = Math.min(
                    this.getFilter().getExtractAmount(simulatedStack, origin),
                    fluidTransporter.getFilter().getInsertAmount(simulatedStack, destination)
                );

                int amount = Math.min(filteredAmount, possibleAmount);
                if (amount <= 0) continue;
                simulatedStack.setAmount(amount);

                int simulatedInserted = destination.fill(simulatedStack, IFluidHandler.FluidAction.SIMULATE);
                if (simulatedInserted > 0) {
                    destination.fill(origin.drain(simulatedInserted, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                    fluidTransporter.addTransferredStack(getSide(), simulatedStack);
                }
            }
        }
    }

    @Override
    public void updateClient() {
        super.updateClient();
        for (Direction value : Direction.values()) {
            while (this.queue.computeIfAbsent(value, direction -> new ArrayList<>()).size() < QUEUE_SIZE)
                this.queue.get(value).addFirst(FluidStack.EMPTY);

            this.queue.get(value).addFirst(FluidStack.EMPTY);

            while (this.queue.get(value).size() > QUEUE_SIZE)
                this.queue.get(value).removeLast();
        }
    }

    public void addTransferredStack(Direction direction, @NotNull FluidStack stack) {
        syncRender(direction, (CompoundTag) stack.saveOptional(getLevel().registryAccess()));
    }

    @Override
    public void handleRenderSync(Direction origin, CompoundTag compoundNBT) {
        this.queue.computeIfAbsent(origin, direction -> new ArrayList<>()).addFirst(FluidStack.parseOptional(getLevel().registryAccess(), compoundNBT));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderTransfer(Vector3f pos, Direction direction, int step, PoseStack stack, int combinedOverlayIn, MultiBufferSource buffer, float frame, Level level) {
        super.renderTransfer(pos, direction, step, stack, combinedOverlayIn, buffer, frame, level);
        if (step < queue.computeIfAbsent(direction, v -> new ArrayList<>()).size()) {
            float scale = 0.10f;
            stack.scale(scale, scale, scale);
            FluidStack fluidStack = queue.get(direction).get(step);
            stack.pushPose();
            stack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
            //stack.mulPose(Axis.ZP.rotationDegrees(90f));
            stack.mulPose(Axis.XP.rotationDegrees(-90f));
            VertexConsumer buffer1 = buffer.getBuffer(TransporterTESR.TYPE);
            Matrix4f matrix = stack.last().pose();
            float pX1 = 1;
            float u = 1;
            float pX2 = 0;
            float u2 = 0;
            float xOffset = -0.75f;
            float yOffset = -0f;
            float zOffset = -0.75f;
            int alpha = 1;
            float red = 0;
            float green = 0;
            float blue = 0;
            if (fluidStack.isEmpty()) {
                Color CLOSE = Color.CYAN;
                Color FAR = new Color(0x6800FF);
                double ratio = (step + 2.5) / (double) QUEUE_SIZE;
                red = (int) Math.abs((ratio * FAR.getRed()) + ((1 - ratio) * CLOSE.getRed())) / 256F;
                green = (int) Math.abs((ratio * FAR.getGreen()) + ((1 - ratio) * CLOSE.getGreen())) / 256F;
                blue = (int) Math.abs((ratio * FAR.getBlue()) + ((1 - ratio) * CLOSE.getBlue())) / 256F;
                stack.scale(0.25f, 0.25f, 0.25f);
            } else {
                Color color = new Color(FluidUtils.getFluidColor(fluidStack));
                red = color.getRed() / 256F;
                green = color.getGreen() / 256F;
                blue = color.getBlue() / 256F;
                stack.scale(0.75f, 0.75f, 0.75f);
            }
            buffer1.addVertex(matrix, pX2 + xOffset, yOffset, 0 + zOffset).setColor(red, green, blue, alpha).setUv(u2, 0);
            buffer1.addVertex(matrix, pX1 + xOffset + 0.5f, yOffset, 0 + zOffset).setColor(red, green, blue, alpha).setUv(u, 0);
            buffer1.addVertex(matrix, pX1 + xOffset + 0.5f, yOffset, 1.5f + zOffset).setColor(red, green, blue, alpha).setUv(u, 1);
            buffer1.addVertex(matrix, pX2 + xOffset, yOffset, 1.5f + zOffset).setColor(red, green, blue, alpha).setUv(u2, 1);
            stack.popPose();

        }
    }

    public static class Factory extends TransporterTypeFactory {

        public Factory() {
            super("fluid");
        }

        @Override
        public TransporterType create(IBlockContainer container, Direction face, TransporterAction action) {
            return new TransporterFluidType(container, this, face, action);
        }

        @Override
        @Nonnull
        public ResourceLocation getModel(Direction upgradeSide, TransporterAction action) {
            return ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/transporters/fluid_transporter_" + action.name().toLowerCase() + "_" + upgradeSide.getSerializedName().toLowerCase());
        }

        @Override
        public Set<ResourceLocation> getTextures() {
            return Sets.newHashSet(ResourceLocation.parse("industrialforegoing:block/transporters/fluid"), ResourceLocation.parse("industrialforegoing:block/base/bottom"));
        }

        @Override
        public boolean canBeAttachedAgainst(Level world, BlockPos pos, Direction face) {
            return world.getCapability(Capabilities.FluidHandler.BLOCK, pos, face) != null;
        }

        @Nonnull
        @Override
        public ResourceLocation getItemModel() {
            return ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/transporters/fluid_transporter_" + TransporterAction.EXTRACT.name().toLowerCase() + "_" + Direction.NORTH.getSerializedName().toLowerCase());
        }

        @Override
        public void registerRecipe(RecipeOutput consumer) {
            TitaniumShapedRecipeBuilder.shapedRecipe(getUpgradeItem(), 2)
                    .pattern("IPI").pattern("GMG").pattern("ICI")
                    .define('I', Tags.Items.DUSTS_REDSTONE)
                    .define('P', Items.ENDER_PEARL)
                    .define('G', Tags.Items.GEMS_LAPIS)
                    .define('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                    .define('C', Items.PISTON)
                    .save(consumer);
        }
    }
}

package com.buuz135.industrial.block.transportstorage.transporter;

import com.buuz135.industrial.api.IBlockContainer;
import com.buuz135.industrial.api.transporter.FilteredTransporterType;
import com.buuz135.industrial.api.transporter.TransporterType;
import com.buuz135.industrial.api.transporter.TransporterTypeFactory;
import com.buuz135.industrial.block.transportstorage.tile.TransporterTile;
import com.buuz135.industrial.proxy.block.filter.IFilter;
import com.buuz135.industrial.proxy.block.filter.RegulatorFilter;
import com.buuz135.industrial.proxy.client.render.TransporterTESR;
import com.buuz135.industrial.utils.FluidUtils;
import com.buuz135.industrial.utils.IndustrialTags;
import com.buuz135.industrial.utils.Reference;
import com.google.common.collect.Sets;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import com.hrznstudio.titanium.util.TileUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.buuz135.industrial.api.transporter.TransporterTypeFactory.TransporterAction;

public class TransporterFluidType extends FilteredTransporterType<FluidStack, IFluidHandler> {

    public static final int QUEUE_SIZE = 6;

    private HashMap<Direction, List<FluidStack>> queue;

    public TransporterFluidType(IBlockContainer container, TransporterTypeFactory factory, Direction side, TransporterTypeFactory.TransporterAction action) {
        super(container, factory, side, action);
        this.queue = new HashMap<>();
        for (Direction value : Direction.values()) {
            while (this.queue.computeIfAbsent(value, direction -> new ArrayList<>()).size() < QUEUE_SIZE) {
                this.queue.get(value).add(0, FluidStack.EMPTY);
            }
            if (this.queue.size() > QUEUE_SIZE) {
                this.queue.get(value).remove(this.queue.get(value).size() - 1);
            }
        }
    }

    @Override
    public RegulatorFilter<FluidStack, IFluidHandler> createFilter() {
        return new RegulatorFilter<FluidStack, IFluidHandler>(20, 20, 5, 3, 50, 250, 32000, "mb") {
            @Override
            public int matches(FluidStack stack, IFluidHandler iFluidHandler, boolean isRegulated) {
                int amount = 0;
                if (isEmpty()) return stack.getAmount();
                if (isRegulated) {
                    for (int i = 0; i < iFluidHandler.getTanks(); i++) {
                        if (iFluidHandler.isFluidValid(i, stack) && iFluidHandler.getFluidInTank(i).isFluidEqual(stack)) {
                            amount += iFluidHandler.getFluidInTank(i).getAmount();
                        }
                    }
                }
                for (IFilter.GhostSlot slot : this.getFilter()) {
                    FluidStack original = FluidUtil.getFluidContained(slot.getStack()).orElse(null);
                    if (original != null && original.isFluidEqual(stack)) {
                        int allowedAmount = isRegulated ? slot.getAmount() : Integer.MAX_VALUE;
                        int returnAmount = Math.min(stack.getAmount(), allowedAmount - amount);
                        if (returnAmount > 0) return returnAmount;
                    }
                }
                return 0;
            }
        };
    }

    @Override
    public void update() {
        super.update();
        float speed = getSpeed();
        if (!getWorld().isClientSide && getWorld().getGameTime() % (Math.max(1, 4 - speed)) == 0) {
            IBlockContainer container = getContainer();
            if (getAction() == TransporterTypeFactory.TransporterAction.EXTRACT && container instanceof TransporterTile) {
                for (Direction direction : ((TransporterTile) container).getTransporterTypeMap().keySet()) {
                    TransporterType transporterType = ((TransporterTile) container).getTransporterTypeMap().get(direction);
                    if (transporterType instanceof TransporterFluidType && transporterType.getAction() == TransporterTypeFactory.TransporterAction.INSERT) {
                        TileUtil.getTileEntity(getWorld(), getPos().relative(this.getSide())).ifPresent(tileEntity -> tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, getSide().getOpposite()).ifPresent(origin -> {
                            TileUtil.getTileEntity(getWorld(), getPos().relative(direction)).ifPresent(otherTile -> otherTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite()).ifPresent(destination -> {
                                int amount = (int) (50 * getEfficiency());
                                FluidStack simulatedStack = origin.drain(amount, IFluidHandler.FluidAction.SIMULATE);
                                int filteredAmount = ((TransporterFluidType) transporterType).getFilter().matches(simulatedStack, destination, ((TransporterFluidType) transporterType).isRegulated());
                                if (filter(this.getFilter(), isWhitelist(), simulatedStack, origin, false) && filteredAmount > 0 && filter(((TransporterFluidType) transporterType).getFilter(), ((TransporterFluidType) transporterType).isWhitelist(), simulatedStack, destination, ((TransporterFluidType) transporterType).isRegulated())) {
                                    int simulatedInserted = destination.fill(simulatedStack, IFluidHandler.FluidAction.SIMULATE);
                                    if (simulatedInserted > 0) {
                                        destination.fill(origin.drain(simulatedInserted, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                                        ((TransporterFluidType) transporterType).addTransferedStack(getSide(), simulatedStack);
                                    }
                                }
                            }));
                        }));
                    }
                }
            }
        }
    }

    private boolean filter(RegulatorFilter<FluidStack, IFluidHandler> filter, boolean whitelist, FluidStack fluidStack, IFluidHandler handler, boolean isRegulated) {
        int accepts = filter.matches(fluidStack, handler, isRegulated);
        if (whitelist && filter.isEmpty()) {
            return false;
        }
        return filter.isEmpty() != (whitelist == (accepts > 0));
    }

    @Override
    public void updateClient() {
        super.updateClient();
        for (Direction value : Direction.values()) {
            while (this.queue.computeIfAbsent(value, direction -> new ArrayList<>()).size() < QUEUE_SIZE) {
                this.queue.get(value).add(0, FluidStack.EMPTY);
            }
            this.queue.get(value).add(0, FluidStack.EMPTY);
            while (this.queue.get(value).size() > QUEUE_SIZE) {
                this.queue.get(value).remove(this.queue.get(value).size() - 1);
            }
        }
    }

    public void addTransferedStack(Direction direction, FluidStack stack) {
        syncRender(direction, stack.writeToNBT(new CompoundTag()));
    }

    @Override
    public void handleRenderSync(Direction origin, CompoundTag compoundNBT) {
        this.queue.computeIfAbsent(origin, direction -> new ArrayList<>()).add(0, FluidStack.loadFluidStackFromNBT(compoundNBT));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderTransfer(Vector3f pos, Direction direction, int step, PoseStack stack, int combinedOverlayIn, MultiBufferSource buffer) {
        super.renderTransfer(pos, direction, step, stack, combinedOverlayIn, buffer);
        // TODO: 22/08/2021 render stuff.
//        if (step < queue.computeIfAbsent(direction, v -> new ArrayList<>()).size()) {
//            float scale = 0.10f;
//            stack.scale(scale, scale, scale);
//            FluidStack fluidStack = queue.get(direction).get(step);
//            stack.pushPose();
//            stack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
//            stack.mulPose(Vector3f.ZP.rotationDegrees(90f));
//            stack.mulPose(Vector3f.XP.rotationDegrees(90f));
//            VertexConsumer buffer1 = buffer.getBuffer(TransporterTESR.TYPE);
//            Matrix4f matrix = stack.last().pose();
//            float pX1 = 1;
//            float u = 1;
//            float pX2 = 0;
//            float u2 = 0;
//            float xOffset = -0.75f;
//            float yOffset = -0f;
//            float zOffset = -0.75f;
//            int alpha = 512;
//            int red = 0;
//            int green = 0;
//            int blue = 0;
//            if (fluidStack.isEmpty()) {
//                Color CLOSE = Color.CYAN;
//                Color FAR = new Color(0x6800FF);
//                double ratio = (step + 2.5) / (double) QUEUE_SIZE;
//                red = (int) Math.abs((ratio * FAR.getRed()) + ((1 - ratio) * CLOSE.getRed()));
//                green = (int) Math.abs((ratio * FAR.getGreen()) + ((1 - ratio) * CLOSE.getGreen()));
//                blue = (int) Math.abs((ratio * FAR.getBlue()) + ((1 - ratio) * CLOSE.getBlue()));
//                stack.scale(0.25f, 0.25f, 0.25f);
//            } else {
//                Color color = new Color(FluidUtils.getFluidColor(fluidStack));
//                red = color.getRed();
//                green = color.getGreen();
//                blue = color.getBlue();
//                stack.scale(0.75f, 0.75f, 0.75f);
//            }
//            buffer1.vertex(matrix, pX2 + xOffset, yOffset, 0 + zOffset).uv(u2, 0).color(red, green, blue, alpha).endVertex();
//            buffer1.vertex(matrix, pX1 + xOffset + 0.5f, yOffset, 0 + zOffset).uv(u, 0).color(red, green, blue, alpha).endVertex();
//            buffer1.vertex(matrix, pX1 + xOffset + 0.5f, yOffset, 1.5f + zOffset).uv(u, 1).color(red, green, blue, alpha).endVertex();
//            buffer1.vertex(matrix, pX2 + xOffset, yOffset, 1.5f + zOffset).uv(u2, 1).color(red, green, blue, alpha).endVertex();
//            stack.popPose();
//
//        }
    }

    public static class Factory extends TransporterTypeFactory {

        public Factory() {
            setRegistryName("fluid");
        }

        @Override
        public TransporterType create(IBlockContainer container, Direction face, TransporterAction action) {
            return new TransporterFluidType(container, this, face, action);
        }

        @Override
        @Nonnull
        public ResourceLocation getModel(Direction upgradeSide, TransporterAction action) {
            return new ResourceLocation(Reference.MOD_ID, "block/transporters/fluid_transporter_" + action.name().toLowerCase() + "_" + upgradeSide.getSerializedName().toLowerCase());
        }

        @Override
        public Set<ResourceLocation> getTextures() {
            return Sets.newHashSet(new ResourceLocation("industrialforegoing:blocks/transporters/fluid"), new ResourceLocation("industrialforegoing:blocks/base/bottom"));
        }

        @Override
        public boolean canBeAttachedAgainst(Level world, BlockPos pos, Direction face) {
            return TileUtil.getTileEntity(world, pos).map(tileEntity -> tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face).isPresent()).orElse(false);
        }

        @Nonnull
        @Override
        public ResourceLocation getItemModel() {
            return new ResourceLocation(Reference.MOD_ID, "block/transporters/fluid_transporter_" + TransporterAction.EXTRACT.name().toLowerCase() + "_" + Direction.NORTH.getSerializedName().toLowerCase());
        }

        @Override
        public void registerRecipe(Consumer<FinishedRecipe> consumer) {
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

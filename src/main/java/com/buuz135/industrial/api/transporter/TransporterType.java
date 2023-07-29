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
package com.buuz135.industrial.api.transporter;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.api.IBlockContainer;
import com.buuz135.industrial.api.conveyor.gui.IGuiComponent;
import com.buuz135.industrial.block.transportstorage.transporter.TransporterVoxelShapes;
import com.buuz135.industrial.gui.component.custom.TextureGuiComponent;
import com.buuz135.industrial.item.addon.EfficiencyAddonItem;
import com.buuz135.industrial.item.addon.SpeedAddonItem;
import com.buuz135.industrial.proxy.network.TransporterSyncMessage;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.api.augment.AugmentTypes;
import com.hrznstudio.titanium.item.AugmentWrapper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TransporterType implements INBTSerializable<CompoundTag> {

    private IBlockContainer container;
    private TransporterTypeFactory factory;
    private Direction side;
    private TransporterTypeFactory.TransporterAction action;
    private ItemStack speed;
    private ItemStack efficiency;

    public TransporterType(IBlockContainer container, TransporterTypeFactory factory, Direction side, TransporterTypeFactory.TransporterAction action) {
        this.container = container;
        this.factory = factory;
        this.side = side;
        this.action = action;
        this.speed = ItemStack.EMPTY;
        this.efficiency = ItemStack.EMPTY;
    }

    public boolean onUpgradeActivated(Player player, InteractionHand hand) {
        ItemStack handStack = player.getItemInHand(hand);
        if (!handStack.isEmpty()) {
            if (efficiency.isEmpty() && handStack.getItem() instanceof EfficiencyAddonItem) {
                efficiency = ItemHandlerHelper.copyStackWithSize(handStack, 1);
                handStack.shrink(1);
                return true;
            }
            if (speed.isEmpty() && handStack.getItem() instanceof SpeedAddonItem) {
                speed = ItemHandlerHelper.copyStackWithSize(handStack, 1);
                handStack.shrink(1);
                return true;
            }
        }
        return false;
    }

    public Collection<ItemStack> getDrops() {
        Collection<ItemStack> drops = new ArrayList<>();
        drops.add(new ItemStack(this.getFactory().getUpgradeItem(), 1));
        if (!this.efficiency.isEmpty()) {
            drops.add(this.efficiency);
        }
        if (!this.speed.isEmpty()) {
            drops.add(this.speed);
        }
        return drops;
    }

    public IBlockContainer getContainer() {
        return container;
    }

    public Level getLevel() {
        return getContainer().getBlockWorld();
    }

    public BlockPos getPos() {
        return getContainer().getBlockPosition();
    }

    public TransporterTypeFactory getFactory() {
        return factory;
    }

    public Direction getSide() {
        return side;
    }

    public TransporterTypeFactory.TransporterAction getAction() {
        return action;
    }

    public void update() {

    }

    public void updateClient() {

    }

    public void handleEntity(Entity entity) {

    }

    public void onUpgradeRemoved() {

    }


    public int getRedstoneOutput() {
        return 0;
    }

    public VoxelShape getBorderBoundingBox() {
        switch (side) {
            case DOWN:
                return TransporterVoxelShapes.DOWN_RING;
            case NORTH:
                return TransporterVoxelShapes.NORTH_RING;
            case EAST:
                return TransporterVoxelShapes.EAST_RING;
            case SOUTH:
                return TransporterVoxelShapes.SOUTH_RING;
            case WEST:
                return TransporterVoxelShapes.WEST_RING;
            case UP:
                return TransporterVoxelShapes.UP_RING;
        }
        return Shapes.empty();
    }

    public VoxelShape getCenterBoundingBox() {
        if (action == TransporterTypeFactory.TransporterAction.EXTRACT) {
            switch (side) {
                case DOWN:
                    return TransporterVoxelShapes.DOWN_MIDDLE_EXTRACT;
                case NORTH:
                    return TransporterVoxelShapes.NORTH_MIDDLE_EXTRACT;
                case EAST:
                    return TransporterVoxelShapes.EAST_MIDDLE_EXTRACT;
                case SOUTH:
                    return TransporterVoxelShapes.SOUTH_MIDDLE_EXTRACT;
                case WEST:
                    return TransporterVoxelShapes.WEST_MIDDLE_EXTRACT;
                case UP:
                    return TransporterVoxelShapes.UP_MIDDLE_EXTRACT;
            }
        }
        switch (side) {
            case DOWN:
                return TransporterVoxelShapes.DOWN_MIDDLE_INSERT;
            case NORTH:
                return TransporterVoxelShapes.NORTH_MIDDLE_INSERT;
            case EAST:
                return TransporterVoxelShapes.EAST_MIDDLE_INSERT;
            case SOUTH:
                return TransporterVoxelShapes.SOUTH_MIDDLE_INSERT;
            case WEST:
                return TransporterVoxelShapes.WEST_MIDDLE_INSERT;
            case UP:
                return TransporterVoxelShapes.UP_MIDDLE_INSERT;
        }
        return Shapes.empty();
    }

    public boolean hasGui() {
        return true;
    }

    public void handleButtonInteraction(int buttonId, CompoundTag compound) {

    }

    public void handleRenderSync(Direction origin, CompoundTag compoundNBT) {

    }

    public void syncRender(Direction origin, CompoundTag compoundNBT) {
        IndustrialForegoing.NETWORK.sendToNearby(getLevel(), getPos(), 32, new TransporterSyncMessage(getPos(), compoundNBT, getSide().get3DDataValue(), origin.get3DDataValue()));
    }

    public void addComponentsToGui(List<IGuiComponent> componentList) {
        ResourceLocation res = new ResourceLocation(Reference.MOD_ID, "textures/gui/machines.png");
        componentList.add(new TextureGuiComponent(158, 4, 14, 14, res, 96, 233) {
            @Nullable
            @Override
            public List<Component> getTooltip(int guiX, int guiY, double mouseX, double mouseY) {
                List<Component> components = new ArrayList<>();
                if (!speed.isEmpty()) {
                    components.add(speed.getHoverName());
                }
                if (!efficiency.isEmpty()) {
                    components.add(efficiency.getHoverName());
                }
                if (components.isEmpty()) {
                    components.add(Component.literal("No Addons"));
                }
                return components;
            }
        });
    }

    public boolean ignoresCollision() {
        return false;
    }

    public void toggleAction() {
        if (action == TransporterTypeFactory.TransporterAction.EXTRACT) {
            action = TransporterTypeFactory.TransporterAction.INSERT;
        } else {
            action = TransporterTypeFactory.TransporterAction.EXTRACT;
        }
        this.getContainer().requestSync();
        if (this.getLevel().isClientSide && getContainer() instanceof BlockEntity)
            this.getLevel().getModelDataManager().requestRefresh((BlockEntity) getContainer());
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundNBT = new CompoundTag();
        compoundNBT.putBoolean("Insert", action == TransporterTypeFactory.TransporterAction.INSERT);
        compoundNBT.put("Efficiency", this.efficiency.serializeNBT());
        compoundNBT.put("Speed", this.speed.serializeNBT());
        return compoundNBT;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        action = nbt.getBoolean("Insert") ? TransporterTypeFactory.TransporterAction.INSERT : TransporterTypeFactory.TransporterAction.EXTRACT;
        speed = ItemStack.of(nbt.getCompound("Speed"));
        efficiency = ItemStack.of(nbt.getCompound("Efficiency"));
    }

    public float getSpeed() {
        return this.speed.isEmpty() ? 1 : AugmentWrapper.getType(this.speed, AugmentTypes.SPEED);
    }

    public float getEfficiency() {
        return this.efficiency.isEmpty() ? 1 : ((1 - AugmentWrapper.getType(this.efficiency, AugmentTypes.EFFICIENCY)) / 0.1f) * 32;
    }

    @OnlyIn(Dist.CLIENT)
    public void renderTransfer(Vector3f pos, Direction direction, int step, PoseStack stack, int combinedOverlayIn, MultiBufferSource buffer, float frame, Level level) {

    }
}

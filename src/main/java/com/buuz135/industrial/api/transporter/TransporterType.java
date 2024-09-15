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
import com.buuz135.industrial.block.transportstorage.tile.TransporterTile;
import com.buuz135.industrial.block.transportstorage.transporter.TransporterVoxelShapes;
import com.buuz135.industrial.proxy.network.TransporterSyncMessage;
import com.hrznstudio.titanium.api.augment.AugmentTypes;
import com.hrznstudio.titanium.item.AugmentWrapper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TransporterType implements INBTSerializable<CompoundTag> {

    private IBlockContainer container;
    private TransporterTypeFactory factory;
    private Direction side;
    private TransporterTypeFactory.TransporterAction action;

    public TransporterType(IBlockContainer container, TransporterTypeFactory factory, Direction side, TransporterTypeFactory.TransporterAction action) {
        this.container = container;
        this.factory = factory;
        this.side = side;
        this.action = action;
    }

    public Collection<ItemStack> getDrops() {
        Collection<ItemStack> drops = new ArrayList<>();
        drops.add(new ItemStack(this.getFactory().getUpgradeItem(), 1));
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
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag compoundNBT = new CompoundTag();
        compoundNBT.putBoolean("Insert", action == TransporterTypeFactory.TransporterAction.INSERT);
        return compoundNBT;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        action = nbt.getBoolean("Insert") ? TransporterTypeFactory.TransporterAction.INSERT : TransporterTypeFactory.TransporterAction.EXTRACT;
    }

    public float getSpeed() {
        var tile = getContainer();
        if (tile instanceof TransporterTile transporterTile && transporterTile.hasAugmentInstalled(AugmentTypes.SPEED)) {
            return AugmentWrapper.getType(transporterTile.getInstalledAugments(AugmentTypes.SPEED).getFirst(), AugmentTypes.SPEED);
        }
        return 1;
    }

    public float getEfficiency() {
        var tile = getContainer();
        if (tile instanceof TransporterTile transporterTile && transporterTile.hasAugmentInstalled(AugmentTypes.EFFICIENCY)) {
            return ((1 - AugmentWrapper.getType(transporterTile.getInstalledAugments(AugmentTypes.EFFICIENCY).getFirst(), AugmentTypes.EFFICIENCY)) / 0.1f) * 32;
        }
        return 1;
    }

    @OnlyIn(Dist.CLIENT)
    public void renderTransfer(Vector3f pos, Direction direction, int step, PoseStack stack, int combinedOverlayIn, MultiBufferSource buffer, float frame, Level level) {

    }
}

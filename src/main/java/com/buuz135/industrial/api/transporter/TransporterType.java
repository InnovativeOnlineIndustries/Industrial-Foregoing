package com.buuz135.industrial.api.transporter;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.api.IBlockContainer;
import com.buuz135.industrial.api.conveyor.gui.IGuiComponent;
import com.buuz135.industrial.block.transportstorage.transporter.TransporterVoxelShapes;
import com.buuz135.industrial.proxy.network.TransporterSyncMessage;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TransporterType implements INBTSerializable<CompoundNBT> {

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

    public boolean onUpgradeActivated(PlayerEntity player, Hand hand) {
        return false;
    }

    public Collection<ItemStack> getDrops() {
        return Collections.singleton(new ItemStack(this.getFactory().getUpgradeItem(), 1));
    }

    public IBlockContainer getContainer() {
        return container;
    }

    public World getWorld() {
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
        return VoxelShapes.empty();
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
        return VoxelShapes.empty();
    }

    public boolean hasGui() {
        return true;
    }

    public void handleButtonInteraction(int buttonId, CompoundNBT compound) {

    }

    public void handleRenderSync(Direction origin, CompoundNBT compoundNBT) {

    }

    public void syncRender(Direction origin, CompoundNBT compoundNBT) {
        IndustrialForegoing.NETWORK.sendToNearby(getWorld(), getPos(), 32, new TransporterSyncMessage(getPos(), compoundNBT, getSide().getIndex(), origin.getIndex()));
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
        if (this.getWorld().isRemote && getContainer() instanceof TileEntity)
            ModelDataManager.requestModelDataRefresh((TileEntity) getContainer());
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putBoolean("Insert", action == TransporterTypeFactory.TransporterAction.INSERT);
        return compoundNBT;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        action = nbt.getBoolean("Insert") ? TransporterTypeFactory.TransporterAction.INSERT : TransporterTypeFactory.TransporterAction.EXTRACT;
    }

    @OnlyIn(Dist.CLIENT)
    public void renderTransfer(Vector3f pos, Direction direction, int step, MatrixStack stack, int combinedOverlayIn, IRenderTypeBuffer buffer) {

    }
}

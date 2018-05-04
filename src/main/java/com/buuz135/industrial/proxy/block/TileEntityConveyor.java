package com.buuz135.industrial.proxy.block;

import com.buuz135.industrial.utils.MovementUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.buuz135.industrial.proxy.block.BlockConveyor.*;

public class TileEntityConveyor extends TileEntity {

    private EnumFacing facing;
    private BlockConveyor.EnumType type;
    private int color;

    public TileEntityConveyor() {
        this.facing = EnumFacing.NORTH;
        this.type = BlockConveyor.EnumType.FLAT;
        this.color = 0;
    }

    public EnumFacing getFacing() {
        return facing;
    }

    public void setFacing(EnumFacing facing) {
        this.facing = facing;
        markForUpdate();
    }

    public EnumType getType() {
        return type;
    }

    public void setType(EnumType type) {
        this.type = type;
        markForUpdate();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        markForUpdate();
    }

    public void setColor(EnumDyeColor color) {
        this.color = color.getDyeDamage();
        markForUpdate();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setString("Facing", facing.getName());
        compound.setString("Type", type.getName());
        compound.setInteger("Color", color);
        return compound;
    }

    @Override
    protected void setWorldCreate(World worldIn) {
        super.setWorldCreate(worldIn);
        this.world = worldIn;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.facing = EnumFacing.valueOf((compound.getString("Facing").toUpperCase()));
        this.type = BlockConveyor.EnumType.valueOf(compound.getString("Type").toUpperCase());
        this.color = compound.getInteger("Color");
    }

    public void markForUpdate() {
        this.world.setBlockState(pos, this.world.getBlockState(pos).withProperty(TYPE, type).withProperty(FACING, facing));
        this.world.notifyBlockUpdate(getPos(), getWorld().getBlockState(getPos()), getWorld().getBlockState(getPos()), 3);
        markDirty();
    }

    public List<AxisAlignedBB> getCollisionBoxes() {
        List<AxisAlignedBB> boxes = new ArrayList<>();
        EnumFacing facing = this.facing;
        if (type.isDown()) facing = facing.getOpposite();
        double height = 1;
        while (height > 0) {
            if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
                boxes.add(new AxisAlignedBB(0, 0, facing == EnumFacing.NORTH ? 0 : 1D - height, 1, 1 - height, facing == EnumFacing.NORTH ? height : 1));
            }
            if (facing == EnumFacing.WEST || facing == EnumFacing.EAST) {
                boxes.add(new AxisAlignedBB(facing == EnumFacing.WEST ? 0 : 1D - height, 0, 0, facing == EnumFacing.WEST ? height : 1, 1 - height, 1));
            }
            height -= 0.1D;
        }
        return boxes;
    }

    public void handleEntityMovement(Entity entity) {
        MovementUtils.handleConveyorMovement(entity, facing, this.pos, type);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return false;
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new SPacketUpdateTileEntity(getPos(), 1, tag);
    }
}

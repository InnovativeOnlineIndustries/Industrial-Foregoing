package com.buuz135.industrial.api.conveyor;

import com.buuz135.industrial.proxy.block.Cuboid;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class ConveyorUpgrade implements INBTSerializable<NBTTagCompound> {
    private IConveyorContainer container;
    private ConveyorUpgradeFactory factory;
    private EnumFacing side;

    public static Cuboid EMPTY_BB = new Cuboid(0, 0, 0, 0, 0, 0);

    public ConveyorUpgrade(IConveyorContainer container, ConveyorUpgradeFactory factory, EnumFacing side) {
        this.container = container;
        this.factory = factory;
        this.side = side;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {

    }

    public IConveyorContainer getContainer() {
        return container;
    }

    public World getWorld() {
        return getContainer().getConveyorWorld();
    }

    public BlockPos getPos() {
        return getContainer().getConveyorPosition();
    }

    public ConveyorUpgradeFactory getFactory() {
        return factory;
    }

    public EnumFacing getSide() {
        return side;
    }

    public void update() {

    }

    public void handleEntity(Entity entity) {

    }

    public int getRedstoneOutput() {
        return 0;
    }

    public Cuboid getBoundingBox() {
        return EMPTY_BB;
    }
}
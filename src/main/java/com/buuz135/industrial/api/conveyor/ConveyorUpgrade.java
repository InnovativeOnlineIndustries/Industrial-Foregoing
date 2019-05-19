/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
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
package com.buuz135.industrial.api.conveyor;

import com.buuz135.industrial.api.conveyor.gui.IGuiComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class ConveyorUpgrade implements INBTSerializable<NBTTagCompound> {

    private IConveyorContainer container;
    private ConveyorUpgradeFactory factory;
    private EnumFacing side;

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

    public boolean onUpgradeActivated(EntityPlayer player, EnumHand hand) {
        return false;
    }

    public Collection<ItemStack> getDrops() {
        return Collections.singleton(new ItemStack(this.getFactory().getUpgradeItem(), 1));
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

    public void onUpgradeRemoved() {

    }

    public int getRedstoneOutput() {
        return 0;
    }

    public VoxelShape getBoundingBox() {
        return VoxelShapes.empty();
    }

    public boolean hasGui() {
        return false;
    }

    public void handleButtonInteraction(int buttonId, NBTTagCompound compound) {

    }

    public void addComponentsToGui(List<IGuiComponent> componentList) {
    }

}
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
package com.buuz135.industrial.proxy.block.upgrade;

import com.buuz135.industrial.api.conveyor.ConveyorUpgrade;
import com.buuz135.industrial.api.conveyor.ConveyorUpgradeFactory;
import com.buuz135.industrial.api.conveyor.IConveyorContainer;
import com.buuz135.industrial.api.conveyor.gui.IGuiComponent;
import com.buuz135.industrial.gui.component.FilterGuiComponent;
import com.buuz135.industrial.gui.component.StateButtonInfo;
import com.buuz135.industrial.gui.component.TexturedStateButtonGuiComponent;
import com.buuz135.industrial.proxy.block.Cuboid;
import com.buuz135.industrial.proxy.block.filter.IFilter;
import com.buuz135.industrial.proxy.block.filter.ItemStackFilter;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ConveyorDroppingUpgrade extends ConveyorUpgrade {

    public static Cuboid BB = new Cuboid(0.0625 * 3, 0.0625, 0.0625 * 3, 0.0625 * 13, 0.0625 * 1.2, 0.0625 * 13, EnumFacing.DOWN.getIndex(), false);

    private ItemStackFilter filter;
    private boolean whitelist;

    public ConveyorDroppingUpgrade(IConveyorContainer container, ConveyorUpgradeFactory factory, EnumFacing side) {
        super(container, factory, side);
        this.filter = new ItemStackFilter(20, 20, 5, 3);
        this.whitelist = false;
    }

    @Override
    public void handleEntity(Entity entity) {
        super.handleEntity(entity);
        if (entity instanceof EntityPlayer) return;
        if (whitelist != filter.matches(entity)) return;
        if (entity instanceof EntityItem) {
            TileEntity tile = getWorld().getTileEntity(getPos().offset(EnumFacing.DOWN));
            if (tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP)) {
                IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
                if (getBoundingBox().aabb().offset(getPos()).grow(0.01).intersects(entity.getEntityBoundingBox())) {
                    ItemStack stack = ((EntityItem) entity).getItem();
                    for (int i = 0; i < handler.getSlots(); i++) {
                        stack = handler.insertItem(i, stack, false);
                        if (stack.isEmpty()) {
                            entity.setDead();
                            break;
                        } else {
                            ((EntityItem) entity).setItem(stack);
                        }
                    }
                }
            }
        }
        if (entity.isDead) return;
        double entityHeight = entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY;
        BlockPos pos = this.getPos().down((int) Math.ceil(entityHeight));
        boolean space = true;
        for (int y = pos.getY(); y < this.getPos().getY(); ++y) {
            if (!this.getWorld().isAirBlock(new BlockPos(pos.getX(), y, pos.getZ()))) {
                space = false;
                break;
            }
        }
        if (space) {
            entity.motionX = 0;
            entity.motionY = 0;
            entity.motionZ = 0;
            entity.setPosition(pos.getX() + 0.5, pos.getY() - 0.1, pos.getZ() + 0.5);
            entity.onGround = false;
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = super.serializeNBT() == null ? new NBTTagCompound() : super.serializeNBT();
        compound.setTag("Filter", filter.serializeNBT());
        compound.setBoolean("Whitelist", whitelist);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        if (nbt.hasKey("Filter")) filter.deserializeNBT(nbt.getCompoundTag("Filter"));
        whitelist = nbt.getBoolean("Whitelist");
    }

    @Override
    public Cuboid getBoundingBox() {
        return BB;
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    public void handleButtonInteraction(int buttonId, NBTTagCompound compound) {
        super.handleButtonInteraction(buttonId, compound);
        if (buttonId >= 0 && buttonId < filter.getFilter().length) {
            this.filter.setFilter(buttonId, new ItemStack(compound));
            this.getContainer().requestSync();
        }
        if (buttonId == 16) {
            whitelist = !whitelist;
            this.getContainer().requestSync();
        }
    }

    @Override
    public void addComponentsToGui(List<IGuiComponent> componentList) {
        super.addComponentsToGui(componentList);
        componentList.add(new FilterGuiComponent(this.filter.getLocX(), this.filter.getLocY(), this.filter.getSizeX(), this.filter.getSizeY()) {
            @Override
            public IFilter getFilter() {
                return ConveyorDroppingUpgrade.this.filter;
            }
        });
        ResourceLocation res = new ResourceLocation(Reference.MOD_ID, "textures/gui/machines.png");
        componentList.add(new TexturedStateButtonGuiComponent(16, 133, 20, 18, 18,
                new StateButtonInfo(0, res, 1, 214, new String[]{"whitelist"}),
                new StateButtonInfo(1, res, 20, 214, new String[]{"blacklist"})) {
            @Override
            public int getState() {
                return whitelist ? 0 : 1;
            }
        });
    }

    public static class Factory extends ConveyorUpgradeFactory {

        public Factory() {
            setRegistryName("dropping");
        }

        @Override
        public ConveyorUpgrade create(IConveyorContainer container, EnumFacing face) {
            return new ConveyorDroppingUpgrade(container, this, face);
        }

        @Override
        public Set<ResourceLocation> getTextures() {
            return Collections.singleton(new ResourceLocation(Reference.MOD_ID, "blocks/conveyor_dropping_upgrade"));
        }

        @Nonnull
        @Override
        public Set<EnumFacing> getValidFacings() {
            return DOWN;
        }

        @Override
        public EnumFacing getSideForPlacement(World world, BlockPos pos, EntityPlayer player) {
            return EnumFacing.DOWN;
        }

        @Override
        @Nonnull
        public ResourceLocation getModel(EnumFacing upgradeSide, EnumFacing conveyorFacing) {
            return new ResourceLocation(Reference.MOD_ID, "block/conveyor_upgrade_dropping");
        }

        @Nonnull
        @Override
        public ResourceLocation getItemModel() {
            return new ResourceLocation(Reference.MOD_ID, "conveyor_dropping_upgrade");
        }
    }
}

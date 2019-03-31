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
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.proxy.block.Cuboid;
import com.buuz135.industrial.proxy.block.filter.IFilter;
import com.buuz135.industrial.proxy.block.filter.ItemStackFilter;
import com.buuz135.industrial.proxy.block.tile.TileEntityConveyor;
import com.buuz135.industrial.registry.IFRegistries;
import com.buuz135.industrial.utils.Reference;
import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ConveyorExtractionUpgrade extends ConveyorUpgrade {

    public static Cuboid NORTHBB = new Cuboid(0.0625 * 4, 0.0625 * 3, -0.0625 * 2, 0.0625 * 12, 0.0625 * 11, 0.0625 * 3, EnumFacing.NORTH.getIndex());
    public static Cuboid SOUTHBB = new Cuboid(0.0625 * 4, 0.0625 * 3, 0.0625 * 13, 0.0625 * 12, 0.0625 * 11, 0.0625 * 18, EnumFacing.SOUTH.getIndex());
    public static Cuboid EASTBB = new Cuboid(0.0625 * 13, 0.0625 * 3, 0.0625 * 4, 0.0625 * 18, 0.0625 * 11, 0.0625 * 12, EnumFacing.EAST.getIndex());
    public static Cuboid WESTBB = new Cuboid(-0.0625 * 2, 0.0625 * 3, 0.0625 * 4, 0.0625 * 3, 0.0625 * 11, 0.0625 * 12, EnumFacing.WEST.getIndex());

    private boolean fast = false;
    private ItemStackFilter filter;
    private boolean whitelist;
    private List<EntityItem> items;

    public ConveyorExtractionUpgrade(IConveyorContainer container, ConveyorUpgradeFactory factory, EnumFacing side) {
        super(container, factory, side);
        this.filter = new ItemStackFilter(20, 20, 5, 3);
        this.whitelist = false;
        this.items = new ArrayList<>();
    }

    @Override
    public Cuboid getBoundingBox() {
        switch (getSide()) {
            default:
            case NORTH:
                return NORTHBB;
            case SOUTH:
                return SOUTHBB;
            case EAST:
                return EASTBB;
            case WEST:
                return WESTBB;
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setBoolean("fast", fast);
        compound.setTag("Filter", filter.serializeNBT());
        compound.setBoolean("Whitelist", whitelist);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        fast = nbt.getBoolean("fast");
        if (nbt.hasKey("Filter")) filter.deserializeNBT(nbt.getCompoundTag("Filter"));
        whitelist = nbt.getBoolean("Whitelist");
    }

    @Override
    public boolean onUpgradeActivated(EntityPlayer player, EnumHand hand) {
        ItemStack held = player.getHeldItem(hand);
        if (held.getItem() == Items.GLOWSTONE_DUST && !fast) {
            fast = true;
            held.shrink(1);
            return true;
        }
        return false;
    }

    @Override
    public Collection<ItemStack> getDrops() {
        if (!fast)
            return super.getDrops();
        return Sets.newHashSet(
                new ItemStack(ItemRegistry.conveyorUpgradeItem, 1, IFRegistries.CONVEYOR_UPGRADE_REGISTRY.getID(getFactory()) - 1),
                new ItemStack(Items.GLOWSTONE_DUST)
        );
    }

    @Override
    public void update() {
        if (getWorld().isRemote)
            return;
        items.removeIf(entityItem -> entityItem.getItem().isEmpty() || entityItem.isDead);
        if (items.size() >= 20) return;
        if (getWorld().getTotalWorldTime() % (fast ? 10 : 20) == 0) {
            IItemHandler itemHandler = getHandlerCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            if (itemHandler != null) {
                for (int i = 0; i < itemHandler.getSlots(); i++) {
                    ItemStack stack = itemHandler.extractItem(i, 4, true);
                    if (stack.isEmpty() || whitelist != filter.matches(stack))
                        continue;
                    EntityItem item = new EntityItem(getWorld(), getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5);
                    item.motionX = 0;
                    item.motionY = 0;
                    item.motionZ = 0;
                    item.setPickupDelay(40);
                    item.setItem(itemHandler.extractItem(i, 4, false));
                    if (getWorld().spawnEntity(item)) {
                        items.add(item);
                    }
                    break;
                }
            }
        }
        if (getContainer() instanceof TileEntityConveyor) {
            IFluidTank tank = ((TileEntityConveyor) getContainer()).getTank();
            IFluidHandler fluidHandler = getHandlerCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
            if (fluidHandler != null && fluidHandler.drain(250, false) != null && ((FluidTank) tank).canFillFluidType(fluidHandler.drain(250, false)) && whitelist == filter.matches(fluidHandler.drain(250, false))) {
                FluidStack drain = fluidHandler.drain(tank.fill(fluidHandler.drain(250, false), true), true);
                if (drain != null && drain.amount > 0) getContainer().requestFluidSync();
            }
        }
    }

    @Nullable
    private <T> T getHandlerCapability(Capability<T> capability) {
        BlockPos offsetPos = getPos().offset(getSide());
        TileEntity tile = getWorld().getTileEntity(offsetPos);
        if (tile != null && tile.hasCapability(capability, getSide().getOpposite()))
            return tile.getCapability(capability, getSide().getOpposite());
        for (Entity entity : getWorld().getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(offsetPos), EntitySelectors.NOT_SPECTATING)) {
            if (entity instanceof EntityMob) continue;
            if (entity.hasCapability(capability, entity instanceof EntityPlayerMP ? null : getSide().getOpposite()))
                return entity.getCapability(capability, entity instanceof EntityPlayerMP ? null : getSide().getOpposite());
        }
        return null;
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
                return ConveyorExtractionUpgrade.this.filter;
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
            setRegistryName("extraction");
        }

        @Override
        public ConveyorUpgrade create(IConveyorContainer container, EnumFacing face) {
            return new ConveyorExtractionUpgrade(container, this, face);
        }

        @Override
        @Nonnull
        public ResourceLocation getModel(EnumFacing upgradeSide, EnumFacing conveyorFacing) {
            return new ResourceLocation(Reference.MOD_ID, "block/conveyor_upgrade_extractor_" + upgradeSide.getName().toLowerCase());
        }

        @Nonnull
        @Override
        public ResourceLocation getItemModel() {
            return new ResourceLocation(Reference.MOD_ID, "conveyor_extraction_upgrade");
        }
    }
}
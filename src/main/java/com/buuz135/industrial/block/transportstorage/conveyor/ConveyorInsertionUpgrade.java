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
package com.buuz135.industrial.block.transportstorage.conveyor;

import com.buuz135.industrial.api.conveyor.ConveyorUpgrade;
import com.buuz135.industrial.api.conveyor.ConveyorUpgradeFactory;
import com.buuz135.industrial.api.conveyor.IConveyorContainer;
import com.buuz135.industrial.api.conveyor.gui.IGuiComponent;
import com.buuz135.industrial.block.transportstorage.tile.ConveyorTile;
import com.buuz135.industrial.gui.component.FilterGuiComponent;
import com.buuz135.industrial.gui.component.StateButtonInfo;
import com.buuz135.industrial.gui.component.TexturedStateButtonGuiComponent;
import com.buuz135.industrial.module.ModuleTransportStorage;
import com.buuz135.industrial.proxy.block.filter.IFilter;
import com.buuz135.industrial.proxy.block.filter.ItemStackFilter;
import com.buuz135.industrial.utils.IndustrialTags;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;

public class ConveyorInsertionUpgrade extends ConveyorUpgrade {

    public static VoxelShape NORTHBB = VoxelShapes.create(0.0625 * 2, 0, -0.0625 * 2, 0.0625 * 14, 0.0625 * 9, 0.0625 * 2);
    public static VoxelShape SOUTHBB = VoxelShapes.create(0.0625 * 2, 0, 0.0625 * 14, 0.0625 * 14, 0.0625 * 9, 0.0625 * 18);
    public static VoxelShape EASTBB = VoxelShapes.create(0.0625 * 14, 0, 0.0625 * 2, 0.0625 * 18, 0.0625 * 9, 0.0625 * 14);
    public static VoxelShape WESTBB = VoxelShapes.create(-0.0625 * 2, 0, 0.0625 * 2, 0.0625 * 2, 0.0625 * 9, 0.0625 * 14);

    public static VoxelShape NORTHBB_BIG = VoxelShapes.create(0.0625 * 2, 0, -0.0625 * 2, 0.0625 * 14, 0.0625 * 9, 0.0625 * 14);
    public static VoxelShape SOUTHBB_BIG = VoxelShapes.create(0.0625 * 2, 0, 0.0625 * 2, 0.0625 * 14, 0.0625 * 9, 0.0625 * 18);
    public static VoxelShape EASTBB_BIG = VoxelShapes.create(0.0625 * 2, 0, 0.0625 * 2, 0.0625 * 18, 0.0625 * 9, 0.0625 * 14);
    public static VoxelShape WESTBB_BIG = VoxelShapes.create(-0.0625 * 2, 0, 0.0625 * 2, 0.0625 * 14, 0.0625 * 9, 0.0625 * 14);

    private ItemStackFilter filter;
    private boolean whitelist;
    private boolean fullArea;

    public ConveyorInsertionUpgrade(IConveyorContainer container, ConveyorUpgradeFactory factory, Direction side) {
        super(container, factory, side);
        this.filter = new ItemStackFilter(20, 20, 5, 3);
        this.whitelist = false;
        this.fullArea = false;
    }

    @Override
    public void handleEntity(Entity entity) {
        if (getWorld().isRemote)
            return;
        if (entity instanceof ItemEntity) {
            getHandlerCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
                if (getWorkingBox().getBoundingBox().offset(getPos()).grow(0.01).intersects(entity.getBoundingBox())) {
                    if (whitelist != filter.matches((ItemEntity) entity)) return;
                    ItemStack stack = ((ItemEntity) entity).getItem();
                    for (int i = 0; i < handler.getSlots(); i++) {
                        stack = handler.insertItem(i, stack, false);
                        if (stack.isEmpty()) {
                            entity.remove();
                            break;
                        } else {
                            ((ItemEntity) entity).setItem(stack);
                        }
                    }
                }
            });

        }
    }

    @Override
    public void update() {
        if (getWorld().isRemote)
            return;
        if (getWorld().getGameTime() % 2 == 0 && getContainer() instanceof ConveyorTile) {
            IFluidTank tank = ((ConveyorTile) getContainer()).getTank();
            getHandlerCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(fluidHandler -> {
                if (!tank.drain(50, IFluidHandler.FluidAction.SIMULATE).isEmpty() && fluidHandler.fill(tank.drain(50, IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.SIMULATE) > 0 && whitelist == filter.matches(tank.drain(50, IFluidHandler.FluidAction.SIMULATE))) {
                    FluidStack drain = tank.drain(fluidHandler.fill(tank.drain(50, IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                    if (!drain.isEmpty() && drain.getAmount() > 0) getContainer().requestFluidSync();
                }
            });
        }
    }

    private <T> LazyOptional<T> getHandlerCapability(Capability<T> capability) {
        BlockPos offsetPos = getPos().offset(getSide());
        TileEntity tile = getWorld().getTileEntity(offsetPos);
        if (tile != null && tile.getCapability(capability, getSide().getOpposite()).isPresent())
            return tile.getCapability(capability, getSide().getOpposite());
        for (Entity entity : getWorld().getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(offsetPos))) {
            if (entity.getCapability(capability, entity instanceof ServerPlayerEntity ? null : getSide().getOpposite()).isPresent())
                return entity.getCapability(capability, entity instanceof ServerPlayerEntity ? null : getSide().getOpposite());
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = super.serializeNBT() == null ? new CompoundNBT() : super.serializeNBT();
        compound.put("Filter", filter.serializeNBT());
        compound.putBoolean("Whitelist", whitelist);
        compound.putBoolean("FullArea", fullArea);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Filter")) filter.deserializeNBT(nbt.getCompound("Filter"));
        whitelist = nbt.getBoolean("Whitelist");
        fullArea = nbt.getBoolean("FullArea");
    }

    @Override
    public VoxelShape getBoundingBox() {
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

    private VoxelShape getWorkingBox() {
        if (!fullArea) return getBoundingBox();
        switch (getSide()) {
            default:
            case NORTH:
                return NORTHBB_BIG;
            case SOUTH:
                return SOUTHBB_BIG;
            case EAST:
                return EASTBB_BIG;
            case WEST:
                return WESTBB_BIG;
        }
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    public void handleButtonInteraction(int buttonId, CompoundNBT compound) {
        super.handleButtonInteraction(buttonId, compound);
        if (buttonId >= 0 && buttonId < filter.getFilter().length) {
            this.filter.setFilter(buttonId, ItemStack.read(compound));
            this.getContainer().requestSync();
        }
        if (buttonId == 16) {
            whitelist = !whitelist;
            this.getContainer().requestSync();
        }
        if (buttonId == 17) {
            fullArea = !fullArea;
            this.getContainer().requestSync();
        }
    }

    @Override
    public void addComponentsToGui(List<IGuiComponent> componentList) {
        super.addComponentsToGui(componentList);
        componentList.add(new FilterGuiComponent(this.filter.getLocX(), this.filter.getLocY(), this.filter.getSizeX(), this.filter.getSizeY()) {
            @Override
            public IFilter getFilter() {
                return ConveyorInsertionUpgrade.this.filter;
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
        componentList.add(new TexturedStateButtonGuiComponent(17, 133, 20 + 35, 18, 18,
                new StateButtonInfo(0, res, 39, 214, new String[]{"insert_near"}),
                new StateButtonInfo(1, res, 58, 214, new String[]{"insert_all"})) {
            @Override
            public int getState() {
                return fullArea ? 1 : 0;
            }
        });
    }

    public static class Factory extends ConveyorUpgradeFactory {

        public Factory() {
            setRegistryName("insertion");
        }

        @Override
        public ConveyorUpgrade create(IConveyorContainer container, Direction face) {
            return new ConveyorInsertionUpgrade(container, this, face);
        }

        @Override
        @Nonnull
        public ResourceLocation getModel(Direction upgradeSide, Direction conveyorFacing) {
            return new ResourceLocation(Reference.MOD_ID, "block/conveyor_upgrade_inserter_" + upgradeSide.getString().toLowerCase());
        }

        @Nonnull
        @Override
        public ResourceLocation getItemModel() {
            return new ResourceLocation(Reference.MOD_ID, "conveyor_insertion_upgrade");
        }

        @Override
        public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
            TitaniumShapedRecipeBuilder.shapedRecipe(getUpgradeItem())
                    .patternLine("IPI").patternLine("IDI").patternLine("ICI")
                    .key('I', Tags.Items.INGOTS_IRON)
                    .key('P', IndustrialTags.Items.PLASTIC)
                    .key('D', Blocks.HOPPER)
                    .key('C', ModuleTransportStorage.CONVEYOR)
                    .build(consumer);

        }
    }
}
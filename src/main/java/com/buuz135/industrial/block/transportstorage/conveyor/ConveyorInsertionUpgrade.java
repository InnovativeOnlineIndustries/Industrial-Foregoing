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

package com.buuz135.industrial.block.transportstorage.conveyor;

import com.buuz135.industrial.api.IBlockContainer;
import com.buuz135.industrial.api.conveyor.ConveyorUpgrade;
import com.buuz135.industrial.api.conveyor.ConveyorUpgradeFactory;
import com.buuz135.industrial.api.conveyor.gui.IGuiComponent;
import com.buuz135.industrial.block.transportstorage.tile.ConveyorTile;
import com.buuz135.industrial.gui.component.StateButtonInfo;
import com.buuz135.industrial.gui.component.custom.FilterGuiComponent;
import com.buuz135.industrial.gui.component.custom.TexturedStateButtonGuiComponent;
import com.buuz135.industrial.module.ModuleTransportStorage;
import com.buuz135.industrial.proxy.block.filter.IFilter;
import com.buuz135.industrial.proxy.block.filter.ItemStackFilter;
import com.buuz135.industrial.utils.IndustrialTags;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
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

    public static VoxelShape NORTHBB = Shapes.box(0.0625 * 2, 0, -0.0625 * 2, 0.0625 * 14, 0.0625 * 9, 0.0625 * 2);
    public static VoxelShape SOUTHBB = Shapes.box(0.0625 * 2, 0, 0.0625 * 14, 0.0625 * 14, 0.0625 * 9, 0.0625 * 18);
    public static VoxelShape EASTBB = Shapes.box(0.0625 * 14, 0, 0.0625 * 2, 0.0625 * 18, 0.0625 * 9, 0.0625 * 14);
    public static VoxelShape WESTBB = Shapes.box(-0.0625 * 2, 0, 0.0625 * 2, 0.0625 * 2, 0.0625 * 9, 0.0625 * 14);

    public static VoxelShape NORTHBB_BIG = Shapes.box(0.0625 * 2, 0, -0.0625 * 2, 0.0625 * 14, 0.0625 * 9, 0.0625 * 14);
    public static VoxelShape SOUTHBB_BIG = Shapes.box(0.0625 * 2, 0, 0.0625 * 2, 0.0625 * 14, 0.0625 * 9, 0.0625 * 18);
    public static VoxelShape EASTBB_BIG = Shapes.box(0.0625 * 2, 0, 0.0625 * 2, 0.0625 * 18, 0.0625 * 9, 0.0625 * 14);
    public static VoxelShape WESTBB_BIG = Shapes.box(-0.0625 * 2, 0, 0.0625 * 2, 0.0625 * 14, 0.0625 * 9, 0.0625 * 14);

    private ItemStackFilter filter;
    private boolean whitelist;
    private boolean fullArea;

    public ConveyorInsertionUpgrade(IBlockContainer container, ConveyorUpgradeFactory factory, Direction side) {
        super(container, factory, side);
        this.filter = new ItemStackFilter(20, 20, 5, 3);
        this.whitelist = false;
        this.fullArea = false;
    }

    @Override
    public void handleEntity(Entity entity) {
        if (getWorld().isClientSide)
            return;
        if (entity instanceof ItemEntity) {
            getHandlerCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
                if (getWorkingBox().bounds().move(getPos()).inflate(0.01).intersects(entity.getBoundingBox())) {
                    if (whitelist != filter.matches((ItemEntity) entity)) return;
                    ItemStack stack = ((ItemEntity) entity).getItem();
                    for (int i = 0; i < handler.getSlots(); i++) {
                        stack = handler.insertItem(i, stack, false);
                        if (stack.isEmpty()) {
                            ((ItemEntity) entity).setItem(ItemStack.EMPTY);
                            entity.remove(Entity.RemovalReason.KILLED);
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
        if (getWorld().isClientSide)
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
        BlockPos offsetPos = getPos().relative(getSide());
        BlockEntity tile = getWorld().getBlockEntity(offsetPos);
        if (tile != null && tile.getCapability(capability, getSide().getOpposite()).isPresent())
            return tile.getCapability(capability, getSide().getOpposite());
        for (Entity entity : getWorld().getEntitiesOfClass(Entity.class, new AABB(0, 0, 0, 1, 1, 1).move(offsetPos))) {
            if (entity.getCapability(capability, entity instanceof ServerPlayer ? null : getSide().getOpposite()).isPresent())
                return entity.getCapability(capability, entity instanceof ServerPlayer ? null : getSide().getOpposite());
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = super.serializeNBT() == null ? new CompoundTag() : super.serializeNBT();
        compound.put("Filter", filter.serializeNBT());
        compound.putBoolean("Whitelist", whitelist);
        compound.putBoolean("FullArea", fullArea);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
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
    public void handleButtonInteraction(int buttonId, CompoundTag compound) {
        super.handleButtonInteraction(buttonId, compound);
        if (buttonId >= 0 && buttonId < filter.getFilter().length) {
            this.filter.setFilter(buttonId, ItemStack.of(compound));
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
            super("insertion");
        }

        @Override
        public ConveyorUpgrade create(IBlockContainer container, Direction face) {
            return new ConveyorInsertionUpgrade(container, this, face);
        }

        @Override
        @Nonnull
        public ResourceLocation getModel(Direction upgradeSide, Direction conveyorFacing) {
            return new ResourceLocation(Reference.MOD_ID, "block/conveyor_upgrade_inserter_" + upgradeSide.getSerializedName().toLowerCase());
        }

        @Nonnull
        @Override
        public ResourceLocation getItemModel() {
            return new ResourceLocation(Reference.MOD_ID, "conveyor_insertion_upgrade");
        }

        @Override
        public void registerRecipe(Consumer<FinishedRecipe> consumer) {
            TitaniumShapedRecipeBuilder.shapedRecipe(getUpgradeItem())
                    .pattern("IPI").pattern("IDI").pattern("ICI")
                    .define('I', Tags.Items.INGOTS_IRON)
                    .define('P', IndustrialTags.Items.PLASTIC)
                    .define('D', Blocks.HOPPER)
                    .define('C', ModuleTransportStorage.CONVEYOR.getLeft().get())
                    .save(consumer);

        }
    }
}

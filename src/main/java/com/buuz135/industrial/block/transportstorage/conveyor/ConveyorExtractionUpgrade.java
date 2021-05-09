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
import com.google.common.collect.Sets;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class ConveyorExtractionUpgrade extends ConveyorUpgrade {

    public static VoxelShape NORTHBB = VoxelShapes.create(0.0625 * 4, 0.0625 * 3, -0.0625 * 2, 0.0625 * 12, 0.0625 * 11, 0.0625 * 3);
    public static VoxelShape SOUTHBB = VoxelShapes.create(0.0625 * 4, 0.0625 * 3, 0.0625 * 13, 0.0625 * 12, 0.0625 * 11, 0.0625 * 18);
    public static VoxelShape EASTBB = VoxelShapes.create(0.0625 * 13, 0.0625 * 3, 0.0625 * 4, 0.0625 * 18, 0.0625 * 11, 0.0625 * 12);
    public static VoxelShape WESTBB = VoxelShapes.create(-0.0625 * 2, 0.0625 * 3, 0.0625 * 4, 0.0625 * 3, 0.0625 * 11, 0.0625 * 12);

    private boolean fast = false;
    private ItemStackFilter filter;
    private boolean whitelist;
    private List<ItemEntity> items;

    public ConveyorExtractionUpgrade(IBlockContainer container, ConveyorUpgradeFactory factory, Direction side) {
        super(container, factory, side);
        this.filter = new ItemStackFilter(20, 20, 5, 3);
        this.whitelist = false;
        this.items = new ArrayList<>();
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

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = new CompoundNBT();
        compound.putBoolean("fast", fast);
        compound.put("Filter", filter.serializeNBT());
        compound.putBoolean("Whitelist", whitelist);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        fast = nbt.getBoolean("fast");
        if (nbt.contains("Filter")) filter.deserializeNBT(nbt.getCompound("Filter"));
        whitelist = nbt.getBoolean("Whitelist");
    }

    @Override
    public boolean onUpgradeActivated(PlayerEntity player, Hand hand) {
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
                new ItemStack(this.getFactory().getUpgradeItem(), 1),
                new ItemStack(Items.GLOWSTONE_DUST)
        );
    }

    @Override
    public void update() {
        if (getWorld().isRemote)
            return;
        items.removeIf(ItemEntity -> ItemEntity.getItem().isEmpty() || !ItemEntity.isAlive());
        if (items.size() >= 20) return;
        if (getWorld().getGameTime() % (fast ? 10 : 20) == 0) {
            getHandlerCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(itemHandler -> {
                for (int i = 0; i < itemHandler.getSlots(); i++) {
                    ItemStack stack = itemHandler.extractItem(i, 4, true);
                    if (stack.isEmpty() || whitelist != filter.matches(stack))
                        continue;
                    ItemEntity item = new ItemEntity(getWorld(), getPos().getX() + 0.5, getPos().getY() + 0.2, getPos().getZ() + 0.5);
                    item.setMotion(0, -1, 0);
                    item.setPickupDelay(40);
                    item.setItem(itemHandler.extractItem(i, 4, false));
                    if (getWorld().addEntity(item)) {
                        items.add(item);
                    }
                    break;
                }
            });
        }
        if (getContainer() instanceof ConveyorTile) {
            IFluidTank tank = ((ConveyorTile) getContainer()).getTank();
            getHandlerCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(fluidHandler -> {
                if (!fluidHandler.drain(250, IFluidHandler.FluidAction.SIMULATE).isEmpty() && whitelist == filter.matches(fluidHandler.drain(250, IFluidHandler.FluidAction.SIMULATE))) {
                    FluidStack drain = fluidHandler.drain(tank.fill(fluidHandler.drain(250, IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                    if (drain.isEmpty() && drain.getAmount() > 0) getContainer().requestFluidSync();
                }
            });

        }
    }

    private <T> LazyOptional<T> getHandlerCapability(Capability<T> capability) {
        BlockPos offsetPos = getPos().offset(getSide());
        TileEntity tile = getWorld().getTileEntity(offsetPos);
        if (tile != null && tile.getCapability(capability, getSide().getOpposite()).isPresent())
            return tile.getCapability(capability, getSide().getOpposite());
        for (Entity entity : getWorld().getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(offsetPos)/*, EntitySelectors.NOT_SPECTATING*/)) {
            if (entity.getCapability(capability, entity instanceof ServerPlayerEntity ? null : getSide().getOpposite()).isPresent())
                return entity.getCapability(capability, entity instanceof ServerPlayerEntity ? null : getSide().getOpposite());
        }
        return LazyOptional.empty();
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
        public ConveyorUpgrade create(IBlockContainer container, Direction face) {
            return new ConveyorExtractionUpgrade(container, this, face);
        }

        @Override
        @Nonnull
        public ResourceLocation getModel(Direction upgradeSide, Direction conveyorFacing) {
            return new ResourceLocation(Reference.MOD_ID, "block/conveyor_upgrade_extractor_" + upgradeSide.getString().toLowerCase());
        }

        @Nonnull
        @Override
        public ResourceLocation getItemModel() {
            return new ResourceLocation(Reference.MOD_ID, "conveyor_extraction_upgrade");
        }

        @Override
        public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
            TitaniumShapedRecipeBuilder.shapedRecipe(getUpgradeItem())
                    .patternLine("IPI").patternLine("IDI").patternLine("ICI")
                    .key('I', Tags.Items.INGOTS_IRON)
                    .key('P', IndustrialTags.Items.PLASTIC)
                    .key('D', Blocks.DISPENSER)
                    .key('C', ModuleTransportStorage.CONVEYOR)
                    .build(consumer);
        }
    }
}
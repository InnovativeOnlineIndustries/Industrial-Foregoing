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
package com.buuz135.industrial.tile.agriculture;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.item.addon.FortuneAddonItem;
import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.tile.api.IAcceptsFortuneAddon;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.buuz135.industrial.utils.WorkUtils;
import com.robrit.moofluids.common.entity.EntityFluidCow;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.inventory.FluidTankType;

import java.util.List;

public class AnimalResourceHarvesterTile extends WorkingAreaElectricMachine implements IAcceptsFortuneAddon {

    private ItemStackHandler outItems;
    private IFluidTank tank;

    public AnimalResourceHarvesterTile() {
        super(AnimalResourceHarvesterTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        tank = this.addSimpleFluidTank(8000, "tank", EnumDyeColor.WHITE, 50, 25, FluidTankType.OUTPUT, fluidStack -> false, fluidStack -> true);
        outItems = new ItemStackHandler(3 * 4) {
            @Override
            protected void onContentsChanged(int slot) {
                AnimalResourceHarvesterTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(outItems, EnumDyeColor.ORANGE, "Output Items", 18 * 5 + 3, 25, 4, 3) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }
        });
        this.addInventoryToStorage(outItems, "outItems");
    }

    @Override
    public float work() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;
        List<EntityAnimal> animals = this.world.getEntitiesWithinAABB(EntityAnimal.class, getWorkingArea());
        boolean hasWorked = false;
        int fortune = getFortuneLevel();
        for (EntityAnimal living : animals) {
            if (!ItemStackUtils.isInventoryFull(outItems) && living instanceof IShearable && ((IShearable) living).isShearable(new ItemStack(Items.SHEARS), this.world, living.getPosition())) {
                List<ItemStack> stacks = ((IShearable) living).onSheared(new ItemStack(Items.SHEARS), this.world, null, fortune);
                for (ItemStack stack : stacks) {
                    ItemHandlerHelper.insertItem(outItems, stack, false);
                }
                hasWorked = true;
            }
            //Check if the animal is a Fluid Cow from moofluids. If the cow has a different
            //fluid than what is in the tank, skip it
            if (tank.getFluidAmount() <= 7000 &&
                    !(Loader.isModLoaded("moofluids") && tank.getFluidAmount() > 0 &&
                            living instanceof EntityFluidCow &&
                            ((EntityFluidCow) living).getEntityFluid() != tank.getFluid().getFluid())) {
                FakePlayer player = IndustrialForegoing.getFakePlayer(this.world);
                player.setPosition(living.posX, living.posY, living.posZ);
                player.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.BUCKET));
                if (living.processInteract(player, EnumHand.MAIN_HAND)) {
                    ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
                    if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                        IFluidHandlerItem fluidHandlerItem = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
                        tank.fill(fluidHandlerItem.drain(Integer.MAX_VALUE, true), true);
                        hasWorked = true;
                    }
                }
            }
        }
        for (EntitySquid animal : this.world.getEntitiesWithinAABB(EntitySquid.class, getWorkingArea())) {
            if (!ItemStackUtils.isInventoryFull(outItems) && world.rand.nextBoolean() && world.rand.nextBoolean() && world.rand.nextBoolean() && world.rand.nextBoolean()) {
                ItemHandlerHelper.insertItem(outItems, new ItemStack(Items.DYE), false);
                hasWorked = true;
            }
        }
        return hasWorked ? 1 : 0;
    }

    @Override
    protected boolean acceptsFluidItem(ItemStack stack) {
        return ItemStackUtils.acceptsFluidItem(stack);
    }

    @Override
    protected void processFluidItems(ItemStackHandler fluidItems) {
        ItemStackUtils.fillItemFromTank(fluidItems, tank);
    }

    @Override
    public int getFortuneLevel() {
        return hasAddon(FortuneAddonItem.class) ? getAddon(FortuneAddonItem.class).getLevel(getAddonStack(FortuneAddonItem.class)) : 0;
    }
}

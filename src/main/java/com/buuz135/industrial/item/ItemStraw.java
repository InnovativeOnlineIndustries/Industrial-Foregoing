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
package com.buuz135.industrial.item;

import com.buuz135.industrial.api.straw.StrawHandler;
import com.buuz135.industrial.utils.StrawUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ItemStraw extends IFCustomItem {
    public ItemStraw() {
        super("straw");
        setMaxStackSize(1);
    }

    @Override
    @Nonnull
    public ItemStack onItemUseFinish(@Nonnull ItemStack heldStack, World world, EntityLivingBase entity) {
        if (!world.isRemote && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            RayTraceResult result = rayTrace(world, player, true);
            if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos pos = result.getBlockPos();
                IBlockState state = world.getBlockState(pos);
                Block block = state.getBlock();
                Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
                if (fluid != null) {
                    FluidStack stack = new FluidStack(fluid, Fluid.BUCKET_VOLUME);
                    StrawUtils.getStrawHandler(stack).ifPresent(handler -> handler.onDrink(world, pos, stack, player, false));
                    world.setBlockToAir(pos);
                    return heldStack;
                } else if (block.hasTileEntity(state)) {
                    TileEntity tile = world.getTileEntity(pos);
                    if (tile != null) {
                        if (tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
                            IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                            IFluidTankProperties[] fluidTankProperties = handler.getTankProperties();
                            for (IFluidTankProperties properties : fluidTankProperties) {
                                FluidStack stack = properties.getContents();
                                if (stack != null) {
                                    fluid = stack.getFluid();
                                    if (fluid != null && stack.amount >= Fluid.BUCKET_VOLUME) {
                                        FluidStack copiedStack = stack.copy();
                                        copiedStack.amount = Fluid.BUCKET_VOLUME;
                                        FluidStack out = handler.drain(copiedStack, false);
                                        if (out != null && out.amount == 1000) {
                                            StrawUtils.getStrawHandler(stack).ifPresent(strawHandler -> strawHandler.onDrink(world, pos, stack, player, true));
                                            handler.drain(copiedStack, true);
                                            return heldStack;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return super.onItemUseFinish(heldStack, world, entity);
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn) {
        RayTraceResult result = rayTrace(worldIn, playerIn, true);
        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = result.getBlockPos();
            IBlockState state = worldIn.getBlockState(pos);
            Block block = state.getBlock();
            Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
            if (fluid != null) {
                Optional<StrawHandler> handler = StrawUtils.getStrawHandler(new FluidStack(fluid, Fluid.BUCKET_VOLUME));
                if (handler.isPresent())
                    playerIn.setActiveHand(handIn);
                return ActionResult.newResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
            } else if (block.hasTileEntity(state)) {
                TileEntity tile = worldIn.getTileEntity(pos);
                if (tile != null) {
                    if (tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
                        IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                        IFluidTankProperties[] fluidTankProperties = handler.getTankProperties();
                        for (IFluidTankProperties properties : fluidTankProperties) {
                            FluidStack stack = properties.getContents();
                            if (stack != null) {
                                fluid = stack.getFluid();
                                Optional<StrawHandler> strawHandler = StrawUtils.getStrawHandler(stack);
                                if (fluid != null && strawHandler.isPresent() && stack.amount >= Fluid.BUCKET_VOLUME) {
                                    FluidStack out = handler.drain(stack, false);
                                    if (out != null && out.amount >= 1000) {
                                        playerIn.setActiveHand(handIn);
                                        return ActionResult.newResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 30;
    }

    @Override
    @Nonnull
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.DRINK;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add("\"The One Who Codes\"");
    }
}
package com.buuz135.industrial.item;

import com.buuz135.industrial.api.fluid.IFluidDrinkHandler;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class ItemStraw extends IFCustomItem {
    public ItemStraw() {
        super("straw");
        setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack heldStack, World world, EntityLivingBase entity) {
        if (!world.isRemote && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            RayTraceResult result = rayTrace(world, player, true);
            if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
                Map<String, IFluidDrinkHandler> map = StrawUtils.getDrinkHandlers();
                BlockPos pos = result.getBlockPos();
                IBlockState state = world.getBlockState(pos);
                Block block = state.getBlock();
                Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
                if (fluid != null && map.containsKey(fluid.getName())) {
                    map.get(fluid.getName()).onDrink(world, pos, new FluidStack(fluid, Fluid.BUCKET_VOLUME), player, false);
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
                                    if (fluid != null && map.containsKey(fluid.getName()) && stack.amount >= Fluid.BUCKET_VOLUME) {
                                        FluidStack copiedStack = stack.copy();
                                        copiedStack.amount = Fluid.BUCKET_VOLUME;
                                        FluidStack out = handler.drain(copiedStack, false);
                                        if (out != null && out.amount == 1000) {
                                            map.get(fluid.getName()).onDrink(world, pos, out, player, true);
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
            Map<String, IFluidDrinkHandler> map = StrawUtils.getDrinkHandlers();
            BlockPos pos = result.getBlockPos();
            IBlockState state = worldIn.getBlockState(pos);
            Block block = state.getBlock();
            Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
            if (fluid != null && map.containsKey(fluid.getName())) {
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
                                if (fluid != null && map.containsKey(fluid.getName()) && stack.amount >= Fluid.BUCKET_VOLUME) {
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
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.DRINK;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add("\"The One Who Codes\"");
    }
}
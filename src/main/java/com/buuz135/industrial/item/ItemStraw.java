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

package com.buuz135.industrial.item;

import com.buuz135.industrial.api.straw.StrawHandler;
import com.buuz135.industrial.utils.IndustrialTags;
import com.buuz135.industrial.utils.StrawUtils;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

;

public class ItemStraw extends IFCustomItem {

    public ItemStraw(ItemGroup group) {
        super("straw", group, new Properties().maxStackSize(1));
    }

    @Override
    @Nonnull
    public ItemStack onItemUseFinish(@Nonnull ItemStack heldStack, World world, LivingEntity entity) {
        if (!world.isRemote && entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            RayTraceResult result = rayTrace(world, player, RayTraceContext.FluidMode.SOURCE_ONLY);
            if (result != null && result.getType() == RayTraceResult.Type.BLOCK) {
                BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult) result;
                BlockPos pos = blockRayTraceResult.getPos();
                BlockState state = world.getBlockState(pos);
                Block block = state.getBlock();
                FluidState fluidState = state.getFluidState();
                if (fluidState != Fluids.EMPTY.getDefaultState() && block instanceof IBucketPickupHandler && fluidState.isSource()) {
                    StrawUtils.getStrawHandler(fluidState.getFluid()).ifPresent(handler -> {
                        handler.onDrink(world, pos, ((IBucketPickupHandler) block).pickupFluid(world, pos, state), player, false);
                    });
                    return heldStack;
                } /*else if (block.hasTileEntity(state)) {
                    TileEntity tile = world.getTileEntity(pos);
                    if (tile != null) {
                        OptionalCapabilityInstance<IFluidHandler> fluidhandlercap= tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
                        if (fluidhandlercap.isPresent()) {
                            IFluidHandler handler = fluidhandlercap.orElseThrow(RuntimeException::new);
                            IFluidTankProperties[] fluidTankProperties = handler.getTankProperties();
                            for (IFluidTankProperties properties : fluidTankProperties) {
                                FluidStack stack = properties.getContents();
                                if (stack != null) {
                                    Fluid fluid = stack.getFluid();
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
                }*/
            }
        }
        return super.onItemUseFinish(heldStack, world, entity);
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, @Nonnull Hand handIn) {
        RayTraceResult result = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY);
        if (result != null && result.getType() == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult) result;
            BlockPos pos = blockRayTraceResult.getPos();
            BlockState state = worldIn.getBlockState(pos);
            Block block = state.getBlock();
            FluidState fluid = state.getFluidState();//FluidRegistry.lookupFluidForBlock(block);
            if (fluid != null) {
                Optional<StrawHandler> handler = StrawUtils.getStrawHandler(fluid.getFluid());
                if (handler.isPresent()) {
                    playerIn.setActiveHand(handIn);
                    return ActionResult.resultSuccess(playerIn.getHeldItem(handIn)); //success accepted
                }
            }/*
            if (block.hasTileEntity(state)) {
                TileEntity tile = worldIn.getTileEntity(pos);
                if (tile != null) {
                    OptionalCapabilityInstance<IFluidHandler> fluidhandlercap = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
                    if (fluidhandlercap.isPresent()) {
                        IFluidHandler handler = fluidhandlercap.orElseThrow(RuntimeException::new);
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
                                        return ActionResult.newResult(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
                                    }
                                }
                            }
                        }
                    }
                }
            }*/
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public int getUseDuration(ItemStack p_77626_1_) {
        return 30;
    }

    @Override
    public UseAction getUseAction(ItemStack p_77661_1_) {
        return UseAction.DRINK;
    }

    @Override
    public boolean hasTooltipDetails(@Nullable Key key) {
        return true;
    }

    @Override
    public void addTooltipDetails(@Nullable Key key, ItemStack stack, List<ITextComponent> tooltip, boolean advanced) {
        super.addTooltipDetails(key, stack, tooltip, advanced);
        tooltip.add(new StringTextComponent(TextFormatting.GRAY + "\"The One Who Codes\""));
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("PP ").patternLine(" P ").patternLine(" P ")
                .key('P', IndustrialTags.Items.PLASTIC)
                .build(consumer);
    }
}
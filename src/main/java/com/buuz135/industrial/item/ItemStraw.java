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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

;

public class ItemStraw extends IFCustomItem {

    public ItemStraw(CreativeModeTab group) {
        super("straw", group, new Properties().stacksTo(1));
    }

    @Override
    @Nonnull
    public ItemStack finishUsingItem(@Nonnull ItemStack heldStack, Level world, LivingEntity entity) {
        if (!world.isClientSide && entity instanceof Player) {
            Player player = (Player) entity;
            HitResult result = getPlayerPOVHitResult(world, player, ClipContext.Fluid.SOURCE_ONLY);
            if (result != null && result.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockRayTraceResult = (BlockHitResult) result;
                BlockPos pos = blockRayTraceResult.getBlockPos();
                BlockState state = world.getBlockState(pos);
                Block block = state.getBlock();
                FluidState fluidState = state.getFluidState();
                if (fluidState != Fluids.EMPTY.defaultFluidState() && block instanceof BucketPickup && fluidState.isSource()) {
                    StrawUtils.getStrawHandler(fluidState.getType()).ifPresent(handler -> {
                        // TODO: 26/07/2021 Fix
//                        handler.onDrink(world, pos, ((BucketPickup) block).pickupBlock(world, pos, state), player, false);
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
        return super.finishUsingItem(heldStack, world, entity);
    }

    @Override
    @Nonnull
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, @Nonnull InteractionHand handIn) {
        HitResult result = getPlayerPOVHitResult(worldIn, playerIn, ClipContext.Fluid.SOURCE_ONLY);
        if (result != null && result.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockRayTraceResult = (BlockHitResult) result;
            BlockPos pos = blockRayTraceResult.getBlockPos();
            BlockState state = worldIn.getBlockState(pos);
            Block block = state.getBlock();
            FluidState fluid = state.getFluidState();//FluidRegistry.lookupFluidForBlock(block);
            if (fluid != null) {
                Optional<StrawHandler> handler = StrawUtils.getStrawHandler(fluid.getType());
                if (handler.isPresent()) {
                    playerIn.startUsingItem(handIn);
                    return InteractionResultHolder.success(playerIn.getItemInHand(handIn)); //success accepted
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
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public int getUseDuration(ItemStack p_77626_1_) {
        return 30;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack p_77661_1_) {
        return UseAnim.DRINK;
    }

    @Override
    public boolean hasTooltipDetails(@Nullable Key key) {
        return true;
    }

    @Override
    public void addTooltipDetails(@Nullable Key key, ItemStack stack, List<Component> tooltip, boolean advanced) {
        super.addTooltipDetails(key, stack, tooltip, advanced);
        tooltip.add(new TextComponent(ChatFormatting.GRAY + "\"The One Who Codes\""));
    }

    @Override
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .pattern("PP ").pattern(" P ").pattern(" P ")
                .define('P', IndustrialTags.Items.PLASTIC)
                .save(consumer);
    }
}

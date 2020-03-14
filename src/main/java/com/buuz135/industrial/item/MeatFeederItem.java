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

import com.buuz135.industrial.module.ModuleCore;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class MeatFeederItem extends IFCustomItem {

    public MeatFeederItem(ItemGroup group) {
        super("meat_feeder", group, new Properties().maxStackSize(1));
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        FluidHandlerItemStack handlerItemStack = new FluidHandlerItemStack(stack, 128000) {
            @Override
            public boolean canFillFluidType(FluidStack fluid) {
                return fluid.getFluid().isEquivalentTo(ModuleCore.MEAT.getSourceFluid());
            }
        };
        handlerItemStack.fill(new FluidStack(ModuleCore.MEAT.getSourceFluid(), 0), IFluidHandler.FluidAction.EXECUTE);
        return handlerItemStack;
    }

    @Override
    public boolean hasTooltipDetails(@Nullable Key key) {
        return key == null;
    }

    @Override
    public void addTooltipDetails(@Nullable Key key, ItemStack stack, List<ITextComponent> tooltip, boolean advanced) {
        super.addTooltipDetails(key, stack, tooltip, advanced);
        //stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(iFluidHandlerItem -> {
        //    tooltip.add(new StringTextComponent(iFluidHandlerItem.getFluidInTank(0).getAmount() + "/" + iFluidHandlerItem.getTankCapacity(0) + "mb of Meat"));
        //})
        //;
        if (stack.getTag() != null && key == null) {
            tooltip.add(new StringTextComponent(NumberFormat.getNumberInstance(Locale.ROOT).format(stack.getTag().getCompound("Fluid").getInt("Amount")) + TextFormatting.GOLD + "/" + TextFormatting.WHITE + NumberFormat.getNumberInstance(Locale.ROOT).format(128000) + TextFormatting.GOLD + "mb of Meat"));
        }
    }

    public int getFilledAmount(ItemStack stack) {
        FluidHandlerItemStack handlerItemStack = (FluidHandlerItemStack) stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElseThrow(RuntimeException::new);
        return (handlerItemStack.getFluid() == null ? 0 : handlerItemStack.getFluid().getAmount());
    }

    public void drain(ItemStack stack, int amount) {
        FluidHandlerItemStack handlerItemStack = (FluidHandlerItemStack) stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElseThrow(RuntimeException::new);
        handlerItemStack.drain(new FluidStack(ModuleCore.MEAT.getSourceFluid(), amount), IFluidHandler.FluidAction.EXECUTE);
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("pip").patternLine("gig").patternLine(" i ")
                .key('p', ModuleCore.PLASTIC)
                .key('i', Tags.Items.INGOTS_IRON)
                .key('g', Items.GLASS_BOTTLE)
                .build(consumer);
    }
}

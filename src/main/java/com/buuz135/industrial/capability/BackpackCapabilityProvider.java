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
package com.buuz135.industrial.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.buuz135.industrial.item.infinity.InfinityCapabilityProvider;
import com.buuz135.industrial.item.infinity.InfinityEnergyStorage;
import com.buuz135.industrial.worlddata.BackpackDataManager;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.capability.FluidHandlerScreenProviderItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BackpackCapabilityProvider extends InfinityCapabilityProvider {

    private LazyOptional<IItemHandler> itemHandlerLazyOptional;

    public BackpackCapabilityProvider(ItemStack stack, IFactory<? extends FluidHandlerScreenProviderItemStack> tankFactory, IFactory<InfinityEnergyStorage> energyFactory) {
        super(stack, tankFactory, energyFactory);
    }

    @Nullable
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
//        if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY == capability) {
//            if (itemHandlerLazyOptional == null && this.getStack().hasTag()){
//                String id = this.getStack().getTag().getString("Id");
//                if (BackpackDataManager.CLIENT_SIDE_BACKPACKS.containsKey(id)){
//                    itemHandlerLazyOptional = LazyOptional.of(() -> BackpackDataManager.CLIENT_SIDE_BACKPACKS.get(id));
//                } else if (ServerLifecycleHooks.getCurrentServer() != null){
//                    BackpackDataManager manager = BackpackDataManager.getData(ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD));
//                    if (manager != null){
//                        BackpackDataManager.BackpackItemHandler backpack = manager.getBackpack(id);
//                        if (backpack != null){
//                            itemHandlerLazyOptional = LazyOptional.of(() -> backpack);
//                        }
//                    }
//                }
//            }
//            if (itemHandlerLazyOptional != null){
//                return itemHandlerLazyOptional.cast();
//            }
//        }
        return super.getCapability(capability, facing);
    }
}

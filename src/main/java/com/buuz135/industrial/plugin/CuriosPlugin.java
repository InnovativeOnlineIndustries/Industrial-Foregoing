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

package com.buuz135.industrial.plugin;

import com.buuz135.industrial.module.ModuleTool;
import com.hrznstudio.titanium.annotation.plugin.FeaturePlugin;
import com.hrznstudio.titanium.network.locator.PlayerInventoryFinder;
import com.hrznstudio.titanium.plugin.FeaturePluginInstance;
import com.hrznstudio.titanium.plugin.PluginPhase;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Optional;

@FeaturePlugin(value = "curios", type = FeaturePlugin.FeaturePluginType.MOD)
public class CuriosPlugin implements FeaturePluginInstance {

    public static final String CURIOS = "curios";


    public static ItemStack getStack(LivingEntity entity, String preset, int index) {
        Optional<ICuriosItemHandler> maybeCuriosInventory = CuriosApi.getCuriosInventory(entity);
        return maybeCuriosInventory.map(iCuriosItemHandler -> {
            for (ICurioStacksHandler value : iCuriosItemHandler.getCurios().values()) {
                if (value != null) {
                    for (int i = 0; i < value.getStacks().getSlots(); i++) {
                        if (value.getStacks().getStackInSlot(i).is(ModuleTool.INFINITY_BACKPACK)) {
                            return value.getStacks().getStackInSlot(i);
                        }
                    }
                }
            }
            return ItemStack.EMPTY;
        }).orElse(ItemStack.EMPTY);
    }

    public static void setStack(LivingEntity entity, String preset, int index, ItemStack stack) {
        CuriosApi.getCuriosInventory(entity).map(iCuriosItemHandler -> iCuriosItemHandler.getStacksHandler(preset)).ifPresent(iCurioStacksHandler -> iCurioStacksHandler.get().getStacks().setStackInSlot(index, stack));
    }

    @Override
    public void execute(PluginPhase phase) {
        if (phase == PluginPhase.CONSTRUCTION) {
            /*EventManager.forgeGeneric(AttachCapabilitiesEvent.class, ItemStack.class).process(event -> {
                AttachCapabilitiesEvent<ItemStack> stackEvent = (AttachCapabilitiesEvent<ItemStack>) event;
                ItemStack stack = stackEvent.getObject();
                if (stack.getItem() instanceof MeatFeederItem) {
                    stackEvent.addCapability(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath() + "_curios"), new ICapabilityProvider() {
                        @Override
                        public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
                            if (cap == CuriosCapability.ITEM) return LazyOptional.of(MeatFeedCurios::new).cast();
                            return LazyOptional.empty();
                        }
                    });
                }
                if (stack.getItem() instanceof ItemInfinityBackpack) {
                    stackEvent.addCapability(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath() + "_curios"), new ICapabilityProvider() {
                        @Override
                        public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
                            if (cap == CuriosCapability.ITEM)
                                return LazyOptional.of(InfinityBackpackCurios::new).cast();
                            return LazyOptional.empty();
                        }
                    });
                }
            }).subscribe();*/
            PlayerInventoryFinder.FINDERS.put(CURIOS, new PlayerInventoryFinder(playerEntity -> 1, (playerEntity, integer) -> getStack(playerEntity, "back", 0), (playerEntity, slot, stack) -> setStack(playerEntity, "back", slot, stack)));
        }
    }

}

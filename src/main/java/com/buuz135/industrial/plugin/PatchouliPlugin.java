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

import com.buuz135.industrial.item.MeatFeederItem;
import com.buuz135.industrial.item.infinity.item.ItemInfinityBackpack;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.plugin.curios.InfinityBackpackCurios;
import com.buuz135.industrial.plugin.curios.MeatFeedCurios;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.annotation.plugin.FeaturePlugin;
import com.hrznstudio.titanium.api.ISpecialCreativeTabItem;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.network.locator.PlayerInventoryFinder;
import com.hrznstudio.titanium.plugin.FeaturePluginInstance;
import com.hrznstudio.titanium.plugin.PluginPhase;
import com.hrznstudio.titanium.tab.TitaniumTab;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

import javax.annotation.Nullable;

@FeaturePlugin(value = "patchouli", type = FeaturePlugin.FeaturePluginType.MOD)
public class PatchouliPlugin implements FeaturePluginInstance {


    @Override
    public void execute(PluginPhase phase) {
        if (phase == PluginPhase.CONSTRUCTION) {
            EventManager.mod(BuildCreativeModeTabContentsEvent.class).process(buildCreativeModeTabContentsEvent -> {
                if (ModuleCore.TAB_CORE.getResourceLocation().equals(buildCreativeModeTabContentsEvent.getTabKey().location())){
                    var item = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("patchouli", "guide_book")));
                    item.getOrCreateTag().putString("patchouli:book", "industrialforegoing:industrial_foregoing");
                    buildCreativeModeTabContentsEvent.accept(item);
                }
            }).subscribe();
        }
    }


}

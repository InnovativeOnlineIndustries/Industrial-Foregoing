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

package com.buuz135.industrial.module;

import com.buuz135.industrial.entity.InfinityLauncherProjectileEntity;
import com.buuz135.industrial.entity.InfinityNukeEntity;
import com.buuz135.industrial.entity.InfinityTridentEntity;
import com.buuz135.industrial.item.MeatFeederItem;
import com.buuz135.industrial.item.MobImprisonmentToolItem;
import com.buuz135.industrial.item.infinity.item.*;
import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.capability.CapabilityItemStackHolder;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.itemstack.ItemStackHarness;
import com.hrznstudio.titanium.itemstack.ItemStackHarnessRegistry;
import com.hrznstudio.titanium.module.DeferredRegistryHelper;
import com.hrznstudio.titanium.network.IButtonHandler;
import com.hrznstudio.titanium.tab.TitaniumTab;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModuleTool implements IModule {

    public static TitaniumTab TAB_TOOL = new TitaniumTab(new ResourceLocation(Reference.MOD_ID , "tool"));

    public static RegistryObject<Item> MEAT_FEEDER;
    public static RegistryObject<Item> MOB_IMPRISONMENT_TOOL;
    public static RegistryObject<Item> INFINITY_DRILL;
    public static RegistryObject<Item> MOB_ESSENCE_TOOL;
    public static RegistryObject<Item> INFINITY_SAW;
    public static RegistryObject<Item> INFINITY_HAMMER;
    public static RegistryObject<Item> INFINITY_TRIDENT;
    public static RegistryObject<Item> INFINITY_BACKPACK;
    public static RegistryObject<Item> INFINITY_LAUNCHER;
    public static RegistryObject<SoundEvent> NUKE_CHARGING;
    public static RegistryObject<SoundEvent> NUKE_ARMING;
    public static RegistryObject<SoundEvent> NUKE_EXPLOSION;

    public static RegistryObject<EntityType<?>> TRIDENT_ENTITY_TYPE;
    public static RegistryObject<EntityType<?>> INFINITY_LAUNCHER_PROJECTILE_ENTITY_TYPE;

    public static RegistryObject<Item> INFINITY_NUKE;
    public static RegistryObject<EntityType<?>> INFINITY_NUKE_ENTITY_TYPE;

    @Override
    public void generateFeatures(DeferredRegistryHelper registryHelper) {
        MEAT_FEEDER = registryHelper.registerGeneric(ForgeRegistries.ITEMS.getRegistryKey(), "meat_feeder", () -> new MeatFeederItem(TAB_TOOL));
        MOB_IMPRISONMENT_TOOL = registryHelper.registerGeneric(ForgeRegistries.ITEMS.getRegistryKey(), "mob_imprisonment_tool", () -> new MobImprisonmentToolItem(TAB_TOOL));
        INFINITY_DRILL = registryHelper.registerGeneric(ForgeRegistries.ITEMS.getRegistryKey(), "infinity_drill", () -> new ItemInfinityDrill(TAB_TOOL));
        //features.add(Feature.builder("mob_essence_tool").content(ForgeRegistries.ITEMS.getRegistryKey(), MOB_ESSENCE_TOOL = new MobEssenceToolItem(TAB_TOOL)));
        INFINITY_SAW = registryHelper.registerGeneric(ForgeRegistries.ITEMS.getRegistryKey(), "infinity_saw", () -> new ItemInfinitySaw(TAB_TOOL));
        INFINITY_HAMMER = registryHelper.registerGeneric(ForgeRegistries.ITEMS.getRegistryKey(), "infinity_hammer", () -> new ItemInfinityHammer(TAB_TOOL));
        INFINITY_TRIDENT = registryHelper.registerGeneric(ForgeRegistries.ITEMS.getRegistryKey(), "infinity_trident", () -> new ItemInfinityTrident(TAB_TOOL));
        TRIDENT_ENTITY_TYPE = registryHelper.registerEntityType("trident_entity", () -> EntityType.Builder.<InfinityTridentEntity>of(InfinityTridentEntity::new, MobCategory.MISC).sized(0.5F, 0.5F)
                .setShouldReceiveVelocityUpdates(true)
                .setCustomClientFactory((spawnEntity, world) -> new InfinityTridentEntity((EntityType<? extends InfinityTridentEntity>) TRIDENT_ENTITY_TYPE.get(), world)).clientTrackingRange(4).updateInterval(20).build("trident_entity"));
        INFINITY_BACKPACK = registryHelper.registerGeneric(ForgeRegistries.ITEMS.getRegistryKey(), "infinity_backpack", () -> new ItemInfinityBackpack());
        INFINITY_LAUNCHER = registryHelper.registerGeneric(ForgeRegistries.ITEMS.getRegistryKey(), "infinity_launcher", () -> new ItemInfinityLauncher(TAB_TOOL));
        INFINITY_LAUNCHER_PROJECTILE_ENTITY_TYPE = registryHelper.registerEntityType("launcher_projectile_entity", () -> EntityType.Builder.<InfinityLauncherProjectileEntity>of(InfinityLauncherProjectileEntity::new, MobCategory.MISC).sized(0.5F, 0.5F)
                .setShouldReceiveVelocityUpdates(true)
                .setCustomClientFactory((spawnEntity, world) -> new InfinityLauncherProjectileEntity((EntityType<? extends InfinityLauncherProjectileEntity>) INFINITY_LAUNCHER_PROJECTILE_ENTITY_TYPE.get(), world)).clientTrackingRange(4).updateInterval(20).build("launcher_projectile_entity"));
        INFINITY_NUKE = registryHelper.registerGeneric(ForgeRegistries.ITEMS.getRegistryKey(), "infinity_nuke", () -> new ItemInfinityNuke(TAB_TOOL));
        INFINITY_NUKE_ENTITY_TYPE = registryHelper.registerEntityType("infinity_nuke", () -> EntityType.Builder.<InfinityNukeEntity>of(InfinityNukeEntity::new, MobCategory.MISC).sized(0.5F, 1.5F)
                .setShouldReceiveVelocityUpdates(true)
                .setCustomClientFactory((spawnEntity, world) -> new InfinityNukeEntity((EntityType<? extends InfinityNukeEntity>) INFINITY_NUKE_ENTITY_TYPE.get(), world)).fireImmune().clientTrackingRange(8).updateInterval(20).build("infinity_nuke"));
        NUKE_CHARGING = registryHelper.registerGeneric(ForgeRegistries.SOUND_EVENTS.getRegistryKey(), "nuke_charging", () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(Reference.MOD_ID, "nuke_charging"), 128));
        NUKE_ARMING = registryHelper.registerGeneric(ForgeRegistries.SOUND_EVENTS.getRegistryKey(), "nuke_arming", () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(Reference.MOD_ID, "nuke_arming"), 16));
        NUKE_EXPLOSION = registryHelper.registerGeneric(ForgeRegistries.SOUND_EVENTS.getRegistryKey(), "nuke_explosion", () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(Reference.MOD_ID, "nuke_explosion"), 128));

        ItemStackHarnessRegistry.register(INFINITY_SAW, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), ForgeCapabilities.ENERGY, ForgeCapabilities.FLUID_HANDLER_ITEM, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));
        ItemStackHarnessRegistry.register(INFINITY_DRILL, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), ForgeCapabilities.ENERGY, ForgeCapabilities.FLUID_HANDLER_ITEM, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));
        ItemStackHarnessRegistry.register(INFINITY_HAMMER, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), ForgeCapabilities.ENERGY, ForgeCapabilities.FLUID_HANDLER_ITEM, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));
        ItemStackHarnessRegistry.register(INFINITY_TRIDENT, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), ForgeCapabilities.ENERGY, ForgeCapabilities.FLUID_HANDLER_ITEM, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));
        ItemStackHarnessRegistry.register(INFINITY_TRIDENT, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), ForgeCapabilities.ENERGY, ForgeCapabilities.FLUID_HANDLER_ITEM, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));
        ItemStackHarnessRegistry.register(INFINITY_BACKPACK, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), ForgeCapabilities.ENERGY, ForgeCapabilities.FLUID_HANDLER_ITEM, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));
        ItemStackHarnessRegistry.register(INFINITY_LAUNCHER, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), ForgeCapabilities.ENERGY, ForgeCapabilities.FLUID_HANDLER_ITEM, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));
        ItemStackHarnessRegistry.register(INFINITY_NUKE, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), ForgeCapabilities.ENERGY, ForgeCapabilities.FLUID_HANDLER_ITEM, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));
        EventManager.forge(BlockEvent.BreakEvent.class).filter(breakEvent -> breakEvent.getPlayer().getMainHandItem().getItem() == INFINITY_SAW.get() && BlockUtils.isLog((Level) breakEvent.getLevel(), breakEvent.getPos())).process(breakEvent -> {
            breakEvent.setCanceled(true);
            breakEvent.getPlayer().getMainHandItem().mineBlock((Level) breakEvent.getLevel(), breakEvent.getState(), breakEvent.getPos(), breakEvent.getPlayer());
        }).subscribe();

    }
}

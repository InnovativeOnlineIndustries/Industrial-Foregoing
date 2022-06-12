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
import com.buuz135.industrial.item.MobEssenceToolItem;
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
import com.hrznstudio.titanium.tab.AdvancedTitaniumTab;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class ModuleTool implements IModule {

    public static AdvancedTitaniumTab TAB_TOOL = new AdvancedTitaniumTab(Reference.MOD_ID + "_tool", true);

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

    public static RegistryObject<EntityType<?>> TRIDENT_ENTITY_TYPE;
    public static RegistryObject<EntityType<?>> INFINITY_LAUNCHER_PROJECTILE_ENTITY_TYPE;

    public static RegistryObject<Item> INFINITY_NUKE;
    public static RegistryObject<EntityType<?>> INFINITY_NUKE_ENTITY_TYPE;

    @Override
    public void generateFeatures(DeferredRegistryHelper registryHelper) {
        MEAT_FEEDER  = registryHelper.registerGeneric(Item.class, "meat_feeder", () -> new MeatFeederItem(TAB_TOOL));
        MOB_IMPRISONMENT_TOOL  = registryHelper.registerGeneric(Item.class, "mob_imprisonment_tool", () -> new MobImprisonmentToolItem(TAB_TOOL));
        INFINITY_DRILL  = registryHelper.registerGeneric(Item.class, "infinity_drill", () -> new ItemInfinityDrill(TAB_TOOL));
        //features.add(Feature.builder("mob_essence_tool").content(Item.class, MOB_ESSENCE_TOOL = new MobEssenceToolItem(TAB_TOOL)));
        INFINITY_SAW  = registryHelper.registerGeneric(Item.class, "infinity_saw", () -> new ItemInfinitySaw(TAB_TOOL));
        INFINITY_HAMMER  = registryHelper.registerGeneric(Item.class, "infinity_hammer", () -> new ItemInfinityHammer(TAB_TOOL));
        INFINITY_TRIDENT  = registryHelper.registerGeneric(Item.class, "infinity_trident", () -> new ItemInfinityTrident(TAB_TOOL));
        TRIDENT_ENTITY_TYPE =  registryHelper.registerEntityType("trident_entity", () ->  EntityType.Builder.<InfinityTridentEntity>of(InfinityTridentEntity::new, MobCategory.MISC).sized(0.5F, 0.5F)
                .setShouldReceiveVelocityUpdates(true)
                .setCustomClientFactory((spawnEntity, world) -> new InfinityTridentEntity((EntityType<? extends InfinityTridentEntity>) TRIDENT_ENTITY_TYPE.get(), world)).clientTrackingRange(4).updateInterval(20).build("trident_entity"));
        INFINITY_BACKPACK  = registryHelper.registerGeneric(Item.class, "infinity_backpack", () -> new ItemInfinityBackpack());
        INFINITY_LAUNCHER  = registryHelper.registerGeneric(Item.class, "infinity_launcher", () -> new ItemInfinityLauncher(TAB_TOOL));
        INFINITY_LAUNCHER_PROJECTILE_ENTITY_TYPE = registryHelper.registerEntityType("launcher_projectile_entity",  () -> EntityType.Builder.<InfinityLauncherProjectileEntity>of(InfinityLauncherProjectileEntity::new, MobCategory.MISC).sized(0.5F, 0.5F)
                .setShouldReceiveVelocityUpdates(true)
                .setCustomClientFactory((spawnEntity, world) -> new InfinityLauncherProjectileEntity((EntityType<? extends InfinityLauncherProjectileEntity>) INFINITY_LAUNCHER_PROJECTILE_ENTITY_TYPE.get(), world)).clientTrackingRange(4).updateInterval(20).build("launcher_projectile_entity"));
        INFINITY_NUKE  = registryHelper.registerGeneric(Item.class, "infinity_nuke", () -> new ItemInfinityNuke(TAB_TOOL));
        INFINITY_NUKE_ENTITY_TYPE = registryHelper.registerEntityType("infinity_nuke", () -> EntityType.Builder.<InfinityNukeEntity>of(InfinityNukeEntity::new, MobCategory.MISC).sized(0.5F, 1.5F)
                .setShouldReceiveVelocityUpdates(true)
                .setCustomClientFactory((spawnEntity, world) -> new InfinityNukeEntity((EntityType<? extends InfinityNukeEntity>) INFINITY_NUKE_ENTITY_TYPE.get(), world)).fireImmune().clientTrackingRange(8).updateInterval(20).build("infinity_nuke"));
        NUKE_CHARGING = registryHelper.registerGeneric(SoundEvent.class, "nuke_charging", () -> new SoundEvent(new ResourceLocation(Reference.MOD_ID, "nuke_charging")));
        TAB_TOOL.addIconStack(() -> new ItemStack(INFINITY_DRILL.orElse(Items.STONE)));
        ItemStackHarnessRegistry.register(INFINITY_SAW, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), CapabilityEnergy.ENERGY, CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));
        ItemStackHarnessRegistry.register(INFINITY_DRILL, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), CapabilityEnergy.ENERGY, CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));
        ItemStackHarnessRegistry.register(INFINITY_HAMMER, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), CapabilityEnergy.ENERGY, CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));
        ItemStackHarnessRegistry.register(INFINITY_TRIDENT, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), CapabilityEnergy.ENERGY, CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));
        ItemStackHarnessRegistry.register(INFINITY_TRIDENT, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), CapabilityEnergy.ENERGY, CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));
        ItemStackHarnessRegistry.register(INFINITY_BACKPACK, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), CapabilityEnergy.ENERGY, CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));
        ItemStackHarnessRegistry.register(INFINITY_LAUNCHER, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), CapabilityEnergy.ENERGY, CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));
        ItemStackHarnessRegistry.register(INFINITY_NUKE, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), CapabilityEnergy.ENERGY, CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));
        EventManager.forge(BlockEvent.BreakEvent.class).filter(breakEvent -> breakEvent.getPlayer().getMainHandItem().getItem() == INFINITY_SAW.get() && BlockUtils.isLog((Level) breakEvent.getWorld(), breakEvent.getPos())).process(breakEvent -> {
            breakEvent.setCanceled(true);
            breakEvent.getPlayer().getMainHandItem().mineBlock((Level) breakEvent.getWorld(), breakEvent.getState(), breakEvent.getPos(), breakEvent.getPlayer());
        }).subscribe();

    }
}

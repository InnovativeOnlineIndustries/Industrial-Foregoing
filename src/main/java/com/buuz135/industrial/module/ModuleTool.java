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
import com.hrznstudio.titanium.module.Feature;
import com.hrznstudio.titanium.network.IButtonHandler;
import com.hrznstudio.titanium.tab.AdvancedTitaniumTab;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import java.util.ArrayList;
import java.util.List;

public class ModuleTool implements IModule {

    public static AdvancedTitaniumTab TAB_TOOL = new AdvancedTitaniumTab(Reference.MOD_ID + "_tool", true);

    public static MeatFeederItem MEAT_FEEDER;
    public static MobImprisonmentToolItem MOB_IMPRISONMENT_TOOL;
    public static ItemInfinityDrill INFINITY_DRILL;
    public static MobEssenceToolItem MOB_ESSENCE_TOOL;
    public static ItemInfinitySaw INFINITY_SAW;
    public static ItemInfinityHammer INFINITY_HAMMER;
    public static ItemInfinityTrident INFINITY_TRIDENT;
    public static ItemInfinityBackpack INFINITY_BACKPACK;
    public static ItemInfinityLauncher INFINITY_LAUNCHER;
    public static final SoundEvent NUKE_CHARGING = new SoundEvent(new ResourceLocation(Reference.MOD_ID, "nuke_charging")).setRegistryName(new ResourceLocation(Reference.MOD_ID, "nuke_charging"));

    public static EntityType<InfinityTridentEntity> TRIDENT_ENTITY_TYPE;
    public static EntityType<InfinityLauncherProjectileEntity> INFINITY_LAUNCHER_PROJECTILE_ENTITY_TYPE;

    public static ItemInfinityNuke INFINITY_NUKE;
    public static EntityType<InfinityNukeEntity> INFINITY_NUKE_ENTITY_TYPE;

    static {
        TRIDENT_ENTITY_TYPE = (EntityType<InfinityTridentEntity>) EntityType.Builder.<InfinityTridentEntity>of(InfinityTridentEntity::new, MobCategory.MISC).sized(0.5F, 0.5F)
                .setShouldReceiveVelocityUpdates(true)
                .setCustomClientFactory((spawnEntity, world) -> new InfinityTridentEntity(TRIDENT_ENTITY_TYPE, world)).clientTrackingRange(4).updateInterval(20).build("trident_entity").setRegistryName(Reference.MOD_ID, "trident_entity");
        INFINITY_LAUNCHER_PROJECTILE_ENTITY_TYPE = (EntityType<InfinityLauncherProjectileEntity>) EntityType.Builder.<InfinityLauncherProjectileEntity>of(InfinityLauncherProjectileEntity::new, MobCategory.MISC).sized(0.5F, 0.5F)
                .setShouldReceiveVelocityUpdates(true)
                .setCustomClientFactory((spawnEntity, world) -> new InfinityLauncherProjectileEntity(INFINITY_LAUNCHER_PROJECTILE_ENTITY_TYPE, world)).clientTrackingRange(4).updateInterval(20).build("launcher_projectile_entity").setRegistryName(Reference.MOD_ID, "launcher_projectile_entity");
        INFINITY_NUKE_ENTITY_TYPE = (EntityType<InfinityNukeEntity>) EntityType.Builder.<InfinityNukeEntity>of(InfinityNukeEntity::new, MobCategory.MISC).sized(0.5F, 1.5F)
                .setShouldReceiveVelocityUpdates(true)
                .setCustomClientFactory((spawnEntity, world) -> new InfinityNukeEntity(INFINITY_NUKE_ENTITY_TYPE, world)).fireImmune().clientTrackingRange(8).updateInterval(20).noSummon().build("infinity_nuke").setRegistryName(Reference.MOD_ID, "infinity_nuke");
    }

    @Override
    public List<Feature.Builder> generateFeatures() {
        List<Feature.Builder> features = new ArrayList<>();
        features.add(Feature.builder("meat_feeder").content(Item.class, MEAT_FEEDER = new MeatFeederItem(TAB_TOOL)));
        features.add(Feature.builder("mob_imprisonment_tool").content(Item.class, MOB_IMPRISONMENT_TOOL = new MobImprisonmentToolItem(TAB_TOOL)));
        features.add(Feature.builder("infinity_drill").content(Item.class, INFINITY_DRILL = new ItemInfinityDrill(TAB_TOOL)));
        //features.add(Feature.builder("mob_essence_tool").content(Item.class, MOB_ESSENCE_TOOL = new MobEssenceToolItem(TAB_TOOL)));
        features.add(Feature.builder("infinity_saw").content(Item.class, INFINITY_SAW = new ItemInfinitySaw(TAB_TOOL)).event(EventManager.forge(BlockEvent.BreakEvent.class).filter(breakEvent -> breakEvent.getPlayer().getMainHandItem().getItem() == INFINITY_SAW && BlockUtils.isLog((Level) breakEvent.getWorld(), breakEvent.getPos())).process(breakEvent -> {
            breakEvent.setCanceled(true);
            breakEvent.getPlayer().getMainHandItem().mineBlock((Level) breakEvent.getWorld(), breakEvent.getState(), breakEvent.getPos(), breakEvent.getPlayer());
        })));
        features.add(createFeature(INFINITY_HAMMER = new ItemInfinityHammer(TAB_TOOL)));
        features.add(Feature.builder("infinity_trident")
                .content(Item.class, INFINITY_TRIDENT = new ItemInfinityTrident(TAB_TOOL))
                .content(EntityType.class, (EntityType) TRIDENT_ENTITY_TYPE));
        features.add(createFeature(INFINITY_BACKPACK = new ItemInfinityBackpack()));
        features.add(Feature.builder("infinity_launcher")
                .content(Item.class, INFINITY_LAUNCHER = new ItemInfinityLauncher(TAB_TOOL))
                .content(EntityType.class, (EntityType) INFINITY_LAUNCHER_PROJECTILE_ENTITY_TYPE)
        );
        features.add(Feature.builder("infinity_nuke")
                .content(Item.class, INFINITY_NUKE = new ItemInfinityNuke(TAB_TOOL))
                .content(EntityType.class, (EntityType) INFINITY_NUKE_ENTITY_TYPE)
        );
        TAB_TOOL.addIconStack(new ItemStack(INFINITY_DRILL));
        ItemStackHarnessRegistry.register(INFINITY_SAW, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), CapabilityEnergy.ENERGY, CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));
        ItemStackHarnessRegistry.register(INFINITY_DRILL, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), CapabilityEnergy.ENERGY, CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));
        ItemStackHarnessRegistry.register(INFINITY_HAMMER, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), CapabilityEnergy.ENERGY, CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));
        ItemStackHarnessRegistry.register(INFINITY_TRIDENT, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), CapabilityEnergy.ENERGY, CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));
        ItemStackHarnessRegistry.register(INFINITY_TRIDENT, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), CapabilityEnergy.ENERGY, CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));
        ItemStackHarnessRegistry.register(INFINITY_BACKPACK, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), CapabilityEnergy.ENERGY, CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));
        ItemStackHarnessRegistry.register(INFINITY_LAUNCHER, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), CapabilityEnergy.ENERGY, CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));
        ItemStackHarnessRegistry.register(INFINITY_NUKE, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), CapabilityEnergy.ENERGY, CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));
        return features;
    }
}

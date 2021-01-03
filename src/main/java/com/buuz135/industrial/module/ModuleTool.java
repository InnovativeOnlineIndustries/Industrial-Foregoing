package com.buuz135.industrial.module;

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
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
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

    public static EntityType<InfinityTridentEntity> TRIDENT_ENTITY_TYPE;

    static {
        TRIDENT_ENTITY_TYPE = (EntityType<InfinityTridentEntity>) EntityType.Builder.<InfinityTridentEntity>create(InfinityTridentEntity::new, EntityClassification.MISC).size(0.5F, 0.5F)
                .setShouldReceiveVelocityUpdates(true)
                .setCustomClientFactory((spawnEntity, world) -> new InfinityTridentEntity(TRIDENT_ENTITY_TYPE,world)).trackingRange(4).func_233608_b_(20).build("trident_entity").setRegistryName(Reference.MOD_ID, "trident_entity");
    }


    @Override
    public List<Feature.Builder> generateFeatures() {
        List<Feature.Builder> features = new ArrayList<>();
        features.add(Feature.builder("meat_feeder").content(Item.class, MEAT_FEEDER = new MeatFeederItem(TAB_TOOL)));
        features.add(Feature.builder("mob_imprisonment_tool").content(Item.class, MOB_IMPRISONMENT_TOOL = new MobImprisonmentToolItem(TAB_TOOL)));
        features.add(Feature.builder("infinity_drill").content(Item.class, INFINITY_DRILL = new ItemInfinityDrill(TAB_TOOL)));
        features.add(Feature.builder("mob_essence_tool").content(Item.class, MOB_ESSENCE_TOOL = new MobEssenceToolItem(TAB_TOOL)));
        features.add(Feature.builder("infinity_saw").content(Item.class, INFINITY_SAW = new ItemInfinitySaw(TAB_TOOL)).event(EventManager.forge(BlockEvent.BreakEvent.class).filter(breakEvent -> breakEvent.getPlayer().getHeldItemMainhand().getItem() == INFINITY_SAW && BlockUtils.isLog((World) breakEvent.getWorld(), breakEvent.getPos())).process(breakEvent -> {
            breakEvent.setCanceled(true);
            breakEvent.getPlayer().getHeldItemMainhand().onBlockDestroyed((World) breakEvent.getWorld(), breakEvent.getState(), breakEvent.getPos(), breakEvent.getPlayer());
        })));
        features.add(createFeature(INFINITY_HAMMER = new ItemInfinityHammer(TAB_TOOL)));
        features.add(Feature.builder("infinity_trident")
                .content(Item.class, INFINITY_TRIDENT = new ItemInfinityTrident(TAB_TOOL))
                .content(EntityType.class, (EntityType) TRIDENT_ENTITY_TYPE ));
        features.add(createFeature(INFINITY_BACKPACK = new ItemInfinityBackpack()));
        TAB_TOOL.addIconStack(new ItemStack(INFINITY_DRILL));
        ItemStackHarnessRegistry.register(INFINITY_SAW, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), CapabilityEnergy.ENERGY, CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));
        ItemStackHarnessRegistry.register(INFINITY_DRILL, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), CapabilityEnergy.ENERGY, CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));
        ItemStackHarnessRegistry.register(INFINITY_HAMMER, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), CapabilityEnergy.ENERGY, CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));
        ItemStackHarnessRegistry.register(INFINITY_TRIDENT, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), CapabilityEnergy.ENERGY, CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));
        ItemStackHarnessRegistry.register(INFINITY_TRIDENT, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), CapabilityEnergy.ENERGY, CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));
        ItemStackHarnessRegistry.register(INFINITY_BACKPACK, stack -> new ItemStackHarness(stack, null, (IButtonHandler) stack.getItem(), CapabilityEnergy.ENERGY, CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY));

        return features;
    }
}
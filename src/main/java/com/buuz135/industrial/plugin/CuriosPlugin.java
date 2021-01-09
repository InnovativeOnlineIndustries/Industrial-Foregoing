package com.buuz135.industrial.plugin;

import com.buuz135.industrial.item.MeatFeederItem;
import com.buuz135.industrial.item.infinity.item.ItemInfinityBackpack;
import com.buuz135.industrial.plugin.curios.InfinityBackpackCurios;
import com.buuz135.industrial.plugin.curios.MeatFeedCurios;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.annotation.plugin.FeaturePlugin;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.network.locator.PlayerInventoryFinder;
import com.hrznstudio.titanium.plugin.FeaturePluginInstance;
import com.hrznstudio.titanium.plugin.PluginPhase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

import javax.annotation.Nullable;

@FeaturePlugin(value = "curios", type = FeaturePlugin.FeaturePluginType.MOD)
public class CuriosPlugin implements FeaturePluginInstance {

    public static final String CURIOS = "curios";

    @Override
    public void execute(PluginPhase phase) {
        if (phase == PluginPhase.CONSTRUCTION) {
            EventManager.forgeGeneric(AttachCapabilitiesEvent.class, ItemStack.class).process(event -> {
                AttachCapabilitiesEvent<ItemStack> stackEvent = (AttachCapabilitiesEvent<ItemStack>) event;
                ItemStack stack =  stackEvent.getObject();
                if (stack.getItem() instanceof MeatFeederItem){
                    stackEvent.addCapability(new ResourceLocation(Reference.MOD_ID, stack.getItem().getRegistryName().getPath() + "_curios"), new ICapabilityProvider() {
                        @Override
                        public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
                            if (cap == CuriosCapability.ITEM) return LazyOptional.of(MeatFeedCurios::new).cast();
                            return LazyOptional.empty();
                        }
                    });
                }
                if (stack.getItem() instanceof ItemInfinityBackpack){
                    stackEvent.addCapability(new ResourceLocation(Reference.MOD_ID, stack.getItem().getRegistryName().getPath() + "_curios"), new ICapabilityProvider() {
                        @Override
                        public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
                            if (cap == CuriosCapability.ITEM) return LazyOptional.of(InfinityBackpackCurios::new).cast();
                            return LazyOptional.empty();
                        }
                    });
                }
            }).subscribe();
            EventManager.mod(InterModEnqueueEvent.class).process(interModEnqueueEvent -> {
                InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.HEAD.getMessageBuilder().build());
                InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BACK.getMessageBuilder().build());
            }).subscribe();
            PlayerInventoryFinder.FINDERS.put(CURIOS, new PlayerInventoryFinder(playerEntity -> 1, (playerEntity, integer) -> getStack(playerEntity, SlotTypePreset.BACK, 0), (playerEntity, slot, stack) -> setStack(playerEntity, SlotTypePreset.BACK, slot, stack)));
        }
    }

    public static ItemStack getStack(LivingEntity entity, SlotTypePreset preset, int index){
        return CuriosApi.getCuriosHelper().getCuriosHandler(entity).map(iCuriosItemHandler -> iCuriosItemHandler.getStacksHandler(preset.getIdentifier())).map(iCurioStacksHandler -> iCurioStacksHandler.get().getStacks().getStackInSlot(index)).orElse(ItemStack.EMPTY);
    }

    public static void setStack(LivingEntity entity, SlotTypePreset preset, int index, ItemStack stack){
        CuriosApi.getCuriosHelper().getCuriosHandler(entity).map(iCuriosItemHandler -> iCuriosItemHandler.getStacksHandler(preset.getIdentifier())).ifPresent(iCurioStacksHandler -> iCurioStacksHandler.get().getStacks().setStackInSlot(index, stack));
    }

}

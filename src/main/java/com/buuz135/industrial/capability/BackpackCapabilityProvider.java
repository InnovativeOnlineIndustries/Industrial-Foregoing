package com.buuz135.industrial.capability;

import com.buuz135.industrial.item.infinity.InfinityCapabilityProvider;
import com.buuz135.industrial.item.infinity.InfinityEnergyStorage;
import com.buuz135.industrial.worlddata.BackpackDataManager;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.capability.FluidHandlerScreenProviderItemStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BackpackCapabilityProvider extends InfinityCapabilityProvider {

    private LazyOptional<IItemHandler> itemHandlerLazyOptional;

    public BackpackCapabilityProvider(ItemStack stack, IFactory<? extends FluidHandlerScreenProviderItemStack> tankFactory, IFactory<InfinityEnergyStorage> energyFactory) {
        super(stack, tankFactory, energyFactory);
    }

    @Nullable
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY == capability) {
            if (itemHandlerLazyOptional == null && this.getStack().hasTag()){
                BackpackDataManager manager = BackpackDataManager.getData(ServerLifecycleHooks.getCurrentServer().getWorld(World.OVERWORLD));
                if (manager != null){
                    String id = this.getStack().getTag().getString("Id");
                    BackpackDataManager.BackpackItemHandler backpack = manager.getBackpack(id);
                    if (backpack != null){
                        itemHandlerLazyOptional = LazyOptional.of(() -> backpack);
                    }
                }
            }
            if (itemHandlerLazyOptional != null){
                return itemHandlerLazyOptional.cast();
            }
        }
        return super.getCapability(capability, facing);
    }
}

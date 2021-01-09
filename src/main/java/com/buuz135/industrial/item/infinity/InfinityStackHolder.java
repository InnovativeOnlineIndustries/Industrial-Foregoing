package com.buuz135.industrial.item.infinity;

import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.api.client.IScreenAddonProvider;
import com.hrznstudio.titanium.capability.ItemStackHolderCapability;
import com.hrznstudio.titanium.network.locator.PlayerInventoryFinder;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InfinityStackHolder extends ItemStackHolderCapability implements IScreenAddonProvider {

    public static PlayerInventoryFinder.Target TARGET = null;

    public InfinityStackHolder() {
        super(() -> {
            if (TARGET == null) return ItemStack.EMPTY;
            return TARGET.getFinder().getStackGetter().apply(Minecraft.getInstance().player, TARGET.getSlot());
        });
    }

    @Override
    public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
        List<IFactory<? extends IScreenAddon>> factory = new ArrayList<>();
        if (getHolder().get().getItem() instanceof IInfinityDrillScreenAddons) {
            factory.addAll(((IInfinityDrillScreenAddons) getHolder().get().getItem()).getScreenAddons(getHolder()));
        }
        return factory;
    }

}
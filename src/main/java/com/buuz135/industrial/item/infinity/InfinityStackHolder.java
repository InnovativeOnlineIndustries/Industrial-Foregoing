package com.buuz135.industrial.item.infinity;

import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.api.client.IScreenAddonProvider;
import com.hrznstudio.titanium.capability.ItemStackHolderCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import java.util.ArrayList;
import java.util.List;

public class InfinityStackHolder extends ItemStackHolderCapability implements IScreenAddonProvider {

    public InfinityStackHolder() {
        super(() -> {
            ItemStack stack = Minecraft.getInstance().player.getHeldItem(Hand.MAIN_HAND);
            if (!(stack.getItem() instanceof IInfinityDrillScreenAddons))
                stack = Minecraft.getInstance().player.getHeldItem(Hand.OFF_HAND);
            return stack;
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
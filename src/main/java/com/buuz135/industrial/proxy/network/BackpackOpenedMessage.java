package com.buuz135.industrial.proxy.network;

import com.buuz135.industrial.item.infinity.InfinityStackHolder;
import com.hrznstudio.titanium.network.Message;
import com.hrznstudio.titanium.network.locator.PlayerInventoryFinder;
import net.minecraftforge.fml.network.NetworkEvent;

public class BackpackOpenedMessage extends Message {

    private int slot;
    private String finder;

    public BackpackOpenedMessage(int slot, String finder) {
        this.slot = slot;
        this.finder = finder;
    }

    public BackpackOpenedMessage() {
    }

    @Override
    protected void handleMessage(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            InfinityStackHolder.TARGET = new PlayerInventoryFinder.Target(finder, PlayerInventoryFinder.get(finder).get(), slot);
        });
    }
}

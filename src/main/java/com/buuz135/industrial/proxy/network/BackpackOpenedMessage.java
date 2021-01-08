package com.buuz135.industrial.proxy.network;

import com.buuz135.industrial.item.infinity.InfinityStackHolder;
import com.hrznstudio.titanium.network.Message;
import net.minecraftforge.fml.network.NetworkEvent;

public class BackpackOpenedMessage extends Message {

    private int slot;

    public BackpackOpenedMessage(int slot) {
        this.slot = slot;
    }

    public BackpackOpenedMessage() {
    }

    @Override
    protected void handleMessage(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            InfinityStackHolder.SLOT = slot;
        });
    }
}

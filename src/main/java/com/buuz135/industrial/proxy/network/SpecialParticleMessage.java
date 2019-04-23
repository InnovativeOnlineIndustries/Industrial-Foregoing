package com.buuz135.industrial.proxy.network;

import com.buuz135.industrial.item.infinity.OneThreeFiveHandler;
import com.hrznstudio.titanium.network.Message;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;

public class SpecialParticleMessage extends Message {

    public UUID uuid;

    public SpecialParticleMessage(UUID uuid) {
        this.uuid = uuid;
    }

    public SpecialParticleMessage() {
    }

    @Override
    protected void handleMessage(NetworkEvent.Context context) {
        OneThreeFiveHandler.SPECIAL_ENTITIES.put(uuid, System.currentTimeMillis());
    }


}

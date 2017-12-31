package com.buuz135.industrial.proxy.event;

import com.buuz135.industrial.utils.IFFakePlayer;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FakePlayerRideEntityHandler {

    @SubscribeEvent
    public void onFakePlayerRide(EntityMountEvent entityMountEvent) {
        if (entityMountEvent.getEntityMounting() instanceof IFFakePlayer) entityMountEvent.setCanceled(true);
    }
}

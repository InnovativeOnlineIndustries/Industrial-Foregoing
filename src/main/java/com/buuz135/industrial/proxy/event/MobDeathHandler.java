package com.buuz135.industrial.proxy.event;

import com.buuz135.industrial.proxy.CommonProxy;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MobDeathHandler {

    @SubscribeEvent
    public void onDeath(LivingDropsEvent event) {
        if (event.getSource().equals(CommonProxy.custom)) {
            event.getDrops().clear();
        }
    }
}

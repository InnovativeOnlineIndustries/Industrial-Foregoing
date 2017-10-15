package com.buuz135.industrial.proxy;

import com.buuz135.industrial.api.rednet.IRednetReader;
import com.buuz135.industrial.utils.rednetreaders.RedstoneRednetReader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RednetReaderRegistry {

    @SubscribeEvent
    public void onRegistry(RegistryEvent.Register<IRednetReader> event) {
        event.getRegistry().register(new RedstoneRednetReader());
    }
}

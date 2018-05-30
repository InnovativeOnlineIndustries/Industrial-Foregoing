package com.buuz135.industrial.proxy;

import com.buuz135.industrial.api.conveyor.ConveyorUpgradeFactory;
import com.buuz135.industrial.proxy.block.upgrade.ConveyorDetectorUpgrade;
import com.buuz135.industrial.proxy.block.upgrade.ConveyorExtractionUpgrade;
import com.buuz135.industrial.proxy.block.upgrade.ConveyorInsertionUpgrade;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;


public class ConveyorRegistry {

    @SubscribeEvent
    public void register(RegistryEvent.Register<ConveyorUpgradeFactory> event) {
        IForgeRegistry<ConveyorUpgradeFactory> registry = event.getRegistry();
        registry.register(new ConveyorExtractionUpgrade.Factory());
        registry.register(new ConveyorInsertionUpgrade.Factory());
        registry.register(new ConveyorDetectorUpgrade.Factory());
    }
}
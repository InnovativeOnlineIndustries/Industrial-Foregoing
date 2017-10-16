package com.buuz135.industrial.utils.apihandlers;


import com.buuz135.industrial.api.plant.PlantRecollectable;
import com.buuz135.industrial.utils.apihandlers.plant.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlantRecollectableRegistryHandler {

    @SubscribeEvent
    public void onEvent(RegistryEvent.Register<PlantRecollectable> iPlantRecollectables) {
        iPlantRecollectables.getRegistry().register(new BlockCropPlantRecollectable());
        iPlantRecollectables.getRegistry().register(new BlockNetherWartRecollectable());
        iPlantRecollectables.getRegistry().register(new DoubleTallPlantRecollectable());
        iPlantRecollectables.getRegistry().register(new PumpkinMelonPlantRecollectable());
        iPlantRecollectables.getRegistry().register(new TreePlantRecollectable());
    }
}

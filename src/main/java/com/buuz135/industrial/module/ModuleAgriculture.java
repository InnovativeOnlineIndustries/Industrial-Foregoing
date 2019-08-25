package com.buuz135.industrial.module;

import com.buuz135.industrial.api.plant.PlantRecollectable;
import com.buuz135.industrial.block.agriculture.PlantGathererBlock;
import com.buuz135.industrial.registry.IFRegistries;
import com.buuz135.industrial.utils.apihandlers.plant.*;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.module.Feature;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;

import java.util.ArrayList;
import java.util.List;

public class ModuleAgriculture implements IModule {

    public static PlantGathererBlock PLANT_GATHERER = new PlantGathererBlock();

    @Override
    public List<Feature.Builder> generateFeatures() {
        List<Feature.Builder> builders = new ArrayList<>();
        builders.add(Feature.builder("crop_farming")
                .content(Block.class, PLANT_GATHERER)
                .event(EventManager.forge(RegistryEvent.NewRegistry.class).process(newRegistry -> IFRegistries.poke()))
                .content(PlantRecollectable.class, new BlockCropPlantRecollectable())
                .content(PlantRecollectable.class, new BlockNetherWartRecollectable())
                .content(PlantRecollectable.class, new DoubleTallPlantRecollectable())
                .content(PlantRecollectable.class, new PumpkinMelonPlantRecollectable())
                .content(PlantRecollectable.class, new TreePlantRecollectable())
                .content(PlantRecollectable.class, new ChorusFruitRecollectable())
        );
        return builders;
    }
}

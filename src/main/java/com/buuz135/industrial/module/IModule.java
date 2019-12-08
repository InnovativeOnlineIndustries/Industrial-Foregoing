package com.buuz135.industrial.module;

import com.hrznstudio.titanium.module.Feature;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.List;

public interface IModule {

    List<Feature.Builder> generateFeatures();

    default Feature.Builder createFeature(ForgeRegistryEntry entry) {
        return Feature.builder(entry.getRegistryName().getPath()).content(entry.getRegistryType(), entry);
    }
}

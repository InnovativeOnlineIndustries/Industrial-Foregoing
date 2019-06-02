package com.buuz135.industrial.module;

import com.hrznstudio.titanium.module.Feature;

import java.util.List;

public interface IModule {

    List<Feature.Builder> generateFeatures();
}

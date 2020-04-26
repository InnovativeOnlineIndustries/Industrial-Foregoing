package com.buuz135.industrial.plugin;

import com.hrznstudio.titanium.annotation.plugin.FeaturePlugin;
import com.hrznstudio.titanium.material.ResourceRegistry;
import com.hrznstudio.titanium.material.ResourceType;
import com.hrznstudio.titanium.plugin.FeaturePluginInstance;
import com.hrznstudio.titanium.plugin.PluginPhase;

@FeaturePlugin(value = ResourceRegistry.PLUGIN_NAME, type = FeaturePlugin.FeaturePluginType.FEATURE)
public class IndustrialForegoingResourcePlugin implements FeaturePluginInstance {
    @Override
    public void execute(PluginPhase phase) {
        if (phase == PluginPhase.CONSTRUCTION) {
            ResourceRegistry.getOrCreate("iron").add(ResourceType.GEAR);
            ResourceRegistry.getOrCreate("gold").add(ResourceType.GEAR);
            ResourceRegistry.getOrCreate("diamond").add(ResourceType.GEAR);
        }
    }
}

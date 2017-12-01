package com.buuz135.industrial.utils.apihandlers.json;

import com.buuz135.industrial.config.CustomConfiguration;
import com.google.gson.JsonObject;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.function.BooleanSupplier;

public class ConfigurationConditionFactory implements IConditionFactory {
    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject json) {
        if (json.has("value") && CustomConfiguration.configValues.containsKey(json.get("value").getAsString())) {
            return () -> CustomConfiguration.configValues.get(json.get("value").getAsString());
        }
        return () -> false;
    }
}

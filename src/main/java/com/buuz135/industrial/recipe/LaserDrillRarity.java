package com.buuz135.industrial.recipe;

import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hrznstudio.titanium.network.CompoundSerializableDataHandler;
import com.hrznstudio.titanium.recipe.serializer.JSONSerializableDataHandler;
import net.minecraft.world.biome.Biome;

public class LaserDrillRarity {

    static {
        JSONSerializableDataHandler.map(LaserDrillRarity[].class, values -> {
            JsonArray array = new JsonArray();
            for (LaserDrillRarity type : values) {
                JsonObject object = new JsonObject();
                object.add("whitelist", JSONSerializableDataHandler.write(Biome[].class, type.whitelist));
                object.add("blacklist", JSONSerializableDataHandler.write(Biome[].class, type.blacklist));
                object.addProperty("depth_min", type.depth_min);
                object.addProperty("depth_max", type.depth_max);
                object.addProperty("weight", type.weight);
                return object;
            }
            return array;
        }, element -> Streams.stream(element.getAsJsonArray()).map(JsonElement::getAsJsonObject).map(jsonObject -> new LaserDrillRarity(
                JSONSerializableDataHandler.read(Biome[].class, jsonObject.getAsJsonObject("whitelist")),
                JSONSerializableDataHandler.read(Biome[].class, jsonObject.getAsJsonObject("blacklist")),
                jsonObject.get("depth_min").getAsInt(),
                jsonObject.get("depth_max").getAsInt(),
                jsonObject.get("weight").getAsInt()
        )).toArray(LaserDrillRarity[]::new));
        CompoundSerializableDataHandler.map(LaserDrillRarity[].class, buf -> {
            LaserDrillRarity[] rarity = new LaserDrillRarity[buf.readInt()];
            for (int i = 0; i < rarity.length; i++) {
                rarity[i] = new LaserDrillRarity(CompoundSerializableDataHandler.readBiomeArray(buf), CompoundSerializableDataHandler.readBiomeArray(buf), buf.readInt(), buf.readInt(), buf.readInt());
            }
            return rarity;
        }, (buf, laserDrillRarities) -> {
            buf.writeInt(laserDrillRarities.length);
            for (LaserDrillRarity laserDrillRarity : laserDrillRarities) {
                CompoundSerializableDataHandler.writeBiomeArray(buf, laserDrillRarity.whitelist);
                CompoundSerializableDataHandler.writeBiomeArray(buf, laserDrillRarity.blacklist);
                buf.writeInt(laserDrillRarity.depth_min);
                buf.writeInt(laserDrillRarity.depth_max);
                buf.writeInt(laserDrillRarity.weight);
            }
        });
    }

    public Biome[] whitelist;
    public Biome[] blacklist;
    public int depth_min;
    public int depth_max;
    public int weight;

    public LaserDrillRarity(Biome[] whitelist, Biome[] blacklist, int depth_min, int depth_max, int weight) {
        this.whitelist = whitelist;
        this.blacklist = blacklist;
        this.depth_min = depth_min;
        this.depth_max = depth_max;
        this.weight = weight;
    }
}

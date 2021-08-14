/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.buuz135.industrial.recipe;

import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hrznstudio.titanium.network.CompoundSerializableDataHandler;
import com.hrznstudio.titanium.recipe.serializer.JSONSerializableDataHandler;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;

import static net.minecraft.world.biome.Biomes.*;

public class LaserDrillRarity {

    public static RegistryKey<Biome>[] END = new RegistryKey[]{THE_END, THE_VOID, SMALL_END_ISLANDS, END_BARRENS, END_HIGHLANDS, END_MIDLANDS};
    public static RegistryKey<Biome>[] NETHER = new RegistryKey[]{NETHER_WASTES, BASALT_DELTAS, WARPED_FOREST, CRIMSON_FOREST, SOUL_SAND_VALLEY};
    public static RegistryKey<Biome>[] OIL = new RegistryKey[]{DESERT, DESERT_HILLS, DESERT_LAKES, OCEAN, COLD_OCEAN, DEEP_COLD_OCEAN, DEEP_FROZEN_OCEAN, DEEP_LUKEWARM_OCEAN, DEEP_WARM_OCEAN, WARM_OCEAN};

     public static void init(){
        JSONSerializableDataHandler.map(LaserDrillRarity[].class, values -> {
            JsonArray array = new JsonArray();
            for (LaserDrillRarity type : values) {
                JsonObject object = new JsonObject();
                object.add("whitelist", JSONSerializableDataHandler.write(RegistryKey[].class, type.whitelist));
                object.add("blacklist", JSONSerializableDataHandler.write(RegistryKey[].class, type.blacklist));
                object.addProperty("depth_min", type.depth_min);
                object.addProperty("depth_max", type.depth_max);
                object.addProperty("weight", type.weight);
                array.add(object);
            }
            return array;
        }, element -> {
            return Streams.stream(element.getAsJsonArray()).map(JsonElement::getAsJsonObject).map(jsonObject -> new LaserDrillRarity(
                    JSONSerializableDataHandler.read(RegistryKey[].class, jsonObject.getAsJsonObject("whitelist")),
                    JSONSerializableDataHandler.read(RegistryKey[].class, jsonObject.getAsJsonObject("blacklist")),
                    jsonObject.get("depth_min").getAsInt(),
                    jsonObject.get("depth_max").getAsInt(),
                    jsonObject.get("weight").getAsInt()
            )).toArray(LaserDrillRarity[]::new);
        });
        CompoundSerializableDataHandler.map(LaserDrillRarity[].class, buf -> {
            LaserDrillRarity[] rarity = new LaserDrillRarity[buf.readInt()];
            for (int i = 0; i < rarity.length; i++) {
                rarity[i] = new LaserDrillRarity((RegistryKey<Biome>[]) CompoundSerializableDataHandler.readRegistryArray(buf), (RegistryKey<Biome>[]) CompoundSerializableDataHandler.readRegistryArray(buf), buf.readInt(), buf.readInt(), buf.readInt());
            }
            return rarity;
        }, (buf, laserDrillRarities) -> {
            buf.writeInt(laserDrillRarities.length);
            for (LaserDrillRarity laserDrillRarity : laserDrillRarities) {
                CompoundSerializableDataHandler.writeRegistryArray(buf, laserDrillRarity.whitelist);
                CompoundSerializableDataHandler.writeRegistryArray(buf, laserDrillRarity.blacklist);
                buf.writeInt(laserDrillRarity.depth_min);
                buf.writeInt(laserDrillRarity.depth_max);
                buf.writeInt(laserDrillRarity.weight);
            }
        });
    }

    public RegistryKey[] whitelist;
    public RegistryKey[] blacklist;
    public int depth_min;
    public int depth_max;
    public int weight;

    public LaserDrillRarity(RegistryKey[] whitelist, RegistryKey[] blacklist, int depth_min, int depth_max, int weight) {
        this.whitelist = whitelist;
        this.blacklist = blacklist;
        this.depth_min = depth_min;
        this.depth_max = depth_max;
        this.weight = weight;
    }
}

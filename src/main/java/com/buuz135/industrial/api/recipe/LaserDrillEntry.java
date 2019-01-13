/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
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
package com.buuz135.industrial.api.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LaserDrillEntry {

    public static List<ResourceLocation> default_files = null;
    public static List<LaserDrillEntry>[] LASER_DRILL_ENTRIES;
    public static List<LaserDrillEntryExtended> LASER_DRILL_UNIQUE_VALUES;

    private int laserMeta;
    private ItemStack stack;
    private int weight;
    private List<Biome> whitelist;
    private List<Biome> blacklist;

    /**
     * Represents an entry for the laser drill.
     *
     * @param laserMeta The metadata of the lens item.
     * @param stack     The ItemStack as an output.
     * @param weight    The weight in the global pool of items.
     */

    public LaserDrillEntry(int laserMeta, ItemStack stack, int weight, List<Biome> whitelist, List<Biome> blacklist) {
        this.laserMeta = laserMeta;
        this.stack = stack;
        this.weight = weight;
        this.whitelist = whitelist;
        this.blacklist = blacklist;
    }

    public static void addOreFile(ResourceLocation l) {
        if (default_files == null) {
            default_files = new LinkedList<ResourceLocation>();
        }
        default_files.add(l);
    }

    //@SideOnly(Side.SERVER)
    public static void loadLaserConfigs(File configDir) {
        //Generate default files if absent.
        Path ores_path = configDir.toPath().resolve("laser_drill_ores");
        if (!Files.exists(ores_path)) {
            ores_path.toFile().mkdir();
        }

        for (ResourceLocation l : default_files) {
            Path l_path = ores_path.resolve(l.getPath());
            if (!Files.exists(l_path)) {
                InputStream in = null;
                try {
                    in = LaserDrillEntry.class.getClassLoader().getResourceAsStream("assets" + "/" + l.getNamespace() + "/" + l.getPath());
                    OutputStream out = new FileOutputStream(l_path.toFile());
                    int read;
                    byte[] buffer = new byte[4096];
                    while ((read = in.read(buffer)) > 0) {
                        out.write(buffer, 0, read);
                    }
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //Load configuration data.
        LASER_DRILL_ENTRIES = new LinkedList[256];
        for (int i = 0; i < LASER_DRILL_ENTRIES.length; i++) {
            LASER_DRILL_ENTRIES[i] = new LinkedList<LaserDrillEntry>();
        }
        LASER_DRILL_UNIQUE_VALUES = new ArrayList<>();

        DirectoryStream<Path> ds;
        try {
            ds = Files.newDirectoryStream(ores_path, "*.{json}");
            for (Path p : ds) {
                loadConfig(p.toFile());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadConfig(File f) {
        try {
            JsonReader j = new JsonReader(new FileReader(f));
            JsonArray head = new JsonParser().parse(j).getAsJsonArray();
            for (JsonElement o : head) {
                JsonObject ore = o.getAsJsonObject();

                ItemStack itemStack;
                String itemName = ore.getAsJsonPrimitive("item").getAsString();
                if (itemName.startsWith("ore")) {
                    if (OreDictionary.doesOreNameExist(itemName) && OreDictionary.getOres(itemName).size() > 0)
                        itemStack = OreDictionary.getOres(itemName).get(0).copy();
                    else continue;
                } else {
                    String[] item_strings = itemName.split(":");
                    ResourceLocation item_location = new ResourceLocation(item_strings[0], item_strings[1]);
                    Item item = ForgeRegistries.ITEMS.getValue(item_location);
                    if (item == null) continue;
                    if (item_strings.length > 2) {
                        itemStack = new ItemStack(item, 1, Integer.parseInt(item_strings[2]));
                    } else {
                        itemStack = new ItemStack(item);
                    }
                }
                if (itemStack.isEmpty()) continue;

                int color = ore.getAsJsonPrimitive("color").getAsInt();
                JsonArray rarities = ore.getAsJsonArray("rarity");
                for (JsonElement r : rarities) {
                    JsonObject rarity_data = r.getAsJsonObject();

                    List<Biome> whitelist = new LinkedList<Biome>();
                    List<Biome> blacklist = new LinkedList<Biome>();

                    String[] blacklist_strings = rarity_data.getAsJsonPrimitive("blacklist").getAsString().split(",");
                    for (int i = 0; i < blacklist_strings.length; i++) {
                        if (!blacklist_strings[i].isEmpty()) {
                            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(blacklist_strings[i].trim()));
                            if (biome != null) blacklist.add(biome);
                        }
                    }

                    String[] whitelist_strings = rarity_data.getAsJsonPrimitive("whitelist").getAsString().split(",");
                    for (int i = 0; i < whitelist_strings.length; i++) {
                        if (!whitelist_strings[i].isEmpty()) {
                            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(whitelist_strings[i].trim()));
                            if (biome != null) whitelist.add(biome);
                        }
                    }

                    int amount = rarity_data.get("weight").getAsInt();
                    int min_depth = rarity_data.get("depth_min").getAsInt();
                    int max_depth = rarity_data.get("depth_max").getAsInt();

                    for (int d = min_depth; d <= max_depth; d++) {
                        LASER_DRILL_ENTRIES[d].add(new LaserDrillEntry(color, itemStack, amount, whitelist, blacklist));
                    }
                    findForOre(itemStack, new LaserDrillEntryExtended(color, itemStack)).getRarities().add(new OreRarity(amount, whitelist, blacklist, max_depth, min_depth));
                }
            }
            j.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static LaserDrillEntryExtended findForOre(ItemStack itemStack, LaserDrillEntryExtended defaultEntry) {
        for (LaserDrillEntryExtended laserDrillUniqueValue : LASER_DRILL_UNIQUE_VALUES) {
            if (itemStack.isItemEqual(laserDrillUniqueValue.getStack())) return laserDrillUniqueValue;
        }
        LASER_DRILL_UNIQUE_VALUES.add(defaultEntry);
        return defaultEntry;
    }

    public int getLaserMeta() {
        return laserMeta;
    }

    public ItemStack getStack() {
        return stack;
    }

    public int getWeight() {
        return weight;
    }

    public List<Biome> getBlacklist() {
        return blacklist;
    }

    public List<Biome> getWhitelist() {
        return whitelist;
    }

    @Override
    public String toString() {
        return stack.getDisplayName();
    }

    public static class LaserDrillEntryExtended {
        private int laserMeta;
        private ItemStack stack;
        private List<OreRarity> rarities;

        public LaserDrillEntryExtended(int laserMeta, ItemStack stack) {
            this.laserMeta = laserMeta;
            this.stack = stack;
            this.rarities = new ArrayList<>();
        }

        public int getLaserMeta() {
            return laserMeta;
        }

        public ItemStack getStack() {
            return stack;
        }

        public List<OreRarity> getRarities() {
            return rarities;
        }
    }

    public static class OreRarity {

        private int weight;
        private List<Biome> whitelist;
        private List<Biome> blacklist;
        private int maxY;
        private int minY;

        public OreRarity(int weight, List<Biome> whitelist, List<Biome> blacklist, int maxY, int minY) {
            this.weight = weight;
            this.whitelist = whitelist;
            this.blacklist = blacklist;
            this.maxY = maxY;
            this.minY = minY;
        }

        public int getWeight() {
            return weight;
        }

        public List<Biome> getWhitelist() {
            return whitelist;
        }

        public List<Biome> getBlacklist() {
            return blacklist;
        }

        public int getMaxY() {
            return maxY;
        }

        public int getMinY() {
            return minY;
        }
    }
}

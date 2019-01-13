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
package com.buuz135.industrial.utils;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class RecipeUtils {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Set<String> USED_OD_NAMES = new TreeSet<>();
    private static File RECIPE_DIR = null;

    private static void setupDir() {
        if (RECIPE_DIR == null) {
            RECIPE_DIR = new File("../src/main/resources/assets/industrialforegoing/recipes/");
        }
    }

    public static void addShapedRecipe(ItemStack result, Object... components) {
        setupDir();
        if (!RECIPE_DIR.exists() || result.isEmpty()) return;
        // GameRegistry.addShapedRecipe(result, components);
        boolean hasGeneratedFrameRecipe = false;
        for (int i = 0; i < components.length; i++) {
            if (components[i].equals(MachineCaseItem.INSTANCE)) {
                hasGeneratedFrameRecipe = true;
                addShapedRecipe(result, "_enderio", generateOptionalJson("enderio", "useEnderIOFrames"), replaceFrameWith(new FakeItemStack("enderio:item_material", 0), components));
                addShapedRecipe(result, "_thermal", generateOptionalJson("thermalexpansion", "useTEFrames"), replaceFrameWith(new FakeItemStack("thermalexpansion:frame", 0), components));
                addShapedRecipe(result, "", generateOptionalJson("industrialforegoing", "useOriginalFrames"), components);
                addShapedRecipe(result, "_mekanism", generateOptionalJson("mekanism", "useMekanismFrames"), replaceFrameWith(new FakeItemStack("mekanism:basicblock", 8), components));
                break;
            }
        }
        if (!hasGeneratedFrameRecipe) {
            addShapedRecipe(result, "", new HashMap<>(), components);
        }
    }

    private static Map<String, Object> generateOptionalJson(String modID, String configValueName) {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("conditions", Arrays.asList(ImmutableMap.of("type", "forge:and", "values", Arrays.asList(ImmutableMap.of("type", "forge:mod_loaded", "modid", modID), ImmutableMap.of("type", "industrialforegoing:configuration_value",
                "value", configValueName)))));
        return objectMap;
    }

    private static Object[] replaceFrameWith(Object item, Object... components) {
        Object[] objects = Arrays.copyOf(components, components.length);
        for (int i = 0; i < objects.length; i++) {
            if (objects[i].equals(MachineCaseItem.INSTANCE)) objects[i] = item;
        }
        return objects;
    }


    public static void addShapedRecipe(ItemStack result, String nameExtra, Map<String, Object> json, Object... components) {
        setupDir();
        if (!RECIPE_DIR.exists() || result.isEmpty()) return;
        List<String> pattern = new ArrayList<>();
        int i = 0;
        while (i < components.length && components[i] instanceof String) {
            pattern.add((String) components[i]);
            i++;
        }
        json.put("pattern", pattern);

        boolean isOreDict = false;
        Map<String, Map<String, Object>> key = new HashMap<>();
        Character curKey = null;
        for (; i < components.length; i++) {
            Object o = components[i];
            if (o instanceof Character) {
                if (curKey != null)
                    throw new IllegalArgumentException("Provided two char keys in a row");
                curKey = (Character) o;
            } else {
                if (curKey == null)
                    throw new IllegalArgumentException("Providing object without a char key");
                if (o instanceof String)
                    isOreDict = true;
                key.put(Character.toString(curKey), serializeItem(o));
                curKey = null;
            }
        }
        json.put("key", key);
        json.put("type", isOreDict ? "forge:ore_shaped" : "minecraft:crafting_shaped");
        json.put("result", serializeItem(result));

        // names the json the same name as the output's registry name
        // repeatedly adds _alt if a file already exists
        // janky I know but it works
        String suffix = result.getItem().getHasSubtypes() ? "_" + result.getItemDamage() : "";
        File f = new File(RECIPE_DIR, result.getItem().getRegistryName().getPath() + suffix + nameExtra + ".json");


        try (FileWriter w = new FileWriter(f)) {
            GSON.toJson(json, w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addShapelessRecipe(ItemStack result, Object... components) {
        addShapelessRecipe("", result, components);
    }

    public static void addShapelessRecipe(String name, ItemStack result, Object... components) {
        setupDir();
        if (!RECIPE_DIR.exists() || result.isEmpty()) return;
        // addShapelessRecipe(result, components);
        for (int i = 0; i < components.length; ++i) {
            if (components[i].equals(MachineCaseItem.INSTANCE)) components[i] = "IFCORE";
        }
        Map<String, Object> json = new HashMap<>();

        boolean isOreDict = false;
        List<Map<String, Object>> ingredients = new ArrayList<>();
        for (Object o : components) {
            if (o instanceof String)
                isOreDict = true;
            ingredients.add(serializeItem(o));
        }
        json.put("ingredients", ingredients);
        json.put("type", isOreDict ? "forge:ore_shapeless" : "minecraft:crafting_shapeless");
        json.put("result", serializeItem(result));

        // names the json the same name as the output's registry name
        // repeatedly adds _alt if a file already exists
        // janky I know but it works
        String suffix = result.getItem().getHasSubtypes() ? "_" + result.getItemDamage() : "";
        File f = new File(RECIPE_DIR, result.getItem().getRegistryName().getPath() + suffix + (!name.isEmpty() ? "_" + name : "") + ".json");

//        while (f.exists()) {
//            suffix += "_alt";
//            f = new File(RECIPE_DIR, result.getItem().getRegistryName().getPath() + suffix + ".json");
//        }


        try (FileWriter w = new FileWriter(f)) {
            GSON.toJson(json, w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Object> serializeItem(Object thing) {
        if (thing instanceof FakeItemStack) {
            FakeItemStack stack = (FakeItemStack) thing;
            Map<String, Object> ret = new HashMap<>();
            ret.put("item", stack.name);
            ret.put("data", stack.meta);
            return ret;
        }
        if (thing instanceof Item) {
            return serializeItem(new ItemStack((Item) thing));
        }
        if (thing instanceof Block) {
            return serializeItem(new ItemStack((Block) thing));
        }
        if (thing instanceof ItemStack) {
            ItemStack stack = (ItemStack) thing;
            Map<String, Object> ret = new HashMap<>();
            ret.put("item", stack.getItem().getRegistryName().toString());
            if (stack.getItem().getHasSubtypes() || stack.getItemDamage() != 0) {
                ret.put("data", stack.getItemDamage());
            }
            if (stack.getCount() > 1) {
                ret.put("count", stack.getCount());
            }

            if (stack.hasTagCompound()) {
                throw new IllegalArgumentException("nbt not implemented");
            }

            return ret;
        }
        if (thing instanceof String) {
            Map<String, Object> ret = new HashMap<>();
            USED_OD_NAMES.add((String) thing);
            ret.put("item", "#" + ((String) thing).toUpperCase(Locale.ROOT));
            return ret;
        }

        throw new IllegalArgumentException("Not a block, item, stack, or od name");
    }

    public static void generateConstants() {
        if (!RECIPE_DIR.exists()) return;
        List<Map<String, Object>> json = new ArrayList<>();
        for (String s : USED_OD_NAMES) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("name", s.toUpperCase(Locale.ROOT));
            entry.put("ingredient", ImmutableMap.of("type", "forge:ore_dict", "ore", s));
            json.add(entry);
        }
        Map<String, Object> entry = new HashMap<>();
        entry.put("name", "IFWITHER");
        //eentry.put("conditions", Arrays.asList(ImmutableMap.of("type", "industrialforegoing:configuration_value", "value", "machines.wither_builder.HCWither")));
        entry.put("ingredient", ImmutableMap.of("item", "minecraft:nether_star", "data", 0));
        json.add(entry);
        entry = new HashMap<>();
        entry.put("name", "IFWITHER");
        entry.put("conditions", Arrays.asList(ImmutableMap.of("type", "industrialforegoing:configuration_value", "value", "machines.wither_builder.HCWither")));
        entry.put("ingredient", ImmutableMap.of("item", "minecraft:skull", "data", 1));
        json.add(entry);

        json.addAll(createOreConditionItem("TIER3", "ingotTin", "minecraft:coal_block"));
        json.addAll(createOreConditionItem("TIER4", "ingotCopper", "minecraft:red_sandstone"));
        json.addAll(createOreConditionItem("TIER5", "ingotBronze", "minecraft:glowstone"));
        json.addAll(createOreConditionItem("TIER6", "ingotSilver", "minecraft:iron_block"));
        json.addAll(createOreConditionItem("TIER10", "ingotPlatinum", "minecraft:prismarine_shard"));

        try (FileWriter w = new FileWriter(new File(RECIPE_DIR, "_constants.json"))) {
            GSON.toJson(json, w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Map<String, Object>> createOreConditionItem(String name, String oreDict, String defaultItem) {
        Map<String, Object> def = new HashMap<>();
        def.put("name", name);
        def.put("ingredient", ImmutableMap.of("item", defaultItem, "data", 0));
        Map<String, Object> ore = new HashMap<>();
        ore.put("name", name);
        ore.put("conditions", Arrays.asList(ImmutableMap.of("type", "teslacorelib:ore_dict", "ore", oreDict)));
        ore.put("ingredient", ImmutableMap.of("type", "forge:ore_dict", "ore", oreDict));
        return Arrays.asList(def, ore);
    }

    private static class FakeItemStack {
        private String name;
        private int meta;

        public FakeItemStack(String name, int meta) {
            this.name = name;
            this.meta = meta;
        }
    }
}

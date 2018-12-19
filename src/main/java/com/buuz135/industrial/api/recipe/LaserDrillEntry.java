/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2018, Buuz135
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

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class LaserDrillEntry {
	
	//TODO Get rid of this?
	// Or use it to build the cache that I want.

	public static List<ResourceLocation> default_files = null;
	public static List<LaserDrillEntry> [] LASER_DRILL_ENTRIES;

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

    public int getLaserMeta() {
        return laserMeta;
    }

    public ItemStack getStack() {
        return stack;
    }

    public int getWeight() {
        return weight;
    }
    
    public List<Biome> getBlacklist(){
    	return blacklist;
    }
    
    public List<Biome> getWhitelist(){
    	return whitelist;
    }
	
	public static void addOreFile(ResourceLocation l){
		if(default_files == null){
			default_files = new LinkedList<ResourceLocation>();
		}
		default_files.add(l);
	}
	
	//@SideOnly(Side.SERVER)
	public static void loadLaserConfigs(FMLPreInitializationEvent event){
		//Generate default files if absent.
		File configDir = event.getModConfigurationDirectory();
		Path ores_path = configDir.toPath().resolve("laser_drill_ores");
		if(!Files.exists(ores_path)){
			ores_path.toFile().mkdir();
		}
		
		for(ResourceLocation l : default_files){
			Path l_path = ores_path.resolve(l.getPath());
			if(!Files.exists(l_path)){
				InputStream in = null;
				try {
					in = Minecraft.getMinecraft().getResourceManager().getResource(l).getInputStream();
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
		LASER_DRILL_ENTRIES = new LinkedList[255];
		for(int i = 0; i < LASER_DRILL_ENTRIES.length; i++){
			LASER_DRILL_ENTRIES[i] = new LinkedList<LaserDrillEntry>();
		}
		
		DirectoryStream<Path> ds;
        try{
        	ds = Files.newDirectoryStream(ores_path,"*.{json}");
            for (Path p : ds) {
            	loadConfig(p.toFile());
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	public static void loadConfig(File f){
		 try {
			JsonReader j = new JsonReader(new FileReader(f));
			JsonArray head = new JsonParser().parse(j).getAsJsonArray();
			for(JsonElement o : head){
				JsonObject ore = o.getAsJsonObject();
				
				String[] item_strings = ore.getAsJsonPrimitive("item").getAsString().split(":");
				ResourceLocation item_location = new ResourceLocation(item_strings[0], item_strings[1]);
				ItemStack item;
				if(item_strings.length > 2){
					item = new ItemStack(ForgeRegistries.ITEMS.getValue(item_location), Integer.parseInt(item_strings[2]));
				} else{
					item = new ItemStack(ForgeRegistries.ITEMS.getValue(item_location));
				}
				
				int color = ore.getAsJsonPrimitive("color").getAsInt();
				
				JsonArray rarities = ore.getAsJsonArray("rarity");
				for(JsonElement r : rarities){
					JsonObject rarity_data = r.getAsJsonObject();
					
					List<Biome> whitelist = new LinkedList<Biome>();
					List<Biome> blacklist = new LinkedList<Biome>();
					
					String[] blacklist_strings = rarity_data.getAsJsonPrimitive("blacklist").getAsString().split(",");
					for(int i = 0; i < blacklist_strings.length; i++){
						blacklist.add(ForgeRegistries.BIOMES.getValue(new ResourceLocation(blacklist_strings[i].trim())));
					}
					
					String[] whitelist_strings = rarity_data.getAsJsonPrimitive("whitelist").getAsString().split(",");
					for(int i = 0; i < whitelist_strings.length; i++){
						if(!whitelist_strings[i].isEmpty()){
							whitelist.add(ForgeRegistries.BIOMES.getValue(new ResourceLocation(whitelist_strings[i].trim())));
						}
					}
					
					int amount = rarity_data.get("weight").getAsInt();
					int min_depth = rarity_data.get("depth_min").getAsInt();
					int max_depth = rarity_data.get("depth_max").getAsInt();
					
					for(int d = min_depth; d <= max_depth; d++){
						LASER_DRILL_ENTRIES[d].add(new LaserDrillEntry(color, item, amount, whitelist, blacklist));
					}
				}
			}
			j.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString(){
		return stack.getDisplayName();
	}
}

package com.buuz135.industrial.recipe;

import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.recipe.serializer.GenericSerializer;
import com.hrznstudio.titanium.recipe.serializer.SerializableRecipe;
import com.hrznstudio.titanium.util.TagUtil;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class LaserDrillOreRecipe extends SerializableRecipe {

    public static GenericSerializer<LaserDrillOreRecipe> SERIALIZER = new GenericSerializer<>(new ResourceLocation(Reference.MOD_ID, "laser_drill_ore"), LaserDrillOreRecipe.class);
    public static List<LaserDrillOreRecipe> RECIPES = new ArrayList<>();

    public static void init(){
        createWithDefault("coal", Blocks.COAL_ORE, 15, 5, 132, 10 ,4);
        createWithDefault("iron", Blocks.IRON_ORE, 12, 5, 68, 20, 3);
        createWithDefault("redstone", Blocks.REDSTONE_ORE, 14, 5, 16, 28, 4);
        new LaserDrillOreRecipe("gold", Ingredient.fromItems(Blocks.GOLD_ORE), 4, null,
                new LaserDrillRarity(new RegistryKey[]{Biomes.BADLANDS, Biomes.BADLANDS_PLATEAU, Biomes.ERODED_BADLANDS, Biomes.MODIFIED_BADLANDS_PLATEAU, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.WOODED_BADLANDS_PLATEAU}, new RegistryKey[0], 32, 80, 16),
                new LaserDrillRarity(new RegistryKey[0], LaserDrillRarity.END, 5, 32, 6),
                new LaserDrillRarity(new RegistryKey[0], LaserDrillRarity.END, 0, 255, 1));
        createWithDefault("lapis", Blocks.LAPIS_ORE, 11, 13, 34, 14, 2);
        new LaserDrillOreRecipe("emerald", Ingredient.fromItems(Blocks.GOLD_ORE), 5, null,
                new LaserDrillRarity(new RegistryKey[]{Biomes.MOUNTAINS, Biomes.MOUNTAIN_EDGE, Biomes.GRAVELLY_MOUNTAINS, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.SNOWY_MOUNTAINS, Biomes.SNOWY_TAIGA_MOUNTAINS}, new RegistryKey[0], 5, 29, 8),
                new LaserDrillRarity(new RegistryKey[0], LaserDrillRarity.END, 0, 255, 1));
        createWithDefault("diamond", Blocks.DIAMOND_ORE, 3, 5, 16, 4, 1);
        new LaserDrillOreRecipe("quartz", Ingredient.fromItems(Blocks.NETHER_QUARTZ_ORE), 0, null,
                new LaserDrillRarity(LaserDrillRarity.NETHER, new RegistryKey[0], 7, 117, 12),
                new LaserDrillRarity(new RegistryKey[0], LaserDrillRarity.END, 0, 255, 1));
        new LaserDrillOreRecipe("glowstone", Ingredient.fromItems(Blocks.GLOWSTONE), 4, null,
                new LaserDrillRarity(LaserDrillRarity.NETHER, new RegistryKey[0], 7, 117, 8),
                new LaserDrillRarity(new RegistryKey[0], LaserDrillRarity.END, 0, 255, 1));
        new LaserDrillOreRecipe("uranium", Ingredient.fromTag(TagUtil.getItemTag(new ResourceLocation("forge", "ores/uranium"))), 5, new ResourceLocation("forge", "ores/uranium"),
                new LaserDrillRarity(LaserDrillRarity.NETHER, new RegistryKey[0], 5, 29, 5),
                new LaserDrillRarity(new RegistryKey[0], LaserDrillRarity.END, 0, 255, 1));
        createWithDefault("sulfur", 4, 5, 10, 6, 1);
        createWithDefault("galena", 10, 15, 30, 6, 1);
        new LaserDrillOreRecipe("iridium", Ingredient.fromTag(TagUtil.getItemTag(new ResourceLocation("forge", "ores/iridium"))), 0, new ResourceLocation("forge", "ores/iridium"),
                new LaserDrillRarity(LaserDrillRarity.END, new RegistryKey[0], 5, 68, 8),
                new LaserDrillRarity(new RegistryKey[0], LaserDrillRarity.END, 0, 255, 1));
        createWithDefault("ruby", 14, 30, 70, 6, 1);
        createWithDefault("sapphire", 11, 30, 70, 6, 1);
        createWithDefault("peridot", 13, 30, 70, 6, 1);
        createWithDefault("sodalite", 11, 30, 70, 6, 1);
        createWithDefault("bauxite", 12, 50, 100, 6, 1);
        createWithDefault("pyrite", 12, 30, 70, 3, 1);
        createWithDefault("cinnabar", 14, 30, 70, 2, 1);
        createEnd("tungsten", 15, 20, 70, 4);
        createEnd("sheldonite", 0, 30, 70, 6);
        createWithDefault("platinum", 3, 5, 16, 3, 1);
        createWithDefault("tetrahedrite", 14, 60, 90, 4, 1);
        createWithDefault("tin", 8, 64, 96, 8, 2);
        createWithDefault("lead", 10, 10, 40, 6, 1);
        createWithDefault("silver", 7, 10, 40, 5, 1);
        createWithDefault("copper", 1, 35, 65, 10, 2);
        createWithDefault("aluminum", 12, 68, 84, 5, 1);
        createWithDefault("nickel", 12, 5, 68, 4, 1);
        createEnd("draconium", 10, 60, 95, 10);
        createWithDefault("yellorium", 4, 16, 68, 3, 1);
        createNether("cobalt", 11, 34, 96, 8);
        createNether("ardite", 4, 89, 116, 8);
    }

    public static LaserDrillOreRecipe createWithDefault(String name, IItemProvider output, int color, int min, int max, int weight, int defaultWeight){
        return createWithDefault(name, Ingredient.fromItems(output), color, min, max, weight, defaultWeight, null);
    }

    public static LaserDrillOreRecipe createWithDefault(String name, int color, int min, int max, int weight, int defaultWeight){
        ResourceLocation rl = new ResourceLocation("forge", "ores/" + name);
        return createWithDefault(name, Ingredient.fromTag(TagUtil.getItemTag(rl)), color, min, max, weight, defaultWeight, rl);
    }

    public static LaserDrillOreRecipe createWithDefault(String name, Ingredient output, int color, int min, int max, int weight, int defaultWeight, ResourceLocation isTag){
        return new LaserDrillOreRecipe(name, output, color, isTag,
                new LaserDrillRarity(new RegistryKey[0], LaserDrillRarity.END, min, max, weight),
                new LaserDrillRarity(new RegistryKey[0], LaserDrillRarity.END, 0, 255, defaultWeight));
    }

    public static LaserDrillOreRecipe createEnd(String name, int color, int min, int max, int weight){
        ResourceLocation rl = new ResourceLocation("forge", "ores/" + name);
        return createEnd(name, Ingredient.fromTag(TagUtil.getItemTag(rl)), color, min, max, weight, rl);
    }

    public static LaserDrillOreRecipe createEnd(String name, Ingredient output, int color, int min, int max, int weight, ResourceLocation isTag){
        return new LaserDrillOreRecipe(name, output, color, isTag,
                new LaserDrillRarity( LaserDrillRarity.END,new RegistryKey[0], min, max, weight));
    }

    public static LaserDrillOreRecipe createNether(String name, int color, int min, int max, int weight){
        ResourceLocation rl = new ResourceLocation("forge", "ores/" + name);
        return createNether(name, Ingredient.fromTag(TagUtil.getItemTag(rl)), color, min, max, weight, rl);
    }

    public static LaserDrillOreRecipe createNether(String name, Ingredient output, int color, int min, int max, int weight, ResourceLocation isTag){
        return new LaserDrillOreRecipe(name, output, color,isTag,
                new LaserDrillRarity(LaserDrillRarity.NETHER,new RegistryKey[0], min, max, weight));
    }

    public Ingredient output;
    public LaserDrillRarity[] rarity;
    public int pointer = 0;
    public Ingredient catalyst;
    private ResourceLocation isTag;

    public LaserDrillOreRecipe(String name, Ingredient output, Ingredient catalyst, ResourceLocation isTag, LaserDrillRarity... rarity) {
        super(new ResourceLocation(Reference.MOD_ID, name));
        this.output = output;
        this.catalyst = catalyst;
        this.rarity = rarity;
        this.isTag = isTag;
        RECIPES.add(this);
    }

    public LaserDrillOreRecipe(String name,Ingredient output, int color, ResourceLocation isTag, LaserDrillRarity... rarity) { //TODO Add lenses
        this(name, output, Ingredient.fromItems(ModuleCore.LASER_LENS[color]),isTag, rarity);
    }

    public LaserDrillOreRecipe(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return false;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public GenericSerializer<? extends SerializableRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType() {
        return SERIALIZER.getRecipeType();
    }

    @Override
    public Pair<ICondition, IConditionSerializer> getOutputCondition() {
        if (isTag != null){
            return Pair.of(new NotCondition(new TagEmptyCondition(isTag)), NotCondition.Serializer.INSTANCE);
        }
        return null;
    }
}

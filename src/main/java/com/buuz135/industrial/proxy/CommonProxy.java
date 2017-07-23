package com.buuz135.industrial.proxy;

import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.utils.CraftingUtils;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Random;

public class CommonProxy {

    public static Random random;

    public static DamageSource custom = new DamageSource("if_custom");

    public void preInit(FMLPreInitializationEvent event) {
        random = new Random();
        IForgeRegistry<Block> blocks = GameRegistry.findRegistry(Block.class);
        IForgeRegistry<Item> items = GameRegistry.findRegistry(Item.class);
        IForgeRegistry<IRecipe> recipes = GameRegistry.findRegistry(IRecipe.class);
        FluidsRegistry.registerFluids();
        ItemRegistry.registerItems(items);
        BlockRegistry.registerBlocks(blocks, items, recipes);

        MinecraftForge.EVENT_BUS.register(new MeatFeederTickHandler());
        MinecraftForge.EVENT_BUS.register(new MobDeathHandler());

        CustomConfiguration.config = new Configuration(event.getSuggestedConfigurationFile());

    }

    public void init() {
        RecipeUtils.generateConstants();
    }

    public void postInit() {
        CustomConfiguration.sync();
        CraftingUtils.generateCrushedRecipes();
    }
}

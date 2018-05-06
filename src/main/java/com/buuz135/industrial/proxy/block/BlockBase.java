package com.buuz135.industrial.proxy.block;

import com.buuz135.industrial.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

public class BlockBase extends Block {

    public static final List<BlockBase> BLOCKS = new ArrayList<>();

    public BlockBase(String name) {
        super(Material.ROCK);
        this.setRegistryName(new ResourceLocation(Reference.MOD_ID, name));
        this.setUnlocalizedName(Reference.MOD_ID + "." + name);
        BLOCKS.add(this);
    }

    public void registerBlock(IForgeRegistry<Block> blocks) {
        blocks.register(this);
    }

    public void registerItem(IForgeRegistry<Item> items) {
        items.register(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }

    public void registerRender() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }

    public void createRecipe() {

    }
}

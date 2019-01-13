package com.buuz135.industrial.item;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IForgeRegistry;

public class IFFoodItem extends ItemFood {
    public IFFoodItem(String name, int food, float saturation) {
        super(food, saturation, false);
        setTranslationKey(Reference.MOD_ID + "_" + name);
        setCreativeTab(IndustrialForegoing.creativeTab);
        setRegistryName(Reference.MOD_ID, name);
    }

    @Override
    public CreativeTabs[] getCreativeTabs() {
        return new CreativeTabs[]{
                IndustrialForegoing.creativeTab,
                CreativeTabs.FOOD
        };
    }

    public void register(IForgeRegistry<Item> items) {
        items.register(this);
    }

    public void registerRender() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }

    public void createRecipe() {

    }
}
package com.buuz135.industrial.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;

public class LaserLensItem extends IFCustomItem {

    public LaserLensItem() {
        super("laser_lens");
        setMaxStackSize(1);
        setHasSubtypes(true);
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
        for (int i = 0; i < 16; ++i) subItems.add(new ItemStack(this, 1, i));
    }

    @Override
    public void registerRender() {
        for (int i = 0; i < 16; ++i)
            ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(this.getRegistryName().toString() + i, "inventory"));
    }

}

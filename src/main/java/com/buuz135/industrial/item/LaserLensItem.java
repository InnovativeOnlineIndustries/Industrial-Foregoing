package com.buuz135.industrial.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

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
    public void register() {
        super.register();
        for (int i = 0; i < 16; ++i)
            GameRegistry.addRecipe(new ShapedRecipes(3, 3, new ItemStack[]{ItemStack.EMPTY, new ItemStack(Items.IRON_INGOT), ItemStack.EMPTY,
                    new ItemStack(Items.IRON_INGOT), new ItemStack(Blocks.STAINED_GLASS_PANE, 1, i), new ItemStack(Items.IRON_INGOT),
                    ItemStack.EMPTY, new ItemStack(Items.IRON_INGOT), ItemStack.EMPTY}, new ItemStack(this, 1, i)));
    }

    @Override
    public void registerRender() {
        for (int i = 0; i < 16; ++i)
            ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(this.getRegistryName().toString() + i, "inventory"));
    }

}

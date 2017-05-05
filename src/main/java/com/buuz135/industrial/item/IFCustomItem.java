package com.buuz135.industrial.item;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class IFCustomItem extends Item {


    public IFCustomItem(String name) {
        setUnlocalizedName(name);
        setCreativeTab(IndustrialForegoing.creativeTab);
        setRegistryName(Reference.MOD_ID, name);
    }

    public void register() {
        GameRegistry.register(this);
        //TODO Add recipe handenling
    }

    public void registerRender() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }
}

package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.tile.agriculture.MobSlaughterFactoryTile;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.config.Configuration;

public class MobSlaughterFactoryBlock extends CustomOrientedBlock<MobSlaughterFactoryTile> {

    private int meatValue;

    public MobSlaughterFactoryBlock() {
        super("mob_slaughter_factory", MobSlaughterFactoryTile.class, Material.ROCK,1000,40);
    }

    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        meatValue = CustomConfiguration.config.getInt("meatValue", "machines"+Configuration.CATEGORY_SPLITTER+this.getRegistryName().getResourcePath().toString(),5, 1, Integer.MAX_VALUE,"Machine can perform a work action");
    }
}

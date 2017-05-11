package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.tile.agriculture.MobSlaughterFactoryTile;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.config.Configuration;

public class MobSlaughterFactoryBlock extends CustomOrientedBlock<MobSlaughterFactoryTile> {

    private float meatValue;

    public MobSlaughterFactoryBlock() {
        super("mob_slaughter_factory", MobSlaughterFactoryTile.class, Material.ROCK, 1000, 40);
    }

    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        meatValue = CustomConfiguration.config.getFloat("meatValue", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 5, 1, Integer.MAX_VALUE, "Mob health multiplier, mobHealth * meatValue = meat mb produced");
    }

    public float getMeatValue() {
        return meatValue;
    }
}

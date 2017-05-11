package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.config.Configuration;
import net.ndrei.teslacorelib.blocks.OrientedBlock;
import net.ndrei.teslacorelib.tileentities.SidedTileEntity;

public class CustomOrientedBlock<T extends SidedTileEntity> extends OrientedBlock {

    private boolean workDisabled;
    private int energyForWork;
    private int energyRate;

    protected CustomOrientedBlock(String registryName, Class teClass) {
        super(Reference.MOD_ID, IndustrialForegoing.creativeTab, registryName, teClass);
    }

    protected CustomOrientedBlock(String registryName, Class teClass, Material material, int energyForWork, int energyRate) {
        super(Reference.MOD_ID, IndustrialForegoing.creativeTab, registryName, teClass, material);
        this.energyForWork = energyForWork;
        this.energyRate = energyRate;
    }

    public void getMachineConfig() {
        workDisabled = CustomConfiguration.config.getBoolean("workDisabled", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), false, "Machine can perform a work action");
        Class<T> type = null;
        if (energyForWork != 0 && energyRate != 0) {
            energyForWork = CustomConfiguration.config.getInt("energyForWork", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), energyForWork, 1, Integer.MAX_VALUE, "How much energy needs a machine to work");
            energyRate = CustomConfiguration.config.getInt("energyRate", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), energyRate, 1, Integer.MAX_VALUE, "Energy input rate of a machine");
        }
    }

    public boolean isWorkDisabled() {
        return workDisabled;
    }

    public int getEnergyForWork() {
        return energyForWork;
    }

    public int getEnergyRate() {
        return energyRate;
    }
}

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

    protected CustomOrientedBlock(String registryName, Class teClass) {
        super(Reference.MOD_ID, IndustrialForegoing.creativeTab, registryName, teClass);
    }

    protected CustomOrientedBlock(String registryName, Class teClass, Material material) {
        super(Reference.MOD_ID, IndustrialForegoing.creativeTab, registryName, teClass, material);
    }

    public void getMachineConfig(){
        workDisabled = CustomConfiguration.config.getBoolean("workDisabled", Configuration.CATEGORY_GENERAL+Configuration.CATEGORY_SPLITTER+this.getRegistryName().getResourcePath().toString(),false, "Machine can perform a work action");
    }

    public boolean isWorkDisabled() {
        return workDisabled;
    }
}

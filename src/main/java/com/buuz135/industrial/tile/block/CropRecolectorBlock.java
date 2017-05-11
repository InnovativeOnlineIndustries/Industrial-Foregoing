package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.tile.agriculture.CropRecolectorTile;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.config.Configuration;

public class CropRecolectorBlock extends CustomOrientedBlock<CropRecolectorTile> {

    private int sludgeOperation;
    private int treeOperations;

    public CropRecolectorBlock() {
        super("crop_recolector", CropRecolectorTile.class, Material.ROCK, 400, 40);
    }

    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        sludgeOperation = CustomConfiguration.config.getInt("sludgeOperation", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 20, 1, 8000, "How much sludge is produced when the machine does an operation");
        treeOperations = CustomConfiguration.config.getInt("treeOperations", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 25, 1, 64, "Amount of operations done when chopping a tree");
    }

    public int getSludgeOperation() {
        return sludgeOperation;
    }


    public int getTreeOperations() {
        return treeOperations;
    }
}

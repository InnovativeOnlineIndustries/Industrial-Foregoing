package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.crop.CropRecolectorTile;
import net.minecraft.block.material.Material;

public class CropRecolectorBlock extends CustomOrientedBlock<CropRecolectorTile> {

    public CropRecolectorBlock() {
        super("crop_recolector", CropRecolectorTile.class, Material.ROCK);
    }
}

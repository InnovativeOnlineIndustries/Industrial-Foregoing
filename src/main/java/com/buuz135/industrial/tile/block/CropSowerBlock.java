package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.crop.CropSowerTile;
import net.minecraft.block.material.Material;

public class CropSowerBlock extends CustomOrientedBlock<CropSowerTile> {

    public CropSowerBlock() {
        super("crop_sower", CropSowerTile.class, Material.ROCK,400,40);
    }
}

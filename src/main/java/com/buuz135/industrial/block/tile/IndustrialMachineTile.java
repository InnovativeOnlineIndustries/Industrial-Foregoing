package com.buuz135.industrial.block.tile;

import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.MachineTile;

public abstract class IndustrialMachineTile<T extends IndustrialMachineTile<T>> extends MachineTile<T> {

    public IndustrialMachineTile(BasicTileBlock<T> basicTileBlock) {
        super(basicTileBlock);
    }

}

package com.buuz135.industrial.block;

import com.hrznstudio.titanium.block.BlockTileBase;
import com.hrznstudio.titanium.block.tile.TileBase;

public abstract class OrientedBlock<T extends TileBase> extends BlockTileBase<T> {
    public OrientedBlock(String name, Builder properties, Class<T> tileClass) {
        super(name, properties, tileClass);
    }
}

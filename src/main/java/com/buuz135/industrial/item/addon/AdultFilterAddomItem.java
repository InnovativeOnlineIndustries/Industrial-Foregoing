package com.buuz135.industrial.item.addon;

import com.buuz135.industrial.tile.agriculture.AnimalIndependenceSelectorTile;
import net.ndrei.teslacorelib.tileentities.SidedTileEntity;

public class AdultFilterAddomItem extends CustomAddon {

    public AdultFilterAddomItem() {
        super("adult_filter");
    }

    @Override
    public boolean canBeAddedTo(SidedTileEntity machine) {
        return machine instanceof AnimalIndependenceSelectorTile;
    }
}

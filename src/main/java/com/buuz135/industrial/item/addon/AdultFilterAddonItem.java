package com.buuz135.industrial.item.addon;

import com.buuz135.industrial.tile.agriculture.AnimalIndependenceSelectorTile;
import net.ndrei.teslacorelib.tileentities.SidedTileEntity;

public class AdultFilterAddonItem extends CustomAddon {

    public AdultFilterAddonItem() {
        super("adult_filter");
    }

    @Override
    public boolean canBeAddedTo(SidedTileEntity machine) {
        return machine instanceof AnimalIndependenceSelectorTile;
    }
}

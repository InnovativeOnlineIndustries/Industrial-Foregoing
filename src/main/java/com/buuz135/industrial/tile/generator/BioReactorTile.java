package com.buuz135.industrial.tile.generator;

import com.buuz135.industrial.api.recipe.BioReactorEntry;
import com.buuz135.industrial.api.recipe.IReactorEntry;
import com.buuz135.industrial.proxy.FluidsRegistry;
import net.minecraftforge.fluids.Fluid;

import java.util.List;

public class BioReactorTile extends AbstractReactorTile {

    public BioReactorTile() {
        super(BioReactorTile.class.getName().hashCode());
    }

    @Override
    public List<IReactorEntry> getReactorsEntries() {
        return BioReactorEntry.BIO_REACTOR_ENTRIES;
    }

    @Override
    public Fluid getProducedFluid() {
        return FluidsRegistry.BIOFUEL;
    }
}

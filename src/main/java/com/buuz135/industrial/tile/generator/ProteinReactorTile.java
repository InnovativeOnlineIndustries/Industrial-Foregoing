package com.buuz135.industrial.tile.generator;

import com.buuz135.industrial.api.recipe.IReactorEntry;
import com.buuz135.industrial.api.recipe.ProteinReactorEntry;
import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.proxy.FluidsRegistry;
import net.minecraftforge.fluids.Fluid;

import java.util.List;

public class ProteinReactorTile extends AbstractReactorTile {

    public ProteinReactorTile() {
        super(ProteinReactorTile.class.getName().hashCode());
    }

    @Override
    public List<IReactorEntry> getReactorsEntries() {
        return ProteinReactorEntry.PROTEIN_REACTOR_ENTRIES;
    }

    @Override
    public Fluid getProducedFluid() {
        return FluidsRegistry.PROTEIN;
    }

    @Override
    public int amountProduced() {
        return BlockRegistry.proteinReactorBlock.getBaseAmount();
    }
}

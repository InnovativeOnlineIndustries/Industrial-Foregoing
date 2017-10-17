package com.buuz135.industrial.tile.generator;

import com.buuz135.industrial.proxy.FluidsRegistry;
import net.minecraftforge.fluids.Fluid;

public class ProteinGeneratorTile extends AbstractReactorGeneratorTile {

    public ProteinGeneratorTile() {//1440000
        super(ProteinReactorTile.class.getName().hashCode(), 320);
    }

    @Override
    public Fluid getFluid() {
        return FluidsRegistry.PROTEIN;
    }
}

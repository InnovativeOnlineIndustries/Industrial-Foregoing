package com.buuz135.industrial.tile.generator;

import com.buuz135.industrial.proxy.FluidsRegistry;
import net.minecraftforge.fluids.Fluid;

public class BiofuelGeneratorTile extends AbstractReactorGeneratorTile {

    public BiofuelGeneratorTile() {
        super(BioReactorTile.class.getName().hashCode(), 160);
    }

    @Override
    public Fluid getFluid() {
        return FluidsRegistry.BIOFUEL;
    }
}

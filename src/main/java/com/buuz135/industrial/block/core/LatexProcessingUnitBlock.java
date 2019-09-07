package com.buuz135.industrial.block.core;

import com.buuz135.industrial.block.core.tile.LatexProcessingUnitTile;
import com.buuz135.industrial.config.MachineCoreConfig;
import com.hrznstudio.titanium.annotation.config.ConfigFile;
import com.hrznstudio.titanium.annotation.config.ConfigVal;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.block.BlockRotation;
import net.minecraft.block.Blocks;

import javax.annotation.Nonnull;

@ConfigFile.Child(MachineCoreConfig.class)
public class LatexProcessingUnitBlock extends BlockRotation<LatexProcessingUnitTile> {

    @ConfigVal(comment = "Power consumed every tick when the machine is working")
    public static int POWER_CONSUMED_EVERY_TICK = 20;

    public LatexProcessingUnitBlock() {
        super("latex_processing_unit", Properties.from(Blocks.STONE), LatexProcessingUnitTile.class);
    }

    @Override
    public IFactory<LatexProcessingUnitTile> getTileEntityFactory() {
        return LatexProcessingUnitTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }
}

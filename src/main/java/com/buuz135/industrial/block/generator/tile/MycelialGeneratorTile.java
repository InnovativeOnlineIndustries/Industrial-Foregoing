package com.buuz135.industrial.block.generator.tile;

import com.buuz135.industrial.block.generator.MycelialGeneratorType;
import com.buuz135.industrial.block.tile.IndustrialGeneratorTile;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import net.minecraft.item.DyeColor;

import javax.annotation.Nonnull;

public class MycelialGeneratorTile extends IndustrialGeneratorTile<MycelialGeneratorTile> {

    @Save
    private int powerGeneration;
    private MycelialGeneratorType type;

    public MycelialGeneratorTile(BasicTileBlock<MycelialGeneratorTile> basicTileBlock, MycelialGeneratorType type) {
        super(basicTileBlock);
        this.type = type;
        this.powerGeneration = 10;
    }

    @Override
    public int consumeFuel() {
        return 0;
    }

    @Override
    public boolean canStart() {
        return false;
    }

    @Override
    public int getEnergyProducedEveryTick() {
        return 0;
    }

    @Override
    public ProgressBarComponent<MycelialGeneratorTile> getProgressBar() {
        return new ProgressBarComponent<MycelialGeneratorTile>(30, 20, 0, 100)
                .setComponentHarness(this)
                .setBarDirection(ProgressBarComponent.BarDirection.VERTICAL_UP)
                .setColor(DyeColor.CYAN);
    }

    @Override
    public int getEnergyCapacity() {
        return 100000;
    }

    @Override
    public int getExtractingEnergy() {
        return 0;
    }

    @Nonnull
    @Override
    public MycelialGeneratorTile getSelf() {
        return this;
    }

    @Override
    public boolean isSmart() {
        return true;
    }
}

package com.buuz135.industrial.tile;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.ndrei.teslacorelib.tileentities.ElectricGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CustomGeneratorMachine extends ElectricGenerator {

    protected CustomGeneratorMachine(int typeId) {
        super(typeId);
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        List<EnumFacing> facings = new ArrayList<>();
        facings.addAll(Arrays.asList(EnumFacing.values()));
        this.sideConfig.setSidesForColor(EnumDyeColor.LIGHT_BLUE, facings);
    }

    @Override
    protected long getEnergyOutputRate() {
        return Integer.MAX_VALUE;
    }
}

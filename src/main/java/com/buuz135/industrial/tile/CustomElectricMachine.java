package com.buuz135.industrial.tile;

import com.buuz135.industrial.tile.block.CustomOrientedBlock;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.ndrei.teslacorelib.tileentities.ElectricMachine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomElectricMachine extends ElectricMachine {

    protected CustomElectricMachine(int typeId) {
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
    protected float performWork() {
        return 0;
    }

    @Override
    protected int getEnergyForWork() {
        return this.getBlockType() instanceof CustomOrientedBlock ? ((CustomOrientedBlock) this.getBlockType()).getEnergyForWork() : Integer.MAX_VALUE;
    }

    @Override
    protected int getEnergyForWorkRate() {
        return this.getBlockType() instanceof CustomOrientedBlock ? ((CustomOrientedBlock) this.getBlockType()).getEnergyRate() : Integer.MAX_VALUE;
    }

}

package com.buuz135.industrial.tile;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.ndrei.teslacorelib.tileentities.SidedTileEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CustomSidedTileEntity extends SidedTileEntity {

    protected CustomSidedTileEntity(int entityTypeId) {
        super(entityTypeId);
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        List<EnumFacing> facings = new ArrayList<>();
        facings.addAll(Arrays.asList(EnumFacing.values()));
        Arrays.stream(EnumDyeColor.values()).forEach(enumDyeColor -> this.getSideConfig().setSidesForColor(enumDyeColor, facings));
    }
}

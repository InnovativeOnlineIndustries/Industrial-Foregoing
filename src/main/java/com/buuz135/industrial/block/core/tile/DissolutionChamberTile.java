package com.buuz135.industrial.block.core.tile;

import com.buuz135.industrial.block.tile.IndustrialProcessingTile;
import com.buuz135.industrial.module.ModuleCore;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.tile.fluid.SidedFluidTank;
import com.hrznstudio.titanium.block.tile.inventory.SidedInvHandler;
import net.minecraft.item.DyeColor;
import org.apache.commons.lang3.tuple.Pair;

public class DissolutionChamberTile extends IndustrialProcessingTile {

    @Save
    private SidedInvHandler input;
    @Save
    private SidedFluidTank inputFluid;
    @Save
    private SidedInvHandler output;
    @Save
    private SidedFluidTank outputFluid;

    public DissolutionChamberTile() {
        super(ModuleCore.DISSOLUTION_CHAMBER, 100, 100, 100);
        int slotSpacing = 22;
        this.addInventory(input = (SidedInvHandler) new SidedInvHandler("input", 32, 20, 8, 0).
                setColor(DyeColor.LIGHT_BLUE).
                setSlotPosition(integer -> {
                    switch (integer) {
                        case 1:
                            return Pair.of(slotSpacing, 0);
                        case 2:
                            return Pair.of(slotSpacing * 2, 0);
                        case 3:
                            return Pair.of(0, slotSpacing);
                        case 4:
                            return Pair.of(slotSpacing * 2, slotSpacing);
                        case 5:
                            return Pair.of(0, slotSpacing * 2);
                        case 6:
                            return Pair.of(slotSpacing, slotSpacing * 2);
                        case 7:
                            return Pair.of(slotSpacing * 2, slotSpacing * 2);
                        default:
                            return Pair.of(0, 0);
                    }
                }).
                setSlotLimit(1));
    }

    @Override
    public boolean canIncrease() {
        return false;
    }

    @Override
    public Runnable onFinish() {
        return null;
    }

    @Override
    protected int getTickPower() {
        return 0;
    }
}

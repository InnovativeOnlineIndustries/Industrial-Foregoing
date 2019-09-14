package com.buuz135.industrial.block.core.tile;

import com.buuz135.industrial.block.tile.IndustrialProcessingTile;
import com.buuz135.industrial.module.ModuleCore;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.tile.fluid.PosFluidTank;
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
        super(ModuleCore.DISSOLUTION_CHAMBER, 102, 41, 100);
        int slotSpacing = 22;
        int offset = 2;
        this.addInventory(input = (SidedInvHandler) new SidedInvHandler("input", 34, 19, 8, 0).
                setColor(DyeColor.LIGHT_BLUE).
                setSlotPosition(integer -> {
                    switch (integer) {
                        case 1:
                            return Pair.of(slotSpacing, -offset);
                        case 2:
                            return Pair.of(slotSpacing * 2, 0);
                        case 3:
                            return Pair.of(-offset, slotSpacing);
                        case 4:
                            return Pair.of(slotSpacing * 2 + offset, slotSpacing);
                        case 5:
                            return Pair.of(0, slotSpacing * 2);
                        case 6:
                            return Pair.of(slotSpacing, slotSpacing * 2 + offset);
                        case 7:
                            return Pair.of(slotSpacing * 2, slotSpacing * 2);
                        default:
                            return Pair.of(0, 0);
                    }
                }).
                setSlotLimit(1).
                setTile(this));
        this.addTank(this.inputFluid = (SidedFluidTank) new SidedFluidTank("input_fluid", 8000, 33 + slotSpacing, 18 + slotSpacing, 1).
                setColor(DyeColor.LIME).
                setTankType(PosFluidTank.Type.SMALL).
                setTile(this).
                setTankAction(PosFluidTank.Action.FILL)
        );
        this.addInventory(this.output = (SidedInvHandler) new SidedInvHandler("output", 129, 22, 3, 2).
                setColor(DyeColor.ORANGE).
                setRange(1, 3).
                setTile(this));
        this.addTank(this.outputFluid = (SidedFluidTank) new SidedFluidTank("output_fluid", 16000, 149, 20, 3).
                setColor(DyeColor.MAGENTA).
                setTile(this).
                setTankAction(PosFluidTank.Action.DRAIN));
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

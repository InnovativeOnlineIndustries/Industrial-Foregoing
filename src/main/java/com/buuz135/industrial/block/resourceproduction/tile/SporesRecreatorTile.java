package com.buuz135.industrial.block.resourceproduction.tile;

import com.buuz135.industrial.block.tile.IndustrialProcessingTile;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.tile.fluid.PosFluidTank;
import com.hrznstudio.titanium.block.tile.fluid.SidedFluidTank;
import com.hrznstudio.titanium.block.tile.inventory.SidedInvHandler;
import com.hrznstudio.titanium.util.ItemHandlerUtil;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class SporesRecreatorTile extends IndustrialProcessingTile {

    @Save
    private SidedFluidTank tank;
    @Save
    private SidedInvHandler input;
    @Save
    private SidedInvHandler output;

    public SporesRecreatorTile() {
        super(ModuleResourceProduction.SPORES_RECREATOR, 79, 40);
        addTank(tank = (SidedFluidTank) new SidedFluidTank("water", 1000, 31, 20, 0).
                setColor(DyeColor.CYAN).
                setTankAction(PosFluidTank.Action.FILL).
                setTile(this).
                setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(Fluids.WATER))
        );
        addInventory(input = (SidedInvHandler) new SidedInvHandler("input", 53, 22, 3, 1)
                .setColor(DyeColor.BLUE)
                .setRange(1, 3)
                .setTile(this)
                .setInputFilter((stack, integer) -> stack.getItem().isIn(Tags.Items.MUSHROOMS))
                .setOutputFilter((stack, integer) -> false)
        );
        addInventory(output = (SidedInvHandler) new SidedInvHandler("output", 110, 22, 9, 2)
                .setColor(DyeColor.ORANGE)
                .setRange(3, 3)
                .setTile(this)
                .setInputFilter((stack, integer) -> false)
        );
    }

    @Override
    public boolean canIncrease() {
        return !ItemHandlerUtil.getFirstItem(input).isEmpty() && tank.getFluidAmount() >= 500 && ItemHandlerHelper.insertItem(output, new ItemStack(ItemHandlerUtil.getFirstItem(input).getItem(), 2), true).isEmpty();
    }

    @Override
    public Runnable onFinish() {
        return () -> {
            ItemStack outputStack = new ItemStack(ItemHandlerUtil.getFirstItem(input).getItem(), 2);
            tank.drainForced(500, IFluidHandler.FluidAction.EXECUTE);
            ItemHandlerUtil.getFirstItem(input).shrink(1);
            ItemHandlerHelper.insertItem(output, outputStack, false);
        };
    }

    @Override
    protected int getTickPower() {
        return 40;
    }
}

package com.buuz135.industrial.block.resourceproduction.tile;

import com.buuz135.industrial.block.tile.IndustrialProcessingTile;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.tile.fluid.PosFluidTank;
import com.hrznstudio.titanium.block.tile.fluid.SidedFluidTank;
import com.hrznstudio.titanium.block.tile.inventory.SidedInvHandler;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class SludgeRefinerTile extends IndustrialProcessingTile {

    @Save
    private SidedFluidTank sludge;
    @Save
    private SidedInvHandler output;

    public SludgeRefinerTile() {
        super(ModuleResourceProduction.SLUDGE_REFINER, 53, 40);
        addTank(sludge = (SidedFluidTank) new SidedFluidTank("sludge", 8000, 31, 20, 0)
                .setColor(DyeColor.MAGENTA)
                .setTile(this)
                .setTankAction(PosFluidTank.Action.FILL)
        );
        addInventory(output = (SidedInvHandler) new SidedInvHandler("output", 80, 22, 5 * 3, 1)
                .setColor(DyeColor.ORANGE)
                .setRange(5, 3)
                .setInputFilter((stack, integer) -> false)
                .setTile(this)
        );
    }

    @Override
    public boolean canIncrease() {
        return sludge.getFluidAmount() >= 500;
    }

    @Override
    public Runnable onFinish() {
        return () -> {
            Item item = this.world.getTags().getItems().get(IndustrialTags.Items.SLUDGE_OUTPUT.getId()).getRandomElement(this.world.rand);
            if (item != null && ItemHandlerHelper.insertItem(output, new ItemStack(item), true).isEmpty()) {
                sludge.drainForced(500, IFluidHandler.FluidAction.EXECUTE);
                ItemHandlerHelper.insertItem(output, new ItemStack(item), false);
            }
        };
    }

    @Override
    protected int getTickPower() {
        return 20;
    }
}

package com.buuz135.industrial.block.agriculturehusbandry.tile;

import com.buuz135.industrial.api.plant.PlantRecollectable;
import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.IndustrialWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.registry.IFRegistries;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.tile.fluid.SidedFluidTank;
import com.hrznstudio.titanium.block.tile.inventory.SidedInvHandler;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;
import java.util.Optional;

public class PlantGathererTile extends IndustrialAreaWorkingTile {

    @Save
    private SidedInvHandler output;
    @Save
    private SidedFluidTank tank;

    public PlantGathererTile() {
        super(ModuleAgricultureHusbandry.PLANT_GATHERER, 100, RangeManager.RangeType.BEHIND);
        addInventory(output = (SidedInvHandler) new SidedInvHandler("output", 70, 22, 3 * 4, 0)
                .setColor(DyeColor.ORANGE)
                .setRange(4, 3)
                .setTile(this));
        addTank(tank = (SidedFluidTank) new SidedFluidTank("sludge", 1000, 43, 20, 1)
                .setColor(DyeColor.PINK)
                .setTile(this));
    }

    @Override
    public IndustrialWorkingTile.WorkAction work() {
        Optional<PlantRecollectable> optional = IFRegistries.PLANT_RECOLLECTABLES_REGISTRY.getValues().stream().filter(plantRecollectable -> plantRecollectable.canBeHarvested(this.world, getPointedBlockPos(), this.world.getBlockState(getPointedBlockPos()))).findFirst();
        if (optional.isPresent()) {
            List<ItemStack> drops = optional.get().doHarvestOperation(this.world, getPointedBlockPos(), this.world.getBlockState(getPointedBlockPos()));
            drops.forEach(stack -> ItemHandlerHelper.insertItem(output, stack, false));
            if (optional.get().shouldCheckNextPlant(this.world, getPointedBlockPos(), this.world.getBlockState(getPointedBlockPos()))) {
                increasePointer();
            }
            return new WorkAction(0.1f, 0);
        }
        increasePointer();
        return new WorkAction(.3f, 0);
    }
}

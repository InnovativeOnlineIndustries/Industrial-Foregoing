package com.buuz135.industrial.block.agriculturehusbandry.tile;

import com.buuz135.industrial.api.plant.PlantRecollectable;
import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.IndustrialWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.registry.IFRegistries;
import com.buuz135.industrial.utils.BlockUtils;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.tile.fluid.SidedFluidTank;
import com.hrznstudio.titanium.block.tile.inventory.SidedInvHandler;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;
import java.util.Optional;

public class PlantGathererTile extends IndustrialAreaWorkingTile {

    @Save
    private SidedInvHandler output;
    @Save
    private SidedFluidTank tank;

    public PlantGathererTile() {
        super(ModuleAgricultureHusbandry.PLANT_GATHERER, RangeManager.RangeType.BEHIND);
        addInventory(output = (SidedInvHandler) new SidedInvHandler("output", 70, 22, 3 * 5, 0)
                .setColor(DyeColor.ORANGE)
                .setRange(5, 3)
                .setTile(this));
        addTank(tank = (SidedFluidTank) new SidedFluidTank("sludge", 8000, 45, 20, 1)
                .setColor(DyeColor.MAGENTA)
                .setTile(this));
    }

    @Override
    public IndustrialWorkingTile.WorkAction work() {
        if (hasEnergy(400)) {
            int amount = Math.max(1, BlockUtils.getBlockPosInAABB(getWorkingArea().getBoundingBox()).size() / 4);
            for (int i = 0; i < amount; i++) {
                if (isLoaded(getPointedBlockPos())) {
                    Optional<PlantRecollectable> optional = IFRegistries.PLANT_RECOLLECTABLES_REGISTRY.getValues().stream().filter(plantRecollectable -> plantRecollectable.canBeHarvested(this.world, getPointedBlockPos(), this.world.getBlockState(getPointedBlockPos()))).findFirst();
                    if (optional.isPresent()) {
                        List<ItemStack> drops = optional.get().doHarvestOperation(this.world, getPointedBlockPos(), this.world.getBlockState(getPointedBlockPos()));
                        tank.fill(new FluidStack(ModuleCore.SLUDGE.getSourceFluid(), 10 * drops.size()), IFluidHandler.FluidAction.EXECUTE);
                        drops.forEach(stack -> ItemHandlerHelper.insertItem(output, stack, false));
                        if (optional.get().shouldCheckNextPlant(this.world, getPointedBlockPos(), this.world.getBlockState(getPointedBlockPos()))) {
                            increasePointer();
                        }
                        return new WorkAction(0.3f, 400);
                    }
                }
                increasePointer();
            }
        }
        increasePointer();
        return new WorkAction(1f, 0);
    }

    @Override
    public int getMaxProgress() {
        return 40;
    }
}

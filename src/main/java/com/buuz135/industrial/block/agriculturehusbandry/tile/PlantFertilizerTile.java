package com.buuz135.industrial.block.agriculturehusbandry.tile;

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.module.ModuleCore;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.util.ItemHandlerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;

public class PlantFertilizerTile extends IndustrialAreaWorkingTile<PlantFertilizerTile> {

    @Save
    public SidedInventoryComponent<PlantFertilizerTile> fertilizer;

    public PlantFertilizerTile() {
        super(ModuleAgricultureHusbandry.PLANT_FERTILIZER, RangeManager.RangeType.BEHIND);
        addInventory(fertilizer = (SidedInventoryComponent<PlantFertilizerTile>) new SidedInventoryComponent<PlantFertilizerTile>("fertilizer", 50, 22, 3 * 6, 0).
                setColor(DyeColor.BROWN).
                setInputFilter((stack, integer) -> stack.getItem().equals(ModuleCore.FERTILIZER)).
                setOutputFilter((stack, integer) -> false).
                setRange(6, 3).
                setComponentHarness(this)
        );
    }

    @Override
    public WorkAction work() {
        ItemStack stack = ItemHandlerUtil.getFirstItem(fertilizer);
        if (!stack.isEmpty() && hasEnergy(1000)) {
            BlockPos pointer = getPointedBlockPos();
            if (isLoaded(pointer)) {
                BlockState state = this.world.getBlockState(pointer);
                Block block = state.getBlock();
                if (block instanceof IGrowable) {
                    if (((IGrowable) block).canGrow(world, pointer, state, false) && ((IGrowable) block).canUseBonemeal(world, world.rand, pointer, state)) {
                        stack.shrink(1);
                        ((IGrowable) block).func_225535_a_((ServerWorld) world, world.rand, pointer, state);
                        if (((IGrowable) block).canGrow(world, pointer, state, false)) {
                            return new WorkAction(0.25f, 1000);
                        } else {
                            increasePointer();
                            return new WorkAction(0.5f, 1000);
                        }
                    } else {
                        increasePointer();
                        return new WorkAction(0.5f, 0);
                    }
                }
            }
        }
        increasePointer();
        return new WorkAction(1, 0);
    }

    @Override
    public int getMaxProgress() {
        return 50;
    }

    @Nonnull
    @Override
    public PlantFertilizerTile getSelf() {
        return this;
    }
}

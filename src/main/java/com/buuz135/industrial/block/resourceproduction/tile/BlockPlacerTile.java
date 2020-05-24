package com.buuz135.industrial.block.resourceproduction.tile;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.config.machine.resourceproduction.BlockPlacerConfig;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.IFFakePlayer;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.energy.NBTEnergyHandler;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;

import javax.annotation.Nonnull;

public class BlockPlacerTile extends IndustrialAreaWorkingTile<BlockPlacerTile> {

    private int getMaxProgress;
    private int getPowerPerOperation;

    @Save
    private SidedInventoryComponent<BlockPlacerTile> input;

    public BlockPlacerTile() {
        super(ModuleResourceProduction.BLOCK_PLACER, RangeManager.RangeType.BEHIND, false);
        this.addInventory(this.input = (SidedInventoryComponent<BlockPlacerTile>) new SidedInventoryComponent<BlockPlacerTile>("input", 54, 22, 3 * 6, 0).
                setColor(DyeColor.BLUE).
                setRange(6, 3));
        this.getMaxProgress = BlockPlacerConfig.maxProgress;
        this.getPowerPerOperation = BlockPlacerConfig.powerPerOperation;
    }

    @Override
    public WorkAction work() {
        if (hasEnergy(getPowerPerOperation)) {
            if (isLoaded(getPointedBlockPos()) && world.isAirBlock(getPointedBlockPos())) {
                for (int i = 0; i < input.getSlots(); i++) {
                    if (!input.getStackInSlot(i).isEmpty() && input.getStackInSlot(i).getItem() instanceof BlockItem) {
                        IFFakePlayer fakePlayer = (IFFakePlayer) IndustrialForegoing.getFakePlayer(world, getPointedBlockPos());
                        if (fakePlayer.placeBlock(this.world, getPointedBlockPos(), input.getStackInSlot(i))) {
                            increasePointer();
                            return new WorkAction(1, getPowerPerOperation);
                        }
                    }
                }
            } else {
                increasePointer();
            }
        }
        return new WorkAction(1, 0);
    }

    @Override
    protected IFactory<NBTEnergyHandler> getEnergyHandlerFactory() {
        return () -> new NBTEnergyHandler(this, BlockPlacerConfig.maxStoredPower);
    }

    @Override
    public int getMaxProgress() {
        return getMaxProgress;
    }

    @Nonnull
    @Override
    public BlockPlacerTile getSelf() {
        return this;
    }
}

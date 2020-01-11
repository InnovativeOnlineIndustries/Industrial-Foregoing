package com.buuz135.industrial.block.resourceproduction.tile;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.item.RangeAddonItem;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.IFFakePlayer;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.augment.IAugment;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;

import javax.annotation.Nonnull;

public class BlockPlacerTile extends IndustrialAreaWorkingTile<BlockPlacerTile> {

    @Save
    private SidedInventoryComponent<BlockPlacerTile> input;

    public BlockPlacerTile() {
        super(ModuleResourceProduction.BLOCK_PLACER, RangeManager.RangeType.BEHIND);
        this.addInventory(this.input = (SidedInventoryComponent<BlockPlacerTile>) new SidedInventoryComponent<BlockPlacerTile>("input", 54, 22, 3 * 6, 0).
                setColor(DyeColor.BLUE).
                setRange(6, 3));
    }

    @Override
    public WorkAction work() {
        if (hasEnergy(1000)) {
            if (isLoaded(getPointedBlockPos()) && world.isAirBlock(getPointedBlockPos())) {
                for (int i = 0; i < input.getSlots(); i++) {
                    if (!input.getStackInSlot(i).isEmpty() && input.getStackInSlot(i).getItem() instanceof BlockItem) {
                        IFFakePlayer fakePlayer = (IFFakePlayer) IndustrialForegoing.getFakePlayer(world, getPointedBlockPos());
                        if (fakePlayer.placeBlock(this.world, getPointedBlockPos(), input.getStackInSlot(i))) {
                            increasePointer();
                            return new WorkAction(1f, 1000);
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
    public boolean canAcceptAugment(IAugment augment) {
        if (augment.getAugmentType().equals(RangeAddonItem.RANGE)) return false;
        return super.canAcceptAugment(augment);
    }

    @Nonnull
    @Override
    public BlockPlacerTile getSelf() {
        return this;
    }
}

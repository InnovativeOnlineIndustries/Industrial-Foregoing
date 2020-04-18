package com.buuz135.industrial.block.resourceproduction.tile;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.config.machine.resourceproduction.BlockBreakerConfig;
import com.buuz135.industrial.item.RangeAddonItem;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.BlockUtils;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.augment.IAugment;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.energy.NBTEnergyHandler;
import net.minecraft.block.Blocks;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class BlockBreakerTile extends IndustrialAreaWorkingTile<BlockBreakerTile> {

    private int getMaxProgress;
    private int getPowerPerOperation;

    @Save
    private SidedInventoryComponent<BlockBreakerTile> output;

    public BlockBreakerTile() {
        super(ModuleResourceProduction.BLOCK_BREAKER, RangeManager.RangeType.BEHIND);
        this.addInventory(this.output = (SidedInventoryComponent<BlockBreakerTile>) new SidedInventoryComponent<BlockBreakerTile>("output", 54, 22, 3 * 6, 0).
                setColor(DyeColor.ORANGE).
                setRange(6, 3));
        this.getMaxProgress = BlockBreakerConfig.maxProgress;
        this.getPowerPerOperation = BlockBreakerConfig.powerPerOperation;
    }

    @Override
    public WorkAction work() {
        if (hasEnergy(getPowerPerOperation)) {
            if (isLoaded(getPointedBlockPos()) && !world.isAirBlock(getPointedBlockPos()) && BlockUtils.canBlockBeBroken(this.world, getPointedBlockPos())) {
                FakePlayer fakePlayer = IndustrialForegoing.getFakePlayer(this.world, getPointedBlockPos());
                fakePlayer.setHeldItem(Hand.MAIN_HAND, new ItemStack(Items.DIAMOND_PICKAXE));
                if (this.world.getBlockState(getPointedBlockPos()).canHarvestBlock(this.world, getPointedBlockPos(), fakePlayer)) {
                    for (ItemStack blockDrop : BlockUtils.getBlockDrops(this.world, getPointedBlockPos())) {
                        ItemStack result = ItemHandlerHelper.insertItem(output, blockDrop, false);
                        if (!result.isEmpty()) {
                            BlockUtils.spawnItemStack(result, this.world, getPointedBlockPos());
                        }
                    }
                    this.world.setBlockState(getPointedBlockPos(), Blocks.AIR.getDefaultState());
                    increasePointer();
                    return new WorkAction(1, getPowerPerOperation);
                }
            } else {
                increasePointer();
            }
        }
        return new WorkAction(1, 0);
    }

    @Override
    protected IFactory<NBTEnergyHandler> getEnergyHandlerFactory() {
        return () -> new NBTEnergyHandler(this, BlockBreakerConfig.maxStoredPower);
    }

    @Override
    public int getMaxProgress() {
        return getMaxProgress;
    }

    @Override
    public boolean canAcceptAugment(IAugment augment) {
        if (augment.getAugmentType().equals(RangeAddonItem.RANGE)) return false;
        return super.canAcceptAugment(augment);
    }

    @Nonnull
    @Override
    public BlockBreakerTile getSelf() {
        return this;
    }
}

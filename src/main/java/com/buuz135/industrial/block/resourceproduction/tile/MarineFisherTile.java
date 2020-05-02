package com.buuz135.industrial.block.resourceproduction.tile;

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.config.machine.resourceproduction.MarineFisherConfig;
import com.buuz135.industrial.item.RangeAddonItem;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.BlockUtils;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.augment.IAugment;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.energy.NBTEnergyHandler;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.*;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class MarineFisherTile extends IndustrialAreaWorkingTile<MarineFisherTile> {

    private int maxProgress;
    private int powerPerOperation;

    @Save
    private SidedInventoryComponent<MarineFisherTile> output;

    public MarineFisherTile() {
        super(ModuleResourceProduction.MARINE_FISHER, RangeManager.RangeType.BOTTOM);
        addInventory(output = (SidedInventoryComponent<MarineFisherTile>) new SidedInventoryComponent<MarineFisherTile>("output", 50, 22, 3 * 6, 0)
                .setColor(DyeColor.ORANGE)
                .setRange(6, 3)
                .setComponentHarness(this));
        this.maxProgress = MarineFisherConfig.maxProgress;
        this.powerPerOperation = MarineFisherConfig.powerPerOperation;
    }

    @Override
    public WorkAction work() {
        if (hasEnergy(powerPerOperation)) {
            if (getWaterSources() < 9) return new WorkAction(1, 0);
            LootTable fishingTable = this.world.getServer().getLootTableManager().getLootTableFromLocation(LootTables.GAMEPLAY_FISHING);
            LootContext.Builder context = new LootContext.Builder((ServerWorld) this.world).withParameter(LootParameters.POSITION, this.pos).withParameter(LootParameters.TOOL, new ItemStack(Items.FISHING_ROD));
            fishingTable.generate(context.build(LootParameterSets.FISHING)).forEach(stack -> ItemHandlerHelper.insertItem(output, stack, false));
            return new WorkAction(1f, powerPerOperation);
        }
        return new WorkAction(1f, 0);
    }

    @Override
    protected IFactory<NBTEnergyHandler> getEnergyHandlerFactory() {
        return () -> new NBTEnergyHandler(this, MarineFisherConfig.maxStoredPower);
    }

    @Override
    public int getMaxProgress() {
        return maxProgress;
    }

    @Nonnull
    @Override
    public MarineFisherTile getSelf() {
        return this;
    }

    @Override
    public boolean canAcceptAugment(IAugment augment) {
        if (augment.getAugmentType().equals(RangeAddonItem.RANGE)) return false;
        return super.canAcceptAugment(augment);
    }

    public VoxelShape getWorkingArea() {
        return new RangeManager(this.pos, this.getFacingDirection(), RangeManager.RangeType.BOTTOM).get(1);
    }

    private int getWaterSources() {
        int amount = 0;
        for (BlockPos pos : BlockUtils.getBlockPosInAABB(getWorkingArea().getBoundingBox())) {
            if (!world.isAreaLoaded(pos, pos)) continue;
            IFluidState fluidState = this.world.getFluidState(pos);
            if (fluidState.getFluid().equals(Fluids.WATER) && fluidState.isSource()) {
                ++amount;
            }
        }
        return amount;
    }

}

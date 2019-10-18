package com.buuz135.industrial.block.generator.tile;

import com.buuz135.industrial.block.tile.IndustrialGeneratorTile;
import com.buuz135.industrial.module.ModuleGenerator;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.tile.inventory.SidedInvHandler;
import com.hrznstudio.titanium.block.tile.progress.PosProgressBar;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

public class PitifulGeneratorTile extends IndustrialGeneratorTile {

    @Save
    private SidedInvHandler fuel;

    public PitifulGeneratorTile() {
        super(ModuleGenerator.PITIFUL_GENERATOR);
        this.addInventory(fuel = (SidedInvHandler) new SidedInvHandler("fuel", 46, 22, 1, 0)
                .setColor(DyeColor.ORANGE)
                .setColorGuiEnabled(false)
                .setInputFilter((itemStack, integer) -> FurnaceTileEntity.isFuel(itemStack))
                .setTile(this)
        );
    }

    @Override
    public boolean onActivated(PlayerEntity playerIn, Hand hand, Direction facing, double hitX, double hitY, double hitZ) {
        if (!super.onActivated(playerIn, hand, facing, hitX, hitY, hitZ)) {
            openGui(playerIn);
            return true;
        }
        return false;
    }

    @Override
    public int consumeFuel() {
        int time = FurnaceTileEntity.getBurnTimes().getOrDefault(fuel.getStackInSlot(0).getItem(), 100);
        fuel.getStackInSlot(0).shrink(1);
        return time;
    }

    @Override
    public boolean canStart() {
        return !fuel.getStackInSlot(0).isEmpty() && FurnaceTileEntity.getBurnTimes().get(fuel.getStackInSlot(0).getItem()) != null;
    }

    @Override
    public int getEnergyProducedEveryTick() {
        return 30;
    }

    @Override
    public PosProgressBar getProgressBar() {
        return new PosProgressBar(30, 20, 0, 100)
                .setTile(this)
                .setBarDirection(PosProgressBar.BarDirection.VERTICAL_UP)
                .setColor(DyeColor.CYAN);
    }

    @Override
    public int getEnergyCapacity() {
        return 100000;
    }

    @Override
    public int getExtractingEnergy() {
        return 100000;
    }

    @Override
    public boolean isSmart() {
        return false;
    }
}

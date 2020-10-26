package com.buuz135.industrial.block.resourceproduction.tile;

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.config.machine.resourceproduction.LaserDrillConfig;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.BlockUtils;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.client.screen.addon.TextScreenAddon;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.TextFormatting;

public class LaserDrillTile extends IndustrialAreaWorkingTile<LaserDrillTile> {

    @Save
    private BlockPos target;

    public LaserDrillTile() {
        super(ModuleResourceProduction.LASER_DRILL, RangeManager.RangeType.BEHIND, false);
        this.target = BlockPos.ZERO;
        addGuiAddonFactory(() -> new TextScreenAddon(TextFormatting.DARK_GRAY + "Target: ", 44 ,26, false));
        addGuiAddonFactory(() -> new TextScreenAddon("Target: ", 44 ,36, false){
                    @Override
                    public String getText() {
                        if (target.equals(BlockPos.ZERO) || target == null) return TextFormatting.DARK_GRAY +  "404 Not Found";
                        return  TextFormatting.DARK_GRAY + "X: " + target.getX() + " Y: " + target.getY() + " Z: " + target.getZ();
                    }
                }
        );
    }

    @Override
    public WorkAction work() {
        if (!target.equals(BlockPos.ZERO) && !isValidTarget(target)){
            target = BlockPos.ZERO;
            markForUpdate();
        }
        if (target.equals(BlockPos.ZERO)){
            findTarget();
        }
        if (!target.equals(BlockPos.ZERO) && hasEnergy(LaserDrillConfig.powerPerOperation)){
            if (this.world.getTileEntity(target) instanceof ILaserBase){
                ILaserBase laserBase = ((ILaserBase<?>) this.world.getTileEntity(target));
                laserBase.getBar().setProgress(laserBase.getBar().getProgress() + 1);
                return new WorkAction(1, LaserDrillConfig.powerPerOperation);
            }
        }
        return new WorkAction(1, 0);
    }

    @Override
    public VoxelShape getWorkingArea() {
        Vector3i vector =  this.getFacingDirection().getOpposite().getDirectionVec();
        return VoxelShapes.create(-1, 0, -1, 2, 3, 2).withOffset(this.pos.getX() + vector.getX() * 2, this.pos.getY() + vector.getY() * 2 -1, this.pos.getZ() + vector.getZ() * 2);
    }

    public void findTarget(){
        for (BlockPos blockPos : BlockUtils.getBlockPosInAABB(getWorkingArea().getBoundingBox())) {
            if (isValidTarget(blockPos)){
                this.target = blockPos;
                markForUpdate();
                return;
            }
        }
    }

    public boolean isValidTarget(BlockPos pos){
        if (pos.equals(BlockPos.ZERO)) return false;
        return this.world.getTileEntity(pos) instanceof ILaserBase;
    }

    @Override
    public LaserDrillTile getSelf() {
        return this;
    }

    @Override
    protected EnergyStorageComponent<LaserDrillTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(LaserDrillConfig.maxStoredPower, 10, 20);
    }

    public BlockPos getTarget() {
        return target;
    }

    @Override
    public int getMaxProgress() {
        return LaserDrillConfig.maxProgress;
    }
}

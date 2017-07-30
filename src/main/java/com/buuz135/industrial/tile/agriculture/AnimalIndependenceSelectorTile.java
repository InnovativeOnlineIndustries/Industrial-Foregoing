package com.buuz135.industrial.tile.agriculture;

import com.buuz135.industrial.item.addon.AdultFilterAddonItem;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class AnimalIndependenceSelectorTile extends WorkingAreaElectricMachine {

    public AnimalIndependenceSelectorTile() {
        super(AnimalIndependenceSelectorTile.class.getName().hashCode(), 2, 2, false);
    }


    @Override
    public float work() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;

        AxisAlignedBB area = getWorkingArea();
        List<EntityAgeable> animals = this.world.getEntitiesWithinAABB(EntityAgeable.class, area);
        if (animals.size() == 0) return 0;
        EntityAgeable animal = animals.get(0);
        while (animal.isChild() == this.hasAddon(AdultFilterAddonItem.class) && animals.indexOf(animal) + 1 < animals.size())
            animal = animals.get(animals.indexOf(animal) + 1);
        if (animal.isChild() == this.hasAddon(AdultFilterAddonItem.class)) return 0;
        BlockPos pos = this.getPos().offset(this.getFacing(), 1);
        animal.setPositionAndUpdate(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

        return 1;
    }

    @Override
    public AxisAlignedBB getWorkingArea() {
        EnumFacing f = this.getFacing().getOpposite();
        BlockPos corner1 = new BlockPos(0, 0, 0).offset(f, getRadius() + 1);
        return new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX() + 1, this.pos.getY() + 1, this.pos.getZ() + 1).offset(corner1).expand(getRadius(), 0, getRadius()).setMaxY(this.getPos().getY() + getHeight());
    }


}

package com.buuz135.industrial.block.agriculturehusbandry.tile;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.tile.fluid.SidedFluidTank;
import com.hrznstudio.titanium.block.tile.inventory.SidedInvHandler;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;

public class AnimalRancherTile extends IndustrialAreaWorkingTile {

    @Save
    private SidedFluidTank tank;
    @Save
    private SidedInvHandler output;

    public AnimalRancherTile() {
        super(ModuleAgricultureHusbandry.ANIMAL_RANCHER, RangeManager.RangeType.BEHIND);
        this.addTank(tank = (SidedFluidTank) new SidedFluidTank("fluid_output", 8000, 47, 20, 0).
                setColor(DyeColor.WHITE).
                setTile(this)
        );
        this.addInventory(output = (SidedInvHandler) new SidedInvHandler("output", 74, 22, 5 * 3, 1).
                setColor(DyeColor.ORANGE).
                setRange(5, 3).
                setTile(this)
        );
    }

    @Override
    public WorkAction work() {
        if (hasEnergy(400)) {
            List<AnimalEntity> mobs = this.world.getEntitiesWithinAABB(AnimalEntity.class, getWorkingArea().getBoundingBox());
            if (mobs.size() > 0) {
                for (AnimalEntity mob : mobs) {
                    //BUCKET INTERACTION
                    FakePlayer player = IndustrialForegoing.getFakePlayer(world, mob.getPosition());
                    player.setHeldItem(Hand.MAIN_HAND, new ItemStack(Items.BUCKET));
                    if (mob.processInteract(player, Hand.MAIN_HAND)) {
                        player.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
                        return new WorkAction(0.35f, 400);
                    }
                    //SHEAR INTERACTION
                    ItemStack shears = new ItemStack(Items.SHEARS);
                    if (mob instanceof IShearable && ((IShearable) mob).isShearable(shears, this.world, mob.getPosition())) {
                        List<ItemStack> items = ((IShearable) mob).onSheared(shears, this.world, mob.getPosition(), 0);
                        items.forEach(stack -> ItemHandlerHelper.insertItem(output, stack, false));
                        if (items.size() > 0) {
                            return new WorkAction(0.35f, 400);
                        }
                    }
                }
            }
        }
        return new WorkAction(1, 0);
    }
}

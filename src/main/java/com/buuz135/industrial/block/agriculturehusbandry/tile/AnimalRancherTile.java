package com.buuz135.industrial.block.agriculturehusbandry.tile;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.config.machine.agriculturehusbandry.AnimalRancherConfig;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.energy.NBTEnergyHandler;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.List;

public class AnimalRancherTile extends IndustrialAreaWorkingTile<AnimalRancherTile> {

    private int maxProgress;
    private int powerPerOperation;

    @Save
    private SidedFluidTankComponent<AnimalRancherTile> tank;
    @Save
    private SidedInventoryComponent<AnimalRancherTile> output;

    public AnimalRancherTile() {
        super(ModuleAgricultureHusbandry.ANIMAL_RANCHER, RangeManager.RangeType.BEHIND, true);
        this.addTank(tank = (SidedFluidTankComponent<AnimalRancherTile>) new SidedFluidTankComponent<AnimalRancherTile>("fluid_output", AnimalRancherConfig.maxTankSize, 47, 20, 0).
                setColor(DyeColor.WHITE).
                setComponentHarness(this)
        );
        this.addInventory(output = (SidedInventoryComponent<AnimalRancherTile>) new SidedInventoryComponent<AnimalRancherTile>("output", 74, 22, 5 * 3, 1).
                setColor(DyeColor.ORANGE).
                setRange(5, 3).
                setComponentHarness(this)
        );
        this.maxProgress = AnimalRancherConfig.maxProgress;
        this.powerPerOperation = AnimalRancherConfig.powerPerOperation;
    }

    @Override
    public WorkAction work() {
        if (hasEnergy(powerPerOperation)) {
            List<AnimalEntity> mobs = this.world.getEntitiesWithinAABB(AnimalEntity.class, getWorkingArea().getBoundingBox());
            if (mobs.size() > 0) {
                for (AnimalEntity mob : mobs) {
                    //BUCKET INTERACTION
                    FakePlayer player = IndustrialForegoing.getFakePlayer(world, mob.getPosition());
                    player.setHeldItem(Hand.MAIN_HAND, new ItemStack(Items.BUCKET));
                    if (mob.processInteract(player, Hand.MAIN_HAND)) {
                        ItemStack stack = player.getHeldItem(Hand.MAIN_HAND);
                        if (stack.getItem() instanceof BucketItem) {
                            tank.fillForced(new FluidStack(((BucketItem) stack.getItem()).getFluid(), FluidAttributes.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
                            player.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
                            return new WorkAction(0.35f, powerPerOperation);
                        }
                        player.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
                    }
                    //SHEAR INTERACTION
                    ItemStack shears = new ItemStack(Items.SHEARS);
                    if (mob instanceof IShearable && ((IShearable) mob).isShearable(shears, this.world, mob.getPosition())) {
                        List<ItemStack> items = ((IShearable) mob).onSheared(shears, this.world, mob.getPosition(), 0);
                        items.forEach(stack -> ItemHandlerHelper.insertItem(output, stack, false));
                        if (items.size() > 0) {
                            return new WorkAction(0.35f, powerPerOperation);
                        }
                    }
                }
            }
        }
        return new WorkAction(1, 0);
    }

    @Override
    protected IFactory<NBTEnergyHandler> getEnergyHandlerFactory() {
        return () -> new NBTEnergyHandler(this, AnimalRancherConfig.maxStoredPower);
    }

    @Override
    public int getMaxProgress() {
        return maxProgress;
    }

    @Nonnull
    @Override
    public AnimalRancherTile getSelf() {
        return this;
    }

}

package com.buuz135.industrial.block.core.tile;

import com.buuz135.industrial.block.tile.IndustrialProcessingTile;
import com.buuz135.industrial.config.machine.core.DissolutionChamberConfig;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.energy.NBTEnergyHandler;
import com.hrznstudio.titanium.util.RecipeUtil;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;

public class DissolutionChamberTile extends IndustrialProcessingTile<DissolutionChamberTile> {

    private int maxProgress;
    private int powerPerTick;

    @Save
    private SidedInventoryComponent<DissolutionChamberTile> input;
    @Save
    private SidedFluidTankComponent<DissolutionChamberTile> inputFluid;
    @Save
    private SidedInventoryComponent<DissolutionChamberTile> output;
    @Save
    private SidedFluidTankComponent<DissolutionChamberTile> outputFluid;
    private DissolutionChamberRecipe currentRecipe;

    public DissolutionChamberTile() {
        super(ModuleCore.DISSOLUTION_CHAMBER, 102, 41);
        int slotSpacing = 22;
        this.addInventory(input = (SidedInventoryComponent<DissolutionChamberTile>) new SidedInventoryComponent<DissolutionChamberTile>("input", 34, 19, 8, 0).
                setColor(DyeColor.LIGHT_BLUE).
                setSlotPosition(integer -> getSlotPos(integer)).
                setSlotLimit(1).
                setOutputFilter((stack, integer) -> false).
                setComponentHarness(this).
                setOnSlotChanged((stack, integer) -> checkForRecipe()));
        this.addTank(this.inputFluid = (SidedFluidTankComponent<DissolutionChamberTile>) new SidedFluidTankComponent<DissolutionChamberTile>("input_fluid", DissolutionChamberConfig.maxInputTankSize, 33 + slotSpacing, 18 + slotSpacing, 1).
                setColor(DyeColor.LIME).
                setTankType(FluidTankComponent.Type.SMALL).
                setComponentHarness(this).
                setTankAction(FluidTankComponent.Action.FILL).
                setOnContentChange(() -> checkForRecipe())
        );
        this.addInventory(this.output = (SidedInventoryComponent<DissolutionChamberTile>) new SidedInventoryComponent<DissolutionChamberTile>("output", 129, 22, 3, 2).
                setColor(DyeColor.ORANGE).
                setRange(1, 3).
                setInputFilter((stack, integer) -> false).
                setComponentHarness(this));
        this.addTank(this.outputFluid = (SidedFluidTankComponent<DissolutionChamberTile>) new SidedFluidTankComponent<DissolutionChamberTile>("output_fluid", DissolutionChamberConfig.maxOutputTankSize, 149, 20, 3).
                setColor(DyeColor.MAGENTA).
                setComponentHarness(this).
                setTankAction(FluidTankComponent.Action.DRAIN));
        this.maxProgress = DissolutionChamberConfig.maxProgress;
        this.powerPerTick = DissolutionChamberConfig.powerPerTick;
    }

    private void checkForRecipe() {
        if (isServer()) {
            if (currentRecipe != null && currentRecipe.matches(input, inputFluid)) {
                return;
            }
            currentRecipe = RecipeUtil.getRecipes(this.world, DissolutionChamberRecipe.SERIALIZER.getRecipeType()).stream().filter(dissolutionChamberRecipe -> dissolutionChamberRecipe.matches(input, inputFluid)).findFirst().orElse(null);
        }
    }

    @Override
    public void setWorldAndPos(World world, BlockPos pos) {
        super.setWorldAndPos(world, pos);
        checkForRecipe();
    }

    @Override
    public boolean canIncrease() {
        return currentRecipe != null && ItemHandlerHelper.insertItem(output, currentRecipe.output.copy(), true).isEmpty() && outputFluid.fillForced(currentRecipe.outputFluid.copy(), IFluidHandler.FluidAction.SIMULATE) == currentRecipe.outputFluid.getAmount();
    }

    @Override
    public Runnable onFinish() {
        return () -> {
            if (currentRecipe != null) {
                DissolutionChamberRecipe dissolutionChamberRecipe = currentRecipe;
                inputFluid.drainForced(dissolutionChamberRecipe.inputFluid, IFluidHandler.FluidAction.EXECUTE);
                for (int i = 0; i < input.getSlots(); i++) {
                    input.getStackInSlot(i).shrink(1);
                }
                if (dissolutionChamberRecipe.outputFluid != null && !dissolutionChamberRecipe.outputFluid.isEmpty())
                    outputFluid.fillForced(dissolutionChamberRecipe.outputFluid.copy(), IFluidHandler.FluidAction.EXECUTE);
                ItemHandlerHelper.insertItem(output, dissolutionChamberRecipe.output.copy(), false);
                //checkForRecipe();
            }
        };
    }

    @Override
    protected IFactory<NBTEnergyHandler> getEnergyHandlerFactory() {
        return () -> new NBTEnergyHandler(this, DissolutionChamberConfig.maxStoredPower);
    }

    @Override
    protected int getTickPower() {
        return powerPerTick;
    }

    @Override
    public int getMaxProgress() {
        return currentRecipe != null ? currentRecipe.processingTime : maxProgress;
    }

    public static Pair<Integer, Integer> getSlotPos(int slot) {
        int slotSpacing = 22;
        int offset = 2;
        switch (slot) {
            case 1:
                return Pair.of(slotSpacing, -offset);
            case 2:
                return Pair.of(slotSpacing * 2, 0);
            case 3:
                return Pair.of(-offset, slotSpacing);
            case 4:
                return Pair.of(slotSpacing * 2 + offset, slotSpacing);
            case 5:
                return Pair.of(0, slotSpacing * 2);
            case 6:
                return Pair.of(slotSpacing, slotSpacing * 2 + offset);
            case 7:
                return Pair.of(slotSpacing * 2, slotSpacing * 2);
            default:
                return Pair.of(0, 0);
        }
    }

    @Nonnull
    @Override
    public DissolutionChamberTile getSelf() {
        return this;
    }
}

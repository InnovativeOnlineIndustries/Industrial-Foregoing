package com.buuz135.industrial.block.core.tile;

import com.buuz135.industrial.block.tile.IndustrialProcessingTile;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.tile.fluid.PosFluidTank;
import com.hrznstudio.titanium.block.tile.fluid.SidedFluidTank;
import com.hrznstudio.titanium.block.tile.inventory.SidedInvHandler;
import com.hrznstudio.titanium.util.RecipeUtil;
import net.minecraft.item.DyeColor;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.Pair;

public class DissolutionChamberTile extends IndustrialProcessingTile {

    @Save
    private SidedInvHandler input;
    @Save
    private SidedFluidTank inputFluid;
    @Save
    private SidedInvHandler output;
    @Save
    private SidedFluidTank outputFluid;
    private DissolutionChamberRecipe currentRecipe;

    public DissolutionChamberTile() {
        super(ModuleCore.DISSOLUTION_CHAMBER, 102, 41);
        int slotSpacing = 22;
        this.addInventory(input = (SidedInvHandler) new SidedInvHandler("input", 34, 19, 8, 0).
                setColor(DyeColor.LIGHT_BLUE).
                setSlotPosition(integer -> getSlotPos(integer)).
                setSlotLimit(1).
                setTile(this).
                setOnSlotChanged((stack, integer) -> checkForRecipe()));
        this.addTank(this.inputFluid = (SidedFluidTank) new SidedFluidTank("input_fluid", 8000, 33 + slotSpacing, 18 + slotSpacing, 1).
                setColor(DyeColor.LIME).
                setTankType(PosFluidTank.Type.SMALL).
                setTile(this).
                setTankAction(PosFluidTank.Action.FILL).
                setOnContentChange(() -> checkForRecipe())
        );
        this.addInventory(this.output = (SidedInvHandler) new SidedInvHandler("output", 129, 22, 3, 2).
                setColor(DyeColor.ORANGE).
                setRange(1, 3).
                setInputFilter((stack, integer) -> false).
                setTile(this));
        this.addTank(this.outputFluid = (SidedFluidTank) new SidedFluidTank("output_fluid", 16000, 149, 20, 3).
                setColor(DyeColor.MAGENTA).
                setTile(this).
                setTankAction(PosFluidTank.Action.DRAIN));
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
    public void setWorld(World worldIn) {
        super.setWorld(worldIn);
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
    protected int getTickPower() {
        return 60;
    }

    @Override
    public int getMaxProgress() {
        return currentRecipe != null ? currentRecipe.processingTime : 100;
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
}

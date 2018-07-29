package com.buuz135.industrial.tile.ore;

import com.buuz135.industrial.api.ore.OreFluidEntrySieve;
import com.buuz135.industrial.tile.CustomElectricMachine;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.FluidTankType;
import net.ndrei.teslacorelib.inventory.LockableItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OreSieveTile extends CustomElectricMachine {

    private LockableItemHandler sandInput;
    private IFluidTank input;
    private ItemStackHandler output;
    private boolean shouldConsumeSand;

    public OreSieveTile() {
        super(OreSieveTile.class.getName().hashCode());
        this.shouldConsumeSand = false;
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        input = this.addSimpleFluidTank(8000, "input", EnumDyeColor.BLUE, 60 + 10, 25, FluidTankType.INPUT, fluidStack -> true, fluidStack -> false);
        sandInput = (LockableItemHandler) this.addSimpleInventory(1, "input", EnumDyeColor.GREEN, "input", new BoundingRectangle(18 * 5 - 2 + 10, 26, 18, 18), (stack, integer) -> stack.isItemEqual(new ItemStack(Blocks.SAND)), (stack, integer) -> false, true, null);
        output = (ItemStackHandler) this.addSimpleInventory(1, "output", EnumDyeColor.ORANGE, "output", new BoundingRectangle(18 * 6 + 8 + 10, 26 + 18, 18, 18), (stack, integer) -> false, (stack, integer) -> true, false, null);
    }

    @Override
    public void protectedUpdate() {
        super.protectedUpdate();
        sandInput.setLocked(true);
        sandInput.setFilter(new ItemStack[]{new ItemStack(Blocks.SAND)});
    }

    @Override
    protected float performWork() {
        if (shouldConsumeSand && sandInput.getStackInSlot(0).isEmpty()) {
            return 0;
        }
        for (OreFluidEntrySieve recipe : OreFluidEntrySieve.ORE_FLUID_SIEVE) {
            if (recipe.getInput().isFluidEqual(input.getFluid()) && input.drain(recipe.getInput().amount, false) != null && input.drain(recipe.getInput().amount, false).amount == recipe.getInput().amount && ItemHandlerHelper.insertItem(output, recipe.getOutput().copy(), true).isEmpty()) {
                input.drain(recipe.getInput().amount, true);
                ItemHandlerHelper.insertItem(output, recipe.getOutput().copy(), false);
                if (this.shouldConsumeSand) sandInput.getStackInSlot(0).shrink(1);
                this.shouldConsumeSand = this.world.rand.nextBoolean();
                return 1;
            }
        }
        return 0;
    }

    @NotNull
    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(@NotNull BasicTeslaGuiContainer<?> container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
        pieces.add(new BasicRenderedGuiPiece(84 + 10, 45, 25, 18, new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 24, 5));
        return pieces;
    }

    @Override
    protected boolean shouldAddFluidItemsInventory() {
        return false;
    }
}

package com.buuz135.industrial.tile.ore;

import com.buuz135.industrial.api.ore.OreFluidEntryFermenter;
import com.buuz135.industrial.tile.CustomElectricMachine;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidTank;
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.inventory.FluidTankType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class OreFermenterTile extends CustomElectricMachine {

    private IFluidTank input;
    private IFluidTank output;

    public OreFermenterTile() {
        super(OreFermenterTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        input = this.addSimpleFluidTank(1000, "input", EnumDyeColor.BLUE, 60, 25, FluidTankType.INPUT, fluidStack -> true, fluidStack -> false);
        output = this.addSimpleFluidTank(1000, "output", EnumDyeColor.ORANGE, 115, 25, FluidTankType.OUTPUT, fluidStack -> false, fluidStack -> true);
    }

    @Override
    protected float performWork() {
        if (hasColdSource() && hasHotSource()) {
            for (OreFluidEntryFermenter recipe : OreFluidEntryFermenter.ORE_FLUID_FERMENTER) {
                boolean didWork = false;
                int operations = 10;
                while (operations > 0) {
                    if (recipe.getInput().isFluidEqual(input.getFluid()) && input.drain(recipe.getInput().amount, false) != null && input.drain(recipe.getInput().amount, false).amount == recipe.getInput().amount && output.fill(recipe.getOutput(), false) == recipe.getOutput().amount) {
                        input.drain(recipe.getInput().amount, true);
                        output.fill(recipe.getOutput().copy(), true);
                        didWork = true;
                    }
                    --operations;
                }
                if (didWork) return 1;
            }
        }
        return 0;
    }

    public boolean hasHotSource() {
        IBlockState blockState = this.world.getBlockState(this.pos.down());
        Fluid fluid = FluidRegistry.lookupFluidForBlock(blockState.getBlock());
        if (fluid != null && fluid.getTemperature(world, pos.down()) >= 1300) return true;
        return blockState.getBlock().equals(Blocks.MAGMA) || blockState.getBlock().equals(Blocks.FIRE);
    }

    public boolean hasColdSource() {
        for (EnumFacing facing : new EnumFacing[]{EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST}) {
            IBlockState blockState = this.world.getBlockState(this.pos.offset(facing));
            Fluid fluid = FluidRegistry.lookupFluidForBlock(blockState.getBlock());
            if (fluid != null && fluid.getTemperature(world, pos.offset(facing)) <= 300) return true;
            if (blockState.getBlock().equals(Blocks.ICE) || blockState.getBlock().equals(Blocks.SNOW) || blockState.getBlock().equals(Blocks.PACKED_ICE))
                return true;
        }
        return false;
    }

    @NotNull
    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(@NotNull BasicTeslaGuiContainer<?> container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
        pieces.add(new BasicRenderedGuiPiece(84, 45, 25, 18, new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 24, 5));
        pieces.add(new BasicRenderedGuiPiece(143, 30, 18, 18, new ResourceLocation(Reference.MOD_ID, "textures/gui/machines.png"), 1, 168) {
            @Override
            public void drawBackgroundLayer(@NotNull BasicTeslaGuiContainer<?> container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
                super.drawBackgroundLayer(container, guiX, guiY, partialTicks, mouseX, mouseY);
                container.mc.getTextureManager().bindTexture(new ResourceLocation("industrialforegoing", "textures/gui/machines.png"));
                container.drawTexturedRect(this.getLeft() + 2, this.getTop() + 2, 1, 119, 14, 14);
                if (!hasHotSource()) {
                    container.mc.getTextureManager().bindTexture(new ResourceLocation("teslacorelib", "textures/gui/basic-machine.png"));
                    container.drawTexturedRect(this.getLeft() + 2, this.getTop() + 2, 146, 210, 14, 14);
                }
            }

            @Override
            public void drawForegroundTopLayer(@NotNull BasicTeslaGuiContainer<?> container, int guiX, int guiY, int mouseX, int mouseY) {
                super.drawForegroundTopLayer(container, guiX, guiY, mouseX, mouseY);
                if (!isInside(container, mouseX, mouseY)) return;
                container.drawTooltip(Collections.singletonList((hasHotSource() ? TextFormatting.GREEN : TextFormatting.RED) + new TextComponentTranslation(hasHotSource() ? "text.industrialforegoing.tooltip.found_heat" : "text.industrialforegoing.tooltip.missing_heat").getUnformattedComponentText()), mouseX - guiX, mouseY - guiY);

            }
        });
        pieces.add(new BasicRenderedGuiPiece(143, 56, 18, 18, new ResourceLocation(Reference.MOD_ID, "textures/gui/machines.png"), 1, 168) {
            @Override
            public void drawBackgroundLayer(@NotNull BasicTeslaGuiContainer<?> container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
                super.drawBackgroundLayer(container, guiX, guiY, partialTicks, mouseX, mouseY);
                container.mc.getTextureManager().bindTexture(new ResourceLocation("industrialforegoing", "textures/gui/machines.png"));
                container.drawTexturedRect(this.getLeft() + 2, this.getTop() + 2, 1 + 15, 119, 14, 14);
                if (!hasColdSource()) {
                    container.mc.getTextureManager().bindTexture(new ResourceLocation("teslacorelib", "textures/gui/basic-machine.png"));
                    container.drawTexturedRect(this.getLeft() + 2, this.getTop() + 2, 146, 210, 14, 14);
                }
            }

            @Override
            public void drawForegroundTopLayer(@NotNull BasicTeslaGuiContainer<?> container, int guiX, int guiY, int mouseX, int mouseY) {
                super.drawForegroundTopLayer(container, guiX, guiY, mouseX, mouseY);
                if (!isInside(container, mouseX, mouseY)) return;
                container.drawTooltip(Collections.singletonList((hasColdSource() ? TextFormatting.GREEN : TextFormatting.RED) + new TextComponentTranslation(hasColdSource() ? "text.industrialforegoing.tooltip.found_cold" : "text.industrialforegoing.tooltip.missing_cold").getUnformattedComponentText()), mouseX - guiX, mouseY - guiY);

            }
        });
        return pieces;
    }

    @Override
    protected boolean shouldAddFluidItemsInventory() {
        return false;
    }
}

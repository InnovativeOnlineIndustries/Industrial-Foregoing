package com.buuz135.industrial.tile.world;

import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.inventory.FluidTankType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FluidPumpTile extends WorkingAreaElectricMachine {

    private static final String NBT_FLUID = "Fluid";
    private static final String NBT_WORK = "Work";

    private IFluidTank tank;
    private String fluid;
    private Queue<BlockPos> blocks;
    private boolean isWorkFinished;

    public FluidPumpTile() {
        super(FluidPumpTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        tank = this.addSimpleFluidTank(32000, "output tank", EnumDyeColor.ORANGE, 50, 25, FluidTankType.OUTPUT, fluidStack -> false, fluidStack -> true);
        isWorkFinished = false;
    }

    @Override
    public void protectedUpdate() {
        super.protectedUpdate();
        if (fluid == null && FluidRegistry.lookupFluidForBlock(this.world.getBlockState(this.pos.add(0, -1, 0)).getBlock()) != null)
            fluid = FluidRegistry.lookupFluidForBlock(this.world.getBlockState(this.pos.add(0, -1, 0)).getBlock()).getName();
    }

    @Override
    public float work() {
        if (WorkUtils.isDisabled(this.getBlockType()) || isWorkFinished) return 0;
        if (tank.getFluidAmount() + 1000 <= tank.getCapacity()) {
            if (blocks == null) {
                blocks = new PriorityQueue<>(Comparator.comparingDouble(value -> (this.pos).distanceSq(((BlockPos) value).getX(), this.pos.getY() - 1, ((BlockPos) value).getZ())));
                BlockUtils.getBlockPosInAABB(getWorkingArea()).stream().filter(pos1 -> !this.world.isOutsideBuildHeight(pos1) && FluidRegistry.lookupFluidForBlock(this.world.getBlockState(pos1).getBlock()) != null && FluidRegistry.lookupFluidForBlock(this.world.getBlockState(pos1).getBlock()).getName().equals(fluid)).forEach(pos1 -> blocks.add(pos1));
            }
            if (blocks.isEmpty()) {
                isWorkFinished = true;
                blocks = null;
                return 0;
            }
            BlockPos peeked = blocks.peek();
            while (!blocks.isEmpty() && (this.world.isOutsideBuildHeight(peeked) || FluidRegistry.lookupFluidForBlock(this.world.getBlockState(peeked).getBlock()) == null || !FluidRegistry.lookupFluidForBlock(this.world.getBlockState(peeked).getBlock()).getName().equals(fluid) || this.world.getBlockState(peeked).getBlock().getMetaFromState(this.world.getBlockState(peeked)) != 0)) {
                blocks.poll();
                if (!blocks.isEmpty()) peeked = blocks.peek();
            }
            if (blocks.isEmpty()) {
                blocks = null;
                return 0;
            }
            while (this.world.getBlockState(peeked).getBlock().equals(this.world.getBlockState(peeked.add(0, -1, 0)).getBlock())) {
                peeked = peeked.add(0, -1, 0);
            }
            FluidStack stack = new FluidStack(FluidRegistry.lookupFluidForBlock(this.world.getBlockState(peeked).getBlock()), 1000);
            tank.fill(stack, true);
            if (BlockRegistry.fluidPumpBlock.isReplaceFluidWithCobble())
                this.world.setBlockState(peeked, Blocks.COBBLESTONE.getDefaultState());
            else world.setBlockToAir(peeked);
            return 1;
        }
        return 0;
    }

    //TODO make it fill buckets
    @Override
    public AxisAlignedBB getWorkingArea() {
        return new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX() + 1, this.pos.getY() + 1, this.pos.getZ() + 1).offset(0, -1, 0).grow((Math.ceil((getRadius() * getRadius()) / 2F)), 0, Math.ceil((getRadius() * getRadius()) / 2F));
    }

    @NotNull
    @Override
    public NBTTagCompound writeToNBT(@NotNull NBTTagCompound compound) {
        NBTTagCompound nbtTagCompound = super.writeToNBT(compound);
        nbtTagCompound.setBoolean(NBT_WORK, isWorkFinished);
        if (fluid != null) nbtTagCompound.setString(NBT_FLUID, fluid);
        return nbtTagCompound;
    }

    @Override
    public void readFromNBT(@NotNull NBTTagCompound compound) {
        fluid = null;
        isWorkFinished = compound.getBoolean(NBT_WORK);
        if (compound.hasKey(NBT_FLUID)) fluid = compound.getString(NBT_FLUID);
        if (fluid != null && !FluidRegistry.isFluidRegistered(fluid)) fluid = null;
        super.readFromNBT(compound);
    }

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
        pieces.add(new BasicRenderedGuiPiece(95, 22, 54, 20, null, 0, 0) {
            @Override
            public void drawForegroundLayer(@NotNull BasicTeslaGuiContainer<?> container, int guiX, int guiY, int mouseX, int mouseY) {
                super.drawForegroundLayer(container, guiX, guiY, mouseX, mouseY);
                GlStateManager.pushMatrix();
                GlStateManager.translate(this.getLeft() + 2, this.getTop() + 8, 0);
                GlStateManager.scale(1, 1, 1);
                FontRenderer renderer = Minecraft.getMinecraft().fontRenderer;
                ItemStackUtils.renderItemIntoGUI(fluid == null ? new ItemStack(Items.BUCKET) : FluidUtil.getFilledBucket(new FluidStack(FluidRegistry.getFluid(fluid), 1000)), 34, -5, 7);
                renderer.drawString(TextFormatting.DARK_GRAY + new TextComponentTranslation("text.industrialforegoing.display.filter").getUnformattedText(), 0, 0, 0xFFFFFF);
                GlStateManager.popMatrix();
            }

            @Override
            public void drawForegroundTopLayer(@NotNull BasicTeslaGuiContainer<?> container, int guiX, int guiY, int mouseX, int mouseY) {
                super.drawForegroundTopLayer(container, guiX, guiY, mouseX, mouseY);
                if (isInside(container, mouseX, mouseY))
                    container.drawTooltip(Arrays.asList(fluid != null ? FluidRegistry.getFluid(fluid).getLocalizedName(new FluidStack(FluidRegistry.getFluid(fluid), 1000)) : new TextComponentTranslation("text.industrialforegoing.display.empty").getUnformattedText()), mouseX - guiX, mouseY - guiY);
            }
        });
        return pieces;
    }

    @Override
    public int getActionsWork() {
        return 1;
    }
}

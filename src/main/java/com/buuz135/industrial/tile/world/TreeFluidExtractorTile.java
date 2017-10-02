package com.buuz135.industrial.tile.world;

import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.tile.CustomSidedTileEntity;
import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.buuz135.industrial.utils.WorkUtils;
import lombok.Data;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TreeFluidExtractorTile extends CustomSidedTileEntity {

    private IFluidTank tank;
    private int tick;
    private int id;

    public TreeFluidExtractorTile() {
        super(TreeFluidExtractorTile.class.getName().hashCode());
        tick = 0;
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        tank = this.addFluidTank(FluidsRegistry.LATEX, 8000, EnumDyeColor.GRAY, "Latex tank", new BoundingRectangle(17, 25, 18, 54));
    }

    @Override
    protected boolean supportsAddons() {
        return false;
    }

    @Override
    protected void innerUpdate() {
        if (WorkUtils.isDisabled(this.getBlockType())) return;
        if (this.getWorld().isRemote) return;
        if (!BlockUtils.isLog(this.world, this.pos.offset(this.getFacing().getOpposite())))
            WoodLodProgress.remove(this.world, this.pos.offset(this.getFacing().getOpposite()));
        if (tick % 5 == 0 && BlockUtils.isLog(this.world, this.pos.offset(this.getFacing().getOpposite()))) {
            WoodLodProgress woodLog = WoodLodProgress.getWoodLogOrDefault(this.world, this.pos.offset(this.getFacing().getOpposite()));
            tank.fill(new FluidStack(FluidsRegistry.LATEX, 1), true);
            if (id == 0) id = this.world.rand.nextInt();
            if (world.rand.nextDouble() <= 0.005) woodLog.setProgress(woodLog.getProgress() + 1);
            if (woodLog.getProgress() > 7) {
                woodLog.setProgress(0);
                this.world.setBlockToAir(this.pos.offset(this.getFacing().getOpposite()));
            }
        }
        ++tick;
    }


    @Override
    protected boolean acceptsFluidItem(ItemStack stack) {
        return ItemStackUtils.acceptsFluidItem(stack);
    }

    @Override
    protected void processFluidItems(ItemStackHandler fluidItems) {
        ItemStackUtils.processFluidItems(fluidItems, tank);
    }

    @Override
    public boolean getAllowRedstoneControl() {
        return false;
    }

    @Override
    protected boolean getShowPauseDrawerPiece() {
        return false;
    }

    public @Data
    static class WoodLodProgress {

        public static List<WoodLodProgress> woodLodProgressList = new ArrayList<>();

        private World world;
        private BlockPos blockPos;
        private int progress;
        private int breakID;

        public WoodLodProgress(World world, BlockPos blockPos) {
            this.world = world;
            this.blockPos = blockPos;
            this.progress = 0;
            this.breakID = world.rand.nextInt();
            woodLodProgressList.add(this);
        }

        public static WoodLodProgress getWoodLogOrDefault(World world, BlockPos pos) {
            return woodLodProgressList.stream().filter(woodLodProgress -> woodLodProgress.getBlockPos().equals(pos) && woodLodProgress.getWorld().equals(world)).findAny().orElse(new WoodLodProgress(world, pos));
        }

        public static void remove(World world, BlockPos pos) {
            woodLodProgressList = woodLodProgressList.stream().filter(woodLodProgress -> !(woodLodProgress.getBlockPos().equals(pos) && woodLodProgress.getWorld().equals(world))).collect(Collectors.toList());
        }
    }
}

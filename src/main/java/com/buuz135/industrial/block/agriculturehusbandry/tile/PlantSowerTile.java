package com.buuz135.industrial.block.agriculturehusbandry.tile;

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.item.RangeAddonItem;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.filter.FilterSlot;
import com.hrznstudio.titanium.block.tile.inventory.SidedInvHandler;
import com.hrznstudio.titanium.filter.ItemstackFilter;
import net.minecraft.block.BushBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.IPlantable;

public class PlantSowerTile extends IndustrialAreaWorkingTile {

    public static DyeColor[] COLORS = new DyeColor[]{DyeColor.RED, DyeColor.YELLOW, DyeColor.LIME, DyeColor.CYAN, DyeColor.WHITE, DyeColor.BLUE, DyeColor.PURPLE, DyeColor.MAGENTA, DyeColor.BLACK};

    @Save
    private ItemstackFilter filter;
    @Save
    private SidedInvHandler input;

    public PlantSowerTile() {
        super(ModuleAgricultureHusbandry.PLANT_SOWER, RangeManager.RangeType.TOP_UP);
        addFilter(this.filter = new ItemstackFilter("filter", 9));
        int pos = 0;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                FilterSlot slot = new FilterSlot<>(45 + x * 18, 21 + y * 18, pos, ItemStack.EMPTY);
                slot.setColor(COLORS[pos]);
                this.filter.setFilter(pos, slot);
                ++pos;
            }
        }
        addInventory(this.input = (SidedInvHandler) new SidedInvHandler("input", 54 + 18 * 3, 22, 9, 0).
                setColor(DyeColor.CYAN).
                setInputFilter((itemStack, integer) -> itemStack.getItem() instanceof BlockItem && ((BlockItem) itemStack.getItem()).getBlock() instanceof IPlantable).
                setRange(3, 3).
                setTile(this));
    }

    @Override
    public WorkAction work() {
        BlockPos pos = getPointedBlockPos();
        if (this.world.isAirBlock(pos) && hasEnergy(1000)) {
            int slot = getFilteredSlot(pos);
            ItemStack stack = ItemStack.EMPTY;
            for (int i = 0; i < input.getSlots(); i++) {
                if (filter.getFilterSlots()[slot].getFilter().isEmpty() || filter.getFilterSlots()[slot].getFilter().isItemEqual(input.getStackInSlot(i))) {
                    stack = input.getStackInSlot(i);
                    break;
                }
            }
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof BushBlock) {
                BushBlock block = (BushBlock) ((BlockItem) stack.getItem()).getBlock();
                if (block.isValidPosition(this.world.getBlockState(pos), world, pos)) {
                    if (this.world.setBlockState(pos, block.getPlant(world, pos))) {
                        stack.shrink(1);
                        increasePointer();
                        return new WorkAction(0.2f, 1000);
                    }
                }
            }
        }
        increasePointer();
        return new WorkAction(.35f, 0);
    }

    private int getFilteredSlot(BlockPos pos) {
        int radius = hasAugmentInstalled(RangeAddonItem.RANGE) ? (int) getInstalledAugments(RangeAddonItem.RANGE).get(0).getAugmentRatio() + 1 : 0;
        if (radius == 0) {
            for (int i = 0; i < input.getSlots(); ++i) {
                if (!input.getStackInSlot(i).isEmpty()) {
                    return i;
                }
            }
        }
        int x = Math.round(1.49F * (pos.getX() - this.pos.getX()) / radius);
        int z = Math.round(1.49F * (pos.getZ() - this.pos.getZ()) / radius);
        return 4 + x + 3 * z;
    }
}

package com.buuz135.industrial.block.agriculturehusbandry.tile;

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.item.RangeAddonItem;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.augment.IAugment;
import com.hrznstudio.titanium.api.filter.FilterSlot;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.filter.ItemStackFilter;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.IPlantable;

import javax.annotation.Nonnull;

public class PlantSowerTile extends IndustrialAreaWorkingTile<PlantSowerTile> {

    public static DyeColor[] COLORS = new DyeColor[]{DyeColor.RED, DyeColor.YELLOW, DyeColor.LIME, DyeColor.CYAN, DyeColor.WHITE, DyeColor.BLUE, DyeColor.PURPLE, DyeColor.MAGENTA, DyeColor.BLACK};

    @Save
    private ItemStackFilter filter;
    @Save
    private SidedInventoryComponent<PlantSowerTile> input;

    public PlantSowerTile() {
        super(ModuleAgricultureHusbandry.PLANT_SOWER, RangeManager.RangeType.TOP_UP);
        addFilter(this.filter = new ItemStackFilter("filter", 9) {
            @Override
            public void onContentChanged() {
                super.onContentChanged();
                markForUpdate();
            }
        });
        int pos = 0;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                FilterSlot slot = new FilterSlot<>(45 + x * 18, 21 + y * 18, pos, ItemStack.EMPTY);
                slot.setColor(COLORS[pos]);
                this.filter.setFilter(pos, slot);
                ++pos;
            }
        }
        addInventory(this.input = (SidedInventoryComponent<PlantSowerTile>) new SidedInventoryComponent<PlantSowerTile>("input", 54 + 18 * 3, 22, 9, 0).
                setColor(DyeColor.CYAN).
                setInputFilter((itemStack, integer) -> itemStack.getItem() instanceof BlockItem && ((BlockItem) itemStack.getItem()).getBlock() instanceof IPlantable).
                setRange(3, 3).
                setComponentHarness(this));
    }

    @Override
    public WorkAction work() {
        BlockPos pos = getPointedBlockPos();
        if (isLoaded(pos) && this.world.isAirBlock(pos) && hasEnergy(1000)) {
            int slot = getFilteredSlot(pos);
            ItemStack stack = ItemStack.EMPTY;
            for (int i = 0; i < input.getSlots(); i++) {
                if (input.getStackInSlot(i).isEmpty()) continue;
                if (filter.getFilterSlots()[slot].getFilter().isEmpty() || filter.getFilterSlots()[slot].getFilter().isItemEqual(input.getStackInSlot(i))) {
                    stack = input.getStackInSlot(i);
                    break;
                }
            }
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof IPlantable) {
                Block block = ((BlockItem) stack.getItem()).getBlock();
                if (block.canSustainPlant(this.world.getBlockState(pos.down()), world, pos, Direction.UP, (IPlantable) block)) {
                    if (this.world.setBlockState(pos, ((IPlantable) block).getPlant(world, pos))) {
                        stack.shrink(1);
                        increasePointer();
                        return new WorkAction(0.2f, 1000);
                    }
                }
            }
        }
        increasePointer();
        return new WorkAction(1f, 0);
    }

    private int getFilteredSlot(BlockPos pos) {
        int radius = hasAugmentInstalled(RangeAddonItem.RANGE) ? (int) ((IAugment) getInstalledAugments(RangeAddonItem.RANGE).get(0)).getAugmentRatio() + 1 : 0;
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

    @Override
    public int getMaxProgress() {
        return 40;
    }

    @Nonnull
    @Override
    public PlantSowerTile getSelf() {
        return this;
    }
}

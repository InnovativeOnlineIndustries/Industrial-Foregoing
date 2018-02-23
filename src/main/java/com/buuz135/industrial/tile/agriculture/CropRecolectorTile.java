package com.buuz135.industrial.tile.agriculture;

import com.buuz135.industrial.api.plant.PlantRecollectable;
import com.buuz135.industrial.item.addon.LeafShearingAddonItem;
import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.registry.IFRegistries;
import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.tile.block.CropRecolectorBlock;
import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class CropRecolectorTile extends WorkingAreaElectricMachine {

    private static String NBT_POINTER = "pointer";
    private static String NBT_OPERATION = "operation";

    private IFluidTank sludge;
    private ItemStackHandler outItems;
    private int pointer;
    private int operationAmount;

    public CropRecolectorTile() {
        super(CropRecolectorTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        sludge = this.addFluidTank(FluidsRegistry.SLUDGE, 8000, EnumDyeColor.BLACK, "Sludge tank", new BoundingRectangle(50, 25, 18, 54));
        outItems = new ItemStackHandler(3 * 4) {
            @Override
            protected void onContentsChanged(int slot) {
                CropRecolectorTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(outItems, EnumDyeColor.ORANGE, "Crops output", 18 * 5 + 3, 25, 4, 3) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }

        });
        this.addInventoryToStorage(outItems, "outItems");
    }

    @Override
    public float work() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;
        if (ItemStackUtils.isInventoryFull(outItems)) return 0;
        List<BlockPos> blockPos = BlockUtils.getBlockPosInAABB(getWorkingArea());
        boolean shouldPointerIncrease = true;
        boolean didWork = false;
        if (pointer < blockPos.size() && BlockUtils.canBlockBeBroken(this.world, blockPos.get(pointer))) {
            BlockPos pos = blockPos.get(pointer);
            IBlockState state = this.world.getBlockState(blockPos.get(pointer));
            Optional<PlantRecollectable> recollectable = IFRegistries.PLANT_RECOLLECTABLES_REGISTRY.getValuesCollection().stream().sorted(Comparator.comparingInt(PlantRecollectable::getPriority)).filter(iPlantRecollectable -> iPlantRecollectable.canBeHarvested(this.world, pos, state)).findFirst();
            if (recollectable.isPresent()) {
                PlantRecollectable plantRecollectable = recollectable.get();
                ++operationAmount;
                List<ItemStack> items = plantRecollectable.doHarvestOperation(this.world, pos, state, hasShearingAddon(), operationAmount);
                if (items != null) insertItems(items, outItems);
                if (!plantRecollectable.shouldCheckNextPlant(this.world, pos, state)) shouldPointerIncrease = false;
            }
            didWork = recollectable.isPresent();
        }
        if (shouldPointerIncrease) {
            ++pointer;
            operationAmount = 0;
        }
        if (pointer >= blockPos.size()) pointer = 0;
        return didWork ? 1 : 0.1f;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tagCompound = super.writeToNBT(compound);
        tagCompound.setInteger(NBT_POINTER, pointer);
        tagCompound.setInteger(NBT_OPERATION, operationAmount);
        return tagCompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        pointer = compound.getInteger(NBT_POINTER);
        operationAmount = compound.getInteger(NBT_OPERATION);
    }

    private void insertItems(List<ItemStack> drops, ItemStackHandler outItems) {
        for (ItemStack stack : drops) {
            ItemHandlerHelper.insertItem(outItems, stack, false);
        }
        sludge.fill(new FluidStack(FluidsRegistry.SLUDGE, ((CropRecolectorBlock) this.getBlockType()).getSludgeOperation() * drops.size()), true);
    }


    @Override
    protected boolean acceptsFluidItem(ItemStack stack) {
        return ItemStackUtils.acceptsFluidItem(stack);
    }

    @Override
    protected void processFluidItems(ItemStackHandler fluidItems) {
        ItemStackUtils.processFluidItems(fluidItems, sludge);
    }

    public boolean hasShearingAddon() {
        return hasAddon(LeafShearingAddonItem.class);
    }
}

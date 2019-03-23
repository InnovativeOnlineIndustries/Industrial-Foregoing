/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.industrial.tile.agriculture;

import com.buuz135.industrial.api.plant.PlantRecollectable;
import com.buuz135.industrial.item.addon.LeafShearingAddonItem;
import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.proxy.client.ClientProxy;
import com.buuz135.industrial.registry.IFRegistries;
import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.tile.block.CropRecolectorBlock;
import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.ToggleButtonPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.netsync.SimpleNBTMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class CropRecolectorTile extends WorkingAreaElectricMachine {

    private static String NBT_POINTER = "pointer";
    private static String NBT_OPERATION = "operation";
    private static String NBT_TYPE = "type";

    private IFluidTank sludge;
    private ItemStackHandler outItems;
    private int pointer;
    private int operationAmount;
    private PlantRecollectable.Type type;

    public CropRecolectorTile() {
        super(CropRecolectorTile.class.getName().hashCode());
        type = PlantRecollectable.Type.ANY;
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
            Optional<PlantRecollectable> recollectable = IFRegistries.PLANT_RECOLLECTABLES_REGISTRY.getValuesCollection().stream().sorted(Comparator.comparingInt(PlantRecollectable::getPriority)).filter(iPlantRecollectable -> iPlantRecollectable.canBeHarvested(this.world, pos, state) && (type == PlantRecollectable.Type.ANY || type == iPlantRecollectable.getRecollectableType())).findFirst();
            if (recollectable.isPresent()) {
                PlantRecollectable plantRecollectable = recollectable.get();
                ++operationAmount;
                List<ItemStack> items = plantRecollectable.doHarvestOperation(this.world, pos, state, hasShearingAddon(), operationAmount);
                if (items != null && !items.isEmpty()) insertItems(items, outItems);
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
        tagCompound.setString(NBT_TYPE, type.name());
        return tagCompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        pointer = compound.getInteger(NBT_POINTER);
        operationAmount = compound.getInteger(NBT_OPERATION);
        type = PlantRecollectable.Type.ANY;
        if (compound.hasKey(NBT_TYPE)) type = PlantRecollectable.Type.valueOf(compound.getString(NBT_TYPE));
    }

    private void insertItems(List<ItemStack> drops, ItemStackHandler outItems) {
        for (ItemStack stack : drops) {
            ItemHandlerHelper.insertItem(outItems, stack, false);
        }
        if (sludge.getFluidAmount() < sludge.getCapacity())
            sludge.fill(new FluidStack(FluidsRegistry.SLUDGE, ((CropRecolectorBlock) this.getBlockType()).getSludgeOperation() * drops.size()), true);
    }


    @Override
    protected boolean acceptsFluidItem(ItemStack stack) {
        return ItemStackUtils.acceptsFluidItem(stack);
    }

    @Override
    protected void processFluidItems(ItemStackHandler fluidItems) {
        ItemStackUtils.fillItemFromTank(fluidItems, sludge);
    }

    public boolean hasShearingAddon() {
        return hasAddon(LeafShearingAddonItem.class);
    }

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
        pieces.add(new ToggleButtonPiece(118, 84, 13, 13, 0) {

            @Override
            protected int getCurrentState() {
                return type.ordinal();
            }

            @Override
            protected void renderState(BasicTeslaGuiContainer container, int state, BoundingRectangle box) {

            }

            @Override
            public void drawBackgroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
                super.drawBackgroundLayer(container, guiX, guiY, partialTicks, mouseX, mouseY);
                container.mc.getTextureManager().bindTexture(ClientProxy.GUI);
                container.drawTexturedRect(this.getLeft() - 1, this.getTop() - 1, 49, 56, 16, 16);
                if (type == PlantRecollectable.Type.TREE)
                    ItemStackUtils.renderItemIntoGUI(new ItemStack(Blocks.SAPLING, 1, 4), this.getLeft() + guiX - 2, this.getTop() + guiY - 3, 9);
                else if (type == PlantRecollectable.Type.PLANT)
                    ItemStackUtils.renderItemIntoGUI(new ItemStack(Items.NETHER_WART), this.getLeft() + guiX - 2, this.getTop() + guiY - 2, 9);
                else {
                    ItemStackUtils.renderItemIntoGUI(new ItemStack(Items.NETHER_WART), this.getLeft() + guiX, this.getTop() + guiY - 3, 9);
                    ItemStackUtils.renderItemIntoGUI(new ItemStack(Blocks.SAPLING, 1, 1), this.getLeft() + guiX - 2, this.getTop() + guiY - 4, 8);
                }
            }

            @Override
            protected void clicked() {
                CropRecolectorTile.this.sendToServer(CropRecolectorTile.this.setupSpecialNBTMessage("NEXT_PLANT_TYPE"));
            }

            @NotNull
            @Override
            protected List<String> getStateToolTip(int state) {
                return Arrays.asList(new TextComponentTranslation("text.industrialforegoing.button.gather." + type.name().toLowerCase()).getFormattedText());
            }
        });
        return pieces;
    }

    @Nullable
    @Override
    protected SimpleNBTMessage processClientMessage(@Nullable String messageType, @NotNull NBTTagCompound compound) {
        if (messageType != null && messageType.equalsIgnoreCase("NEXT_PLANT_TYPE")) {
            type = PlantRecollectable.Type.values()[(type.ordinal() + 1) % PlantRecollectable.Type.values().length];
            markDirty();
            forceSync();
        }
        return super.processClientMessage(messageType, compound);
    }
}

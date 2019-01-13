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
package com.buuz135.industrial.tile.magic;

import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.CustomElectricMachine;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.buuz135.industrial.utils.Reference;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.LockedInventoryTogglePiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandler;
import net.ndrei.teslacorelib.inventory.LockableItemHandler;
import net.ndrei.teslacorelib.inventory.SyncProviderLevel;

import java.util.Arrays;
import java.util.List;

public class PotionEnervatorTile extends CustomElectricMachine {

    private static final String NBT_ACTION = "action";

    private IFluidTank fluidTank;
    private LockableItemHandler inputGlassBottles;
    private LockableItemHandler inputIngredients;
    private ItemStackHandler outputPotions;

    private int action = 0;

    public PotionEnervatorTile() {
        super(PotionEnervatorTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        initInputInventories();
        initOutputInventories();
    }

    private void initInputInventories() {
        fluidTank = this.addFluidTank(FluidRegistry.WATER, 8000, EnumDyeColor.BLUE, "Water tank", new BoundingRectangle(44, 25, 18, 54));
        inputGlassBottles = new LockableItemHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                PotionEnervatorTile.this.markDirty();
            }
        };
        this.addInventory(new ColoredItemHandler(inputGlassBottles, EnumDyeColor.ORANGE, "Glass bottles input", new BoundingRectangle(18 * 4 + 10, 25 + 18 * 2, 18, 18)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return stack.getItem().equals(Items.GLASS_BOTTLE);
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }

        });
        this.addInventoryToStorage(inputGlassBottles, "pot_ener_in_glass");
        inputIngredients = new LockableItemHandler(5) {
            @Override
            protected void onContentsChanged(int slot) {
                PotionEnervatorTile.this.markDirty();
            }
        };
        this.addInventory(new ColoredItemHandler(inputIngredients, EnumDyeColor.GREEN, "Ingredients items", new BoundingRectangle(18 * 4 + 10, 25, 5 * 18, 18)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                if (inputIngredients.getLocked()) return super.canInsertItem(slot, stack);
                if (stack.getItem().equals(Items.GLASS_BOTTLE)) return false;
                if (slot == 0) {
                    return stack.getItem().equals(Items.NETHER_WART);
                } else return !stack.getItem().equals(Items.NETHER_WART);
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }
        });
        this.addInventoryToStorage(inputIngredients, "pot_ener_in");
        registerSyncIntPart(NBT_ACTION, nbtTagInt -> action = nbtTagInt.getInt(), () -> new NBTTagInt(action), SyncProviderLevel.GUI);
    }

    private void initOutputInventories() {
        outputPotions = new ItemStackHandler(3) {
            @Override
            protected void onContentsChanged(int slot) {
                PotionEnervatorTile.this.markDirty();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
        this.addInventory(new CustomColoredItemHandler(outputPotions, EnumDyeColor.PURPLE, "Potions items", 18 * 6 + 10, 25 + 18 * 2, 3, 1) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }

        });
        this.addInventoryToStorage(outputPotions, "pot_ener_out");
    }

    @Override
    public void protectedUpdate() {
        super.protectedUpdate();
        if (inputIngredients.getLocked() != inputGlassBottles.getLocked()) toggleInventoryLock(EnumDyeColor.GREEN);
    }


    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
        pieces.add(new BasicRenderedGuiPiece(0, 0, 16, 16, new ResourceLocation(Reference.MOD_ID, "textures/gui/machines.png"), 16, 16) {
            @Override
            public void drawBackgroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
                container.mc.getTextureManager().bindTexture(new ResourceLocation(Reference.MOD_ID, "textures/gui/machines.png"));
                if (action == 0) {
                    container.drawTexturedRect(18 * 4 + 11, 44, 78, 16 * 3, 15, 15);
                } else {
                    container.drawTexturedRect(18 * (action + 3) + 11, 43, 78, 16 * 2, 15, 15);
                }
            }

            @Override
            public void drawForegroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, int mouseX, int mouseY) {
                super.drawForegroundLayer(container, guiX, guiY, mouseX, mouseY);
                if (action == 0) {
                    if (mouseX - guiX > 18 * 4 + 11 && mouseY - guiY > 44 && mouseX - guiX < 18 * 4 + 11 + 15 && mouseY - guiY <= 44 + 15)
                        container.drawTooltip(Arrays.asList("Filling with water"), mouseX - guiX, mouseY - guiY);
                } else {
                    if (mouseX - guiX > 18 * (action + 3) + 11 && mouseY - guiY > 43 && mouseX - guiX < 18 * (action + 3) + 11 + 15 && mouseY - guiY <= 43 + 15)
                        container.drawTooltip(Arrays.asList("Adding ingredient"), mouseX - guiX, mouseY - guiY);
                }
            }
        });
        pieces.add(new BasicRenderedGuiPiece(18 * 4 + 10, 25 + 18 * 2, 0, 0, new ResourceLocation(Reference.MOD_ID, "textures/gui/machines.png"), 0, 0) {
            @Override
            public void drawBackgroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
                //super.drawBackgroundLayer(container, guiX, guiY, partialTicks, mouseX, mouseY);
            }

            @Override
            public void drawMiddleLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
                super.drawMiddleLayer(container, guiX, guiY, partialTicks, mouseX, mouseY);
                if (inputGlassBottles.getStackInSlot(0).isEmpty() && inputGlassBottles.getFilterStack(0).isEmpty())
                    ItemStackUtils.renderItemIntoGUI(new ItemStack(Items.GLASS_BOTTLE), container.getGuiLeft() + 18 * 4 + 11, container.getGuiTop() + 26 + 18 * 2, 8);
                if (inputIngredients.getStackInSlot(0).isEmpty() && inputIngredients.getFilterStack(0).isEmpty())
                    ItemStackUtils.renderItemIntoGUI(new ItemStack(Items.NETHER_WART), container.getGuiLeft() + 18 * 4 + 11, container.getGuiTop() + 26, 8);
                if (inputIngredients.getStackInSlot(1).isEmpty() && inputIngredients.getFilterStack(1).isEmpty()) {
                    ItemStackUtils.renderItemIntoGUI(new ItemStack(Items.REDSTONE), container.getGuiLeft() + 18 * 5 + 11, container.getGuiTop() + 26, 9);
                    ItemStackUtils.renderItemIntoGUI(new ItemStack(Items.GUNPOWDER), container.getGuiLeft() + 18 * 5 + 11, container.getGuiTop() + 26, 8);
                }
                if (inputIngredients.getStackInSlot(2).isEmpty() && inputIngredients.getFilterStack(2).isEmpty()) {
                    ItemStackUtils.renderItemIntoGUI(new ItemStack(Items.SUGAR), container.getGuiLeft() + 18 * 6 + 11, container.getGuiTop() + 26, 9);
                    ItemStackUtils.renderItemIntoGUI(new ItemStack(Items.GLOWSTONE_DUST), container.getGuiLeft() + 18 * 6 + 11, container.getGuiTop() + 26, 8);
                }
                if (inputIngredients.getStackInSlot(3).isEmpty() && inputIngredients.getFilterStack(3).isEmpty()) {
                    ItemStackUtils.renderItemIntoGUI(new ItemStack(Items.FERMENTED_SPIDER_EYE), container.getGuiLeft() + 18 * 7 + 11, container.getGuiTop() + 26, 9);
                    ItemStackUtils.renderItemIntoGUI(new ItemStack(Items.MAGMA_CREAM), container.getGuiLeft() + 18 * 7 + 11, container.getGuiTop() + 26, 8);
                }
                if (inputIngredients.getStackInSlot(4).isEmpty() && inputIngredients.getFilterStack(4).isEmpty()) {
                    ItemStackUtils.renderItemIntoGUI(new ItemStack(Items.GHAST_TEAR), container.getGuiLeft() + 18 * 8 + 11, container.getGuiTop() + 26, 9);
                    ItemStackUtils.renderItemIntoGUI(new ItemStack(Items.DRAGON_BREATH), container.getGuiLeft() + 18 * 8 + 11, container.getGuiTop() + 26, 8);
                }
            }
        });
        pieces.add(1, new LockedInventoryTogglePiece(18 * 7 + 9, 83, this, EnumDyeColor.ORANGE));
        return pieces;
    }

    @Override
    public float performWork() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;
        if (action > 5) action = 0;
        if (action != 0 && outputPotions.getStackInSlot(0).isEmpty() && outputPotions.getStackInSlot(1).isEmpty() && outputPotions.getStackInSlot(2).isEmpty()) {
            action = 0;
            partialSync(NBT_ACTION, true);
            return 1;
        }
        if (action == 0 && inputGlassBottles.getStackInSlot(0).getCount() >= 3 && ItemHandlerHelper.insertItem(outputPotions, new ItemStack(Items.POTIONITEM, 3), true).isEmpty() && fluidTank.getFluidAmount() >= 3000) { //DUMMY STACK
            ItemStack bottles = new ItemStack(Items.POTIONITEM, 3);
            NBTTagCompound c = new NBTTagCompound();
            c.setString("Potion", "minecraft:water");
            bottles.setTagCompound(c);
            ItemHandlerHelper.insertItem(outputPotions, bottles, false);
            fluidTank.drain(3000, true);
            inputGlassBottles.getStackInSlot(0).setCount(inputGlassBottles.getStackInSlot(0).getCount() - 3);
            action = 1;
            partialSync(NBT_ACTION, true);
            return 1;
        } else if (action > 0) {
            ItemStack ingredient = inputIngredients.getStackInSlot(action - 1);
            if (!ingredient.isEmpty()) {
                NonNullList<ItemStack> potions = NonNullList.create();
                potions.add(outputPotions.getStackInSlot(0));
                potions.add(outputPotions.getStackInSlot(1));
                potions.add(outputPotions.getStackInSlot(2));
                if (BrewingRecipeRegistry.hasOutput(potions.get(0), ingredient)) {
                    BrewingRecipeRegistry.brewPotions(potions, ingredient, new int[]{0, 1, 2});
                    for (int i = 0; i < 3; ++i) outputPotions.setStackInSlot(i, potions.get(i));
                    ++action;
                    ingredient.setCount(ingredient.getCount() - 1);
                    if (action > 5) action = 0;
                    partialSync(NBT_ACTION, true);
                    return 1;
                }
            }
        }
        return 0;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tagCompound = super.writeToNBT(compound);
        tagCompound.setInteger(NBT_ACTION, action);
        return tagCompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (!compound.hasKey(NBT_ACTION)) action = 0;
        else action = compound.getInteger(NBT_ACTION);
    }

    public IFluidTank getFluidTank() {
        return fluidTank;
    }

    public ItemStackHandler getOutputPotions() {
        return outputPotions;
    }
}

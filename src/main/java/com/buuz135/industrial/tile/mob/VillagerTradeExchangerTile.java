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
package com.buuz135.industrial.tile.mob;

import com.buuz135.industrial.item.MobImprisonmentToolItem;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.proxy.client.infopiece.ArrowInfoPiece;
import com.buuz135.industrial.proxy.client.infopiece.VillagerTradeExchangerInfoPiece;
import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.CustomElectricMachine;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.utils.Reference;
import lombok.Getter;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.TeslaCoreLib;
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.netsync.SimpleNBTMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VillagerTradeExchangerTile extends CustomElectricMachine {

    private static final String NBT_CURRENT = "Current";

    private ItemStackHandler villager;
    @Getter
    private MerchantRecipeList merchantRecipes;
    @Getter
    private int current;
    private IItemHandlerModifiable input;
    private IItemHandlerModifiable output;

    public VillagerTradeExchangerTile() {
        super(WorkingAreaElectricMachine.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        villager = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                if (!villager.getStackInSlot(0).isEmpty() && villager.getStackInSlot(0).getTagCompound().hasKey("Offers")) {
                    merchantRecipes = new MerchantRecipeList(villager.getStackInSlot(0).getTagCompound().getCompoundTag("Offers"));
                    merchantRecipes.removeIf(MerchantRecipe::hasSecondItemToBuy); // TODO this is a temporary fix until we add actual support for trades with 2 items (#77)
                } else {
                    merchantRecipes = null;
                    current = 0;
                }
            }

            @Override
            public void deserializeNBT(NBTTagCompound nbt) {
                super.deserializeNBT(nbt);
                this.onContentsChanged(0);
            }
        };
        this.addInventoryToStorage(villager, "villager");
        this.addInventory(new CustomColoredItemHandler(villager, EnumDyeColor.BROWN, "Villager Mob Imprisonment Tool", 145, 61, 1, 1) {
            @Override
            public boolean canInsertItem(int slot, @NotNull ItemStack stack) {
                return stack.getItem() instanceof MobImprisonmentToolItem && ItemRegistry.mobImprisonmentToolItem.containsEntity(stack) && ItemRegistry.mobImprisonmentToolItem.getEntityFromStack(stack, VillagerTradeExchangerTile.this.world, false) instanceof EntityVillager;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return super.canExtractItem(slot);
            }
        });
        input = this.addSimpleInventory(1, "input", EnumDyeColor.BLUE, "Trade input", new BoundingRectangle(52, 61, 18, 18),
                (stack, integer) -> true, (stack, integer) -> false, false, null);
        output = this.addSimpleInventory(1, "output", EnumDyeColor.ORANGE, "Trade output", new BoundingRectangle(112, 61, 18, 18),
                (stack, integer) -> false, (stack, integer) -> true, false, null);

    }

    @Override
    protected float performWork() {
        if (merchantRecipes != null && merchantRecipes.size() > current) {
            MerchantRecipe recipe = merchantRecipes.get(current);
            if (ItemHandlerHelper.insertItem(output, recipe.getItemToSell().copy(), true).isEmpty()) {
                if (recipe.getItemToBuy().isItemEqual(input.getStackInSlot(0)) && input.getStackInSlot(0).getCount() >= recipe.getItemToBuy().getCount()) {
                    input.getStackInSlot(0).shrink(recipe.getItemToBuy().getCount());
                    ItemHandlerHelper.insertItem(output, recipe.getItemToSell().copy(), false);
                    return 1;
                }
            }
        }
        return 0;
    }

    @NotNull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound compound1 = super.writeToNBT(compound);
        compound1.setInteger(NBT_CURRENT, current);
        return compound1;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        current = compound.getInteger(NBT_CURRENT);
    }

    @NotNull
    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer<?> container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
        pieces.add(new VillagerTradeExchangerInfoPiece(this));
        pieces.add(new BasicRenderedGuiPiece(54 + 18 + 4, 61, 25, 18, new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 24, 5));
        pieces.add(new ArrowInfoPiece(138, 28, 17, 56, "text.industrialforegoing.button.decrease_trade") {
            @Override
            protected void clicked() {
                if (TeslaCoreLib.INSTANCE.isClientSide()) {
                    VillagerTradeExchangerTile.this.sendToServer(VillagerTradeExchangerTile.this.setupSpecialNBTMessage("TRADE_PREV"));
                }
            }
        });
        pieces.add(new ArrowInfoPiece(156, 28, 33, 56, "text.industrialforegoing.button.increase_trade") {
            @Override
            protected void clicked() {
                if (TeslaCoreLib.INSTANCE.isClientSide()) {
                    VillagerTradeExchangerTile.this.sendToServer(VillagerTradeExchangerTile.this.setupSpecialNBTMessage("TRADE_NEXT"));
                }
            }
        });
        return pieces;
    }


    @Nullable
    @Override
    protected SimpleNBTMessage processClientMessage(String messageType, NBTTagCompound compound) {
        super.processClientMessage(messageType, compound);
        if (messageType.equals("TRADE_NEXT")) {
            nextTrade();
        }
        if (messageType.equals("TRADE_PREV")) {
            prevTrade();
        }
        return null;
    }

    public void nextTrade() {
        if (merchantRecipes == null || merchantRecipes.size() == 0) return;
        current = (current + 1) % merchantRecipes.size();
        forceSync();
    }

    public void prevTrade() {
        if (merchantRecipes == null || merchantRecipes.size() == 0) return;
        current = (current - 1);
        if (current < 0) current = merchantRecipes.size() - 1;
        forceSync();
    }
}

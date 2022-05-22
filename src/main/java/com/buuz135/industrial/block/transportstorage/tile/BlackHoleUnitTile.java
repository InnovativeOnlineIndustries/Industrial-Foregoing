/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
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
package com.buuz135.industrial.block.transportstorage.tile;

import com.buuz135.industrial.gui.component.BigItemGuiAddon;
import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.NumberUtils;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.api.filter.FilterSlot;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.client.screen.addon.BasicButtonAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.component.button.ButtonComponent;
import com.hrznstudio.titanium.filter.ItemStackFilter;
import com.hrznstudio.titanium.util.AssetUtil;
import com.hrznstudio.titanium.util.LangUtil;
import com.hrznstudio.titanium.util.RayTraceUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class BlackHoleUnitTile extends BHTile<BlackHoleUnitTile> {

    private static HashMap<UUID, Long> INTERACTION_LOGGER = new HashMap<>();

    @Save
    private ItemStack blStack;
    @Save
    private int stored;
    @Save
    private ItemStackFilter filter;
    @Save
    private boolean voidItems;
    @Save
    private boolean useStackDisplay;
    @Save
    private boolean hasNBT;

    private BlackHoleHandler handler;
    private final LazyOptional<IItemHandler> lazyStorage;

    public BlackHoleUnitTile(BasicTileBlock<BlackHoleUnitTile> basicTileBlock, BlockEntityType<?> type, Rarity rarity, BlockPos blockPos, BlockState blockState) {
        super(basicTileBlock, type, blockPos, blockState);
        this.blStack = ItemStack.EMPTY;
        this.stored = 0;
        this.voidItems = true;
        this.useStackDisplay = false;
        this.hasNBT = false;
        this.handler = new BlackHoleHandler(BlockUtils.getStackAmountByRarity(rarity));
        this.lazyStorage = LazyOptional.of(() -> handler);
        this.addFilter(filter = new ItemStackFilter("filter" , 1));
        FilterSlot slot = new FilterSlot<>(79, 60 , 0, ItemStack.EMPTY);
        slot.setColor(DyeColor.CYAN);
        this.filter.setFilter(0, slot);

        addButton(new ButtonComponent(82+ 20 * 2, 64+16, 18, 18) {
            @Override
            @OnlyIn(Dist.CLIENT)
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                return Collections.singletonList(() -> new BasicButtonAddon(this) {
                    @Override
                    public void drawBackgroundLayer(PoseStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
                        AssetUtil.drawAsset(stack, screen, provider.getAsset(AssetTypes.ITEM_BACKGROUND), guiX + getPosX(), guiY + getPosY());
                        Minecraft.getInstance().getItemRenderer().renderGuiItem(new ItemStack(voidItems ? Items.MAGMA_CREAM: Items.SLIME_BALL), guiX + getPosX() + 1, guiY + getPosY() + 1);
//                        Lighting.turnOff();
//                        RenderSystem.enableAlphaTest();
                    }

                    @Override
                    public List<Component> getTooltipLines() {
                        List<Component> lines = new ArrayList<>();
                        lines.add(new TextComponent(ChatFormatting.GOLD + LangUtil.getString("tooltip.industrialforegoing.bl." + ( voidItems ? "void_unit" : "no_void_unit"))));
                        return lines;
                    }
                });
            }
        }.setPredicate((playerEntity, compoundNBT) -> {
            this.voidItems = !this.voidItems;
            this.syncObject(this.voidItems);
        }));
        addButton(new ButtonComponent(82+ 20, 64+16, 18, 18) {
            @Override
            @OnlyIn(Dist.CLIENT)
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                return Collections.singletonList(() -> new BasicButtonAddon(this) {
                    @Override
                    public void drawBackgroundLayer(PoseStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
                        AssetUtil.drawAsset(stack, screen, provider.getAsset(AssetTypes.ITEM_BACKGROUND), guiX + getPosX(), guiY + getPosY());
                        Minecraft.getInstance().getItemRenderer().renderGuiItem(new ItemStack(useStackDisplay ? Items.IRON_BLOCK: Items.IRON_INGOT), guiX + getPosX() + 1, guiY + getPosY() + 1);
//                        Lighting.turnOff();
//                        RenderSystem.enableAlphaTest();
                    }

                    @Override
                    public List<Component> getTooltipLines() {
                        List<Component> lines = new ArrayList<>();
                        lines.add(new TextComponent(ChatFormatting.GOLD + LangUtil.getString("tooltip.industrialforegoing.bl." + ( useStackDisplay ? "stack_unit" : "compact_unit"))));
                        return lines;
                    }
                });
            }
        }.setPredicate((playerEntity, compoundNBT) -> {
            this.useStackDisplay = !useStackDisplay;
            this.syncObject(this.useStackDisplay);
        }));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initClient() {
        super.initClient();
        this.addGuiAddonFactory(() -> new BigItemGuiAddon(79, 25) {
            @Override
            public ItemStack getItemStack() {
                return blStack;
            }

            @Override
            public int getAmount() {
                return stored;
            }

            @Override
            public String getAmountDisplay() {
                return getFormatedDisplayAmount();
            }
        });
    }

    @Nonnull
    @Override
    public BlackHoleUnitTile getSelf() {
        return this;
    }

    @Override
    public InteractionResult onActivated(Player playerIn, InteractionHand hand, Direction facing, double hitX, double hitY, double hitZ) {
        if (super.onActivated(playerIn, hand, facing, hitX, hitY, hitZ) == InteractionResult.SUCCESS) {
            return InteractionResult.SUCCESS;
        }
        if (playerIn.isShiftKeyDown()){
            openGui(playerIn);
        } else if (facing.equals(this.getFacingDirection())){
            ItemStack stack = playerIn.getItemInHand(hand);
            if (!stack.isEmpty() && handler.isItemValid(0, stack)) {
                playerIn.setItemInHand(hand, handler.insertItem(0, stack, false));
            } else if (System.currentTimeMillis() - INTERACTION_LOGGER.getOrDefault(playerIn.getUUID(), System.currentTimeMillis()) < 300) {
                for (ItemStack itemStack : playerIn.inventory.items) {
                    if (!itemStack.isEmpty() && handler.insertItem(0, itemStack, true).isEmpty()) {
                        handler.insertItem(0, itemStack.copy(), false);
                        itemStack.setCount(0);
                    }
                }
            }
            INTERACTION_LOGGER.put(playerIn.getUUID(), System.currentTimeMillis());
        }
        return InteractionResult.SUCCESS;
    }

    public void onClicked(Player playerIn) {
        if (isServer()){
            HitResult rayTraceResult = RayTraceUtils.rayTraceSimple(this.level, playerIn, 16, 0);
            if (rayTraceResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockResult = (BlockHitResult) rayTraceResult;
                Direction facing = blockResult.getDirection();
                if (facing.equals(this.getFacingDirection())){
                    ItemHandlerHelper.giveItemToPlayer(playerIn, handler.extractItem(0, playerIn.isShiftKeyDown() ? 64 : 1, false));
                }
            }
        }
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, BlackHoleUnitTile blockEntity) {
        super.serverTick(level, pos, state, blockEntity);
        if (isServer()){
            if (!this.hasNBT && this.blStack.hasTag()){
                ItemStack stack = this.blStack.copy();
                stack.setTag(null);
                this.setStack(stack);
            }
        }
    }

    public void setAmount(int amount){
        boolean equal = amount == stored;
        this.stored = amount;
        if (!equal) syncObject(this.stored);
    }

    public void setStack(ItemStack stack){
        boolean equal = blStack.sameItem(stack) && ItemStack.tagMatches(blStack, stack);
        this.blStack = stack;
        this.hasNBT = this.blStack.hasTag();
        if (!equal) syncObject(this.blStack);
    }

    @Nonnull
    @Override
    public <U> LazyOptional<U> getCapability(@Nonnull Capability<U> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return lazyStorage.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public ItemStack getDisplayStack() {
        return blStack;
    }

    @Override
    public String getFormatedDisplayAmount() {
        if (this.useStackDisplay) return stored == 0 ? "0" : (stored >= 64 ? NumberUtils.getFormatedBigNumber(stored / 64) + " x64" : "") + (stored >= 64 && stored % 64 != 0 ? " + " : "") + (stored % 64 != 0 ? stored % 64 : "");
        return NumberUtils.getFormatedBigNumber(stored);
    }

    private class BlackHoleHandler implements IItemHandler {

        private int amount;

        public BlackHoleHandler(int amount){
            this.amount = amount;
        }

        @Override
        public int getSlots() {
            return 1;
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            ItemStack copied = blStack.copy();
            copied.setCount(stored);
            return copied;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (isItemValid(slot, stack)) {
                int inserted = Math.min(this.amount - stored, stack.getCount());
                if (!simulate){
                    setStack(stack);
                    setAmount(Math.min(stored + inserted, amount));
                }
                if (inserted == stack.getCount() || voidItems) return ItemStack.EMPTY;
                return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - inserted);
            }
            return stack;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (amount == 0) return ItemStack.EMPTY;
            if (blStack.isEmpty()) return ItemStack.EMPTY;
            if (stored <= amount) {
                ItemStack out = blStack.copy();
                int newAmount = stored;
                if (!simulate) {
                    setStack(ItemStack.EMPTY);
                    setAmount(0);
                }
                out.setCount(newAmount);
                return out;
            } else {
                if (!simulate) {
                    setAmount(stored - amount);
                }
                return ItemHandlerHelper.copyStackWithSize(blStack, amount);
            }
        }

        @Override
        public int getSlotLimit(int slot) {
            return amount;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (slot == 0){
                ItemStack fl = blStack;
                if (!filter.getFilterSlots()[slot].getFilter().isEmpty()) {
                    ItemStack filterStack = filter.getFilterSlots()[slot].getFilter();
                    if (filterStack.sameItem(fl) && ItemStack.tagMatches(filterStack, fl)) {
                        fl = filter.getFilterSlots()[slot].getFilter();
                    }
                }
                return fl.isEmpty() || (fl.sameItem(stack) && ItemStack.tagMatches(fl, stack));
            }
            return false;
        }

    }
}

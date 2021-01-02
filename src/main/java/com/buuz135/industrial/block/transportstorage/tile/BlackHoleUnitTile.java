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
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
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

    private BlackHoleHandler handler;
    private final LazyOptional<IItemHandler> lazyStorage;

    public BlackHoleUnitTile(BasicTileBlock<BlackHoleUnitTile> basicTileBlock, Rarity rarity) {
        super(basicTileBlock);
        this.blStack = ItemStack.EMPTY;
        this.stored = 0;
        this.voidItems = true;
        this.useStackDisplay = false;
        this.handler = new BlackHoleHandler(BlockUtils.getStackAmountByRarity(rarity));
        this.lazyStorage = LazyOptional.of(() -> handler);
        this.addFilter(filter = new ItemStackFilter("filter" , 1));
        FilterSlot slot = new FilterSlot<>(79, 60 , 0, ItemStack.EMPTY);
        slot.setColor(DyeColor.CYAN);
        this.filter.setFilter(0, slot);
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
        addButton(new ButtonComponent(82+ 20 * 2, 64+16, 18, 18) {
            @Override
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                return Collections.singletonList(() -> new BasicButtonAddon(this) {
                    @Override
                    public void drawBackgroundLayer(MatrixStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
                        AssetUtil.drawAsset(stack, screen, provider.getAsset(AssetTypes.ITEM_BACKGROUND), guiX + getPosX(), guiY + getPosY());
                        Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(new ItemStack(voidItems ? Items.MAGMA_CREAM: Items.SLIME_BALL), guiX + getPosX() + 1, guiY + getPosY() + 1);
                        RenderHelper.disableStandardItemLighting();
                        RenderSystem.enableAlphaTest();
                    }

                    @Override
                    public List<ITextComponent> getTooltipLines() {
                        List<ITextComponent> lines = new ArrayList<>();
                        lines.add(new StringTextComponent(TextFormatting.GOLD + LangUtil.getString("tooltip.industrialforegoing.bl." + ( voidItems ? "void_unit" : "no_void_unit"))));
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
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                return Collections.singletonList(() -> new BasicButtonAddon(this) {
                    @Override
                    public void drawBackgroundLayer(MatrixStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
                        AssetUtil.drawAsset(stack, screen, provider.getAsset(AssetTypes.ITEM_BACKGROUND), guiX + getPosX(), guiY + getPosY());
                        Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(new ItemStack(useStackDisplay ? Items.IRON_BLOCK: Items.IRON_INGOT), guiX + getPosX() + 1, guiY + getPosY() + 1);
                        RenderHelper.disableStandardItemLighting();
                        RenderSystem.enableAlphaTest();
                    }

                    @Override
                    public List<ITextComponent> getTooltipLines() {
                        List<ITextComponent> lines = new ArrayList<>();
                        lines.add(new StringTextComponent(TextFormatting.GOLD + LangUtil.getString("tooltip.industrialforegoing.bl." + ( useStackDisplay ? "stack_unit" : "compact_unit"))));
                        return lines;
                    }
                });
            }
        }.setPredicate((playerEntity, compoundNBT) -> {
            this.useStackDisplay = !useStackDisplay;
            this.syncObject(this.useStackDisplay);
        }));
    }

    @Nonnull
    @Override
    public BlackHoleUnitTile getSelf() {
        return this;
    }

    @Override
    public ActionResultType onActivated(PlayerEntity playerIn, Hand hand, Direction facing, double hitX, double hitY, double hitZ) {
        if (super.onActivated(playerIn, hand, facing, hitX, hitY, hitZ) == ActionResultType.SUCCESS) {
            return ActionResultType.SUCCESS;
        }
        if (playerIn.isSneaking()){
            openGui(playerIn);
        } else if (facing.equals(this.getFacingDirection())){
            ItemStack stack = playerIn.getHeldItem(hand);
            if (!stack.isEmpty() && handler.isItemValid(0, stack)) {
                playerIn.setHeldItem(hand, handler.insertItem(0, stack, false));
            } else if (System.currentTimeMillis() - INTERACTION_LOGGER.getOrDefault(playerIn.getUniqueID(), System.currentTimeMillis()) < 300) {
                for (ItemStack itemStack : playerIn.inventory.mainInventory) {
                    if (!itemStack.isEmpty() && handler.insertItem(0, itemStack, true).isEmpty()) {
                        handler.insertItem(0, itemStack.copy(), false);
                        itemStack.setCount(0);
                    }
                }
            }
            INTERACTION_LOGGER.put(playerIn.getUniqueID(), System.currentTimeMillis());
        }
        return ActionResultType.SUCCESS;
    }

    public void onClicked(PlayerEntity playerIn) {
        if (isServer()){
            RayTraceResult rayTraceResult = RayTraceUtils.rayTraceSimple(this.world, playerIn, 16, 0);
            if (rayTraceResult.getType() == RayTraceResult.Type.BLOCK) {
                BlockRayTraceResult blockResult = (BlockRayTraceResult) rayTraceResult;
                Direction facing = blockResult.getFace();
                if (facing.equals(this.getFacingDirection())){
                    ItemHandlerHelper.giveItemToPlayer(playerIn, handler.extractItem(0, playerIn.isSneaking() ? 64 : 1, false));
                }
            }
        }
    }

    public void setAmount(int amount){
        boolean equal = amount == stored;
        this.stored = amount;
        if (!equal) syncObject(this.stored);
    }

    public void setStack(ItemStack stack){
        boolean equal = blStack.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(blStack, stack);
        this.blStack = stack;
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
                if (voidItems) inserted = stack.getCount();
                if (!simulate){
                    setStack(stack);
                    setAmount(Math.min(stored + inserted, amount));
                }
                if (inserted == stack.getCount()) return ItemStack.EMPTY;
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
                if (!filter.getFilterSlots()[slot].getFilter().isEmpty()) fl = filter.getFilterSlots()[slot].getFilter();
                return fl.isEmpty() || (fl.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(fl, stack));
            }
            return false;
        }

    }
}

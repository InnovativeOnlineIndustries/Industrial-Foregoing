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
package com.buuz135.industrial.item.addon.movility;

import com.buuz135.industrial.item.addon.CustomAddon;
import com.buuz135.industrial.tile.api.IAcceptsTransferAddons;
import com.buuz135.industrial.utils.Reference;
import lombok.Getter;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.ndrei.teslacorelib.tileentities.SidedTileEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public abstract class TransferAddon extends CustomAddon implements ITransferAction {

    @Getter
    private final ActionMode mode;

    protected TransferAddon(String registryName, ActionMode mode) {
        super(registryName + "_" + mode.name().toLowerCase());
        this.mode = mode;
        setHasSubtypes(true);
        setMaxStackSize(1);
        setTranslationKey(Reference.MOD_ID + "." + registryName);
    }

    public EnumFacing getFacingFromMeta(ItemStack stack) {
        return EnumFacing.values()[stack.getMetadata()];
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        stack.setItemDamage((stack.getMetadata() + 1) % EnumFacing.values().length);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {
            ItemStack stack = player.getHeldItem(hand).copy();
            stack.setItemDamage(facing.getIndex());
            player.setHeldItem(hand, stack);
            return EnumActionResult.SUCCESS;
        }
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TextComponentTranslation("text.industrialforegoing.direction").getFormattedText() + " " + TextFormatting.GRAY + new TextComponentTranslation("text.industrialforegoing." + getFacingFromMeta(stack)).getFormattedText());
        tooltip.add(new TextComponentTranslation("text.industrialforegoing.action").getFormattedText() + " " + TextFormatting.GRAY + new TextComponentTranslation("text.industrialforegoing." + mode.name().toLowerCase()).getFormattedText());
        tooltip.add(new TextComponentTranslation("text.industrialforegoing.tooltip.max_transfer_amount").getUnformattedText() + " " + TextFormatting.GRAY + getTransferAmount(stack) * (stack.getItem() instanceof ItemStackTransferAddon ? 1 : 100) + " " + new TextComponentTranslation("text.industrialforegoing.tooltip." + (stack.getItem() instanceof ItemStackTransferAddon ? "items" : "mb")).getUnformattedText() + "/" + new TextComponentTranslation("text.industrialforegoing.tooltip.tick_time").getUnformattedText());
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return !stack.isItemEnchanted();
    }

    @Override
    public int getItemEnchantability() {
        return 2;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return !stack.isItemEnchanted() && Enchantment.getEnchantmentID(enchantment) == 32;
    }

    public int getTransferAmount(ItemStack stack) {
        return (int) Math.pow(2, stack.isItemEnchanted() ? EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(32), stack) + 1 : 1);
    }

    @Override
    public boolean canBeAddedTo(@NotNull SidedTileEntity machine) {
        return machine instanceof IAcceptsTransferAddons && ((IAcceptsTransferAddons) machine).canAcceptAddon(this);
    }

    @Override
    public float getWorkEnergyMultiplier() {
        return 1.05f;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return new TextComponentTranslation(getTranslationKey() + ".name").getUnformattedText() + " (" + new TextComponentTranslation("text.industrialforegoing." + this.mode.name().toLowerCase()).getUnformattedText() + "/" + new TextComponentTranslation("text.industrialforegoing." + getFacingFromMeta(stack).name().toLowerCase()).getUnformattedText() + ")";
    }

    @Override
    public void registerRenderer() {
        for (EnumFacing facing : EnumFacing.values()) {
            ModelLoader.setCustomModelResourceLocation(this, facing.getIndex(), new ModelResourceLocation(this.getRegistryName().toString() + "_" + facing.getName().toLowerCase(), "inventory"));
        }
    }

    public enum ActionMode {
        PUSH,
        PULL
    }
}

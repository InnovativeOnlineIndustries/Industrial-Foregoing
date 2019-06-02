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
package com.buuz135.industrial.item;

import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class MobImprisonmentToolItem extends IFCustomItem {

    public MobImprisonmentToolItem() {
        super("mob_imprisonment_tool", new Properties().maxStackSize(1));
    }

    @Override
    public EnumActionResult onItemUse(ItemUseContext context) {
        EntityPlayer player = context.getPlayer();
        BlockPos pos = context.getPos();
        EnumFacing facing = context.getFace();
        World worldIn = context.getWorld();
        ItemStack stack = context.getItem();
        if (player.getEntityWorld().isRemote) return EnumActionResult.FAIL;
        if (!containsEntity(stack)) return EnumActionResult.FAIL;
        Entity entity = getEntityFromStack(stack, worldIn, true);
        BlockPos blockPos = pos.offset(facing);
        entity.setPositionAndRotation(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, 0);
        stack.setTag(new NBTTagCompound());
        worldIn.spawnEntity(entity);
        if (entity instanceof EntityLiving) ((EntityLiving) entity).playAmbientSound();
        return EnumActionResult.SUCCESS;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
        if (target.getEntityWorld().isRemote) return false;
        if (target instanceof EntityPlayer || !target.isNonBoss() || !target.isAlive()) return false;
        if (containsEntity(stack)) return false;
        String entityID = EntityType.getId(target.getType()).toString();
        if (isBlacklisted(entityID)) return false;
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("entity", entityID);
        target.writeWithoutTypeId(nbt);
        stack.setTag(nbt);
        playerIn.swingArm(hand);
        playerIn.setHeldItem(hand, stack);
        target.onKillCommand();
        return true;
    }


    public boolean isBlacklisted(String entity) {
        return false;
    }

    public boolean containsEntity(ItemStack stack) {
        return !stack.isEmpty() && stack.hasTag() && stack.getTag().hasKey("entity");
    }

    @Override
    public boolean hasTooltipDetails(@Nullable Key key) {
        return true;
    }

    @Override
    public void addTooltipDetails(@Nullable Key key, ItemStack stack, List<ITextComponent> tooltip, boolean advanced) {
        super.addTooltipDetails(key, stack, tooltip, advanced);
        if (containsEntity(stack)) {
            tooltip.add(new TextComponentString("Mob: " + getID(stack)));//new TextComponentTranslation(EntityList.getTranslationName(new ResourceLocation(getID(stack)))).getUnformattedComponentText());
            tooltip.add(new TextComponentString("Health: " + stack.getTag().getDouble("Health")));
            //if (BlockRegistry.mobDuplicatorBlock.blacklistedEntities.contains(stack.getTag().getString("entity")))
            //    tooltip.add(new TextComponentString(TextFormatting.RED + "Entity blacklisted in the Mob Duplicator"));
        }
    }

    public Entity getEntityFromStack(ItemStack stack, World world, boolean withInfo) {
        Entity entity = EntityType.create(world, new ResourceLocation(stack.getTag().getString("entity")));
        if (withInfo) entity.read(stack.getTag());
        return entity;
    }

    public String getID(ItemStack stack) {
        return stack.getTag().getString("entity");
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), " p ", "pgp", " p ", 'p', ModuleCore.PLASTIC, 'g',
                new ItemStack(Items.GHAST_TEAR));
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        if (!containsEntity(stack))
            return new TextComponentTranslation(super.getTranslationKey(stack));
        return new TextComponentTranslation(super.getTranslationKey(stack)).appendText(" (" + getID(stack) + ")");
    }

}
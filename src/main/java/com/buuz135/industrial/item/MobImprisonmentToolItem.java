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

package com.buuz135.industrial.item;

import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class MobImprisonmentToolItem extends IFCustomItem {

    public MobImprisonmentToolItem(ItemGroup group) {
        super("mob_imprisonment_tool", group, new Properties().maxStackSize(1));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getPos();
        Direction facing = context.getFace();
        World worldIn = context.getWorld();
        ItemStack stack = context.getItem();
        if (!release(player, pos, facing, worldIn, stack)) return ActionResultType.FAIL;
        return ActionResultType.SUCCESS;
    }

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        if (!capture(stack, target)) return ActionResultType.FAIL;
        playerIn.swingArm(hand);
        playerIn.setHeldItem(hand, stack);
        return ActionResultType.SUCCESS;
    }

    public boolean capture(ItemStack stack, LivingEntity target) {
        if (target.getEntityWorld().isRemote) return false;
        if (target instanceof PlayerEntity || !target.isNonBoss() || !target.isAlive()) return false;
        if (containsEntity(stack)) return false;
        if (isBlacklisted(target.getType())) return false;
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("entity", EntityType.getKey(target.getType()).toString());
        target.writeWithoutTypeId(nbt);
        stack.setTag(nbt);
        target.remove(true);
        return true;
    }

    public boolean release(PlayerEntity player, BlockPos pos, Direction facing, World worldIn, ItemStack stack) {
        if (player.getEntityWorld().isRemote) return false;
        if (!containsEntity(stack)) return false;
        Entity entity = getEntityFromStack(stack, worldIn, true, false);
        BlockPos blockPos = pos.offset(facing);
        entity.setPositionAndRotation(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, 0);
        stack.setTag(new CompoundNBT());
        worldIn.addEntity(entity);
        return true;
    }

    public boolean isBlacklisted(EntityType<?> entity) {
        return IndustrialTags.EntityTypes.MOB_IMPRISONMENT_TOOL_BLACKLIST.contains(entity);
    }

    public boolean containsEntity(ItemStack stack) {
        return !stack.isEmpty() && stack.hasTag() && stack.getTag().contains("entity");
    }

    @Override
    public boolean hasTooltipDetails(@Nullable Key key) {
        return key == null;
    }

    @Override
    public void addTooltipDetails(@Nullable Key key, ItemStack stack, List<ITextComponent> tooltip, boolean advanced) {
        super.addTooltipDetails(key, stack, tooltip, advanced);
        if (containsEntity(stack)) {
            tooltip.add(new StringTextComponent("Mob: " + getID(stack)));//new TranslationTextComponent(EntityList.getTranslationName(new ResourceLocation(getID(stack)))).getUnformattedComponentText());
            tooltip.add(new StringTextComponent("Health: " + stack.getTag().getDouble("Health")));
            //if (BlockRegistry.mobDuplicatorBlock.blacklistedEntities.contains(stack.getTag().getString("entity")))
            //    tooltip.add(new TextComponentString(TextFormatting.RED + "Entity blacklisted in the Mob Duplicator"));
        }
    }

    @Nullable
    public Entity getEntityFromStack(ItemStack stack, World world, boolean withInfo, boolean applyDuplicatorFilter) {
        if (stack.hasTag()) {
            EntityType type = EntityType.byKey(stack.getTag().getString("entity")).orElse(null);
            if (type != null && !(applyDuplicatorFilter && IndustrialTags.EntityTypes.MOB_DUPLICATOR_BLACKLIST.contains(type))) {
                Entity entity = type.create(world);
                if (withInfo) {
                    entity.read(stack.getTag());
                } else if (!type.isSummonable()) {
                    return null;
                }
                return entity;
            }
        }
        return null;
    }

    public String getID(ItemStack stack) {
        return stack.getTag().getString("entity");
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        if (!containsEntity(stack))
            return new TranslationTextComponent(super.getTranslationKey(stack));
        return new TranslationTextComponent(super.getTranslationKey(stack)).appendString(" (" + getID(stack) + ")");
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine(" P ").patternLine("PGP").patternLine(" P ")
                .key('P', IndustrialTags.Items.PLASTIC)
                .key('G', Items.GHAST_TEAR)
                .build(consumer);

    }
}

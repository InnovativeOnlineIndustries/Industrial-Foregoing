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
import com.hrznstudio.titanium.util.TagUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class MobImprisonmentToolItem extends IFCustomItem {

    public MobImprisonmentToolItem(CreativeModeTab group) {
        super("mob_imprisonment_tool", group, new Properties().stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getClickedFace();
        Level worldIn = context.getLevel();
        ItemStack stack = context.getItemInHand();
        if (!release(player, pos, facing, worldIn, stack)) return InteractionResult.FAIL;
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity target, InteractionHand hand) {
        if (!capture(stack, target)) return InteractionResult.FAIL;
        playerIn.swing(hand);
        playerIn.setItemInHand(hand, stack);
        return InteractionResult.SUCCESS;
    }

    public boolean capture(ItemStack stack, LivingEntity target) {
        if (target.getCommandSenderWorld().isClientSide) return false;
        if (target instanceof Player || !target.canChangeDimensions() || !target.isAlive()) return false;
        if (containsEntity(stack)) return false;
        if (isBlacklisted(target.getType())) return false;
        CompoundTag nbt = new CompoundTag();
        nbt.putString("entity", EntityType.getKey(target.getType()).toString());
        target.saveWithoutId(nbt);
        stack.setTag(nbt);
        target.remove(Entity.RemovalReason.KILLED);
        return true;
    }

    public boolean release(Player player, BlockPos pos, Direction facing, Level worldIn, ItemStack stack) {
        if (player.getCommandSenderWorld().isClientSide) return false;
        if (!containsEntity(stack)) return false;
        Entity entity = getEntityFromStack(stack, worldIn, true, false);
        BlockPos blockPos = pos.relative(facing);
        entity.absMoveTo(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, 0);
        stack.setTag(null);
        worldIn.addFreshEntity(entity);
        return true;
    }

    public boolean isBlacklisted(EntityType<?> entity) {
        return TagUtil.hasTag(ForgeRegistries.ENTITY_TYPES, entity, IndustrialTags.EntityTypes.MOB_IMPRISONMENT_TOOL_BLACKLIST);
    }

    public boolean containsEntity(ItemStack stack) {
        return !stack.isEmpty() && stack.hasTag() && stack.getTag().contains("entity");
    }

    @Override
    public boolean hasTooltipDetails(@Nullable Key key) {
        return key == null;
    }

    @Override
    public void addTooltipDetails(@Nullable Key key, ItemStack stack, List<Component> tooltip, boolean advanced) {
        super.addTooltipDetails(key, stack, tooltip, advanced);
        if (containsEntity(stack)) {
            tooltip.add(Component.literal("Mob: " + getID(stack)));//new TranslationTextComponent(EntityList.getTranslationName(new ResourceLocation(getID(stack)))).getUnformattedComponentText());
            tooltip.add(Component.literal("Health: " + stack.getTag().getDouble("Health")));
            //if (BlockRegistry.mobDuplicatorBlock.blacklistedEntities.contains(stack.getTag().getString("entity")))
            //    tooltip.add(Component.literalString(TextFormatting.RED + "Entity blacklisted in the Mob Duplicator"));
        }
    }

    @Nullable
    public Entity getEntityFromStack(ItemStack stack, Level world, boolean withInfo, boolean applyDuplicatorFilter) {
        if (stack.hasTag()) {
            EntityType type = EntityType.byString(stack.getTag().getString("entity")).orElse(null);
            if (type != null && !(applyDuplicatorFilter && ForgeRegistries.ENTITY_TYPES.tags().getTag(IndustrialTags.EntityTypes.MOB_DUPLICATOR_BLACKLIST).contains(type))) {
                Entity entity = type.create(world);
                if (withInfo) {
                    entity.load(stack.getTag());
                } else if (!type.canSummon()) {
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
    public Component getName(ItemStack stack) {
        if (!containsEntity(stack))
            return Component.translatable(super.getDescriptionId(stack));
        return Component.translatable(super.getDescriptionId(stack)).append(" (" + getID(stack) + ")");
    }

    @Override
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .pattern(" P ").pattern("PGP").pattern(" P ")
                .define('P', IndustrialTags.Items.PLASTIC)
                .define('G', Items.GHAST_TEAR)
                .save(consumer);

    }
}

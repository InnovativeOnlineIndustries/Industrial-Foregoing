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

import com.buuz135.industrial.utils.IFAttachments;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import com.hrznstudio.titanium.tab.TitaniumTab;
import com.hrznstudio.titanium.util.TagUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;

import javax.annotation.Nullable;
import java.util.List;

public class MobImprisonmentToolItem extends IFCustomItem {

    public MobImprisonmentToolItem(TitaniumTab group) {
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
        if (target instanceof Player || target.getType().is(Tags.EntityTypes.BOSSES) || !target.isAlive()) return false;
        if (containsEntity(stack)) return false;
        if (isBlacklisted(target.getType())) return false;
        CompoundTag nbt = new CompoundTag();
        nbt.putString("entity", EntityType.getKey(target.getType()).toString());
        target.saveWithoutId(nbt);
        stack.set(IFAttachments.MOB_IMPRISONMENT_TOOL, nbt);
        target.remove(Entity.RemovalReason.DISCARDED);
        return true;
    }

    public boolean release(Player player, BlockPos pos, Direction facing, Level worldIn, ItemStack stack) {
        if (player.getCommandSenderWorld().isClientSide) return false;
        if (!containsEntity(stack)) return false;
        Entity entity = getEntityFromStack(stack, worldIn, true, false);
        BlockPos blockPos = pos.relative(facing);
        entity.absMoveTo(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, 0);
        stack.remove(IFAttachments.MOB_IMPRISONMENT_TOOL);
        worldIn.addFreshEntity(entity);
        return true;
    }

    public boolean isBlacklisted(EntityType<?> entity) {
        return TagUtil.hasTag(BuiltInRegistries.ENTITY_TYPE, entity, IndustrialTags.EntityTypes.MOB_IMPRISONMENT_TOOL_BLACKLIST);
    }

    public boolean containsEntity(ItemStack stack) {
        return !stack.isEmpty() && stack.has(IFAttachments.MOB_IMPRISONMENT_TOOL);
    }

    @Override
    public boolean hasTooltipDetails(@Nullable Key key) {
        return key == null;
    }

    @Override
    public void addTooltipDetails(@Nullable Key key, ItemStack stack, List<Component> tooltip, boolean advanced) {
        super.addTooltipDetails(key, stack, tooltip, advanced);
        if (containsEntity(stack)) {
            var nbt = stack.get(IFAttachments.MOB_IMPRISONMENT_TOOL);
            tooltip.add(Component.literal("Mob: " + getID(stack)).withStyle(ChatFormatting.GRAY));//new TranslationTextComponent(EntityList.getTranslationName(ResourceLocation.fromNamespaceAndPath(getID(stack)))).getUnformattedComponentText());
            tooltip.add(Component.literal("Health: " + nbt.getDouble("Health")).withStyle(ChatFormatting.GRAY));
            //if (BlockRegistry.mobDuplicatorBlock.blacklistedEntities.contains(stack.getTag().getString("entity")))
            //    tooltip.add(Component.literalString(TextFormatting.RED + "Entity blacklisted in the Mob Duplicator"));
        }
    }

    @Nullable
    public Entity getEntityFromStack(ItemStack stack, Level world, boolean withInfo, boolean applyDuplicatorFilter) {
        if (stack.has(IFAttachments.MOB_IMPRISONMENT_TOOL)) {
            var nbt = stack.get(IFAttachments.MOB_IMPRISONMENT_TOOL);
            EntityType type = EntityType.byString(nbt.getString("entity")).orElse(null);
            if (type != null && !(applyDuplicatorFilter && TagUtil.hasTag(BuiltInRegistries.ENTITY_TYPE, type, IndustrialTags.EntityTypes.MOB_DUPLICATOR_BLACKLIST))) {
                Entity entity = type.create(world);
                if (withInfo) {
                    entity.load(nbt);
                } else if (!type.canSummon()) {
                    return null;
                }
                return entity;
            }
        }
        return null;
    }

    public String getID(ItemStack stack) {
        var nbt = stack.get(IFAttachments.MOB_IMPRISONMENT_TOOL);
        return nbt.getString("entity");
    }

    @Override
    public Component getName(ItemStack stack) {
        if (!containsEntity(stack))
            return Component.translatable(super.getDescriptionId(stack));
        return Component.translatable(super.getDescriptionId(stack)).append(" (" + getID(stack) + ")");
    }

    @Override
    public void registerRecipe(RecipeOutput consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .pattern(" P ").pattern("PGP").pattern(" P ")
                .define('P', IndustrialTags.Items.PLASTIC)
                .define('G', Items.GHAST_TEAR)
                .save(consumer);

    }
}

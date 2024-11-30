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

package com.buuz135.industrial.item.infinity.item;


import com.buuz135.industrial.item.infinity.InfinityTier;
import com.buuz135.industrial.item.infinity.ItemInfinity;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.tab.TitaniumTab;
import com.hrznstudio.titanium.util.RayTraceUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemInfinityDrill extends ItemInfinity {

    public static int POWER_CONSUMPTION = 10000;
    public static int FUEL_CONSUMPTION = 30;

    public ItemInfinityDrill(TitaniumTab group) {
        //.addToolType(ToolType.PICKAXE, 6).addToolType(ToolType.SHOVEL, 6)
        super("infinity_drill", group, new Properties().stacksTo(1), POWER_CONSUMPTION, FUEL_CONSUMPTION, false);
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return Items.DIAMOND_PICKAXE.supportsEnchantment(new ItemStack(Items.DIAMOND_PICKAXE), enchantment) || Items.DIAMOND_SHOVEL.supportsEnchantment(new ItemStack(Items.DIAMOND_SHOVEL), enchantment);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState blockIn) {
        return Items.DIAMOND_PICKAXE.isCorrectToolForDrops(stack, blockIn) || Items.DIAMOND_SHOVEL.isCorrectToolForDrops(stack, blockIn);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (entityLiving instanceof Player) {
            HitResult rayTraceResult = RayTraceUtils.rayTraceSimple(worldIn, entityLiving, 16, 0);
            if (rayTraceResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockResult = (BlockHitResult) rayTraceResult;
                Direction facing = blockResult.getDirection();
                InfinityTier currentTier = getSelectedTier(stack);
                Pair<BlockPos, BlockPos> area = getArea(pos, facing, currentTier, true);
                List<ItemStack> totalDrops = new ArrayList<>();
                BlockPos.betweenClosed(area.getLeft(), area.getRight()).forEach(blockPos -> {
                    if (enoughFuel(stack) && worldIn.getBlockEntity(blockPos) == null && worldIn instanceof ServerLevel serverLevel && entityLiving instanceof ServerPlayer && !worldIn.isEmptyBlock(blockPos) && BlockUtils.canBlockBeBroken(worldIn, blockPos, entityLiving.getStringUUID())) {
                        BlockState tempState = worldIn.getBlockState(blockPos);
                        Block block = tempState.getBlock();
                        if (!tempState.is(BlockTags.MINEABLE_WITH_PICKAXE) && !tempState.is(BlockTags.MINEABLE_WITH_SHOVEL)) return;
                        if (tempState.getDestroySpeed(worldIn, blockPos) < 0) return;
                        var xp = EnchantmentHelper.processBlockExperience(serverLevel, stack, block.getExpDrop(tempState, worldIn, blockPos, worldIn.getBlockEntity(blockPos), entityLiving, stack));
                        if (block.onDestroyedByPlayer(tempState, worldIn, blockPos, (Player) entityLiving, true, tempState.getFluidState())) {

                            block.destroy(worldIn, blockPos, tempState);
                            //block.harvestBlock(worldIn, (PlayerEntity) entityLiving, blockPos, tempState, null, stack);

                            Block.getDrops(tempState, (ServerLevel) worldIn, blockPos, null, (Player) entityLiving, stack).forEach(itemStack -> {
                                boolean combined = false;
                                for (ItemStack drop : totalDrops) {
                                    if (ItemStack.isSameItemSameComponents(drop, itemStack) && drop.getCount() + itemStack.getCount() <= itemStack.getMaxStackSize()) {
                                        drop.setCount(drop.getCount() + itemStack.getCount());
                                        combined = true;
                                        break;
                                    }
                                }
                                if (!combined) {
                                    totalDrops.add(itemStack);
                                }
                            });
                            block.popExperience((ServerLevel) worldIn, blockPos, xp);
                            consumeFuel(stack);
                        }
                    }
                });
                totalDrops.forEach(itemStack -> {
                    Block.popResource(worldIn, entityLiving.blockPosition(), itemStack);
                });
                worldIn.getEntitiesOfClass(ExperienceOrb.class, new AABB(area.getLeft().getCenter(), area.getRight().getCenter()).inflate(1)).forEach(entityXPOrb -> entityXPOrb.teleportTo(entityLiving.blockPosition().getX(), entityLiving.blockPosition().getY(), entityLiving.blockPosition().getZ()));
            }
        }
        return super.mineBlock(stack, worldIn, state, pos, entityLiving);
    }

    @Override
    public void registerRecipe(RecipeOutput consumer) {
        DissolutionChamberRecipe.createRecipe(consumer, "infinity_drill", new DissolutionChamberRecipe(List.of(
                Ingredient.of(new ItemStack(Items.DIAMOND_BLOCK)),
                Ingredient.of(new ItemStack(Items.DIAMOND_SHOVEL)),
                Ingredient.of(new ItemStack(Items.DIAMOND_BLOCK)),
                Ingredient.of(new ItemStack(Items.DIAMOND_BLOCK)),
                Ingredient.of(new ItemStack(ModuleCore.RANGE_ADDONS[11].get())),
                Ingredient.of(IndustrialTags.Items.GEAR_GOLD),
                Ingredient.of(IndustrialTags.Items.GEAR_GOLD),
                Ingredient.of(IndustrialTags.Items.GEAR_GOLD)
        ), new FluidStack(ModuleCore.PINK_SLIME.getSourceFluid().get(), 2000), 400, Optional.of(new ItemStack(this)), Optional.empty()));

    }

//    public void configuration(Configuration config) {TODO
//        int i = 0;
//        for (DrillTier value : DrillTier.values()) {
//            value.setPowerNeeded(Long.parseLong(config.getString(i + "_" + value.name, Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "infinity_drill" + Configuration.CATEGORY_SPLITTER + "power_values", value.powerNeeded + "", "")));
//            value.setRadius(config.getInt(i + "_" + value.name, Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "infinity_drill" + Configuration.CATEGORY_SPLITTER + "radius", value.radius, 0, Integer.MAX_VALUE, ""));
//            ++i;
//        }
//    }

}

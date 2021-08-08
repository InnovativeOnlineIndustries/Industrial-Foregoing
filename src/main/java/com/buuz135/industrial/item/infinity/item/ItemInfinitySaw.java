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
import com.buuz135.industrial.utils.apihandlers.plant.TreeCache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.block.BlockState;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ItemInfinitySaw extends ItemInfinity {

    public static LoadingCache<Pair<World, BlockPos>, TreeCache> SAW_CACHE = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build(new CacheLoader<Pair<World, BlockPos>, TreeCache>() {
        @Override
        public TreeCache load(Pair<World, BlockPos> key) throws Exception {
            TreeCache cache = new TreeCache(key.getLeft(), key.getRight());
            cache.scanForTreeBlockSection();
            return cache;
        }
    });

    public static int POWER_CONSUMPTION = 10000;
    public static int FUEL_CONSUMPTION = 3;

    public ItemInfinitySaw(ItemGroup group) {
        super("infinity_saw", group, new Properties().maxStackSize(1).addToolType(ToolType.AXE, 3), POWER_CONSUMPTION, FUEL_CONSUMPTION, false);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return Items.DIAMOND_AXE.canApplyAtEnchantingTable(new ItemStack(Items.DIAMOND_AXE), enchantment) ;
    }

    @Override
    public boolean canHarvestBlock(BlockState blockIn) {
        return Items.DIAMOND_AXE.canHarvestBlock(blockIn);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (entityLiving instanceof PlayerEntity) {
            if (BlockUtils.isLog(worldIn, pos)) {
                try {
                    TreeCache cache = SAW_CACHE.get(Pair.of(worldIn, pos));
                    if (cache != null) {
                        List<ItemStack> itemStacks = new ArrayList<>();
                        InfinityTier currentTier = getSelectedTier(stack);
                        int operations = getBlocksAmount(currentTier.getRadius());
                        for (int i = 0; i < operations; ++i) {
                            if (cache.getWoodCache().isEmpty() && cache.getLeavesCache().isEmpty()) {
                                SAW_CACHE.invalidate(Pair.of(worldIn, pos));
                                break;
                            }
                            if (!enoughFuel(stack)) break;
                            if (!cache.getLeavesCache().isEmpty()) {
                                itemStacks.addAll(cache.chop(cache.getLeavesCache(), false));
                            } else {
                                itemStacks.addAll(cache.chop(cache.getWoodCache(), false));
                            }
                            consumeFuel(stack);
                        }
                        itemStacks.forEach(itemStack -> ItemHandlerHelper.giveItemToPlayer((PlayerEntity) entityLiving, itemStack));
                        return false;
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private int getBlocksAmount(int radius) {
        return (radius * 2 + 1) * (radius * 2 + 1);
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        new DissolutionChamberRecipe(this.getRegistryName(),
                new Ingredient.IItemList[]{
                        new Ingredient.SingleItemList(new ItemStack(Items.DIAMOND_BLOCK)),
                        new Ingredient.SingleItemList(new ItemStack(Items.DIAMOND_PICKAXE)),
                        new Ingredient.SingleItemList(new ItemStack(Items.DIAMOND_AXE)),
                        new Ingredient.SingleItemList(new ItemStack(Items.DIAMOND_AXE)),
                        new Ingredient.SingleItemList(new ItemStack(ModuleCore.RANGE_ADDONS[11])),
                        new Ingredient.TagList(IndustrialTags.Items.GEAR_GOLD),
                        new Ingredient.TagList(IndustrialTags.Items.GEAR_GOLD),
                        new Ingredient.TagList(IndustrialTags.Items.GEAR_GOLD),
                },
                new FluidStack(ModuleCore.PINK_SLIME.getSourceFluid(), 2000), 400, new ItemStack(this), FluidStack.EMPTY);
    }
}

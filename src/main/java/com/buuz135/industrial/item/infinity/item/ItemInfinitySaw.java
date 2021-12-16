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
import net.minecraft.core.BlockPos;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ItemInfinitySaw extends ItemInfinity {

    public static LoadingCache<Pair<Level, BlockPos>, TreeCache> SAW_CACHE = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build(new CacheLoader<Pair<Level, BlockPos>, TreeCache>() {
        @Override
        public TreeCache load(Pair<Level, BlockPos> key) throws Exception {
            TreeCache cache = new TreeCache(key.getLeft(), key.getRight());
            cache.scanForTreeBlockSection();
            return cache;
        }
    });

    public static int POWER_CONSUMPTION = 10000;
    public static int FUEL_CONSUMPTION = 3;

    public ItemInfinitySaw(CreativeModeTab group) {
//        .addToolType(ToolAction.AXE, 3)
        super("infinity_saw", group, new Properties().stacksTo(1), POWER_CONSUMPTION, FUEL_CONSUMPTION, false);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return Items.DIAMOND_AXE.canApplyAtEnchantingTable(new ItemStack(Items.DIAMOND_AXE), enchantment) ;
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState blockIn) {
        return Items.DIAMOND_AXE.isCorrectToolForDrops(blockIn);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (entityLiving instanceof Player) {
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
                        itemStacks.forEach(itemStack -> ItemHandlerHelper.giveItemToPlayer((Player) entityLiving, itemStack));
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
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {
        new DissolutionChamberRecipe(this.getRegistryName(),
                new Ingredient.Value[]{
                        new Ingredient.ItemValue(new ItemStack(Items.DIAMOND_BLOCK)),
                        new Ingredient.ItemValue(new ItemStack(Items.DIAMOND_PICKAXE)),
                        new Ingredient.ItemValue(new ItemStack(Items.DIAMOND_AXE)),
                        new Ingredient.ItemValue(new ItemStack(Items.DIAMOND_AXE)),
                        new Ingredient.ItemValue(new ItemStack(ModuleCore.RANGE_ADDONS[11].get())),
                        new Ingredient.TagValue(IndustrialTags.Items.GEAR_GOLD),
                        new Ingredient.TagValue(IndustrialTags.Items.GEAR_GOLD),
                        new Ingredient.TagValue(IndustrialTags.Items.GEAR_GOLD),
                },
                new FluidStack(ModuleCore.PINK_SLIME.getSourceFluid(), 2000), 400, new ItemStack(this), FluidStack.EMPTY);
    }
}

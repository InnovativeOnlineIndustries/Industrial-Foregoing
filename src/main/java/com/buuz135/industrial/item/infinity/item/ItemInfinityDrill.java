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
package com.buuz135.industrial.item.infinity.item;


import com.buuz135.industrial.item.infinity.InfinityTier;
import com.buuz135.industrial.item.infinity.ItemInfinity;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.util.RayTraceUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ItemInfinityDrill extends ItemInfinity {

    public static Material[] mineableMaterials = {Material.ORGANIC, Material.EARTH, Material.ANVIL, Material.CLAY, Material.GLASS, Material.ICE, Material.IRON, Material.PACKED_ICE, Material.PISTON, Material.ROCK, Material.SAND, Material.SNOW};
    public static int POWER_CONSUMPTION = 10000;
    public static int FUEL_CONSUMPTION = 30;

    public ItemInfinityDrill(ItemGroup group) {
        super("infinity_drill", group, new Properties().maxStackSize(1).addToolType(ToolType.PICKAXE, 6).addToolType(ToolType.SHOVEL, 6), POWER_CONSUMPTION, FUEL_CONSUMPTION, false);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.type == EnchantmentType.DIGGER;
    }

    @Override
    public boolean canHarvestBlock(BlockState blockIn) {
        return Items.DIAMOND_PICKAXE.canHarvestBlock(blockIn) || Items.DIAMOND_SHOVEL.canHarvestBlock(blockIn);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (entityLiving instanceof PlayerEntity) {
            RayTraceResult rayTraceResult = RayTraceUtils.rayTraceSimple(worldIn, entityLiving, 16, 0);
            if (rayTraceResult.getType() == RayTraceResult.Type.BLOCK) {
                BlockRayTraceResult blockResult = (BlockRayTraceResult) rayTraceResult;
                Direction facing = blockResult.getFace();
                InfinityTier currentTier = getSelectedTier(stack);
                Pair<BlockPos, BlockPos> area = getArea(pos, facing, currentTier, true);
                List<ItemStack> totalDrops = new ArrayList<>();
                BlockPos.getAllInBoxMutable(area.getLeft(), area.getRight()).forEach(blockPos -> {
                    if (enoughFuel(stack) && worldIn.getTileEntity(blockPos) == null && worldIn instanceof ServerWorld && entityLiving instanceof ServerPlayerEntity && !worldIn.isAirBlock(blockPos)) {
                        BlockState tempState = worldIn.getBlockState(blockPos);
                        Block block = tempState.getBlock();
                        if (!BlockUtils.isBlockstateInMaterial(tempState, mineableMaterials)) return;
                        if (tempState.getBlockHardness(worldIn, blockPos) < 0) return;
                        int xp = ForgeHooks.onBlockBreakEvent(worldIn, ((ServerPlayerEntity) entityLiving).interactionManager.getGameType(), (ServerPlayerEntity) entityLiving, blockPos);
                        if (xp >= 0 && block.removedByPlayer(tempState, worldIn, blockPos, (PlayerEntity) entityLiving, true, tempState.getFluidState())) {
                            block.onPlayerDestroy(worldIn, blockPos, tempState);
                            //block.harvestBlock(worldIn, (PlayerEntity) entityLiving, blockPos, tempState, null, stack);
                            Block.getDrops(tempState, (ServerWorld) worldIn, blockPos, null, (PlayerEntity) entityLiving, stack).forEach(itemStack -> {
                                boolean combined = false;
                                for (ItemStack drop : totalDrops) {
                                    if (ItemHandlerHelper.canItemStacksStack(drop, itemStack)) {
                                        drop.setCount(drop.getCount() + itemStack.getCount());
                                        combined = true;
                                        break;
                                    }
                                }
                                if (!combined) {
                                    totalDrops.add(itemStack);
                                }
                            });
                            block.dropXpOnBlockBreak((ServerWorld) worldIn, blockPos, xp);
                            consumeFuel(stack);
                        }
                    }
                });
                totalDrops.forEach(itemStack -> {
                    Block.spawnAsEntity(worldIn, entityLiving.getPosition(), itemStack);
                });
                worldIn.getEntitiesWithinAABB(ExperienceOrbEntity.class, new AxisAlignedBB(area.getLeft(), area.getRight()).grow(1)).forEach(entityXPOrb -> entityXPOrb.setPositionAndUpdate(entityLiving.getPosition().getX(), entityLiving.getPosition().getY(), entityLiving.getPosition().getZ()));
            }
        }
        return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        new DissolutionChamberRecipe(this.getRegistryName(),
                new Ingredient.IItemList[]{
                        new Ingredient.SingleItemList(new ItemStack(Items.DIAMOND_BLOCK)),
                        new Ingredient.SingleItemList(new ItemStack(Items.DIAMOND_SHOVEL)),
                        new Ingredient.SingleItemList(new ItemStack(Items.DIAMOND_BLOCK)),
                        new Ingredient.SingleItemList(new ItemStack(Items.DIAMOND_BLOCK)),
                        new Ingredient.SingleItemList(new ItemStack(ModuleCore.RANGE_ADDONS[11])),
                        new Ingredient.TagList(IndustrialTags.Items.GEAR_GOLD),
                        new Ingredient.TagList(IndustrialTags.Items.GEAR_GOLD),
                        new Ingredient.TagList(IndustrialTags.Items.GEAR_GOLD),
                },
                new FluidStack(ModuleCore.PINK_SLIME.getSourceFluid(), 2000), 400, new ItemStack(this), FluidStack.EMPTY);
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

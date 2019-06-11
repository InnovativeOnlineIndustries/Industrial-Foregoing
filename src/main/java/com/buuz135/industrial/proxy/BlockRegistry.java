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
package com.buuz135.industrial.proxy;


public class BlockRegistry {

    //public static IFCustomFluidBlock BLOCK_ESSENCE = new IFCustomFluidBlock(FluidsRegistry.ESSENCE, Material.WATER, entityLivingBase -> entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 20 * 15, 0)));
    //public static IFCustomFluidBlock BLOCK_MILK = new IFCustomFluidBlock(FluidsRegistry.MILK, Material.WATER, EntityLivingBase::clearActivePotions);
    //public static IFCustomFluidBlock BLOCK_MEAT = new IFCustomFluidBlock(FluidsRegistry.MEAT, Material.WATER, entityLivingBase -> entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.SATURATION, 1, 1)));
    //public static IFCustomFluidBlock BLOCK_LATEX = new IFCustomFluidBlock(FluidsRegistry.LATEX, Material.WATER, entityLivingBase -> entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20 * 15, 5)));
    //public static IFCustomFluidBlock BLOCK_SEWAGE = new IFCustomFluidBlock(FluidsRegistry.SEWAGE, Material.WATER, entityLivingBase -> entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.POISON, 20, 0)));
    //public static IFCustomFluidBlock BLOCK_SLUDGE = new IFCustomFluidBlock(FluidsRegistry.SLUDGE, Material.LAVA, entityLivingBase -> entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.WITHER, 1, 0)));
    //public static IFCustomFluidBlock BLOCK_BIOFUEL = new IFCustomFluidBlock(FluidsRegistry.BIOFUEL, Material.WATER, entityLivingBase -> entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.SPEED, 20, 1)));
    //public static IFCustomFluidBlock BLOCK_PINK_SLIME = (IFCustomFluidBlock) new IFCustomFluidBlock(FluidsRegistry.PINK_SLIME, Material.WATER, entityLivingBase -> entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.GLOWING, 20 * 10, 0))) {
    //    @Override
    //    public void randomTick(BlockState state, World worldIn, BlockPos pos, Random random) {
    //        if (state.getBlock().getFluidState(state).isSource()) {
    //            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
    //            EntityPinkSlime pinkSlime = new EntityPinkSlime(worldIn);
    //            pinkSlime.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    //            worldIn.spawnEntity(pinkSlime);
    //        }
    //    }
    //}.setTickRandomly(true);
    //public static IFCustomFluidBlock BLOCK_PROTEIN = new IFCustomFluidBlock(FluidsRegistry.PROTEIN, Material.WATER, entityLivingBase -> entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.HASTE, 20 * 3, 0)));


    public static void createRecipes() {
        //CustomOrientedBlock.blockList.forEach(CustomOrientedBlock::createRecipe);
        //BlockBase.BLOCKS.forEach(BlockBase::createRecipe);
        //RecipeUtils.addShapelessRecipe(new ItemStack(blackHoleUnitBlock), blackHoleUnitBlock);
        //RecipeUtils.addShapelessRecipe(new ItemStack(blackHoleTankBlock), blackHoleTankBlock);
    }

    public static void registerBlocks() {
        //mod.addBlocks(
        //        BLOCK_ESSENCE,
        //        BLOCK_MILK,
        //        BLOCK_MEAT,
        //        BLOCK_LATEX,
        //        BLOCK_SEWAGE,
        //        BLOCK_SLUDGE,
        //        BLOCK_BIOFUEL,
        //        BLOCK_PINK_SLIME,
        //        BLOCK_PROTEIN
        //);
    }

}

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

package com.buuz135.industrial.block.resourceproduction;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.resourceproduction.tile.LaserDrillTile;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class LaserDrillBlock extends IndustrialBlock<LaserDrillTile> {

    public LaserDrillBlock() {
        super("laser_drill", Properties.ofFullCopy(Blocks.IRON_BLOCK), LaserDrillTile.class, ModuleResourceProduction.TAB_RESOURCE);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<LaserDrillTile> getTileEntityFactory() {
        return LaserDrillTile::new;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.SIX_WAY;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, Level world, BlockPos pos, RandomSource rand) {
        if (world.getBlockEntity(pos) instanceof LaserDrillTile tile) {
            if (!tile.getTarget().equals(BlockPos.ZERO) && tile.isSpawnParticles()) {
                BlockPos target = tile.getTarget();
                Vec3i vector = tile.getFacingDirection().getOpposite().getNormal();
                Vec3 vec = new Vec3(pos.getX(), pos.getY(), pos.getZ());
                vec = vec.add(0.5, 0.5, 0.5).add(vector.getX() / 2D, vector.getY() / 2D, vector.getZ() / 2D);
                Vec3 velocity = vec.vectorTo(new Vec3(target.getX(), target.getY(), target.getZ()).add(0.5, 0.5, 0.5));
                double vel = 10;
                world.addParticle(ParticleTypes.END_ROD, vec.x(), vec.y(), vec.z(), velocity.x / vel, velocity.y / vel, velocity.z / vel);
            }
        }
    }

    @Override
    public void registerRecipe(RecipeOutput consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this).pattern("pfp").pattern("bmb").pattern("grg")
                .define('p', IndustrialTags.Items.PLASTIC)
                .define('f', IndustrialTags.Items.GEAR_DIAMOND)
                .define('b', Items.PISTON)
                .define('m', IndustrialTags.Items.MACHINE_FRAME_SIMPLE)
                .define('g', IndustrialTags.Items.GEAR_GOLD)
                .define('r', Items.REDSTONE)
                .save(consumer);
    }
}

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
package com.buuz135.industrial.proxy.block;

import com.buuz135.industrial.utils.RayTraceUtils;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockBase extends Block {

    public static final List<BlockBase> BLOCKS = new ArrayList<>();

    public BlockBase(String name) {
        super(Material.ROCK);
        this.setRegistryName(new ResourceLocation(Reference.MOD_ID, name));
        this.setTranslationKey(Reference.MOD_ID + "." + name);
        BLOCKS.add(this);
    }

    public void registerBlock(IForgeRegistry<Block> blocks) {
        blocks.register(this);
    }

    public void registerItem(IForgeRegistry<Item> items) {
        items.register(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }

    public void registerRender() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }

    public void createRecipe() {

    }

    @Nullable
    @Override
    public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
        if (hasCustomBoxes())
            return RayTraceUtils.rayTraceBoxesClosest(start, end, pos, getBoundingBoxes(blockState, worldIn, pos));
        return super.collisionRayTrace(blockState, worldIn, pos, start, end);
    }

    public boolean hasCustomBoxes() {
        return false;
    }

    @Nullable
    public Cuboid getCuboidHit(World worldIn, BlockPos pos, EntityPlayer playerIn) {
        RayTraceResult result = RayTraceUtils.rayTrace(worldIn.getBlockState(pos), worldIn, pos, playerIn, 10, this.getBoundingBoxes(worldIn.getBlockState(pos), worldIn, pos));
        if (result instanceof DistanceRayTraceResult) {
            return (Cuboid) result.hitInfo;
        }
        return null;
    }

    public List<Cuboid> getBoundingBoxes(IBlockState state, IBlockAccess source, BlockPos pos) {
        return Collections.emptyList();
    }

}

package com.buuz135.industrial.proxy.block;

import com.buuz135.industrial.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

public class BlockBase extends Block {

    public static final List<BlockBase> BLOCKS = new ArrayList<>();

    public BlockBase(String name) {
        super(Material.ROCK);
        this.setRegistryName(new ResourceLocation(Reference.MOD_ID, name));
        this.setUnlocalizedName(Reference.MOD_ID + "." + name);
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

    protected RayTraceResult rayTraceBoxesClosest(Vec3d start, Vec3d end, BlockPos pos, List<Cuboid> boxes) {
        List<DistanceRayTraceResult> results = new ArrayList<>();
        for (Cuboid box : boxes) {
            DistanceRayTraceResult hit = rayTraceBox(pos, start, end, box);
            if (hit != null)
                results.add(hit);
        }
        RayTraceResult closestHit = null;
        double curClosest = Double.MAX_VALUE;
        for (DistanceRayTraceResult hit : results) {
            if (curClosest > hit.dist) {
                closestHit = hit;
                curClosest = hit.dist;
            }
        }
        return closestHit;
    }

    protected static DistanceRayTraceResult rayTraceBox(BlockPos pos, Vec3d start, Vec3d end, Cuboid box) {
        Vec3d startRay = start.subtract(new Vec3d(pos));
        Vec3d endRay = end.subtract(new Vec3d(pos));
        RayTraceResult bbResult = box.aabb().calculateIntercept(startRay, endRay);

        if (bbResult != null) {
            Vec3d hitVec = bbResult.hitVec.add(new Vec3d(pos));
            EnumFacing sideHit = bbResult.sideHit;
            double dist = start.squareDistanceTo(hitVec);
            return new DistanceRayTraceResult(hitVec, pos, sideHit, box, dist);
        }
        return null;
    }
}

package com.buuz135.industrial.utils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockUtils {

    public static List<BlockPos> getBlockPosInAABB(AxisAlignedBB axisAlignedBB) {
        List<BlockPos> blocks = new ArrayList<BlockPos>();
        for (double x = axisAlignedBB.minX; x < axisAlignedBB.maxX; ++x) {
            for (double z = axisAlignedBB.minZ; z < axisAlignedBB.maxZ; ++z) {
                for (double y = axisAlignedBB.minY; y < axisAlignedBB.maxY; ++y) {
                    blocks.add(new BlockPos(x, y, z));
                }
            }
        }
        return blocks;
    }

    public static boolean isBlockOreDict(World world, BlockPos pos,String ore){
        IBlockState state = world.getBlockState(pos);
        Item item = Item.getItemFromBlock(state.getBlock());
        if (!item.equals(Items.AIR)){
            ItemStack stack = new ItemStack(item);
            int id = OreDictionary.getOreID(ore);
            for (int i  : OreDictionary.getOreIDs(stack)){
                if (i == id) return true;
            }
        }
        return false;
    }

    public static boolean isLog(World world, BlockPos pos){
        return isBlockOreDict(world,pos,"logWood");
    }

    public static boolean isLeaves(World world, BlockPos pos){
        return isBlockOreDict(world,pos,"treeLeaves");
    }
}

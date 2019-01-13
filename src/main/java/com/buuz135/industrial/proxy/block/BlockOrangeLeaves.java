package com.buuz135.industrial.proxy.block;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class BlockOrangeLeaves extends BlockLeaves {
    public static final PropertyBool FRUITFUL = PropertyBool.create("fruitful");

    public BlockOrangeLeaves() {
        super();
        setRegistryName("orange_leaves");
        setTranslationKey(Reference.MOD_ID + ".orange_leaves");
        setCreativeTab(IndustrialForegoing.creativeTab);
    }

    @Override
    public BlockPlanks.EnumType getWoodType(int meta) {
        return BlockPlanks.EnumType.OAK;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FRUITFUL);
    }

    @Override
    protected void dropApple(World worldIn, BlockPos pos, IBlockState state, int chance) {
        if (state.getValue(FRUITFUL)) {
            spawnAsEntity(worldIn, pos, new ItemStack(ItemRegistry.orange, worldIn.rand.nextInt(2) + 1));
        }
    }

    @Nonnull
    @Override
    public List<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        IBlockState state = world.getBlockState(pos);
        return Collections.singletonList(new ItemStack(this, state.getValue(FRUITFUL) ? 1 : 0));
    }
}

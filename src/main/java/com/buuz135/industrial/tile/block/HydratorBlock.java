package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.agriculture.HydratorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;
import java.util.Random;

public class HydratorBlock extends CustomAreaOrientedBlock<HydratorTile> {

    public HydratorBlock() {
        super("hydrator", HydratorTile.class, Material.ROCK, 1000, 10, RangeType.UP, 2, 0, false);
    }

    @Override
    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pfp", "cmc", "rir",
                'p', ItemRegistry.plastic,
                'f', Items.WATER_BUCKET,
                'r', "gearIron",
                'm', MachineCaseItem.INSTANCE,
                'i', Blocks.PISTON,
                'c', ItemRegistry.fertilizer);
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.AGRICULTURE;
    }

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.add(0, new PageText("When provided with power it will increase the " + PageText.bold("grow speed") + " of the crops in the working area."));
        return pages;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        super.randomDisplayTick(stateIn, worldIn, pos, rand);
        for (BlockPos pos1 : BlockPos.getAllInBoxMutable(pos.add(-2, 0, -2), pos.add(2, 0, 2))) {
            if (worldIn.getBlockState(pos1).getBlock() instanceof IGrowable && rand.nextBoolean() && rand.nextBoolean() && rand.nextBoolean()) {
                worldIn.spawnParticle(EnumParticleTypes.WATER_WAKE, pos1.getX() + rand.nextDouble(), pos1.getY(), pos1.getZ() + rand.nextDouble(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return false;
    }
}

package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.misc.BlackHoleControllerTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.text.DecimalFormat;
import java.util.List;

public class BlackHoleControllerBlock extends CustomOrientedBlock<BlackHoleControllerTile> {

    public BlackHoleControllerBlock() {
        super("black_hole_controller", BlackHoleControllerTile.class, Material.ROCK, 0, 0);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pdp", "ece", "pmp",
                'p', ItemRegistry.plastic,
                'd', Blocks.DIAMOND_BLOCK,
                'e', Items.ENDER_EYE,
                'c', Blocks.ENDER_CHEST,
                'm', MachineCaseItem.INSTANCE);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity t = worldIn.getTileEntity(pos);
        if (t instanceof BlackHoleControllerTile) {
            ((BlackHoleControllerTile) t).dropItems();
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.STORAGE;
    }

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.add(0, new PageText("It can hold 9 " + PageText.bold("Black Hole Units") + " and access it's items. That means 9 times " + PageText.bold(new DecimalFormat().format(Integer.MAX_VALUE)) + " of items."));
        return pages;
    }
}

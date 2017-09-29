package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageItemList;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.api.recipe.LaserDrillEntry;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.world.LaserBaseTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LaserBaseBlock extends CustomOrientedBlock<LaserBaseTile> {

    private int workNeeded;
    private int lenseChanceIncrease;

    public LaserBaseBlock() {
        super("laser_base", LaserBaseTile.class, Material.ROCK, 100000, 100);
    }


    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        workNeeded = CustomConfiguration.config.getInt("workNeeded", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 100, 1, Integer.MAX_VALUE, "Amount of work needed to produce an ore");
        lenseChanceIncrease = CustomConfiguration.config.getInt("lenseChanceIncrease", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 5, 1, Integer.MAX_VALUE, "How much weight each lense increases to the ore");
    }

    public int getWorkNeeded() {
        return workNeeded;
    }

    public int getLenseChanceIncrease() {
        return lenseChanceIncrease;
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pwp", "gwg", "dmd",
                'p', ItemRegistry.plastic,
                'w', Blocks.GLOWSTONE,
                'g', "gearGold",
                'd', "gearDiamond",
                'm', MachineCaseItem.INSTANCE);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity entity = worldIn.getTileEntity(pos);
        if (entity != null && entity instanceof LaserBaseTile) {
            ((LaserBaseTile) entity).onBlockBroken();
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.RESOURCE_PRODUCTION;
    }

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = new ArrayList<>();
        pages.add(new PageText("When fully charged with " + PageText.bold("Laser Drills") + " it will produce a random ore.\n\nYou can increase the chance of producing an ore by adding " + PageText.bold("Laser Lens") + " to it.\n\nYou can decrease the chance of an ore by using an " + PageText.bold("Laser Lens (Inverted)") + ", that will increase the chance of the other ores a bit.\n\n" + PageText.bold("Check JEI for chances.")));
        pages.addAll(PageItemList.generatePagesFromItemStacks(LaserDrillEntry.LASER_DRILL_ENTRIES.stream().map(LaserDrillEntry::getStack).collect(Collectors.toList()), "Produced ores:"));
        pages.addAll(super.getBookDescriptionPages());
        return pages;
    }

}




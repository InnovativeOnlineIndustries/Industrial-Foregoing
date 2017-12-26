package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.mob.MobDuplicatorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.Arrays;
import java.util.List;

public class MobDuplicatorBlock extends CustomAreaOrientedBlock<MobDuplicatorTile> {

    public int essenceNeeded;
    public List<String> blacklistedEntities;
    public boolean enableExactCopy;

    public MobDuplicatorBlock() {
        super("mob_duplicator", MobDuplicatorTile.class, Material.ROCK, 5000, 80, RangeType.UP, 4, 2, false);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pwp", "cmc", "ere",
                'p', ItemRegistry.plastic,
                'w', Items.NETHER_WART,
                'c', Items.MAGMA_CREAM,
                'm', MachineCaseItem.INSTANCE,
                'e', "gemEmerald",
                'r', Items.REDSTONE);
    }

    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        essenceNeeded = CustomConfiguration.config.getInt("essenceNeeded", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 12, 1, Integer.MAX_VALUE, "Essence needed based on mob's health (mobHealth*essenceNeeded)");
        blacklistedEntities = Arrays.asList(CustomConfiguration.config.getStringList("blacklistedEntities", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), new String[]{}, "A list of blacklisted entities like minecraft:creeper"));
        enableExactCopy = CustomConfiguration.config.getBoolean("enableExactCopy", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), false, "Set to true to enable exact copy in the Mob Duplicator.");
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.MOB;
    }

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.add(0, new PageText("When provided with power, " + PageText.bold("Essence") + " and any entity in a " + PageText.bold("Mob Imprisonment Tool") + " it will spawn them around it.\n\nIt will " + PageText.bold("count") + " the entities nearby and stop spawning if there are too many."));
        return pages;
    }
}

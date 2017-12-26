package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.mob.MobRelocatorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;

public class MobRelocatorBlock extends CustomAreaOrientedBlock<MobRelocatorTile> {

    private float essenceMultiplier;

    public MobRelocatorBlock() {
        super("mob_relocator", MobRelocatorTile.class, Material.ROCK, 1000, 40, RangeType.FRONT, 3, 2, false);
        this.setResistance(Float.MAX_VALUE);
    }

    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        essenceMultiplier = CustomConfiguration.config.getFloat("essenceMultiplier", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 1, 0, Integer.MAX_VALUE, "Mob essence multiplier based on the XP orb. Essence mb = xpOrbValue*20*essenceMultiplier");
    }

    public float getEssenceMultiplier() {
        return essenceMultiplier;
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "psp", "bmb", "grg",
                'p', ItemRegistry.plastic,
                's', Items.IRON_SWORD,
                'b', Items.BOOK,
                'm', MachineCaseItem.INSTANCE,
                'g', "gearGold",
                'r', Items.REDSTONE);
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.MOB;
    }

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.add(0, new PageText("When provided with power it will " + PageText.bold("kill") + " any entity in front of it as if a player would.\n\nIt will " + PageText.bold("collect") + " the dropped items and it will transform the experience orbs into " + PageText.bold("Essence") + "."));
        return pages;
    }

    @Override
    public List<String> getTooltip(ItemStack stack) {
        List<String> t = super.getTooltip(stack);
        t.add("* Accepts Adult Filters");
        return t;
    }
}

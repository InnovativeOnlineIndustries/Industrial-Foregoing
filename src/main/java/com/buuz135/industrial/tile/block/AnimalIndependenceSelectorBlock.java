package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.agriculture.AnimalIndependenceSelectorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;

public class AnimalIndependenceSelectorBlock extends CustomAreaOrientedBlock<AnimalIndependenceSelectorTile> {

    public AnimalIndependenceSelectorBlock() {
        super("animal_independence_selector", AnimalIndependenceSelectorTile.class, Material.ROCK, 20 * 20, 20, RangeType.FRONT, 3, 1, false);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pep", "eme", "dgd",
                'p', ItemRegistry.plastic,
                'e', "gemEmerald",
                'm', MachineCaseItem.INSTANCE,
                'd', "dyePurple",
                'g', "gearGold");
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.ANIMAL_HUSBANDRY;
    }

    @Override
    public List<String> getTooltip(ItemStack stack) {
        List<String> t = super.getTooltip(stack);
        t.add(new TextComponentTranslation("text.industrialforegoing.tooltip.adult_filter").getFormattedText());
        return t;
    }
}

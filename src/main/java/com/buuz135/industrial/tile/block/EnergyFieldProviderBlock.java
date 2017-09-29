package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.world.EnergyFieldProviderTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;

public class EnergyFieldProviderBlock extends CustomOrientedBlock<EnergyFieldProviderTile> {

    public EnergyFieldProviderBlock() {
        super("energy_field_provider", EnergyFieldProviderTile.class, Material.ROCK, 1, 1);
    }

    @Override
    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "gug", "gtg", "rdr",
                'g', "ingotGold",
                'u', ItemRegistry.energyFieldAddon,
                't', MachineCaseItem.INSTANCE,
                'r', Items.REPEATER,
                'd', "gearDiamond");
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.MAGIC;
    }

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.add(0, new PageText("The " + PageText.bold("Energy Field Addon") + " is an item that will go into the addon slots of any working machine. It will give " + PageText.bold("power") + " to the machines from it's internal buffer.\n\nThe " + PageText.bold("Energy Field Provider") + " will charge the internal buffer of the addons that are " + PageText.bold("linked") + " to it and are in its " + PageText.bold("range") + "."));
        return pages;
    }
}

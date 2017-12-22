package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.registry.IFRegistries;
import com.buuz135.industrial.tile.agriculture.CropRecolectorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;

public class CropRecolectorBlock extends CustomOrientedBlock<CropRecolectorTile> {

    private int sludgeOperation;
    private int treeOperations;
    private boolean reducedChunkUpdates;
    private int chorusOperations;

    public CropRecolectorBlock() {
        super("crop_recolector", CropRecolectorTile.class, Material.ROCK, 400, 40);
    }

    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        sludgeOperation = CustomConfiguration.config.getInt("sludgeOperation", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 20, 1, 8000, "How much sludge is produced when the machine does an operation");
        treeOperations = CustomConfiguration.config.getInt("treeOperations", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 10, 1, 1024, "Amount of operations done when chopping a tree");
        reducedChunkUpdates = CustomConfiguration.config.getBoolean("reducedChunkUpdates", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), false, "When enabled it will chop down the tree in one go but still consuming the same power");
        chorusOperations = CustomConfiguration.config.getInt("chorusOperations", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 256, 1, 1024, "Amount of operations done when chopping a chorus ammount. Low values could make it to drop items in the ground");

    }

    public int getSludgeOperation() {
        return sludgeOperation;
    }


    public int getTreeOperations() {
        return treeOperations;
    }

    public boolean isReducedChunkUpdates() {
        return reducedChunkUpdates;
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "php", "ama", "grg",
                'p', ItemRegistry.plastic,
                'h', Items.IRON_HOE,
                'a', Items.IRON_AXE,
                'm', MachineCaseItem.INSTANCE,
                'g', "gearGold",
                'r', Items.REDSTONE);
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.AGRICULTURE;
    }

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.add(0, new PageText("When provided with power it will " + PageText.bold("harvest") + " fully grown crops and chop down trees, this includes pumpkins, melons and nether wart. Each operation will produce a bit of " + PageText.bold("sludge") + " that can be used in the " + PageText.bold("Sludge Refiner") + " to obtain some material. (Having the tank full will " + PageText.bold("not") + " slow the machine)"));
        return pages;
    }

    @Override
    public List<String> getTooltip(ItemStack stack) {
        List<String> tooltip = super.getTooltip(stack);
        tooltip.add("* Accepts Leaf Shearing Addon");
        tooltip.add("* Can harvest: ");
        IFRegistries.PLANT_RECOLLECTABLES_REGISTRY.getValues().forEach(plantRecollectable -> tooltip.addAll(plantRecollectable.getRecollectablesNames()));
        return tooltip;
    }

    public int getChorusOperations() {
        return chorusOperations;
    }
}

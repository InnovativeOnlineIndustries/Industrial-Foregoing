/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.plant.PlantRecollectable;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.registry.IFRegistries;
import com.buuz135.industrial.tile.agriculture.CropRecolectorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.config.Configuration;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;

public class CropRecolectorBlock extends CustomAreaOrientedBlock<CropRecolectorTile> {

    private int sludgeOperation;
    private int treeOperations;
    private boolean reducedChunkUpdates;
    private int maxDistanceTreeBlocksScan;

    public CropRecolectorBlock() {
        super("crop_recolector", CropRecolectorTile.class, Material.ROCK, 400, 40, RangeType.FRONT, 11, 0, true);
    }

    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        sludgeOperation = CustomConfiguration.config.getInt("sludgeOperation", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getPath().toString(), 20, 1, 8000, "How much sludge is produced when the machine does an operation");
        treeOperations = CustomConfiguration.config.getInt("treeOperations", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getPath().toString(), 10, 1, 1024, "Amount of operations done when chopping a tree");
        reducedChunkUpdates = CustomConfiguration.config.getBoolean("reducedChunkUpdates", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getPath().toString(), false, "When enabled it will chop down the tree in one go but still consuming the same power");
        maxDistanceTreeBlocksScan = CustomConfiguration.config.getInt("maxDistanceTreeBlocksScan", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getPath().toString(), 100, 0, Integer.MAX_VALUE, "How far the trees will me scanned to be chopped from the ground. WARNING: Increasing this number with big trees may cause some lag when scanning for a tree.");
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

    public int getMaxDistanceTreeBlocksScan() {
        return maxDistanceTreeBlocksScan;
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
    public List<String> getTooltip(ItemStack stack) {
        List<String> tooltip = super.getTooltip(stack);
        tooltip.add(new TextComponentTranslation("text.industrialforegoing.leaf_shearing_addon").getFormattedText());
        tooltip.add(new TextComponentTranslation("text.industrialforegoing.can_harvest").getFormattedText());
        for (PlantRecollectable recollectable : IFRegistries.PLANT_RECOLLECTABLES_REGISTRY.getValues()) {
            for (String string : recollectable.getRecollectablesNames()) {
                tooltip.add(new TextComponentTranslation(string).getFormattedText());
            }
        }
        return tooltip;
    }

}

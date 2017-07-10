package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.agriculture.SludgeRefinerTile;
import com.buuz135.industrial.utils.ItemStackWeightedItem;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.ArrayList;
import java.util.List;

public class SludgeRefinerBlock extends CustomOrientedBlock<SludgeRefinerTile> {
    private List<ItemStackWeightedItem> itemStackWeightedItems;

    public SludgeRefinerBlock() {
        super("sludge_refiner", SludgeRefinerTile.class, Material.ROCK, 200, 10);
        itemStackWeightedItems = new ArrayList<>();
    }

    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        String[] items = CustomConfiguration.config.getStringList("sludgeDrops", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), new String[]{"minecraft:clay_ball 0 4", "minecraft:clay 0 1", "minecraft:dirt 0 4", "minecraft:gravel 0 4", "minecraft:mycelium 0 4", "minecraft:dirt 2 1", "minecraft:sand 1 4", "minecraft:sand 0 4", "minecraft:soul_sand 0 4"}, "List of posible sludge drops changes. Format: 'id metadata weight'");
        for (String string : items) {
            String itemName = string.split(" ")[0];
            int meta = Integer.parseInt(string.split(" ")[1]);
            int weight = Integer.parseInt(string.split(" ")[2]);
            if (Item.getByNameOrId(itemName) != null) {
                ItemStack stack = new ItemStack(Item.getByNameOrId(itemName), 1, meta);
                itemStackWeightedItems.add(new ItemStackWeightedItem(stack, weight));
            }
        }
    }

    public List<ItemStackWeightedItem> getItemStackWeightedItems() {
        return itemStackWeightedItems;
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pbp", "fmf", "igi",
                'p', ItemRegistry.plastic,
                'b', Items.BUCKET,
                'f', Blocks.FURNACE,
                'm', MachineCaseItem.INSTANCE,
                'i', "gearIron",
                'g', "gearGold");
    }
}

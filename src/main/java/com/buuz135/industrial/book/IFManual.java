package com.buuz135.industrial.book;


import com.buuz135.industrial.api.book.CategoryEntry;
import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.block.CustomOrientedBlock;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;

public class IFManual {


    public static void buildManual() {
        BookCategory.GETTING_STARTED.getEntries().put(new ResourceLocation(Reference.MOD_ID, "introduction"), new CategoryEntry("Introduction", new ItemStack(ItemRegistry.plastic),
                Arrays.asList(new PageText("Welcome to Industrial Foregoing's Manual!\n\nTo get started you need to place a Tree Fluid Extractor in front of a log to collect Latex and pump into Latex Processing Unit with some water to get Tiny Rubber. \n\n" + TextFormatting.RED + "NOTE: Machines don't auto eject, neither they pull! Machines accept RF, FE, Tesla and Mek power."))));
        CustomOrientedBlock.blockList.forEach(customOrientedBlock -> customOrientedBlock.getCategory().getEntries().put(customOrientedBlock.getRegistryName(), new CategoryEntry(customOrientedBlock.getLocalizedName(), new ItemStack(customOrientedBlock), customOrientedBlock.getBookDescriptionPages())));
        BookCategory.MOB.getEntries().put(ItemRegistry.mobImprisonmentToolItem.getRegistryName(), new CategoryEntry(ItemRegistry.mobImprisonmentToolItem.getItemStackDisplayName(new ItemStack(ItemRegistry.mobImprisonmentToolItem)), new ItemStack(ItemRegistry.mobImprisonmentToolItem),
                Arrays.asList(new PageText("When right clicked an entity with the item in the hand it will " + PageText.bold("hold") + " the entity inside it."))));

        addItemEntry(ItemRegistry.meatFeederItem, new PageText("When the Meat Feeder is in your " + PageText.bold("inventory") + " and has some " + PageText.bold("Liquid Meat") + " in it, will automatically " + PageText.bold("feed") + " you.\n\nIt acts like a normal fluid container."));
        addItemEntry(ItemRegistry.rangeAddonItem, new PageText("It will increase the working " + PageText.bold("range") + " of some machines. Not all of the machines will accept them."));
        addItemEntry(ItemRegistry.strawItem, new PageText("It allows you to " + PageText.bold("drink") + " in world fluids or in tanks. It can cause different " + PageText.bold("effects") + " depending on the fluid.\n\n\n\n\n\n\n\n(I'm not responsible of the bad effects that the straw can cause)"));
        addItemEntry(ItemRegistry.pinkSlime, new PageText("Can be obtained by killing a " + PageText.bold("Pink Slime") + " that will spawned when placing some " + PageText.bold("Pink Slime Fluid") + " in the world."));
        BookCategory.ITEM.getEntries().put(new ResourceLocation(Reference.MOD_ID, "upgrades"), new CategoryEntry("Upgrades", new ItemStack(ItemRegistry.dryRubber), Arrays.asList(new PageText("Industrial Foregoing doesn't have speed upgrades but you can used " + PageText.bold("Energy Upgrades") + " and " + PageText.bold("Speed Upgrades") + " from Tesla Core Lib to speed up machines. They need the previous tier to be installed to be able to add the next one."))));

    }

    public static void addItemEntry(Item item, IPage... pages) {
        BookCategory.ITEM.getEntries().put(item.getRegistryName(), new CategoryEntry(item.getItemStackDisplayName(new ItemStack(item)), new ItemStack(item), Arrays.asList(pages)));
    }

}

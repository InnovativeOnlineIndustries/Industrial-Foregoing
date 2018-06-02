package com.buuz135.industrial.book;


import com.buuz135.industrial.api.book.CategoryEntry;
import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.api.conveyor.ConveyorUpgradeFactory;
import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.registry.IFRegistries;
import com.buuz135.industrial.tile.block.CustomOrientedBlock;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.List;

public class IFManual {

    public static void buildManual() {
        for (BookCategory bookCategory : BookCategory.values()) {
            bookCategory.getEntries().clear();
        }
        BookCategory.GETTING_STARTED.getEntries().put(new ResourceLocation(Reference.MOD_ID, "introduction"), new CategoryEntry("Introduction", new ItemStack(ItemRegistry.plastic), PageText.createTranslatedPages("text.industrialforegoing.book.welcome_manual")));
        CustomOrientedBlock.blockList.stream().filter(CustomOrientedBlock::isEnabled).forEach(customOrientedBlock -> customOrientedBlock.getCategory().getEntries().put(customOrientedBlock.getRegistryName(), new CategoryEntry(customOrientedBlock.getLocalizedName(), new ItemStack(customOrientedBlock), customOrientedBlock.getBookDescriptionPages())));
        BookCategory.MOB.getEntries().put(ItemRegistry.mobImprisonmentToolItem.getRegistryName(), new CategoryEntry(ItemRegistry.mobImprisonmentToolItem.getItemStackDisplayName(new ItemStack(ItemRegistry.mobImprisonmentToolItem)), new ItemStack(ItemRegistry.mobImprisonmentToolItem),
                PageText.createTranslatedPages("text.industrialforegoing.book.mob_imprisonment_tool")));

        addItemEntry(ItemRegistry.meatFeederItem, PageText.createTranslatedPages("text.industrialforegoing.book.meat_feeder"));
        addItemEntry(ItemRegistry.rangeAddonItem, PageText.createTranslatedPages("text.industrialforegoing.book.range_addon"));
        addItemEntry(ItemRegistry.strawItem, PageText.createTranslatedPages("text.industrialforegoing.book.straw"));
        addItemEntry(ItemRegistry.pinkSlime, PageText.createTranslatedPages("text.industrialforegoing.book.pink_slime"));
        BookCategory.ITEM.getEntries().put(new ResourceLocation(Reference.MOD_ID, "transfer_addons"), new CategoryEntry(new TextComponentTranslation("item.industrialforegoing.itemstack_transfer_addon.name").getUnformattedComponentText(), new ItemStack(ItemRegistry.itemStackTransferAddonPull), PageText.createTranslatedPages("text.industrialforegoing.book.transfer_addon")));
        BookCategory.ITEM.getEntries().put(new ResourceLocation(Reference.MOD_ID, "upgrades"), new CategoryEntry("Upgrades", new ItemStack(ItemRegistry.dryRubber), PageText.createTranslatedPages("text.industrialforegoing.book.upgrades")));

        BookCategory.CONVEYORS.getEntries().put(new ResourceLocation(Reference.MOD_ID, "conveyors"), new CategoryEntry(new TextComponentTranslation("tile.industrialforegoing.conveyor.name").getFormattedText(), new ItemStack(BlockRegistry.blockConveyor), PageText.createTranslatedPages("text.industrialforegoing.book.conveyors")));
        int i = 0;
        for (ConveyorUpgradeFactory conveyorUpgradeFactory : IFRegistries.CONVEYOR_UPGRADE_REGISTRY.getValuesCollection()) {
            addEntry(BookCategory.CONVEYORS, new ItemStack(ItemRegistry.conveyorUpgradeItem, 1, i), "text.industrialforegoing.book.conveyor_upgrade_" + conveyorUpgradeFactory.getRegistryName().getResourcePath());
            ++i;
        }
    }

    public static void addItemEntry(Item item, List<IPage> pages) {
        BookCategory.ITEM.getEntries().put(item.getRegistryName(), new CategoryEntry(item.getItemStackDisplayName(new ItemStack(item)), new ItemStack(item), pages));
    }

    public static void addEntry(BookCategory category, ItemStack stack, String localization) {
        ResourceLocation resourceLocation = new ResourceLocation(stack.getItem().getRegistryName().toString() + stack.getMetadata());
        category.getEntries().put(resourceLocation, new CategoryEntry(stack.getDisplayName(), stack, PageText.createTranslatedPages(localization)));
    }

}

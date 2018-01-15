package com.buuz135.industrial.api.book.gui;

import com.buuz135.industrial.api.book.CategoryEntry;
import com.buuz135.industrial.api.book.button.ItemStackButton;
import com.buuz135.industrial.api.book.button.TextureButton;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class GUIBookMain extends GUIBookBase {

    private HashMap<ItemStackButton, BookCategory> categoriesButtons = new LinkedHashMap<>();
    private GuiButton searchButton;
    private Block block;

    public GUIBookMain(World world, int x, int y, int z) {
        super(null, null);
        BlockPos pos = new BlockPos(x, y, z);
        if (world.getBlockState(pos).getBlock().getRegistryName().getResourceDomain().equals(Reference.MOD_ID)) {
            block = world.getBlockState(pos).getBlock();
        }
    }

    public static ItemStack getCategoryItemStack(BookCategory category) {
        if (category.getEntries().isEmpty()) return new ItemStack(Blocks.BARRIER);
        if (!category.getDisplay().isEmpty()) category.getDisplay();
        for (CategoryEntry entry : category.getEntries().values()) {
            if (!entry.getDisplay().isEmpty()) return entry.getDisplay();
        }
        return new ItemStack(Blocks.BARRIER);
    }

    @Override
    public boolean hasPageLeft() {
        return false;
    }

    @Override
    public boolean hasPageRight() {
        return false;
    }

    @Override
    public boolean hasBackButton() {
        return false;
    }

    @Override
    public boolean hasSearchBar() {
        return false;
    }

    @Override
    public void drawScreenFront(int mouseX, int mouseY, float partialTicks) {
        super.drawScreenFront(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        super.initGui();
        if (block != null) {
            for (BookCategory bookCategory : BookCategory.values()) {
                for (ResourceLocation entry : bookCategory.getEntries().keySet()) {
                    CategoryEntry categoryEntry = bookCategory.getEntries().get(entry);
                    if (categoryEntry.getDisplay().isItemEqual(new ItemStack(block))) {
                        this.mc.displayGuiScreen(new GUIBookPage(this, this, categoryEntry));
                        return;
                    }
                }
            }
        } else {
            int i = 0;
            int renderScale = (this.getGuiXSize() - 40) / 3;
            for (BookCategory category : BookCategory.values()) {
                ItemStackButton button = new ItemStackButton(-235 - i, 21 + this.getGuiLeft() + ((i % 3) * renderScale), 15 + this.getGuiTop() + ((i / 3) * (renderScale + 4)), renderScale, renderScale, category.getName(), getCategoryItemStack(category));
                this.buttonList.add(button);
                categoriesButtons.put(button, category);
                ++i;
            }
            searchButton = new TextureButton(-135, this.getGuiLeft() - 5, this.getGuiTop() + 2, 18, 10, BOOK_EXTRAS, 1, 27, "Search");
            this.buttonList.add(searchButton);
        }
    }

    @Override
    public void onBackButton() {
        this.mc.displayGuiScreen(new GUIBookBase(this, this) {
            @Override
            public boolean hasSearchBar() {
                return true;
            }

            @Override
            public boolean hasPageRight() {
                return true;
            }

            @Override
            public void onRightButton() {
                onBackButton();
            }
        });
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (categoriesButtons.containsKey(button)) {
            if (categoriesButtons.get(button).getEntries().isEmpty()) return;
            this.mc.displayGuiScreen(new GUIBookCantegoryEntries(this, this, categoriesButtons.get(button)));
        } else if (button == searchButton) {
            onBackButton();
        } else {
            super.actionPerformed(button);
        }

    }

}

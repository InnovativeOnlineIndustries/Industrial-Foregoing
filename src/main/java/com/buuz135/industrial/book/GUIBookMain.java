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
package com.buuz135.industrial.book;

import com.buuz135.industrial.api.IndustrialForegoingHelper;
import com.buuz135.industrial.api.book.CategoryEntry;
import com.buuz135.industrial.api.book.IBookCategory;
import com.buuz135.industrial.api.book.button.ItemStackButton;
import com.buuz135.industrial.api.book.button.TextureButton;
import com.buuz135.industrial.api.book.gui.GUIBookBase;
import com.buuz135.industrial.api.book.gui.GUIBookCategoryEntries;
import com.buuz135.industrial.api.book.gui.GUIBookPage;
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

    private HashMap<ItemStackButton, IBookCategory> categoriesButtons = new LinkedHashMap<>();
    private GuiButton searchButton;
    private Block block;

    public GUIBookMain(World world, int x, int y, int z) {
        super(null, null);
        BlockPos pos = new BlockPos(x, y, z);
        if (world.getBlockState(pos).getBlock().getRegistryName().getNamespace().equals(IndustrialForegoingHelper.MOD_ID)) {
            block = world.getBlockState(pos).getBlock();
        }
    }

    public static ItemStack getCategoryItemStack(IBookCategory category) {
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
            for (IBookCategory bookCategory : BookCategory.values()) {
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
                ItemStackButton button = new ItemStackButton(-235 - i, 21 + this.getGuiLeft() + ((i % 3) * renderScale), 13 + this.getGuiTop() + ((i / 3) * (renderScale + 4)), renderScale, renderScale, category.getName(), getCategoryItemStack(category));
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
            this.mc.displayGuiScreen(new GUIBookCategoryEntries(this, this, categoriesButtons.get(button)));
        } else if (button == searchButton) {
            onBackButton();
        } else {
            super.actionPerformed(button);
        }

    }

}

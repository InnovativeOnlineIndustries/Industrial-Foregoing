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

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageRecipe;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.IHasBookDescription;
import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.ndrei.teslacorelib.blocks.OrientedBlock;
import net.ndrei.teslacorelib.tileentities.SidedTileEntity;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomOrientedBlock<T extends SidedTileEntity> extends OrientedBlock implements IHasBookDescription, IHasAdvancedTooltip {

    public static List<CustomOrientedBlock> blockList = new ArrayList<>();

    private boolean workDisabled;
    private boolean enabled;
    private int energyForWork;
    private int energyRate;
    private long energyBuffer;

    protected CustomOrientedBlock(String registryName, Class<T> teClass) {
        super(Reference.MOD_ID, IndustrialForegoing.creativeTab, registryName, teClass);
        blockList.add(this);
    }

    protected CustomOrientedBlock(String registryName, Class teClass, Material material, int energyForWork, int energyRate) {
        super(Reference.MOD_ID, IndustrialForegoing.creativeTab, registryName, teClass, material);
        this.energyForWork = energyForWork;
        this.energyRate = energyRate;
        blockList.add(this);
    }

    public void getMachineConfig() {
        enabled = CustomConfiguration.config.getBoolean("enabled", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getPath().toString(), true, "If disabled it will be removed from the game.");
        workDisabled = CustomConfiguration.config.getBoolean("workDisabled", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getPath().toString(), false, "Machine can perform a work action");
        if (energyForWork != 0 && energyRate != 0) {
            energyForWork = CustomConfiguration.config.getInt("energyForWork", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getPath().toString(), energyForWork, 1, Integer.MAX_VALUE, "How much energy needs a machine to work");
            energyRate = CustomConfiguration.config.getInt("energyRate", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getPath().toString(), energyRate, 1, Integer.MAX_VALUE, "Energy input rate of a machine");
            energyBuffer = CustomConfiguration.config.getInt("energyBuffer", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getPath().toString(), 50000, 1, Integer.MAX_VALUE, "Energy buffer of a machine");
        }
    }

    public boolean isWorkDisabled() {
        return workDisabled;
    }

    public int getEnergyForWork() {
        return energyForWork;
    }

    public int getEnergyRate() {
        return energyRate;
    }

    public long getEnergyBuffer() {
        return energyBuffer;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public abstract void createRecipe();

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = new ArrayList<>();
        pages.addAll(PageText.createTranslatedPages("text.industrialforegoing.book." + getRegistryName().getPath()));
        if (ForgeRegistries.RECIPES.getValue(this.getRegistryName()) != null)
            pages.add(new PageRecipe(this.getRegistryName()));
        return pages;
    }

    @Override
    public List<String> getTooltip(ItemStack stack) {
        List<String> tooltips = new ArrayList<>();
        return tooltips;
    }
}

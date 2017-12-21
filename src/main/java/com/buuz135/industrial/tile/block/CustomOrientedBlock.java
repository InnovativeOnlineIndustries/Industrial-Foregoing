package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageRecipe;
import com.buuz135.industrial.book.IHasBookDescription;
import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.ndrei.teslacorelib.blocks.OrientedBlock;
import net.ndrei.teslacorelib.tileentities.SidedTileEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CustomOrientedBlock<T extends SidedTileEntity> extends OrientedBlock implements IHasBookDescription, IHasAdvancedTooltip {

    public static List<CustomOrientedBlock> blockList = new ArrayList<>();
    private static List<Class> rangeAcceptingTiles = Arrays.asList(AnimalByproductRecolectorBlock.class, CropEnrichMaterialInjectorBlock.class, CropRecolectorBlock.class, CropSowerBlock.class, EnergyFieldProviderBlock.class, FluidPumpBlock.class);

    private boolean workDisabled;
    private boolean enabled;
    private int energyForWork;
    private int energyRate;

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
        enabled = CustomConfiguration.config.getBoolean("enabled", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), true, "If disabled it will be removed from the game.");
        workDisabled = CustomConfiguration.config.getBoolean("workDisabled", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), false, "Machine can perform a work action");
        if (energyForWork != 0 && energyRate != 0) {
            energyForWork = CustomConfiguration.config.getInt("energyForWork", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), energyForWork, 1, Integer.MAX_VALUE, "How much energy needs a machine to work");
            energyRate = CustomConfiguration.config.getInt("energyRate", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), energyRate, 1, Integer.MAX_VALUE, "Energy input rate of a machine");
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

    public boolean isEnabled() {
        return enabled;
    }

    public abstract void createRecipe();

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = new ArrayList<>();
        if (ForgeRegistries.RECIPES.getValue(this.getRegistryName()) != null)
            pages.add(new PageRecipe(this.getRegistryName()));
        return pages;
    }

    @Override
    public List<String> getTooltip(ItemStack stack) {
        List<String> tooltips = new ArrayList<>();
        if (rangeAcceptingTiles.contains(this.getClass())) tooltips.add(TextFormatting.GRAY + "* Accepts range addons");
        return tooltips;
    }
}

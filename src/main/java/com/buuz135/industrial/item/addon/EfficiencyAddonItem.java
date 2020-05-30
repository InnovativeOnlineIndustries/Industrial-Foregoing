package com.buuz135.industrial.item.addon;

import com.buuz135.industrial.block.tile.IndustrialWorkingTile;
import com.buuz135.industrial.item.IFCustomItem;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IMachine;
import com.hrznstudio.titanium.api.augment.IAugment;
import com.hrznstudio.titanium.api.augment.IAugmentType;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

public class EfficiencyAddonItem extends IFCustomItem implements IAugment {

    public static final IAugmentType EFFICIENCY = () -> "efficiency";

    private int tier;

    public EfficiencyAddonItem(int tier, ItemGroup group) {
        super("efficiency_addon_" + tier, group, new Properties().maxStackSize(1));
        this.tier = tier;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        Tag<Item> tierMaterial = tier == 1 ? IndustrialTags.Items.GEAR_GOLD : IndustrialTags.Items.GEAR_DIAMOND;
        new DissolutionChamberRecipe(getRegistryName(), new Ingredient.IItemList[]{
                new Ingredient.SingleItemList(new ItemStack(Items.REDSTONE)),
                new Ingredient.SingleItemList(new ItemStack(Items.REDSTONE)),
                new Ingredient.SingleItemList(new ItemStack(Items.GLASS_PANE)),
                new Ingredient.SingleItemList(new ItemStack(Items.GLASS_PANE)),
                new Ingredient.TagList(tierMaterial),
                new Ingredient.TagList(tierMaterial),
                new Ingredient.SingleItemList(new ItemStack(Items.BLAZE_ROD)),
                new Ingredient.SingleItemList(new ItemStack(Items.BLAZE_ROD))
        }, new FluidStack(ModuleCore.LATEX.getSourceFluid(), 1000), 200, new ItemStack(this), FluidStack.EMPTY);
    }

    @Override
    public IAugmentType getAugmentType() {
        return EFFICIENCY;
    }

    @Override
    public float getAugmentRatio() {
        return tier;
    }

    @Override
    public boolean canWorkIn(IMachine machine) {
        return !machine.hasAugmentInstalled(EFFICIENCY) && machine instanceof IndustrialWorkingTile;
    }

    @Override
    public String getTranslationKey() {
        return new TranslationTextComponent("item.industrialforegoing.addon").getFormattedText() + new TranslationTextComponent("item.industrialforegoing.efficiency").getFormattedText() + "Tier " + tier + " ";
    }
}

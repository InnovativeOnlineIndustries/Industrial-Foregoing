package com.buuz135.industrial.item;

import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

public class LaserLensItem extends IFCustomItem {

    private int color;

    public LaserLensItem(int color) {
        super("laser_lens" + color, ModuleCore.TAB_CORE, new Properties().maxStackSize(1));
        this.color = color;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        new DissolutionChamberRecipe(this.getRegistryName(),
                new Ingredient.IItemList[]{
                        new Ingredient.TagList(Tags.Items.GLASS_PANES),
                        new Ingredient.TagList(Tags.Items.GLASS_PANES),
                        new Ingredient.TagList(Tags.Items.GLASS_PANES),
                        new Ingredient.TagList(Tags.Items.GLASS_PANES),
                        new Ingredient.TagList(DyeColor.byId(color).getTag()),
                },
                new FluidStack(ModuleCore.LATEX.getSourceFluid(), 250), 100, new ItemStack(this), FluidStack.EMPTY);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return new StringTextComponent(new TranslationTextComponent("color.minecraft." + DyeColor.byId(color).getTranslationKey()).getString() +
                " " + new TranslationTextComponent("item.industrialforegoing.laser_lens").getString());
    }
}

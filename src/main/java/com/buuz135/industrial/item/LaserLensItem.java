package com.buuz135.industrial.item;

import com.buuz135.industrial.module.ModuleCore;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.function.Consumer;

public class LaserLensItem extends IFCustomItem {

    private static String[] dyes = {"Black", "Red", "Green", "Brown", "Blue", "Purple", "Cyan", "LightGray", "Gray", "Pink", "Lime", "Yellow", "LightBlue", "Magenta", "Orange", "White"};

    private int color;
    private boolean inverted;

    public LaserLensItem(int color, boolean inverted) {
        super("laser_lens" + (inverted ? "_inverted" : "") + color, ModuleCore.TAB_CORE, new Properties().maxStackSize(1));
        this.color = color;
        this.inverted = inverted;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {

    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return new StringTextComponent(new TranslationTextComponent("color.minecraft." + DyeColor.byId(color).getTranslationKey().replaceAll("_", "")).getString() +
                " " + new TranslationTextComponent("item.industrialforegoing.laser_lens" + (inverted ? "_inverted" : "")).getString());
    }
}

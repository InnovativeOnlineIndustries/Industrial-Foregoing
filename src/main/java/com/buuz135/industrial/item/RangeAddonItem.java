package com.buuz135.industrial.item;

import com.hrznstudio.titanium.api.IMachine;
import com.hrznstudio.titanium.api.augment.IAugment;
import com.hrznstudio.titanium.api.augment.IAugmentType;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class RangeAddonItem extends IFCustomItem implements IAugment {

    public static final IAugmentType RANGE = () -> "range";

    private int tier;

    public RangeAddonItem(int tier, ItemGroup group) {
        super("range_addon" + tier, group, new Properties().maxStackSize(1));
        this.tier = tier;
    }

    @Override
    public String getTranslationKey() {
        return "Tier " + (tier + 1) + " " + new TranslationTextComponent("item.industrialforegoing.range_addon").getFormattedText();
    }


    @Override
    public void addTooltipDetails(@Nullable Key key, ItemStack stack, List<ITextComponent> tooltip, boolean advanced) {
        tooltip.add(new StringTextComponent(TextFormatting.GRAY + new TranslationTextComponent("text.industrialforegoing.extra_range").getFormattedText() + "+" + (tier + 1)));
    }

    @Override
    public boolean hasTooltipDetails(@Nullable Key key) {
        return key == null;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {

    }

    @Override
    public IAugmentType getAugmentType() {
        return RANGE;
    }

    @Override
    public float getAugmentRatio() {
        return tier;
    }

    @Override
    public boolean canWorkIn(IMachine machine) {
        return !machine.hasAugmentInstalled(RANGE);
    }
}

package com.buuz135.industrial.item.addon;

import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.item.AugmentWrapper;
import com.hrznstudio.titanium.tab.TitaniumTab;
import net.minecraft.ChatFormatting;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class SilkTouchAddonItem extends AddonItem {

    public static final com.hrznstudio.titanium.api.augment.IAugmentType SILK_TOUCH = () -> "SilkTouch";

    public SilkTouchAddonItem(TitaniumTab tab) {
        super("silk_touch_addon", tab, new Properties().stacksTo(1));
    }

    @Override
    public void verifyComponentsAfterLoad(ItemStack stack) {
        super.verifyComponentsAfterLoad(stack);
        AugmentWrapper.setType(stack, SILK_TOUCH, 1.0f);
    }

    @Override
    public void registerRecipe(RecipeOutput consumer) {
        DissolutionChamberRecipe.createRecipe(
                consumer,
                "silk_touch_addon",
                new DissolutionChamberRecipe(
                        List.of(
                                Ingredient.of(IndustrialTags.Items.PLASTIC),
                                Ingredient.of(IndustrialTags.Items.PLASTIC),
                                Ingredient.of(Items.GLASS_PANE),
                                Ingredient.of(Items.GLASS_PANE),
                                Ingredient.of(Items.SHEARS),
                                Ingredient.of(IndustrialTags.Items.GEAR_GOLD),
                                Ingredient.of(IndustrialTags.Items.PLASTIC),
                                Ingredient.of(IndustrialTags.Items.PLASTIC)
                        ),
                        new FluidStack(ModuleCore.LATEX.getSourceFluid().get(), 1000),
                        200,
                        Optional.of(new ItemStack(this)),
                        Optional.empty()
                )
        );
    }

    @Override
    public void addTooltipDetails(@Nullable Key key, ItemStack stack, List<Component> tooltip, boolean advanced) {
        tooltip.add(Component.literal(ChatFormatting.GRAY + "Allows the machine to harvest blocks with Silk Touch"));
    }

    @Override
    public boolean hasTooltipDetails(@Nullable Key key) {
        return key == null;
    }
}

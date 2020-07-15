package com.buuz135.industrial.item.addon;

import com.buuz135.industrial.item.IFCustomItem;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.augment.AugmentTypes;
import com.hrznstudio.titanium.item.AugmentWrapper;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

public class EfficiencyAddonItem extends IFCustomItem {

    private int tier;

    public EfficiencyAddonItem(int tier, ItemGroup group) {
        super("efficiency_addon_" + tier, group, new Properties().maxStackSize(1));
        this.tier = tier;
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn) {
        super.onCreated(stack, worldIn, playerIn);
        AugmentWrapper.setType(stack, AugmentTypes.EFFICIENCY, 1 - this.tier * 0.1f);
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        Tag.INamedTag<Item> tierMaterial = tier == 1 ? IndustrialTags.Items.GEAR_GOLD : IndustrialTags.Items.GEAR_DIAMOND;
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
    public String getTranslationKey() {
        return new TranslationTextComponent("item.industrialforegoing.addon").getString() + new TranslationTextComponent("item.industrialforegoing.efficiency").getString() + "Tier " + tier + " ";
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (isInGroup(group)) {
            ItemStack stack = new ItemStack(this);
            AugmentWrapper.setType(stack, AugmentTypes.EFFICIENCY, 1 - this.tier * 0.1f);
            items.add(stack);
        }
    }
}

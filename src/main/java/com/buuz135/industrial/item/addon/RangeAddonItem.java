package com.buuz135.industrial.item.addon;

import com.buuz135.industrial.item.IFCustomItem;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import com.hrznstudio.titanium.api.augment.IAugmentType;
import com.hrznstudio.titanium.item.AugmentWrapper;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class RangeAddonItem extends IFCustomItem {

    private static Item[] MATERIALS = new Item[]{Items.COBBLESTONE, Items.LAPIS_LAZULI, Items.BONE_MEAL, Items.IRON_NUGGET, Items.BLAZE_POWDER, Items.GOLD_NUGGET, Items.IRON_INGOT, Items.GOLD_INGOT, Items.QUARTZ, Items.DIAMOND, Items.POPPED_CHORUS_FRUIT, Items.EMERALD};

    public static final IAugmentType RANGE = () -> "Range";

    private int tier;

    public RangeAddonItem(int tier, ItemGroup group) {
        super("range_addon" + tier, group, new Properties().maxStackSize(1));
        this.tier = tier;
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn) {
        super.onCreated(stack, worldIn, playerIn);
        AugmentWrapper.setType(stack, RANGE, tier);
    }

    @Override
    public String getTranslationKey() {
        return new TranslationTextComponent("item.industrialforegoing.addon").getString() + new TranslationTextComponent("item.industrialforegoing.range_addon").getString() + "Tier " + (tier + 1) + " ";
    }

    @Override
    public void addTooltipDetails(@Nullable Key key, ItemStack stack, List<ITextComponent> tooltip, boolean advanced) {
        tooltip.add(new StringTextComponent(TextFormatting.GRAY + new TranslationTextComponent("text.industrialforegoing.extra_range").getString() + "+" + (tier + 1)));
    }

    @Override
    public boolean hasTooltipDetails(@Nullable Key key) {
        return key == null;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        new DissolutionChamberRecipe(getRegistryName(), new Ingredient.IItemList[]{
                new Ingredient.SingleItemList(new ItemStack(Items.REDSTONE)),
                new Ingredient.SingleItemList(new ItemStack(Items.REDSTONE)),
                new Ingredient.SingleItemList(new ItemStack(Items.GLASS_PANE)),
                new Ingredient.SingleItemList(new ItemStack(Items.GLASS_PANE)),
                new Ingredient.SingleItemList(new ItemStack(MATERIALS[tier])),
                new Ingredient.SingleItemList(new ItemStack(MATERIALS[tier])),
                new Ingredient.SingleItemList(new ItemStack(MATERIALS[tier])),
                new Ingredient.SingleItemList(new ItemStack(MATERIALS[tier]))
        }, new FluidStack(ModuleCore.LATEX.getSourceFluid(), 1000), 200, new ItemStack(this), FluidStack.EMPTY);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (isInGroup(group)) {
            ItemStack stack = new ItemStack(this);
            AugmentWrapper.setType(stack, RANGE, tier);
            items.add(stack);
        }
    }

}

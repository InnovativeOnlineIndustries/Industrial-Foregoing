package com.buuz135.industrial.item.addon;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.api.IAcceptsFortuneAddon;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.tileentities.SidedTileEntity;
import org.jetbrains.annotations.NotNull;

public class FortuneAddonItem extends CustomAddon {

    public FortuneAddonItem() {
        super("fortune_addon");
        setMaxStackSize(1);
    }

    @Override
    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "gpg", "gmg", "gpg",
                'g', Items.EMERALD,
                'p', ItemRegistry.plastic,
                'm', ItemRegistry.pinkSlimeIngot);
    }

    @Override
    public boolean canBeAddedTo(@NotNull SidedTileEntity machine) {
        return machine instanceof IAcceptsFortuneAddon;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return !stack.isItemEnchanted();
    }

    @Override
    public int getItemEnchantability() {
        return 2;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return !stack.isItemEnchanted() && Enchantment.getEnchantmentID(enchantment) == 35;
    }

    public int getLevel(ItemStack stack) {
        if (stack.isItemEnchanted()) {
            return EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(35), stack);
        }
        return 0;
    }
}

package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.agriculture.MobSlaughterFactoryTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.config.Configuration;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;

public class
MobSlaughterFactoryBlock extends CustomAreaOrientedBlock<MobSlaughterFactoryTile> {

    private float meatValue;
    private int damage;

    public MobSlaughterFactoryBlock() {
        super("mob_slaughter_factory", MobSlaughterFactoryTile.class, Material.ROCK, 1000, 40, RangeType.FRONT, 5, 2, true);
    }

    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        meatValue = CustomConfiguration.config.getFloat("meatValue", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 5, 1, Integer.MAX_VALUE, "Mob health multiplier, mobHealth * meatValue = meat mb produced");
        damage = CustomConfiguration.config.getInt("damage", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 300, 0, Integer.MAX_VALUE, "Amount of damage that the machine will do");
    }

    public float getMeatValue() {
        return meatValue;
    }

    public int getDamage() {
        return damage;
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pgp", "sms", "ara",
                'p', ItemRegistry.plastic,
                'g', "gearGold",
                's', Items.IRON_SWORD,
                'm', MachineCaseItem.INSTANCE,
                'a', Items.IRON_AXE,
                'r', Items.REDSTONE);
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.MOB;
    }

    @Override
    public List<String> getTooltip(ItemStack stack) {
        List<String> t = super.getTooltip(stack);
        t.add(new TextComponentTranslation("text.industrialforegoing.tooltip.adult_filter").getFormattedText());
        return t;
    }

}

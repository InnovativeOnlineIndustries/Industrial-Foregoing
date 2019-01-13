/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.mob.MobRelocatorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.config.Configuration;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;

public class MobRelocatorBlock extends CustomAreaOrientedBlock<MobRelocatorTile> {

    private float essenceMultiplier;
    private int damage;

    public MobRelocatorBlock() {
        super("mob_relocator", MobRelocatorTile.class, Material.ROCK, 1000, 40, RangeType.FRONT, 5, 2, true);
        this.setResistance(Float.MAX_VALUE);
    }

    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        essenceMultiplier = CustomConfiguration.config.getFloat("essenceMultiplier", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getPath().toString(), 1, 0, Integer.MAX_VALUE, "Mob essence multiplier based on the XP orb. Essence mb = xpOrbValue*20*essenceMultiplier");
        damage = CustomConfiguration.config.getInt("damage", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getPath().toString(), 300, 0, Integer.MAX_VALUE, "Amount of damage that the machine will do");
    }

    public float getEssenceMultiplier() {
        return essenceMultiplier;
    }

    public int getDamage() {
        return damage;
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "psp", "bmb", "grg",
                'p', ItemRegistry.plastic,
                's', Items.IRON_SWORD,
                'b', Items.BOOK,
                'm', MachineCaseItem.INSTANCE,
                'g', "gearGold",
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
        t.add(new TextComponentTranslation("text.industrialforegoing.tooltip.fortune_addon").getFormattedText());
        return t;
    }
}

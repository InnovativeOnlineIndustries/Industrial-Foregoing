package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.mob.MobRelocatorTile;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

public class MobRelocatorBlock extends CustomOrientedBlock<MobRelocatorTile> {

    private float essenceMultiplier;

    public MobRelocatorBlock() {
        super("mob_relocator", MobRelocatorTile.class, Material.ROCK, 1000, 40);
    }

    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        essenceMultiplier = CustomConfiguration.config.getFloat("essenceMultiplier", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 1, 0, Integer.MAX_VALUE, "Mob essence multiplier based on the mob health. Essence mb = mobHealth*essenceMultiplier");
    }

    public float getEssenceMultiplier() {
        return essenceMultiplier;
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this), "psp", "bmb", "grg",
                'p', ItemRegistry.plastic,
                's', Items.IRON_SWORD,
                'b', Items.BOOK,
                'm', TeslaCoreLib.machineCase,
                'g', "gearGold",
                'r', Items.REDSTONE);
    }
}

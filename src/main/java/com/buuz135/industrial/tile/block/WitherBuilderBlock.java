package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.mob.WitherBuilderTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.ndrei.teslacorelib.items.MachineCaseItem;

public class WitherBuilderBlock extends CustomAreaOrientedBlock<WitherBuilderTile> {

    public boolean HCWither;
    private IBehaviorDispenseItem dispenser;

    public WitherBuilderBlock() {
        super("wither_builder", WitherBuilderTile.class, Material.ROCK, 20000, 500, RangeType.UP, 1, 1, false);
        this.setResistance(Float.MAX_VALUE);
    }

    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        HCWither = CustomConfiguration.config.getBoolean("HCWither", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), false, "If enabled, only the wither builder will be able to place wither skulls. That means that players won't be able to place wither skulls. The recipe will change, but that will need a restart.");
        if (HCWither) {
            if (dispenser == null) dispenser = BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getObject(Items.SKULL);
            BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.SKULL, new BehaviorDefaultDispenseItem());
        } else if (dispenser != null) {
            BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.SKULL, dispenser);
        }
    }

    @Override
    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pnp", "sms", "ggg",
                'p', ItemRegistry.plastic,
                'n', "IFWITHER",
                's', new ItemStack(Items.SKULL, 1, 1),
                'm', MachineCaseItem.INSTANCE,
                'g', Blocks.SOUL_SAND);
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.MOB;
    }


    public boolean isHCWither() {
        return HCWither;
    }
}

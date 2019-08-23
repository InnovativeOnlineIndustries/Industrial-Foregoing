package com.buuz135.industrial.block.generator;

import com.buuz135.industrial.block.generator.tile.PitifulGeneratorTile;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.block.BlockRotation;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;

import javax.annotation.Nonnull;

public class PitifulGeneratorBlock extends BlockRotation<PitifulGeneratorTile> {

    public PitifulGeneratorBlock(ItemGroup group) {
        super("pitiful_generator", Properties.from(Blocks.COBBLESTONE), PitifulGeneratorTile.class);
        this.setItemGroup(group);
        //IndustrialForegoing.RECIPES.addRecipe(CraftingJsonData.ofShaped(new ItemStack(this, 1), new String[]{"pdp", "gmg", "pfp"},
        //        'p', IIngredient.ItemStackIngredient.of(new ItemStack(Blocks.COBBLESTONE)),
        //        'd', IIngredient.TagIngredient.of(Tags.Items.INGOTS_GOLD.getId().toString()),
        //        'g', "gearIron",
        //        'm', IIngredient.ItemStackIngredient.of(new ItemStack(ModuleCore.PITY)),
        //        'f', IIngredient.ItemStackIngredient.of(new ItemStack(Blocks.FURNACE))));
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public IFactory<PitifulGeneratorTile> getTileEntityFactory() {
        return PitifulGeneratorTile::new;
    }
}

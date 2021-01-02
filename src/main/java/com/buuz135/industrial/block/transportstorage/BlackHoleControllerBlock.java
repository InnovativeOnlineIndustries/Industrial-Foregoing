package com.buuz135.industrial.block.transportstorage;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.transportstorage.tile.BlackHoleControllerTile;
import com.buuz135.industrial.module.ModuleTransportStorage;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class BlackHoleControllerBlock extends IndustrialBlock<BlackHoleControllerTile> {

    public BlackHoleControllerBlock() {
        super("black_hole_controller", Properties.from(Blocks.IRON_BLOCK), BlackHoleControllerTile.class, ModuleTransportStorage.TAB_TRANSPORT);
    }

    @Override
    public IFactory<BlackHoleControllerTile> getTileEntityFactory() {
        return BlackHoleControllerTile::new;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("PGP").patternLine("NEN").patternLine("PMP")
                .key('P', IndustrialTags.Items.PLASTIC)
                .key('G', IndustrialTags.Items.GEAR_DIAMOND)
                .key('N', Items.ENDER_EYE)
                .key('E', Tags.Items.CHESTS_ENDER)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_SUPREME)
                .build(consumer);
    }
}

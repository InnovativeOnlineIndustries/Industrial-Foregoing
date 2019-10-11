package com.buuz135.industrial.recipe.provider;

import com.buuz135.industrial.api.conveyor.ConveyorUpgradeFactory;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleTool;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.annotation.MaterialReference;
import com.hrznstudio.titanium.block.BlockBase;
import com.hrznstudio.titanium.recipe.generator.TitaniumRecipeProvider;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapelessRecipeBuilder;
import net.minecraft.data.CookingRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class IndustrialRecipeProvider extends TitaniumRecipeProvider {

    @MaterialReference(type = "gear", material = "iron")
    public static Item IRON_GEAR;
    @MaterialReference(type = "gear", material = "gold")
    public static Item GOLD_GEAR;
    @MaterialReference(type = "gear", material = "diamond")
    public static Item DIAMOND_GEAR;

    public IndustrialRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void register(Consumer<IFinishedRecipe> consumer) {
        BlockBase.BLOCKS.stream().filter(blockBase -> blockBase.getRegistryName().getNamespace().equals(Reference.MOD_ID)).forEach(blockBase -> blockBase.registerRecipe(consumer));
        //TRANSPORT
        ConveyorUpgradeFactory.FACTORIES.forEach(conveyorUpgradeFactory -> conveyorUpgradeFactory.registerRecipe(consumer));
        //TOOL
        ModuleTool.INFINITY_DRILL.registerRecipe(consumer);
        ModuleTool.MOB_IMPRISONMENT_TOOL.registerRecipe(consumer);
        ModuleTool.MEAT_FEEDER.registerRecipe(consumer);
        //CORE
        ModuleCore.STRAW.registerRecipe(consumer);
        TitaniumShapelessRecipeBuilder.shapelessRecipe(ModuleCore.DRY_RUBBER).addIngredient(ModuleCore.TINY_DRY_RUBBER, 9).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(ModuleCore.DRY_RUBBER), ModuleCore.PLASTIC, 0.3f, 200).addCriterion("has_plastic", this.hasItem(ModuleCore.DRY_RUBBER)).build(consumer);
        TitaniumShapedRecipeBuilder.shapedRecipe(ModuleCore.PITY)
                .patternLine("WIW").patternLine("IRI").patternLine("WIW")
                .key('W', ItemTags.LOGS)
                .key('I', Tags.Items.INGOTS_IRON)
                .key('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .build(consumer);
        TitaniumShapedRecipeBuilder.shapedRecipe(IRON_GEAR)
                .patternLine(" P ").patternLine("P P").patternLine(" P ")
                .key('P', Items.IRON_INGOT)
                .build(consumer);
        TitaniumShapedRecipeBuilder.shapedRecipe(GOLD_GEAR)
                .patternLine(" P ").patternLine("P P").patternLine(" P ")
                .key('P', Items.GOLD_INGOT)
                .build(consumer);
        TitaniumShapedRecipeBuilder.shapedRecipe(DIAMOND_GEAR)
                .patternLine(" P ").patternLine("P P").patternLine(" P ")
                .key('P', Items.DIAMOND)
                .build(consumer);
    }
}

package com.buuz135.industrial.recipe.provider;

import com.buuz135.industrial.api.conveyor.ConveyorUpgradeFactory;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleTool;
import com.buuz135.industrial.module.ModuleTransport;
import com.hrznstudio.titanium.recipe.generator.TitaniumRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;

import java.util.function.Consumer;

public class IndustrialRecipeProvider extends TitaniumRecipeProvider {

    public IndustrialRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void register(Consumer<IFinishedRecipe> consumer) {
        //TRANSPORT
        ConveyorUpgradeFactory.FACTORIES.forEach(conveyorUpgradeFactory -> conveyorUpgradeFactory.registerRecipe(consumer));
        ModuleTransport.CONVEYOR.registerRecipe(consumer);
        //TOOL
        ModuleTool.INFINITY_DRILL.registerRecipe(consumer);
        ModuleTool.MOB_IMPRISONMENT_TOOL.registerRecipe(consumer);
        ModuleTool.MEAT_FEEDER.registerRecipe(consumer);
        //CORE
        ModuleCore.STRAW.registerRecipe(consumer);
        ModuleCore.PLASTIC.registerRecipe(consumer);

    }
}

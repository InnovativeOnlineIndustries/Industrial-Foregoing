package com.buuz135.industrial.recipe.provider;

import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import com.buuz135.industrial.recipe.FluidExtractorRecipe;
import com.buuz135.industrial.recipe.LaserDrillFluidRecipe;
import com.buuz135.industrial.recipe.LaserDrillOreRecipe;
import com.hrznstudio.titanium.recipe.generator.IJSONGenerator;
import com.hrznstudio.titanium.recipe.generator.IJsonFile;
import com.hrznstudio.titanium.recipe.generator.TitaniumSerializableProvider;
import net.minecraft.data.DataGenerator;

import java.util.Map;

public class IndustrialSerializableProvider extends TitaniumSerializableProvider {

    public IndustrialSerializableProvider(DataGenerator generatorIn, String modid) {
        super(generatorIn, modid);
    }

    @Override
    public void add(Map<IJsonFile, IJSONGenerator> serializables) {
        DissolutionChamberRecipe.RECIPES.forEach(dissolutionChamberRecipe -> serializables.put(dissolutionChamberRecipe, dissolutionChamberRecipe));
        FluidExtractorRecipe.RECIPES.forEach(fluidExtractorRecipe -> serializables.put(fluidExtractorRecipe, fluidExtractorRecipe));
        LaserDrillFluidRecipe.RECIPES.forEach(laserDrillFluidRecipe -> serializables.put(laserDrillFluidRecipe, laserDrillFluidRecipe));
        LaserDrillOreRecipe.RECIPES.forEach(laserDrillOreRecipe -> serializables.put(laserDrillOreRecipe, laserDrillOreRecipe));
    }
}

/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
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

package com.buuz135.industrial.recipe.provider;

import com.buuz135.industrial.recipe.*;
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
        LaserDrillFluidRecipe.init();
        LaserDrillFluidRecipe.RECIPES.forEach(laserDrillFluidRecipe -> serializables.put(laserDrillFluidRecipe, laserDrillFluidRecipe));
        LaserDrillOreRecipe.init();
        LaserDrillOreRecipe.RECIPES.forEach(laserDrillOreRecipe -> serializables.put(laserDrillOreRecipe, laserDrillOreRecipe));
        StoneWorkGenerateRecipe.RECIPES.forEach(stoneWorkGenerateRecipe -> serializables.put(stoneWorkGenerateRecipe, stoneWorkGenerateRecipe));
        CrusherRecipe.init();
        CrusherRecipe.RECIPES.forEach(crusherRecipe -> serializables.put(crusherRecipe, crusherRecipe));
    }
}

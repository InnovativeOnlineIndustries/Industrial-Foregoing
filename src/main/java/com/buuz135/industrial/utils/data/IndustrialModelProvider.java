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

package com.buuz135.industrial.utils.data;

import com.buuz135.industrial.utils.Reference;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class IndustrialModelProvider extends ModelProvider<BlockModelBuilder> {

    public IndustrialModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), Reference.MOD_ID, "block", BlockModelBuilder::new, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        //for (ConveyorBlock.EnumType type : ConveyorBlock.TYPE.getAllowedValues()) {
        //    for (Direction direction : ConveyorBlock.FACING.getAllowedValues()) {
        //        getBuilder(Reference.MOD_ID + ":conveyor_" + type.getName().toLowerCase() + "_" + direction.getName().toLowerCase()).parent(new ModelFile.UncheckedModelFile(type.getModel())).texture("2", type.getTexture());
        //    }
        //}
    }

    @Override
    public String getName() {
        return "Industrial Foregoing Model Provider";
    }
}

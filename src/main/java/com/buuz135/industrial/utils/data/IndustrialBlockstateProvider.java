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

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.module.ModuleTransportStorage;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class IndustrialBlockstateProvider extends BlockStateProvider {

    private ExistingFileHelper helper;
    private final NonNullLazy<List<Block>> blocks;

    public IndustrialBlockstateProvider(DataGenerator gen, ExistingFileHelper exFileHelper, NonNullLazy<List<Block>> blocks) {
        super(gen, "industrialforegoing", exFileHelper);
        this.helper = exFileHelper;
        this.blocks = blocks;
    }

    public static ResourceLocation getModel(Block block) {
        return new ResourceLocation(ForgeRegistries.BLOCKS.getKey(block).getNamespace(), "block/" + ForgeRegistries.BLOCKS.getKey(block).getPath());
    }

    @Override
    protected void registerStatesAndModels() {
        blocks.get().stream().filter(blockBase -> blockBase instanceof IndustrialBlock)
                .map(blockBase -> (IndustrialBlock) blockBase)
                .filter(industrialBlock -> !(industrialBlock.equals(ModuleAgricultureHusbandry.PLANT_SOWER)))
                .forEach(industrialBlock -> {
                    VariantBlockStateBuilder builder = getVariantBuilder(industrialBlock);
                    if (industrialBlock.getRotationType().getProperties().length > 0) {
                        for (DirectionProperty property : industrialBlock.getRotationType().getProperties()) {
                            for (Direction allowedValue : property.getPossibleValues()) {
                                builder.partialState().with(property, allowedValue)
                                        .addModels(new ConfiguredModel(new ModelFile.UncheckedModelFile(getModel(industrialBlock)), allowedValue.get2DDataValue() == -1 ? allowedValue.getOpposite().getAxisDirection().getStep() * 90 : 0, (int) allowedValue.getOpposite().toYRot(), false));
                            }
                        }
                    } else {
                        builder.partialState().addModels(new ConfiguredModel(new ModelFile.UncheckedModelFile(getModel(industrialBlock))));
                    }
                });
        simpleBlock(ModuleTransportStorage.TRANSPORTER.getLeft().get(), new ModelFile.UncheckedModelFile(modLoc("block/" + ForgeRegistries.BLOCKS.getKey(ModuleTransportStorage.TRANSPORTER.getLeft().get()).getPath())));
        //VariantBlockStateBuilder conveyor = getVariantBuilder(ModuleTransport.CONVEYOR);
        //for (ConveyorBlock.EnumType type : ConveyorBlock.TYPE.getAllowedValues()) {
        //    for (Direction direction : ConveyorBlock.FACING.getAllowedValues()) {
        //        conveyor.partialState().with(ConveyorBlock.TYPE, type).with(ConveyorBlock.FACING, direction)
        //                .addModels(new ConfiguredModel(new BlockModelBuilder(new ResourceLocation(Reference.MOD_ID, "block/conveyor_" + type.getName().toLowerCase() + "_" + direction.getName().toLowerCase()), helper).parent(new ModelFile.UncheckedModelFile(type.getModel())).texture("2", type.getTexture()), 0, (int) direction.getOpposite().getHorizontalAngle(), false));
        //    }
        //}
    }
}

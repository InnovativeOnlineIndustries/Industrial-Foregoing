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

package com.buuz135.industrial.module;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.block.IndustrialBlockItem;
import com.buuz135.industrial.block.generator.*;
import com.buuz135.industrial.block.generator.mycelial.IMycelialGeneratorType;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.module.BlockWithTile;
import com.hrznstudio.titanium.module.DeferredRegistryHelper;
import com.hrznstudio.titanium.tab.TitaniumTab;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ModuleGenerator implements IModule {

    public static TitaniumTab TAB_GENERATOR = new TitaniumTab(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "generator"));

    public static BlockWithTile PITIFUL_GENERATOR = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("pitiful_generator", () -> new PitifulGeneratorBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_GENERATOR), TAB_GENERATOR);
    public static BlockWithTile BIOREACTOR = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("bioreactor", () -> new BioReactorBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_GENERATOR), TAB_GENERATOR);
    public static BlockWithTile BIOFUEL_GENERATOR = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("biofuel_generator", () -> new BiofuelGeneratorBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_GENERATOR), TAB_GENERATOR);
    public static List<BlockWithTile> MYCELIAL_GENERATORS = new ArrayList<BlockWithTile>();
    public static BlockWithTile MYCELIAL_REACTOR = IndustrialForegoing.INSTANCE.getRegistries().registerBlockWithTileItem("mycelial_reactor", () -> new MycelialReactorBlock(), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_GENERATOR), TAB_GENERATOR);

    @Override
    public void generateFeatures(DeferredRegistryHelper helper) {
        for (IMycelialGeneratorType type : IMycelialGeneratorType.TYPES) {
            MYCELIAL_GENERATORS.add(helper.registerBlockWithTileItem("mycelial_" + type.getName(), () -> new MycelialGeneratorBlock(type), blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_GENERATOR), TAB_GENERATOR));
        }
    }
}

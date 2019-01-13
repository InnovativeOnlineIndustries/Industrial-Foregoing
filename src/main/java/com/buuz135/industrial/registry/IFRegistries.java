/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
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
package com.buuz135.industrial.registry;

import com.buuz135.industrial.api.conveyor.ConveyorUpgradeFactory;
import com.buuz135.industrial.api.plant.PlantRecollectable;
import com.buuz135.industrial.api.straw.StrawHandler;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

public class IFRegistries {

    public static final ForgeRegistry<StrawHandler> STRAW_HANDLER_REGISTRY = (ForgeRegistry<StrawHandler>) new RegistryBuilder<StrawHandler>()
            .setName(new ResourceLocation(Reference.MOD_ID, "straw"))
            .setIDRange(1, Integer.MAX_VALUE - 1)
            .setType(StrawHandler.class)
            .disableSaving()
            .create();

    public static final ForgeRegistry<ConveyorUpgradeFactory> CONVEYOR_UPGRADE_REGISTRY = (ForgeRegistry<ConveyorUpgradeFactory>) new RegistryBuilder<ConveyorUpgradeFactory>()
            .setName(new ResourceLocation(Reference.MOD_ID, "conveyor_upgrade"))
            .setIDRange(1, Integer.MAX_VALUE - 1)
            .setType(ConveyorUpgradeFactory.class)
            .create();

    public static final ForgeRegistry<PlantRecollectable> PLANT_RECOLLECTABLES_REGISTRY = (ForgeRegistry<PlantRecollectable>) new RegistryBuilder<PlantRecollectable>()
            .setName(new ResourceLocation(Reference.MOD_ID, "plant_recollectable"))
            .setIDRange(1, Integer.MAX_VALUE - 1)
            .setType(PlantRecollectable.class)
            .disableSaving()
            .create();

    public static void poke() {

    }
}
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
            .create();

    public static void poke() {

    }
}
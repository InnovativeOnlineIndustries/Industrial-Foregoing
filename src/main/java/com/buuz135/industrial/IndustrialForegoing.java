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
package com.buuz135.industrial;

import com.buuz135.industrial.module.*;
import com.buuz135.industrial.proxy.CommonProxy;
import com.buuz135.industrial.proxy.client.ClientProxy;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import com.buuz135.industrial.recipe.FluidExtractorRecipe;
import com.buuz135.industrial.recipe.provider.IndustrialRecipeProvider;
import com.buuz135.industrial.recipe.provider.IndustrialSerializableProvider;
import com.buuz135.industrial.recipe.provider.IndustrialTagsProvider;
import com.buuz135.industrial.registry.IFRegistries;
import com.buuz135.industrial.utils.IFFakePlayer;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.material.ResourceRegistry;
import com.hrznstudio.titanium.material.ResourceType;
import com.hrznstudio.titanium.module.Module;
import com.hrznstudio.titanium.module.ModuleController;
import com.hrznstudio.titanium.recipe.generator.BlockItemModelGeneratorProvider;
import com.hrznstudio.titanium.recipe.generator.titanium.DefaultLootTableProvider;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import java.util.HashMap;

@Mod(Reference.MOD_ID)
public class IndustrialForegoing extends ModuleController {

    private static CommonProxy proxy;
    private static HashMap<Integer, IFFakePlayer> worldFakePlayer = new HashMap<>();

    public IndustrialForegoing() {
        proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        EventManager.mod(FMLCommonSetupEvent.class).process(fmlCommonSetupEvent -> proxy.run()).subscribe();
        EventManager.mod(FMLClientSetupEvent.class).process(fmlClientSetupEvent -> proxy.run()).subscribe();
        EventManager.mod(FMLServerStartingEvent.class).process(fmlServerStartingEvent -> worldFakePlayer.clear()).subscribe();
        EventManager.mod(RegistryEvent.Register.class).filter(register -> register.getGenericType().equals(IRecipeSerializer.class))
                .process(register -> register.getRegistry().registerAll(FluidExtractorRecipe.SERIALIZER, DissolutionChamberRecipe.SERIALIZER)).subscribe();
        IFRegistries.poke();
    }

    public static FakePlayer getFakePlayer(World world) {
        if (worldFakePlayer.containsKey(world.dimension.getType().getId()))
            return worldFakePlayer.get(world.dimension.getType().getId());
        if (world instanceof ServerWorld) {
            IFFakePlayer fakePlayer = new IFFakePlayer((ServerWorld) world);
            worldFakePlayer.put(world.dimension.getType().getId(), fakePlayer);
            return fakePlayer;
        }
        return null;
    }

    public static FakePlayer getFakePlayer(World world, BlockPos pos) {
        FakePlayer player = getFakePlayer(world);
        if (player != null) player.setPositionAndRotation(pos.getX(), pos.getY(), pos.getZ(), 90, 90);
        return player;
    }

    @Override
    public void onPreInit() {
        super.onPreInit();
        ResourceRegistry.getOrCreate("iron").add(ResourceType.GEAR);
        ResourceRegistry.getOrCreate("gold").add(ResourceType.GEAR);
        ResourceRegistry.getOrCreate("diamond").add(ResourceType.GEAR);
    }

    @Override
    public void addDataProvider(GatherDataEvent event) {
        super.addDataProvider(event);
        event.getGenerator().addProvider(new IndustrialRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(new IndustrialSerializableProvider(event.getGenerator(), Reference.MOD_ID));
        event.getGenerator().addProvider(new IndustrialTagsProvider.Blocks(event.getGenerator()));
        event.getGenerator().addProvider(new IndustrialTagsProvider.Items(event.getGenerator()));
        event.getGenerator().addProvider(new DefaultLootTableProvider(event.getGenerator(), Reference.MOD_ID));
        event.getGenerator().addProvider(new BlockItemModelGeneratorProvider(event.getGenerator(), Reference.MOD_ID));
    }

    @Override
    protected void initModules() {
        Module.Builder core = Module.builder("core").description("Module for all the Industrial Foregoing basic features");
        new ModuleCore().generateFeatures().forEach(core::feature);
        addModule(core);

        Module.Builder tool = Module.builder("tools").description("A collection of Industrial Foregoing tools");
        new ModuleTool().generateFeatures().forEach(tool::feature);
        addModule(tool);

        Module.Builder transport = Module.builder("transport").description("All the Industrial Foregoing tools that allow of transport of things");
        new ModuleTransport().generateFeatures().forEach(transport::feature);
        addModule(transport);

        Module.Builder generator = Module.builder("generator").description("All machines that generate power");
        new ModuleGenerator().generateFeatures().forEach(generator::feature);
        addModule(generator);

        Module.Builder agriculture = Module.builder("agriculture").description("All of your farming options");
        new ModuleAgricultureHusbandry().generateFeatures().forEach(agriculture::feature);
        addModule(agriculture);

        Module.Builder resources = Module.builder("resource_production");
        new ModuleResourceProduction().generateFeatures().forEach(resources::feature);
        addModule(resources);
    }

}

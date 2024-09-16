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
package com.buuz135.industrial;

import com.buuz135.industrial.gui.conveyor.ContainerConveyor;
import com.buuz135.industrial.gui.conveyor.GuiConveyor;
import com.buuz135.industrial.gui.transporter.ContainerTransporter;
import com.buuz135.industrial.gui.transporter.GuiTransporter;
import com.buuz135.industrial.module.*;
import com.buuz135.industrial.proxy.CommonProxy;
import com.buuz135.industrial.proxy.client.ClientProxy;
import com.buuz135.industrial.proxy.network.*;
import com.buuz135.industrial.recipe.LaserDrillRarity;
import com.buuz135.industrial.recipe.provider.IndustrialRecipeProvider;
import com.buuz135.industrial.recipe.provider.IndustrialTagsProvider;
import com.buuz135.industrial.registry.IFRegistries;
import com.buuz135.industrial.utils.IFAttachments;
import com.buuz135.industrial.utils.IFFakePlayer;
import com.buuz135.industrial.utils.Reference;
import com.buuz135.industrial.utils.data.IndustrialBlockstateProvider;
import com.buuz135.industrial.utils.data.IndustrialModelProvider;
import com.hrznstudio.titanium.datagenerator.loot.TitaniumLootTableProvider;
import com.hrznstudio.titanium.datagenerator.model.BlockItemModelGeneratorProvider;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.module.ModuleController;
import com.hrznstudio.titanium.network.NetworkHandler;
import com.hrznstudio.titanium.network.locator.PlayerInventoryFinder;
import com.hrznstudio.titanium.reward.Reward;
import com.hrznstudio.titanium.reward.RewardGiver;
import com.hrznstudio.titanium.reward.RewardManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforgespi.language.IModInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Mod(Reference.MOD_ID)
public class IndustrialForegoing extends ModuleController {

    private static CommonProxy proxy;
    private static HashMap<String, IFFakePlayer> worldFakePlayer = new HashMap<>();
    public static NetworkHandler NETWORK = new NetworkHandler(Reference.MOD_ID);
    public static Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);
    public static IndustrialForegoing INSTANCE;
    public static Reward CAT_EARS;
    public static List<String> OWN_MODS_LOADED = new ArrayList<>();

    public IndustrialForegoing(Dist dist, IEventBus modBus, ModContainer container) {
        super(container);
        NETWORK.registerMessage("conveyor_button_interact", ConveyorButtonInteractMessage.class);
        NETWORK.registerMessage("conveyor_splitting_sync_entity", ConveyorSplittingSyncEntityMessage.class);
        NETWORK.registerMessage("special_particle", SpecialParticleMessage.class);
        NETWORK.registerMessage("backpack_sync", BackpackSyncMessage.class);
        NETWORK.registerMessage("backpack_open", BackpackOpenMessage.class);
        NETWORK.registerMessage("backpack_opened", BackpackOpenedMessage.class);
        NETWORK.registerMessage("transporter_sync", TransporterSyncMessage.class);
        NETWORK.registerMessage("transporter_button_interact", TransporterButtonInteractMessage.class);
        NETWORK.registerMessage("plunger_player_hit", PlungerPlayerHitMessage.class);
        proxy = new CommonProxy();
        if (dist.isClient()) {
            EventManager.mod(FMLClientSetupEvent.class).process(fmlClientSetupEvent -> new ClientProxy().run()).subscribe();
            EventManager.mod(ModelEvent.RegisterAdditional.class).process(modelRegistryEvent -> modelRegistryEvent.register(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/catears"), "standalone"))).subscribe();
            this.initClient();
        }

        EventManager.mod(FMLCommonSetupEvent.class).process(fmlCommonSetupEvent -> proxy.run()).subscribe();
        EventManager.forge(ServerStartingEvent.class).process(fmlServerStartingEvent -> worldFakePlayer.clear()).subscribe();
        EventManager.mod(NewRegistryEvent.class).process(IFRegistries::create).subscribe();
        /*
        EventManager.forge(ItemTooltipEvent.class).process(itemTooltipEvent -> BuiltInRegistries.ITEM.tags().getReverseTag(itemTooltipEvent.getItemStack().getItem()).ifPresent(itemIReverseTag -> {
            itemIReverseTag.getTagKeys().forEach(itemTagKey -> itemTooltipEvent.getToolTip().add(Component.literal(itemTagKey.location().toString())));
        })).subscribe();*/
        RewardGiver giver = RewardManager.get().getGiver(UUID.fromString("d28b7061-fb92-4064-90fb-7e02b95a72a6"), "Buuz135");
        try {
            CAT_EARS = new Reward(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "cat_ears"), new URL("https://raw.githubusercontent.com/Buuz135/Industrial-Foregoing/master/contributors.json"), () -> iDist -> {
            }, new String[]{"normal", "cat", "spooky", "snowy"});
            giver.addReward(CAT_EARS);
        } catch (MalformedURLException e) {
            LOGGER.catching(e);
        }

        LaserDrillRarity.init();
        PlayerInventoryFinder.init();
        NeoForgeMod.enableMilkFluid();

        IFAttachments.DR.register(modBus);

        OWN_MODS_LOADED = ModList.get().getMods().stream()
                .filter(iModInfo -> iModInfo.getConfig().getConfigElement("authors").orElse("").toString().contains("Buuz135"))
                .map(IModInfo::getDisplayName).toList();

    }

    private static FakePlayer getFakePlayer(Level world, String uuid) {
        if (worldFakePlayer.containsKey(uuid))
            return worldFakePlayer.get(uuid);
        if (world instanceof ServerLevel serverLevel) {
            IFFakePlayer fakePlayer = new IFFakePlayer(serverLevel, uuid);
            worldFakePlayer.put(uuid, fakePlayer);
            return fakePlayer;
        }
        return null;
    }

    public static FakePlayer getFakePlayer(Level world, BlockPos pos, String uuid) {
        FakePlayer player = getFakePlayer(world, uuid);
        if (player != null) player.absMoveTo(pos.getX(), pos.getY(), pos.getZ(), 90, 90);
        return player;
    }

    @Override
    public void onPreInit() {
        super.onPreInit();
    }

    @Override
    public void addDataProvider(GatherDataEvent event) {
        super.addDataProvider(event);
        Lazy<List<Block>> blocksToProcess = Lazy.of(() ->
                BuiltInRegistries.BLOCK
                        .stream()
                        .filter(block -> !block.getClass().equals(LiquidBlock.class))
                        .filter(basicBlock -> Optional.ofNullable(BuiltInRegistries.BLOCK.getKey(basicBlock))
                                .map(ResourceLocation::getNamespace)
                                .filter(Reference.MOD_ID::equalsIgnoreCase)
                                .isPresent())
                        .collect(Collectors.toList())
        );
        var blockProvider = new IndustrialTagsProvider.Blocks(event.getGenerator(),event.getLookupProvider() ,Reference.MOD_ID, event.getExistingFileHelper());
        event.getGenerator().addProvider(true, blockProvider);
        event.getGenerator().addProvider(true, new IndustrialTagsProvider.Items(event.getGenerator(),event.getLookupProvider(), blockProvider.contentsGetter(), Reference.MOD_ID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(true, new IndustrialRecipeProvider(event.getGenerator(), blocksToProcess, event.getLookupProvider()));
        //event.getGenerator().addProvider(true, new IndustrialSerializableProvider(event.getGenerator(), Reference.MOD_ID));
        event.getGenerator().addProvider(true, new TitaniumLootTableProvider(event.getGenerator(), blocksToProcess, event.getLookupProvider()));
        event.getGenerator().addProvider(true, new BlockItemModelGeneratorProvider(event.getGenerator(), Reference.MOD_ID, blocksToProcess));
        event.getGenerator().addProvider(true, new IndustrialBlockstateProvider(event.getGenerator(), event.getExistingFileHelper(), blocksToProcess));
        event.getGenerator().addProvider(true, new IndustrialModelProvider(event.getGenerator(), event.getExistingFileHelper()));
    }

    @Override
    protected void initModules() {
        INSTANCE = this;


        new ModuleCore().generateFeatures(getRegistries());
        new ModuleTool().generateFeatures(getRegistries());
        new ModuleTransportStorage().generateFeatures(getRegistries());
        new ModuleGenerator().generateFeatures(getRegistries());
        new ModuleAgricultureHusbandry().generateFeatures(getRegistries());
        new ModuleResourceProduction().generateFeatures(getRegistries());
        new ModuleMisc().generateFeatures(getRegistries());

        this.addCreativeTab("core", () -> new ItemStack(ModuleCore.DISSOLUTION_CHAMBER.getBlock()), Reference.MOD_ID + "_core", ModuleCore.TAB_CORE);
        this.addCreativeTab("ag_hus", () -> new ItemStack(ModuleAgricultureHusbandry.PLANT_SOWER.getBlock()), Reference.MOD_ID + "_ag_hus", ModuleAgricultureHusbandry.TAB_AG_HUS);
        this.addCreativeTab("transport", () -> new ItemStack(ModuleTransportStorage.CONVEYOR.getBlock()), Reference.MOD_ID + "_transport", ModuleTransportStorage.TAB_TRANSPORT);
        this.addCreativeTab("resource_production", () -> new ItemStack(ModuleResourceProduction.WATER_CONDENSATOR.getBlock()), Reference.MOD_ID + "_resource_production", ModuleResourceProduction.TAB_RESOURCE);
        this.addCreativeTab("generator", () -> new ItemStack(ModuleGenerator.PITIFUL_GENERATOR.getBlock()), Reference.MOD_ID + "_generator", ModuleGenerator.TAB_GENERATOR);
        this.addCreativeTab("misc", () -> new ItemStack(ModuleMisc.ENCHANTMENT_FACTORY.getBlock()), Reference.MOD_ID + "_misc", ModuleMisc.TAB_MISC);
        this.addCreativeTab("tool", () ->  new ItemStack(ModuleTool.INFINITY_DRILL.get()), Reference.MOD_ID + "_tool", ModuleTool.TAB_TOOL);
    }

    @OnlyIn(Dist.CLIENT)
    private void initClient() {
        EventManager.mod(RegisterMenuScreensEvent.class).process(registerMenuScreensEvent -> {
            registerMenuScreensEvent.register((MenuType<? extends ContainerTransporter>) ContainerTransporter.TYPE.get(), GuiTransporter::new);
            registerMenuScreensEvent.register((MenuType<? extends ContainerConveyor>) ContainerConveyor.TYPE.get(), GuiConveyor::new);
        }).subscribe();
        EventManager.mod(ModelEvent.BakingCompleted.class).process(event -> {
            ClientProxy.ears_baked = event.getModels().get(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/catears"), "standalone"));
        }).subscribe();
        ClientProxy.OPEN_BACKPACK = new KeyMapping("key.industrialforegoing.backpack.desc", -1, "key.industrialforegoing.category");
        EventManager.mod(RegisterKeyMappingsEvent.class).process(event -> {
            event.register(ClientProxy.OPEN_BACKPACK);
        }).subscribe();
        EventManager.forge(ClientTickEvent.Post.class).process(event -> {
            if (ClientProxy.OPEN_BACKPACK.consumeClick()) {
                IndustrialForegoing.NETWORK.sendToServer(new BackpackOpenMessage(Screen.hasControlDown()));
            }
        }).subscribe();
    }
}

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

import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.proxy.CommonProxy;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.utils.IFFakePlayer;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.ndrei.teslacorelib.config.TeslaCoreLibConfig;
import net.ndrei.teslacorelib.items.gears.CoreGearType;

import java.util.Arrays;
import java.util.HashMap;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_ID, version = Reference.VERSION, dependencies = "required-after:forge@[14.23.5.2806,);required-after:teslacorelib@[1.0.15,);", guiFactory = Reference.GUI_FACTORY, updateJSON = "https://raw.githubusercontent.com/Buuz135/Industrial-Foregoing/master/update.json")
public class IndustrialForegoing {

    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);
    public static CreativeTabs creativeTab = new CreativeTabs(Reference.MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(BlockRegistry.blackHoleUnitBlock).isEmpty() ? new ItemStack(ItemRegistry.strawItem) : new ItemStack(BlockRegistry.blackHoleUnitBlock);
        }
    };
    public static IndustrialForegoing instance;
    @SidedProxy(clientSide = Reference.PROXY_CLIENT, serverSide = Reference.PROXY_COMMON)
    private static CommonProxy proxy;
    private static HashMap<Integer, IFFakePlayer> worldFakePlayer = new HashMap<>();

    static {
        if (!FluidRegistry.isUniversalBucketEnabled()) FluidRegistry.enableUniversalBucket();
    }

    public static FakePlayer getFakePlayer(World world) {
        if (worldFakePlayer.containsKey(world.provider.getDimension()))
            return worldFakePlayer.get(world.provider.getDimension());
        if (world instanceof WorldServer) {
            IFFakePlayer fakePlayer = new IFFakePlayer((WorldServer) world);
            worldFakePlayer.put(world.provider.getDimension(), fakePlayer);
            return fakePlayer;
        }
        return null;
    }

    public static FakePlayer getFakePlayer(World world, BlockPos pos) {
        FakePlayer player = getFakePlayer(world);
        if (player != null) player.setPositionAndRotation(pos.getX(), pos.getY(), pos.getZ(), 90, 90);
        return player;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        instance = this;
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
    }

    @Mod.EventHandler
    public void construction(FMLConstructionEvent event) {
        Arrays.asList(TeslaCoreLibConfig.REGISTER_MACHINE_CASE, TeslaCoreLibConfig.REGISTER_GEARS,
                TeslaCoreLibConfig.REGISTER_GEAR_TYPES + "#" + CoreGearType.IRON.getMaterial(),
                TeslaCoreLibConfig.REGISTER_GEAR_TYPES + "#" + CoreGearType.GOLD.getMaterial(),
                TeslaCoreLibConfig.REGISTER_GEAR_TYPES + "#" + CoreGearType.DIAMOND.getMaterial(),
                TeslaCoreLibConfig.REGISTER_ADDONS,
                TeslaCoreLibConfig.REGISTER_SPEED_ADDONS,
                TeslaCoreLibConfig.REGISTER_ENERGY_ADDONS).forEach(s -> TeslaCoreLibConfig.INSTANCE.setDefaultFlag(s, true));
        TeslaCoreLibConfig.INSTANCE.setDefaultFlag(TeslaCoreLibConfig.ALLOW_ENERGY_DISPLAY_CHANGE, false);
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        worldFakePlayer.clear();
    }
}

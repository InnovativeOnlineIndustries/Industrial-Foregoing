package com.buuz135.industrial;

import com.buuz135.industrial.proxy.CommonProxy;
import com.buuz135.industrial.utils.IFFakePlayer;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.HashMap;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_ID, version = Reference.VERSION, dependencies = "after:teslacorelib")
public class IndustrialForegoing {

    @SidedProxy(clientSide = Reference.PROXY_CLIENT, serverSide = Reference.PROXY_COMMON)
    private static CommonProxy proxy;

    public static CreativeTabs creativeTab = new CreativeTabs(Reference.MOD_ID) {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(Items.COAL);
        }
    };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
    }

    private static HashMap<Integer, IFFakePlayer> worldFakePlayer = new HashMap<>();

    public static IFFakePlayer getFakePlayer(World world) {
        if (worldFakePlayer.containsKey(world.provider.getDimension()))
            return worldFakePlayer.get(world.provider.getDimension());
        if (world instanceof WorldServer) {
            IFFakePlayer fakePlayer = new IFFakePlayer((WorldServer) world);
            worldFakePlayer.put(world.provider.getDimension(), fakePlayer);
            return fakePlayer;
        }
        return null;
    }

    static {
        if (!FluidRegistry.isUniversalBucketEnabled()) FluidRegistry.enableUniversalBucket();
    }
}

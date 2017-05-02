package com.buuz135.industrial.proxy.client;

import com.buuz135.industrial.proxy.CommonProxy;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

    public static ResourceLocation GUI = new ResourceLocation(Reference.MOD_ID, "textures/gui/machines.png");

    @Override
    public void preInit() {
        super.preInit();
        OBJLoader.INSTANCE.addDomain(Reference.MOD_ID);
        FluidsRenderRegistry.registerRender();
        BlockRenderRegistry.registerRender();

        MinecraftForge.EVENT_BUS.register(new MachineWorkAreaRender());
        MinecraftForge.EVENT_BUS.register(new MobRenderInPrisonHandler());
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void postInit() {
        super.postInit();
    }
}

package com.buuz135.industrial.proxy.client;

import com.buuz135.industrial.proxy.CommonProxy;
import com.buuz135.industrial.proxy.client.render.LaserDrillSpecialRender;
import com.buuz135.industrial.tile.world.LaserDrillTile;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    public static ResourceLocation GUI = new ResourceLocation(Reference.MOD_ID, "textures/gui/machines.png");
    public static final ResourceLocation beacon = new ResourceLocation("textures/entity/beacon_beam.png");

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        OBJLoader.INSTANCE.addDomain(Reference.MOD_ID);
        ItemRenderRegistry.registerRender();
        FluidsRenderRegistry.registerRender();
        BlockRenderRegistry.registerRender();

        MinecraftForge.EVENT_BUS.register(new MachineWorkAreaRender());
        MinecraftForge.EVENT_BUS.register(new MobRenderInPrisonHandler());

        //ClientRegistry.bindTileEntitySpecialRenderer(PotionEnervatorTile.class, new PotionEnervatorSpecialRenderer());

        ClientRegistry.bindTileEntitySpecialRenderer(LaserDrillTile.class, new LaserDrillSpecialRender());
        //ClientRegistry.bindTileEntitySpecialRenderer(LaserBaseTile.class, new LaserBaseSpecialRender());
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

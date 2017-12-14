package com.buuz135.industrial.proxy.client;

import com.buuz135.industrial.book.IFManual;
import com.buuz135.industrial.entity.EntityPinkSlime;
import com.buuz135.industrial.proxy.CommonProxy;
import com.buuz135.industrial.proxy.client.entity.RenderPinkSlime;
import com.buuz135.industrial.proxy.client.event.IFTextureStichEvent;
import com.buuz135.industrial.proxy.client.event.IFWorldRenderLastEvent;
import com.buuz135.industrial.proxy.client.render.ContributorsCatEarsRender;
import com.buuz135.industrial.utils.Reference;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

public class ClientProxy extends CommonProxy {

    public static final ResourceLocation beacon = new ResourceLocation("textures/entity/beacon_beam.png");
    public static final String CONTRIBUTORS_FILE = "https://raw.githubusercontent.com/Buuz135/Industrial-Foregoing/master/contributors.json";
    public static ResourceLocation GUI = new ResourceLocation(Reference.MOD_ID, "textures/gui/machines.png");
    public static IBakedModel ears_baked;
    public static IModel ears_model;

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        OBJLoader.INSTANCE.addDomain(Reference.MOD_ID);

        //MinecraftForge.EVENT_BUS.register(new MobRenderInPrisonHandler());
        MinecraftForge.EVENT_BUS.register(new IFTextureStichEvent());
        MinecraftForge.EVENT_BUS.register(new IFWorldRenderLastEvent());

    }


    @Override
    public void init() {
        super.init();
        try {
            ears_model = OBJLoader.INSTANCE.loadModel(new ResourceLocation(Reference.MOD_ID, "models/block/catears.obj"));
            ears_baked = ears_model.bake(TRSRTransformation.identity(), DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter());
        } catch (Exception e) {
            e.printStackTrace();
        }
        RenderManager manager = Minecraft.getMinecraft().getRenderManager();
        Map<String, RenderPlayer> map = manager.getSkinMap();
        map.get("default").addLayer(new ContributorsCatEarsRender());
        map.get("slim").addLayer(new ContributorsCatEarsRender());

        try {
            ContributorsCatEarsRender.contributors = new GsonBuilder().create().fromJson(readUrl(CONTRIBUTORS_FILE), ContributorsCatEarsRender.Contributors.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        manager.entityRenderMap.put(EntityPinkSlime.class, new RenderPinkSlime(manager));
    }

    @Override
    public void postInit() {
        super.postInit();
        IFManual.buildManual();
    }

}

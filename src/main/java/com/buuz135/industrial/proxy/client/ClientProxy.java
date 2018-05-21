package com.buuz135.industrial.proxy.client;

import com.buuz135.industrial.book.IFManual;
import com.buuz135.industrial.entity.EntityPinkSlime;
import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.proxy.CommonProxy;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.proxy.block.TileEntityConveyor;
import com.buuz135.industrial.proxy.client.entity.RenderPinkSlime;
import com.buuz135.industrial.proxy.client.event.IFTextureStichEvent;
import com.buuz135.industrial.proxy.client.event.IFTooltipEvent;
import com.buuz135.industrial.proxy.client.event.IFWorldRenderLastEvent;
import com.buuz135.industrial.proxy.client.render.ContributorsCatEarsRender;
import com.buuz135.industrial.tile.misc.BlackHoleTankTile;
import com.buuz135.industrial.utils.Reference;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityList;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
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

        MinecraftForge.EVENT_BUS.register(new IFTextureStichEvent());
        MinecraftForge.EVENT_BUS.register(new IFWorldRenderLastEvent());
        MinecraftForge.EVENT_BUS.register(new IFTooltipEvent());
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
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> ItemDye.DYE_COLORS[EnumDyeColor.byMetadata(stack.getMetadata()).getDyeDamage()], ItemRegistry.artificalDye);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
            if (tintIndex == 0) return ItemDye.DYE_COLORS[EnumDyeColor.byMetadata(stack.getMetadata()).getDyeDamage()];
            return 0xFFFFFFF;
        }, BlockRegistry.blockConveyor.getItem());
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 0) {
                TileEntity entity = worldIn.getTileEntity(pos);
                if (entity instanceof TileEntityConveyor) {
                    return ItemDye.DYE_COLORS[((TileEntityConveyor) entity).getColor()];
                }
            }
            return 0xFFFFFFF;
        }, BlockRegistry.blockConveyor);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
            if (tintIndex == 1 || tintIndex == 2 || tintIndex == 3) {
                EntityList.EntityEggInfo info = null;
                if (stack.hasTagCompound() && stack.getTagCompound().hasKey("entity", Constants.NBT.TAG_STRING)) {
                    ResourceLocation id = new ResourceLocation(stack.getTagCompound().getString("entity"));
                    info = EntityList.ENTITY_EGGS.get(id);
                }
                return info == null ? 0x636363 : tintIndex == 3 ? BlockRegistry.mobDuplicatorBlock.blacklistedEntities.contains(info.spawnedID.toString()) ? 0xDB201A : 0x636363 : tintIndex == 1 ? info.primaryColor : info.secondaryColor;
            }
            return 0xFFFFFF;
        }, ItemRegistry.mobImprisonmentToolItem);
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 0 && worldIn.getTileEntity(pos) instanceof BlackHoleTankTile) {
                BlackHoleTankTile tank = (BlackHoleTankTile) worldIn.getTileEntity(pos);
                if (tank != null && tank.getTank().getFluidAmount() > 0) {
                    if (tank.getTank().getFluid().getFluid() == FluidRegistry.WATER) return 0x0066ff;
                    if (tank.getTank().getFluid().getFluid() == FluidRegistry.LAVA) return 0xe67300;
                    return tank.getTank().getFluid().getFluid().getColor(tank.getTank().getFluid());
                }
            }
            return 0xFFFFFF;
        }, BlockRegistry.blackHoleTankBlock);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
            if (tintIndex == 0 && stack.hasTagCompound() && stack.getTagCompound().hasKey("FluidName") && FluidRegistry.isFluidRegistered(stack.getTagCompound().getString("FluidName"))) {
                Fluid fluid = FluidRegistry.getFluid(stack.getTagCompound().getString("FluidName"));
                if (fluid == FluidRegistry.WATER) return 0x0066ff;
                if (fluid == FluidRegistry.LAVA) return 0xe67300;
                return fluid.getColor();
            }
            return 0xFFFFFF;
        }, BlockRegistry.blackHoleTankBlock);
    }

    @Override
    public void postInit() {
        super.postInit();
        IFManual.buildManual();
    }

}

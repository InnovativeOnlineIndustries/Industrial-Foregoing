package com.buuz135.industrial.proxy.client.event;

import com.buuz135.industrial.utils.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class IFTextureStichEvent {

    @SubscribeEvent
    public void textureStich(TextureStitchEvent.Pre pre) {
        pre.getMap().registerSprite(new ResourceLocation(Reference.MOD_ID, "blocks/catears"));
    }
}

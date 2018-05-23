package com.buuz135.industrial.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

@SideOnly(Side.CLIENT)
public class ColorUtils {

    public static int getColorFrom(ResourceLocation location) {
        TextureMap textureMap = Minecraft.getMinecraft().getTextureMapBlocks();
        TextureAtlasSprite sprite = textureMap.getTextureExtry(location.toString());
        if (sprite == null) return -1;
        if (sprite.getFrameCount() == 0) return -1;
        int[][] pixelMatrix = sprite.getFrameTextureData(0);
        int total = 0, red = 0, blue = 0, green = 0;
        for (int[] pixelArray : pixelMatrix) {
            for (int pixel : pixelArray) {
                Color color = new Color(pixel);
                if (color.getAlpha() < 255) continue;
                ++total;
                red += color.getRed();
                green += color.getGreen();
                blue += color.getBlue();
            }
        }
        if (total > 0) return new Color(red / total, green / total, blue / total, 255).getRGB();
        return -1;
    }

}

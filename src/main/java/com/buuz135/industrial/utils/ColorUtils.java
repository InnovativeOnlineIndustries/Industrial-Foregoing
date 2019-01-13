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
        return getColorFrom(sprite);
    }

    public static int getColorFrom(TextureAtlasSprite sprite) {
        if (sprite == null) return -1;
        if (sprite.getFrameCount() == 0) return -1;
        int[][] pixelMatrix = sprite.getFrameTextureData(0);
        int total = 0, red = 0, blue = 0, green = 0;
        for (int pixel : pixelMatrix[pixelMatrix.length - 1]) {
            Color color = new Color(pixel);
            if (color.getAlpha() < 255) continue;
            ++total;
            red += color.getRed();
            green += color.getGreen();
            blue += color.getBlue();
        }
        if (total > 0) return new Color(red / total, green / total, blue / total, 255).getRGB();
        return -1;
    }

    public static int getColorFrom(TextureAtlasSprite sprite, Color filter) {
        if (sprite == null) return -1;
        if (sprite.getFrameCount() == 0) return -1;
        int[][] pixelMatrix = sprite.getFrameTextureData(0);
        int total = 0, red = 0, blue = 0, green = 0;
        for (int pixel : pixelMatrix[pixelMatrix.length - 1]) {
            Color color = new Color(pixel);
            if (color.getAlpha() < 255 || (color.getRGB() - filter.getRGB() < 100 && color.getRGB() - filter.getRGB() > 100))
                continue;
            ++total;
            red += color.getRed();
            green += color.getGreen();
            blue += color.getBlue();
        }
        if (total > 0) return new Color(red / total, green / total, blue / total, 255).brighter().getRGB();
        return -1;
    }

}

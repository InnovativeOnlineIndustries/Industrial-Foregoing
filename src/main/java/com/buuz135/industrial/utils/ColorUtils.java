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
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class ColorUtils {

    public static int getColorFrom(ResourceLocation location) {
        Texture texture = Minecraft.getInstance().getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        if (texture instanceof AtlasTexture) {
            return getColorFrom(((AtlasTexture) texture).getSprite(location));
        }
        return 0;
    }

    public static int getColorFrom(TextureAtlasSprite sprite) {
        if (sprite == null) return -1;
        if (sprite.getFrameCount() == 0) return -1;
        float total = 0, red = 0, blue = 0, green = 0;
            for (int y = 0; y < sprite.getHeight(); y++) {
                for (int x = 0; x < sprite.getWidth(); x++) {
                    int color = sprite.getPixelRGBA(0, x, y);
                    //Color color = new Color(sprite.getPixelRGBA(frame, x, y), true);
                    //System.out.println(color);
                    //System.out.println(sprite);
                    //System.out.println(color);
                    if ((color >> 24 & 0xFF) != 255) continue;
                    ++total;
                    red +=  (color >> 0) & 0xFF;
                    green += (color >>  8) & 0xFF;
                    blue += (color >> 16) & 0xFF;
                }
            }

        if (total > 0) return new Color( (int)(red / total), (int) (green / total), (int) (blue / total), 255).getRGB();
        return -1;
    }

    public static int getColorFrom(TextureAtlasSprite sprite, Color filter) {
        if (sprite == null) return -1;
        if (sprite.getFrameCount() == 0) return -1;
        int[][] pixelMatrix = new int[][]{{0}};//sprite.getFrameTextureData(0);
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

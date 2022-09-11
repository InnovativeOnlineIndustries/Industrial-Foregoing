/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
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

package com.buuz135.industrial.item.infinity;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.tuple.Pair;

public enum InfinityTier {
    POOR("poor", 0, 0, ChatFormatting.GRAY, 0x7c7c7a),//1x1
    COMMON("common", 4_000_000, 1, ChatFormatting.WHITE, 0xFFFFFF), //3x3
    UNCOMMON("uncommon", 16_000_000, 2, ChatFormatting.GREEN, 0x1ce819), //5x5
    RARE("rare", 80_000_000, 3, ChatFormatting.BLUE, 0x0087ff), //7x7
    EPIC("epic", 480_000_000, 4, ChatFormatting.DARK_PURPLE, 0xe100ff), //9x9
    LEGENDARY("legendary", 3_360_000_000L, 5, ChatFormatting.GOLD, 0xffaa00), //11x11
    ARTIFACT("artifact", Long.MAX_VALUE, 6, ChatFormatting.YELLOW, 0xfff887); //13x13

    private final String name;
    private final ChatFormatting color;
    private final int textureColor;
    private long powerNeeded;
    private int radius;

    InfinityTier(String name, long powerNeeded, int radius, ChatFormatting color, int textureColor) {
        this.name = name;
        this.powerNeeded = powerNeeded;
        this.radius = radius;
        this.color = color;
        this.textureColor = textureColor;
    }

    public static Pair<InfinityTier, InfinityTier> getTierBraquet(long power) {
        InfinityTier lastTier = POOR;
        for (InfinityTier infinityTier : InfinityTier.values()) {
            if (power >= lastTier.getPowerNeeded() && power < infinityTier.getPowerNeeded())
                return Pair.of(lastTier, infinityTier);
            lastTier = infinityTier;
        }
        return Pair.of(ARTIFACT, ARTIFACT);
    }

    public String getLocalizedName() {
        return Component.translatable("text.industrialforegoing.tooltip.infinitydrill." + name).getString();
    }

    public String getName() {
        return name;
    }

    public long getPowerNeeded() {
        return powerNeeded;
    }

    public void setPowerNeeded(long powerNeeded) {
        this.powerNeeded = powerNeeded;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public ChatFormatting getColor() {
        return color;
    }

    public int getTextureColor() {
        return textureColor;
    }

    public InfinityTier getNext(InfinityTier maxTier) {
        InfinityTier lastTier = POOR;
        for (InfinityTier infinityTier : InfinityTier.values()) {
            if (infinityTier == POOR) continue;
            if (lastTier == maxTier) return POOR;
            if (this == lastTier) return infinityTier;
            lastTier = infinityTier;
        }
        return InfinityTier.POOR;
    }

    public InfinityTier getPrev(InfinityTier maxTier) {
        InfinityTier lastTier = POOR;
        if (this == POOR) return maxTier;
        for (InfinityTier infinityTier : InfinityTier.values()) {
            if (infinityTier == POOR) continue;
            if (lastTier == maxTier) return POOR;
            if (this == infinityTier) return lastTier;
            lastTier = infinityTier;
        }
        return maxTier;
    }
}


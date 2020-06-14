package com.buuz135.industrial.item.infinity;

import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.tuple.Pair;

public enum InfinityTier {
    POOR("poor", 0, 0, TextFormatting.GRAY, 0x7c7c7a),//1x1
    COMMON("common", 4_000_000, 1, TextFormatting.WHITE, 0xFFFFFF), //3x3
    UNCOMMON("uncommon", 16_000_000, 2, TextFormatting.GREEN, 0x1ce819), //5x5
    RARE("rare", 80_000_000, 3, TextFormatting.BLUE, 0x0087ff), //7x7
    EPIC("epic", 480_000_000, 4, TextFormatting.DARK_PURPLE, 0xe100ff), //9x9
    LEGENDARY("legendary", 3_360_000_000L, 5, TextFormatting.GOLD, 0xffaa00), //11x11
    ARTIFACT("artifact", Long.MAX_VALUE, 6, TextFormatting.YELLOW, 0xfff887); //13x13

    private final String name;
    private final TextFormatting color;
    private final int textureColor;
    private long powerNeeded;
    private int radius;

    InfinityTier(String name, long powerNeeded, int radius, TextFormatting color, int textureColor) {
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
        return new TranslationTextComponent("text.industrialforegoing.tooltip.infinitydrill." + name).getUnformattedComponentText();
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

    public TextFormatting getColor() {
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


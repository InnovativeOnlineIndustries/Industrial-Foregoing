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

package com.buuz135.industrial.recipe;

import com.hrznstudio.titanium.network.CompoundSerializableDataHandler;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.neoforge.common.Tags;

import javax.annotation.Nullable;
import java.util.List;

public record LaserDrillRarity(BiomeRarity biomeRarity, DimensionRarity dimensionRarity, int depth_min, int depth_max,
                               int weight) {

    public static final Codec<LaserDrillRarity> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(laserDrillRarityInstance ->
            laserDrillRarityInstance.group(
                    BiomeRarity.CODEC.fieldOf("biome_filter").forGetter(o -> o.biomeRarity),
                    DimensionRarity.CODEC.fieldOf("dimension_filter").forGetter(o -> o.dimensionRarity),
                    Codec.INT.fieldOf("depth_min").forGetter(o -> o.depth_min),
                    Codec.INT.fieldOf("depth_max").forGetter(o -> o.depth_max),
                    Codec.INT.fieldOf("weight").forGetter(o -> o.weight)
            ).apply(laserDrillRarityInstance, LaserDrillRarity::new)));

    public static void init() {
        CompoundSerializableDataHandler.map(LaserDrillRarity.class, buf -> {
            return buf.readJsonWithCodec(CODEC);
        }, (buf, laserDrillRarity) -> {
            buf.writeJsonWithCodec(CODEC, laserDrillRarity);
        });
    }

    @Nullable
    public static LaserDrillRarity getValidRarity(Level level, List<LaserDrillRarity> rarities, DimensionType dimensionType, Holder<Biome> biome, int height) {
        for (LaserDrillRarity laserDrillRarity : rarities) {
            if (laserDrillRarity.depth_max() >= height && laserDrillRarity.depth_min() <= height) {
                if (checkForBiome(laserDrillRarity, biome) && checkForDimension(level, laserDrillRarity, dimensionType))
                    return laserDrillRarity;
            }
        }
        return null;
    }

    private static boolean checkForDimension(Level level, LaserDrillRarity rarity, DimensionType dimensionType) {
        if (dimensionType == null) return false;
        var registry = level.registryAccess().registry(Registries.DIMENSION_TYPE).orElse(null);
        if (registry == null) return false;
        return rarity.dimensionRarity().whitelist().isEmpty() ?
                rarity.dimensionRarity().blacklist().stream().noneMatch(dimensionTypeResourceKey -> registry.get(dimensionTypeResourceKey).equals(dimensionType))
                : rarity.dimensionRarity().whitelist().stream().anyMatch(dimensionTypeResourceKey -> registry.get(dimensionTypeResourceKey).equals(dimensionType));
    }

    private static boolean checkForBiome(LaserDrillRarity rarity, Holder<Biome> biome) {
        return rarity.biomeRarity().whitelist().isEmpty() ? rarity.biomeRarity().blacklist().stream().noneMatch(biome::is) : rarity.biomeRarity().whitelist().stream().anyMatch(biome::is);
    }

    public record BiomeRarity(List<TagKey<Biome>> whitelist, List<TagKey<Biome>> blacklist) {

        public static final MapCodec<LaserDrillRarity.BiomeRarity> CODEC = RecordCodecBuilder.mapCodec(in -> in.group(
                TagKey.codec(Registries.BIOME).listOf().fieldOf("whitelist").forGetter(o -> o.whitelist),
                TagKey.codec(Registries.BIOME).listOf().fieldOf("blacklist").forGetter(o -> o.blacklist)
        ).apply(in, LaserDrillRarity.BiomeRarity::new));

        public static List<TagKey<Biome>> END = List.of(BiomeTags.IS_END);
        public static List<TagKey<Biome>> NETHER = List.of(BiomeTags.IS_NETHER);
        public static List<TagKey<Biome>> OIL = List.of(BiomeTags.IS_OCEAN, Tags.Biomes.IS_DESERT);

    }

    public record DimensionRarity(List<ResourceKey<DimensionType>> whitelist,
                                  List<ResourceKey<DimensionType>> blacklist) {

        public static final MapCodec<LaserDrillRarity.DimensionRarity> CODEC = RecordCodecBuilder.mapCodec(in -> in.group(
                ResourceKey.codec(Registries.DIMENSION_TYPE).listOf().fieldOf("whitelist").forGetter(o -> o.whitelist),
                ResourceKey.codec(Registries.DIMENSION_TYPE).listOf().fieldOf("blacklist").forGetter(o -> o.blacklist)
        ).apply(in, LaserDrillRarity.DimensionRarity::new));

    }
}

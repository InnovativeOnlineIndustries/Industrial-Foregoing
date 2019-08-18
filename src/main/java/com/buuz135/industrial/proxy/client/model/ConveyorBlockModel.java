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
package com.buuz135.industrial.proxy.client.model;

import com.buuz135.industrial.api.conveyor.ConveyorUpgrade;
import com.buuz135.industrial.block.BlockConveyor;
import com.buuz135.industrial.module.ModuleTransport;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.*;

public class ConveyorBlockModel implements IDynamicBakedModel {

    public static Cache<Pair<Pair<String, Pair<Direction, Direction>>, Direction>, List<BakedQuad>> CACHE = CacheBuilder.newBuilder().build();
    private IModelState state;
    private VertexFormat format;
    private IBakedModel previousConveyor;
    private Map<Direction, List<BakedQuad>> prevQuads = new HashMap<>();

    public ConveyorBlockModel(IBakedModel previousConveyor) {
        this.previousConveyor = previousConveyor;
        this.state = TRSRTransformation.identity();
        this.format = DefaultVertexFormats.BLOCK;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        if (state == null) {
            if (!prevQuads.containsKey(side))
                prevQuads.put(side, previousConveyor.getQuads(state, side, rand));
            return prevQuads.get(side);
        }
        if (!prevQuads.containsKey(side))
            prevQuads.put(side, previousConveyor.getQuads(state, side, rand));
        List<BakedQuad> quads = new ArrayList<>(prevQuads.get(side));
        if (extraData.hasProperty(ConveyorModelData.UPGRADE_PROPERTY)) {
            for (ConveyorUpgrade upgrade : extraData.getData(ConveyorModelData.UPGRADE_PROPERTY).getUpgrades().values()) {
                if (upgrade == null)
                    continue;
                List<BakedQuad> upgradeQuads = CACHE.getIfPresent(Pair.of(Pair.of(upgrade.getFactory().getRegistryName().toString(), Pair.of(upgrade.getSide(), state.get(BlockConveyor.FACING))), side));
                if (upgradeQuads == null) {
                    try {
                        IBakedModel model = ModuleTransport.CONVEYOR_UPGRADES_CACHE.get(upgrade.getFactory().getModel(upgrade.getSide(), state.get(BlockConveyor.FACING)));
                        upgradeQuads = model.getQuads(state, side, rand, extraData);
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                    CACHE.put(Pair.of(Pair.of(upgrade.getFactory().getRegistryName().toString(), Pair.of(upgrade.getSide(), state.get(BlockConveyor.FACING))), side), upgradeQuads);
                }
                if (!upgradeQuads.isEmpty()) {
                    quads.addAll(upgradeQuads);
                }
            }
        }
        return quads;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return previousConveyor.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return previousConveyor.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return previousConveyor.isBuiltInRenderer();
    }

    @Nonnull
    @Override
    public TextureAtlasSprite getParticleTexture() {
        return previousConveyor.getParticleTexture();
    }

    @Nonnull
    @Override
    public ItemOverrideList getOverrides() {
        return previousConveyor.getOverrides();
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        return previousConveyor.handlePerspective(cameraTransformType);
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return previousConveyor.getItemCameraTransforms();
    }
}
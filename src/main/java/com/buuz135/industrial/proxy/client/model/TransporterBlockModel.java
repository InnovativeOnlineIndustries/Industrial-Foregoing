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

package com.buuz135.industrial.proxy.client.model;

import com.buuz135.industrial.api.transporter.TransporterType;
import com.buuz135.industrial.api.transporter.TransporterTypeFactory;
import com.buuz135.industrial.module.ModuleTransportStorage;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.blaze3d.matrix.MatrixStack;
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
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class TransporterBlockModel implements IDynamicBakedModel {

    public static Cache<Pair<Pair<String, Pair<Direction, TransporterTypeFactory.TransporterAction>>, Direction>, List<BakedQuad>> CACHE = CacheBuilder.newBuilder().build();
    private VertexFormat format;
    private IBakedModel previousModel;
    private Map<Direction, List<BakedQuad>> prevQuads = new HashMap<>();

    public TransporterBlockModel(IBakedModel previousConveyor) {
        this.previousModel = previousConveyor;
        this.format = DefaultVertexFormats.BLOCK;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        CACHE.invalidateAll();
        if (state == null) {
            if (!prevQuads.containsKey(side))
                prevQuads.put(side, previousModel.getQuads(state, side, rand));
            return prevQuads.get(side);
        }
        if (!prevQuads.containsKey(side))
            prevQuads.put(side, previousModel.getQuads(state, side, rand));
        List<BakedQuad> quads = new ArrayList<>(prevQuads.get(side));
        if (extraData.hasProperty(TransporterModelData.UPGRADE_PROPERTY)) {
            for (TransporterType upgrade : extraData.getData(TransporterModelData.UPGRADE_PROPERTY).getUpgrades().values()) {
                if (upgrade == null)
                    continue;
                List<BakedQuad> upgradeQuads = CACHE.getIfPresent(Pair.of(Pair.of(upgrade.getFactory().getRegistryName().toString(), Pair.of(upgrade.getSide(), upgrade.getAction())), side));
                if (upgradeQuads == null) {
                    try {
                        IBakedModel model = ModuleTransportStorage.TRANSPORTER_CACHE.get(upgrade.getFactory().getModel(upgrade.getSide(), upgrade.getAction()));
                        upgradeQuads = model.getQuads(state, side, rand, extraData);
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                    CACHE.put(Pair.of(Pair.of(upgrade.getFactory().getRegistryName().toString(), Pair.of(upgrade.getSide(), upgrade.getAction())), side), upgradeQuads);
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
        return previousModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return previousModel.isGui3d();
    }

    @Override
    public boolean isSideLit() {
        return previousModel.isSideLit();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return previousModel.isBuiltInRenderer();
    }

    @Nonnull
    @Override
    public TextureAtlasSprite getParticleTexture() {
        return previousModel.getParticleTexture();
    }

    @Nonnull
    @Override
    public ItemOverrideList getOverrides() {
        return previousModel.getOverrides();
    }

    @Override
    public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {
        return previousModel.handlePerspective(cameraTransformType, mat);
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return previousModel.getItemCameraTransforms();
    }
}
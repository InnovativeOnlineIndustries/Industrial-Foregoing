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
import com.buuz135.industrial.proxy.block.BlockConveyor;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConveyorBlockModel implements IBakedModel {

    public static Cache<Pair<Pair<String, Pair<EnumFacing, EnumFacing>>, EnumFacing>, List<BakedQuad>> CACHE = CacheBuilder.newBuilder().build();
    private IModelState state;
    private VertexFormat format;
    private IBakedModel previousConveyor;
    private Map<EnumFacing, List<BakedQuad>> prevQuads = new HashMap<>();

    public ConveyorBlockModel(IBakedModel previousConveyor) {
        this.previousConveyor = previousConveyor;
        this.state = TRSRTransformation.identity();
        this.format = DefaultVertexFormats.BLOCK;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (state == null || !(state instanceof IExtendedBlockState)) {
            if (!prevQuads.containsKey(side))
                prevQuads.put(side, previousConveyor.getQuads(state, side, rand));
            return prevQuads.get(side);
        }
        if (!prevQuads.containsKey(side))
            prevQuads.put(side, previousConveyor.getQuads(state, side, rand));
        ConveyorModelData data = ((IExtendedBlockState) state).getValue(ConveyorModelData.UPGRADE_PROPERTY);
        List<BakedQuad> quads = new ArrayList<>(prevQuads.get(side));
        for (ConveyorUpgrade upgrade : data.getUpgrades().values()) {
            if (upgrade == null)
                continue;
            List<BakedQuad> upgradeQuads = CACHE.getIfPresent(Pair.of(Pair.of(upgrade.getFactory().getRegistryName().toString(), Pair.of(upgrade.getSide(), state.getValue(BlockConveyor.FACING))), side));
            if (upgradeQuads == null) {
                try {
                    IModel model = ModelLoaderRegistry.getModel(upgrade.getFactory().getModel(upgrade.getSide(), state.getValue(BlockConveyor.FACING)));
                    upgradeQuads = model.bake(this.state, this.format, location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString())).getQuads(state, side, rand);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                CACHE.put(Pair.of(Pair.of(upgrade.getFactory().getRegistryName().toString(), Pair.of(upgrade.getSide(), state.getValue(BlockConveyor.FACING))), side), upgradeQuads);
            }
            if (!upgradeQuads.isEmpty()) {
                quads.addAll(upgradeQuads);
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
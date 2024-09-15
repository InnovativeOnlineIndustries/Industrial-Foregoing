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

import com.buuz135.industrial.api.conveyor.ConveyorUpgrade;
import com.buuz135.industrial.block.transportstorage.ConveyorBlock;
import com.buuz135.industrial.module.ModuleTransportStorage;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConveyorBlockModel extends BakedModelWrapper<BakedModel> {

    public static Cache<Pair<Pair<String, Pair<Direction, Direction>>, Direction>, List<BakedQuad>> CACHE = CacheBuilder.newBuilder().build();

    private Map<Direction, List<BakedQuad>> prevQuads = new HashMap<>();

    public ConveyorBlockModel(BakedModel previousConveyor) {
        super(previousConveyor);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand, @Nonnull ModelData extraData, RenderType renderType) {
        if (state == null) {
            if (!prevQuads.containsKey(side))
                prevQuads.put(side, originalModel.getQuads(state, side, rand));
            return prevQuads.get(side);
        }
        if (!prevQuads.containsKey(side))
            prevQuads.put(side, originalModel.getQuads(state, side, rand));
        List<BakedQuad> quads = new ArrayList<>(prevQuads.get(side));
        if (extraData.has(ConveyorModelData.UPGRADE_PROPERTY)) {
            for (ConveyorUpgrade upgrade : extraData.get(ConveyorModelData.UPGRADE_PROPERTY).getUpgrades().values()) {
                if (upgrade == null)
                    continue;
                List<BakedQuad> upgradeQuads = CACHE.getIfPresent(Pair.of(Pair.of(upgrade.getFactory().getName(), Pair.of(upgrade.getSide(), state.getValue(ConveyorBlock.FACING))), side));
                if (upgradeQuads == null) {
                    try {
                        BakedModel model = ModuleTransportStorage.CONVEYOR_UPGRADES_CACHE.get(upgrade.getFactory().getModel(upgrade.getSide(), state.getValue(ConveyorBlock.FACING)));
                        upgradeQuads = model.getQuads(state, side, rand, extraData, renderType);
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                    CACHE.put(Pair.of(Pair.of(upgrade.getFactory().getName(), Pair.of(upgrade.getSide(), state.getValue(ConveyorBlock.FACING))), side), upgradeQuads);
                }
                if (!upgradeQuads.isEmpty()) {
                    quads.addAll(upgradeQuads);
                }
            }
        }
        return quads;
    }

}
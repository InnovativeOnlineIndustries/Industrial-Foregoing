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
//package com.buuz135.industrial.plugin;
//
//import com.buuz135.industrial.utils.BlockUtils;
//import com.hrznstudio.titanium.annotation.plugin.FeaturePlugin;
//import com.hrznstudio.titanium.plugin.FeaturePluginInstance;
//import com.hrznstudio.titanium.plugin.PluginPhase;
//import dev.ftb.mods.ftbchunks.data.FTBChunksAPI;
//import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
//
//@FeaturePlugin(value = "ftbchunks", type = FeaturePlugin.FeaturePluginType.MOD)
//public class FTBChunksPlugin implements FeaturePluginInstance {
//    @Override
//    public void execute(PluginPhase phase) {
//        if (phase == PluginPhase.CONSTRUCTION) {
//            BlockUtils.CLAIMED_CHUNK_CHECKER = (world, pos) -> FTBChunksAPI.getManager().getChunk(new ChunkDimPos(world, pos)) == null;
//        }
//    }
//}

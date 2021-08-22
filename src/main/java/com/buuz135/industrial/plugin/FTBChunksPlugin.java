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

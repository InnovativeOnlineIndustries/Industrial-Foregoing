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
package com.buuz135.industrial.utils.explosion;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.utils.BlockUtils;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.lighting.LevelLightEngine;

import java.util.*;

/**
 * Class copied and adapted from Draconic Evolution https:github.com/brandon3055/Draconic-Evolution/blob/master/src/main/java/com/brandon3055/draconicevolution/lib/ExplosionHelper.java
 */
public class ExplosionHelper {

    private static final BlockState AIR = Blocks.AIR.defaultBlockState();
    private final ServerLevel serverWorld;
    //private Map<Integer, LinkedHashSet<Integer>> radialRemovalMap = new HashMap<>();
    public LinkedList<HashSet<Long>> toRemove = new LinkedList<>();
    private BlockPos start;
    //private HashSet<LevelChunk> modifiedChunks = new HashSet<>();
    private HashSet<Long> blocksToUpdate = new HashSet<>();
    private HashSet<Long> lightUpdates = new HashSet<>();
    private HashSet<Long> tilesToRemove = new HashSet<>();
    private HashMap<ChunkPos, LevelChunk> chunkCache = new HashMap<>();

    public ExplosionHelper(ServerLevel serverWorld, BlockPos start) {
        this.serverWorld = serverWorld;
        this.start = start;
    }

    public void setBlocksForRemoval(LinkedList<HashSet<Long>> list) {
        this.toRemove = list;
    }

    public void addBlocksForUpdate(Collection<Long> blocksToUpdate) {
        this.blocksToUpdate.addAll(blocksToUpdate);
    }

    private LevelChunk removeBlock(BlockPos pos) {
        LevelChunk chunk = getChunk(pos);
        BlockState oldState = chunk.getBlockState(pos);
        
        if (oldState.getBlock() instanceof EntityBlock) {
            serverWorld.removeBlock(pos, false);
            serverWorld.getLightEngine().checkBlock(pos);
            return chunk;
        }

        LevelChunkSection storage = getBlockStorage(pos);
        if (storage != null) {
            storage.setBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, AIR);
        }
        setChunkModified(pos);
        serverWorld.getLightEngine().checkBlock(pos);
        return chunk;
    }

    public void setChunkModified(BlockPos blockPos) {
        LevelChunk chunk = getChunk(blockPos);
        setChunkModified(chunk);
    }

    public void setChunkModified(LevelChunk chunk) {
        //modifiedChunks.add(chunk);
    }

    private LevelChunk getChunk(BlockPos pos) {
        ChunkPos cp = new ChunkPos(pos);
        if (!chunkCache.containsKey(cp)) {
            chunkCache.put(cp, serverWorld.getChunk(pos.getX() >> 4, pos.getZ() >> 4));
        }
        return chunkCache.get(cp);
    }

    private LevelChunkSection getBlockStorage(BlockPos pos) {
        LevelChunk chunk = getChunk(pos);
        //return chunk.getSection(chunk.getSectionIndexFromSectionY(pos.getY()));
        return chunk.getSections()[(pos.getY() - serverWorld.getMinBuildHeight()) >> 4];
    }

    /**
     * Call when finished removing blocks to calculate lighting and send chunk updates to the client.
     */
    public void finish() {
        IndustrialForegoing.LOGGER.debug("EH: finish");
        RemovalProcess process = new RemovalProcess(this);
        ExplosionTickHandler.removalProcessList.add(process);
    }

    public boolean isAirBlock(BlockPos pos) {
        return serverWorld.isEmptyBlock(pos);
    }

    public BlockState getBlockState(BlockPos pos) {
        LevelChunkSection storage = getBlockStorage(pos);
        if (storage == null) {
            return Blocks.AIR.defaultBlockState();
        }
        return storage.getBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
    }

    public static class RemovalProcess {

        public boolean isDead = false;
        int index = 0;
        private ExplosionHelper helper;
        private MinecraftServer server;
        private int blocksToUpdatePointer = 0;
        private List<Long> blocksToRmv = new ArrayList<>();
        private long start;

        public RemovalProcess(ExplosionHelper helper) {
            this.helper = helper;
            this.server = helper.serverWorld.getServer();
            this.start = System.currentTimeMillis();
        }

        public void updateProcess() {
            long startTime = Util.getMillis();
            HashSet<LevelChunk> chunks = new HashSet<>();
            while (Util.getMillis() - startTime < 40 && helper.toRemove.size() > 0) {
                IndustrialForegoing.LOGGER.debug("Processing chunks at rad: " + index);
                HashSet<Long> set = helper.toRemove.removeFirst();
                for (long pos : set) {
                    BlockPos blockPos = BlockPos.of(pos);
                    if (BlockUtils.canBlockBeBrokenPlugin(helper.serverWorld, blockPos)) {
                        chunks.add(helper.removeBlock(blockPos));
                    }

                }
                index++;
            }
            finishChunks(chunks);

            if (helper.toRemove.isEmpty()) {
                if (blocksToRmv.isEmpty()) {
                    blocksToRmv = new ArrayList<>(helper.blocksToUpdate);
                }
                if (blocksToUpdatePointer < helper.blocksToUpdate.size()) {
                    updateBlocks();
                } else {
                    IndustrialForegoing.LOGGER.info("Explosion Completed in " + (System.currentTimeMillis() - start) / 1000 + "s");
                    isDead = true;
                    IndustrialForegoing.LOGGER.info("Explosion done");
                }

            }
        }

        public void finishChunks(HashSet<LevelChunk> chunks) {
            for (LevelChunk chunk : chunks) {
                chunk.setUnsaved(true);
                ThreadedLevelLightEngine lightManager = (ThreadedLevelLightEngine) helper.serverWorld.getLightEngine();
                lightManager.lightChunk(chunk, false)
                        .thenRun(() -> helper.serverWorld.getChunkSource().chunkMap.getPlayers(chunk.getPos(), false)
                                .forEach(e -> e.connection.send(new ClientboundLightUpdatePacket(chunk.getPos(), helper.serverWorld.getLightEngine(), null , null, true))));
                //LevelLightEngine lightManager = helper.serverWorld.getLightEngine();
                helper.serverWorld.getChunkSource().chunkMap.getPlayers(chunk.getPos(), false)
                        .forEach(e -> e.connection.send(new ClientboundLevelChunkWithLightPacket(chunk, lightManager,null  , null , true)));
                //ClientboundLevelChunkPacketData packet = new ClientboundLevelChunkPacketData(chunk);
                //helper.serverWorld.getChunkSource().chunkMap.getPlayers(chunk.getPos(), false).forEach(e -> e.connection.send((Packet<?>) packet));
            }
        }

        private void updateBlocks() {
            IndustrialForegoing.LOGGER.debug("Updating Blocks");
            int amount = 1000;
            for (int i = 0; i < amount; i++) {
                if (blocksToUpdatePointer + i < helper.blocksToUpdate.size()) {
                    try {
                        long pos = blocksToRmv.get(blocksToUpdatePointer + i);
                        BlockPos blockPos = BlockPos.of(pos);
                        BlockState state = helper.serverWorld.getBlockState(blockPos);
                        if (state.getBlock() instanceof FallingBlock) {
                            state.getBlock().tick(state, helper.serverWorld, blockPos, helper.serverWorld.random);
                        }
                        state.neighborChanged(helper.serverWorld, blockPos, Blocks.AIR, blockPos.above(), false);
                    } catch (Throwable e) {
                        IndustrialForegoing.LOGGER.error(e);
                    }
                }
            }
            blocksToUpdatePointer += amount;
        }

        public boolean isDead() {
            return isDead;
        }
    }
}

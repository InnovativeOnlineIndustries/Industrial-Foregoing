package com.buuz135.industrial.utils.explosion;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.utils.BlockUtils;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;

import java.util.*;

/**
 * Class copied and adapted from Draconic Evolution https://github.com/brandon3055/Draconic-Evolution/blob/master/src/main/java/com/brandon3055/draconicevolution/lib/ExplosionHelper.java
 */
public class ExplosionHelper {

    private static final BlockState AIR = Blocks.AIR.defaultBlockState();
    private final ServerLevel serverWorld;
    //    private Map<Integer, LinkedHashList<Integer>> radialRemovalMap = new HashMap<>();
    public LinkedList<HashSet<Integer>> toRemove = new LinkedList<>();
    private BlockPos start;
    private ShortPos shortPos;
    //private HashSet<Chunk> modifiedChunks = new HashSet<>();
    private HashSet<Integer> blocksToUpdate = new HashSet<>();
    private HashSet<Integer> lightUpdates = new HashSet<>();
    private HashSet<Integer> tilesToRemove = new HashSet<>();
    private HashMap<ChunkPos, LevelChunk> chunkCache = new HashMap<>();

    public ExplosionHelper(ServerLevel serverWorld, BlockPos start, ShortPos shortPos) {
        this.serverWorld = serverWorld;
        this.start = start;
        this.shortPos = shortPos;
    }

    public void setBlocksForRemoval(LinkedList<HashSet<Integer>> list) {
        this.toRemove = list;
    }

    public void addBlocksForUpdate(Collection<Integer> blocksToUpdate) {
        this.blocksToUpdate.addAll(blocksToUpdate);
    }

    private LevelChunk removeBlock(BlockPos pos) {
        LevelChunk chunk = getChunk(pos);
        BlockState oldState = chunk.getBlockState(pos);

        // TODO: 22/08/2021 hasTileentity.
//        if (oldState.getBlock().hasTileEntity(oldState)) {
//            serverWorld.removeBlock(pos, false);
//            serverWorld.getLightEngine().checkBlock(pos);
//            return chunk;
//        }

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
        return chunk.getSections()[pos.getY() >> 4];
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
        private List<Integer> blocksToRmv = new ArrayList<>();
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
                HashSet<Integer> set = helper.toRemove.removeFirst();
                for (int pos : set) {
                    BlockPos blockPos = helper.shortPos.getActualPos(pos);
                    if (BlockUtils.canBlockBeBrokenPlugin(helper.serverWorld, blockPos)) {
                        chunks.add(helper.removeBlock(helper.shortPos.getActualPos(pos)));
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
//                lightManager.lightChunk(chunk, false)
//                        .thenRun(() -> helper.serverWorld.getChunkSource().chunkMap.getPlayers(chunk.getPos(), false)
//                                .forEach(e -> e.connection.send(new ClientboundLightUpdatePacket(chunk.getPos(), helper.serverWorld.getLightEngine(), true))));
//
//                ClientboundLevelChunkPacket packet = new ClientboundLevelChunkPacket(chunk, 65535);
//                helper.serverWorld.getChunkSource().chunkMap.getPlayers(chunk.getPos(), false).forEach(e -> e.connection.send(packet));
            }
        }

        private void updateBlocks() {
            IndustrialForegoing.LOGGER.debug("Updating Blocks");
            int amount = 1000;
            for (int i = 0; i < amount; i++) {
                if (blocksToUpdatePointer + i < helper.blocksToUpdate.size()) {
                    try {
                        int pos = blocksToRmv.get(blocksToUpdatePointer + i);
                        BlockState state = helper.serverWorld.getBlockState(helper.shortPos.getActualPos(pos));
                        if (state.getBlock() instanceof FallingBlock) {
                            state.getBlock().tick(state, helper.serverWorld, helper.shortPos.getActualPos(pos), helper.serverWorld.random);
                        }
                        state.neighborChanged(helper.serverWorld, helper.shortPos.getActualPos(pos), Blocks.AIR, helper.shortPos.getActualPos(pos).above(), false);
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

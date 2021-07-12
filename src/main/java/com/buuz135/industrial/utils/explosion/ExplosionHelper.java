package com.buuz135.industrial.utils.explosion;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.utils.BlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.network.play.server.SChunkDataPacket;
import net.minecraft.network.play.server.SUpdateLightPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.ServerWorldLightManager;

import java.util.*;

/**
 * Class copied and adapted from Draconic Evolution https://github.com/brandon3055/Draconic-Evolution/blob/master/src/main/java/com/brandon3055/draconicevolution/lib/ExplosionHelper.java
 */
public class ExplosionHelper {

    private static final BlockState AIR = Blocks.AIR.getDefaultState();
    private final ServerWorld serverWorld;
    //    private Map<Integer, LinkedHashList<Integer>> radialRemovalMap = new HashMap<>();
    public LinkedList<HashSet<Integer>> toRemove = new LinkedList<>();
    private BlockPos start;
    private ShortPos shortPos;
    //private HashSet<Chunk> modifiedChunks = new HashSet<>();
    private HashSet<Integer> blocksToUpdate = new HashSet<>();
    private HashSet<Integer> lightUpdates = new HashSet<>();
    private HashSet<Integer> tilesToRemove = new HashSet<>();
    private HashMap<ChunkPos, Chunk> chunkCache = new HashMap<>();

    public ExplosionHelper(ServerWorld serverWorld, BlockPos start, ShortPos shortPos) {
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

    private Chunk removeBlock(BlockPos pos) {
        Chunk chunk = getChunk(pos);
        BlockState oldState = chunk.getBlockState(pos);

        if (oldState.getBlock().hasTileEntity(oldState)) {
            serverWorld.removeBlock(pos, false);
            serverWorld.getLightManager().checkBlock(pos);
            return chunk;
        }

        ChunkSection storage = getBlockStorage(pos);
        if (storage != null) {
            storage.setBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, AIR);
        }
        setChunkModified(pos);
        serverWorld.getLightManager().checkBlock(pos);
        return chunk;
    }

    public void setChunkModified(BlockPos blockPos) {
        Chunk chunk = getChunk(blockPos);
        setChunkModified(chunk);
    }

    public void setChunkModified(Chunk chunk) {
        //modifiedChunks.add(chunk);
    }

    private Chunk getChunk(BlockPos pos) {
        ChunkPos cp = new ChunkPos(pos);
        if (!chunkCache.containsKey(cp)) {
            chunkCache.put(cp, serverWorld.getChunk(pos.getX() >> 4, pos.getZ() >> 4));
        }
        return chunkCache.get(cp);
    }

    private ChunkSection getBlockStorage(BlockPos pos) {
        Chunk chunk = getChunk(pos);
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
        return serverWorld.isAirBlock(pos);
    }

    public BlockState getBlockState(BlockPos pos) {
        ChunkSection storage = getBlockStorage(pos);
        if (storage == null) {
            return Blocks.AIR.getDefaultState();
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
            long startTime = Util.milliTime();
            HashSet<Chunk> chunks = new HashSet<>();
            while (Util.milliTime() - startTime < 40 && helper.toRemove.size() > 0) {
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

        public void finishChunks(HashSet<Chunk> chunks) {
            for (Chunk chunk : chunks) {
                chunk.setModified(true);
                ServerWorldLightManager lightManager = (ServerWorldLightManager) helper.serverWorld.getLightManager();
                lightManager.lightChunk(chunk, false)
                        .thenRun(() -> helper.serverWorld.getChunkProvider().chunkManager.getTrackingPlayers(chunk.getPos(), false)
                                .forEach(e -> e.connection.sendPacket(new SUpdateLightPacket(chunk.getPos(), helper.serverWorld.getLightManager(), true))));

                SChunkDataPacket packet = new SChunkDataPacket(chunk, 65535);
                helper.serverWorld.getChunkProvider().chunkManager.getTrackingPlayers(chunk.getPos(), false).forEach(e -> e.connection.sendPacket(packet));
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
                            state.getBlock().tick(state, helper.serverWorld, helper.shortPos.getActualPos(pos), helper.serverWorld.rand);
                        }
                        state.neighborChanged(helper.serverWorld, helper.shortPos.getActualPos(pos), Blocks.AIR, helper.shortPos.getActualPos(pos).up(), false);
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
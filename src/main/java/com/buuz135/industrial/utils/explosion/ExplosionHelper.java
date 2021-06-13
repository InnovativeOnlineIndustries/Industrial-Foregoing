package com.buuz135.industrial.utils.explosion;

import com.buuz135.industrial.IndustrialForegoing;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

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
    private HashSet<Chunk> modifiedChunks = new HashSet<>();
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

    private void removeBlock(BlockPos pos) {
        Chunk chunk = getChunk(pos);
        BlockState oldState = chunk.getBlockState(pos);

        if (oldState.getBlock().hasTileEntity(oldState)) {
            serverWorld.removeBlock(pos, false);

//            PlayerChunkMap playerChunkMap = serverWorld.getPlayerChunkMap();
//            if (playerChunkMap != null) {
//                PlayerChunkMapEntry watcher = playerChunkMap.getEntry(pos.getX() >> 4, pos.getZ() >> 4);
//                if (watcher != null) {
//                    watcher.sendPacket(new SPacketBlockChange(serverWorld, pos));
//                }
//            }

            serverWorld.getLightManager().checkBlock(pos);
            return;
        }

        ChunkSection storage = getBlockStorage(pos);
        if (storage != null) {
            storage.setBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, AIR);
        }
        setChunkModified(pos);
        serverWorld.getLightManager().checkBlock(pos);
    }

    public void setChunkModified(BlockPos blockPos) {
        Chunk chunk = getChunk(blockPos);
        setChunkModified(chunk);
    }

    public void setChunkModified(Chunk chunk) {
        modifiedChunks.add(chunk);
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

        public RemovalProcess(ExplosionHelper helper) {
            this.helper = helper;
            this.server = helper.serverWorld.getServer();
        }

        public void updateProcess() {
            long startTime = Util.milliTime();
            while (Util.milliTime() - startTime < 50 && helper.toRemove.size() > 0) {
                IndustrialForegoing.LOGGER.debug("Processing chunks at rad: " + index);
                HashSet<Integer> set = helper.toRemove.removeFirst();
                for (int pos : set) {
                    helper.removeBlock(helper.shortPos.getActualPos(pos));
                }
                index++;
            }
            finishChunks();

            if (helper.toRemove.isEmpty()) {
                isDead = true;
                updateBlocks();
            }
        }

        public void finishChunks() {
            for (Chunk chunk : helper.modifiedChunks) {
                chunk.setModified(true);
                ServerWorldLightManager lightManager = (ServerWorldLightManager) helper.serverWorld.getLightManager();
                lightManager.lightChunk(chunk, false)
                        .thenRun(() -> helper.serverWorld.getChunkProvider().chunkManager.getTrackingPlayers(chunk.getPos(), false)
                                .forEach(e -> e.connection.sendPacket(new SUpdateLightPacket(chunk.getPos(), helper.serverWorld.getLightManager(), true))));

                SChunkDataPacket packet = new SChunkDataPacket(chunk, 65535);
                helper.serverWorld.getChunkProvider().chunkManager.getTrackingPlayers(chunk.getPos(), false).forEach(e -> e.connection.sendPacket(packet));
            }

            helper.modifiedChunks.clear();
        }

        private void updateBlocks() {
            IndustrialForegoing.LOGGER.debug("Updating Blocks");

            try {
                IndustrialForegoing.LOGGER.debug("Updating " + helper.blocksToUpdate.size() + " Blocks");
                for (int pos : helper.blocksToUpdate) {
                    BlockState state = helper.serverWorld.getBlockState(helper.shortPos.getActualPos(pos));
                    if (state.getBlock() instanceof FallingBlock) {
                        state.getBlock().tick(state, helper.serverWorld, helper.shortPos.getActualPos(pos), helper.serverWorld.rand);
                    }
                    state.neighborChanged(helper.serverWorld, helper.shortPos.getActualPos(pos), Blocks.AIR, helper.shortPos.getActualPos(pos).up(), false);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }


        public boolean isDead() {
            return isDead;
        }
    }
}
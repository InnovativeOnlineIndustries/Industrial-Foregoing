package com.buuz135.industrial.block_network;

import com.buuz135.industrial.block.transportstorage.tile.PowerCrystalTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.*;

/**
 * A lightweight, in-memory graph helper to compute Power Crystal networks based on tiles' connection lists.
 * <p>
 * The graph is undirected: an edge exists between A and B if either A lists B or B lists A in their connections.
 * This class does not persist anything; it derives connected components on demand from the currently loaded tiles.
 */
public class PowerCrystalNetwork implements INBTSerializable<CompoundTag> {

    private Set<BlockPos> members;
    private String id;
    private long power;

    public PowerCrystalNetwork(Set<BlockPos> members) {
        this.members = new HashSet<>(members);
        this.id = idFromMembers(this.members);
        this.power = 0;
    }

    public static PowerCrystalNetwork build(ServerLevel level, BlockPos start) {
        return new PowerCrystalNetwork(getComponent(level, start));
    }

    /**
     * Returns the set of BlockPos that form the connected component containing start.
     * If the start position does not contain a PowerCrystalTile, returns a singleton set with start.
     */
    public static Set<BlockPos> getComponent(ServerLevel level, BlockPos start) {
        var startBe = level.getBlockEntity(start);
        if (!(startBe instanceof PowerCrystalTile)) {
            return Collections.singleton(start);
        }

        Set<BlockPos> visited = new HashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        visited.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            BlockPos current = queue.removeFirst();
            var currentBe = level.getBlockEntity(current);
            if (!(currentBe instanceof PowerCrystalTile currentTile)) continue;

            // Outgoing neighbors (explicit connections)
            for (BlockPos neighbor : new ArrayList<>(currentTile.getConnections())) {
                if (!visited.contains(neighbor) && isCrystal(level, neighbor) && areConnected(level, current, neighbor)) {
                    visited.add(neighbor);
                    queue.addLast(neighbor);
                }
            }

            // Incoming neighbors (tiles that reference this tile)
            // Since we don't have a global index, we conservatively search over the neighbors referenced by our outgoing neighbors as frontier.
            // However, to ensure undirected behavior without world scans, we enforce bidirectional links when creating connections.
            // For safety, still check the explicitly linked tiles' connections for back-links to discover further nodes.
            for (BlockPos neighbor : new ArrayList<>(currentTile.getConnections())) {
                var be2 = level.getBlockEntity(neighbor);
                if (be2 instanceof PowerCrystalTile neighborTile) {
                    for (BlockPos back : new ArrayList<>(neighborTile.getConnections())) {
                        if (!visited.contains(back) && isCrystal(level, back) && areConnected(level, neighbor, back)) {
                            visited.add(back);
                            queue.addLast(back);
                        }
                    }
                }
            }
        }
        return visited;
    }

    /**
     * Creates a stable network id string for the component that contains start.
     * Uses the lexicographically smallest position in the component as the id seed.
     */
    public static String getNetworkId(ServerLevel level, BlockPos start) {
        Set<BlockPos> component = getComponent(level, start);
        BlockPos seed = component.stream().min(PowerCrystalNetwork::comparePos).orElse(start);
        // Stable, human-readable id: x_y_z
        return seed.getX() + ":" + seed.getY() + ":" + seed.getZ();
    }

    private static String idFromMembers(Set<BlockPos> members) {
        BlockPos seed = members.stream().min(PowerCrystalNetwork::comparePos).orElse(BlockPos.ZERO);
        return seed.getX() + ":" + seed.getY() + ":" + seed.getZ();
    }

    /**
     * Compare positions deterministically.
     */
    private static int comparePos(BlockPos a, BlockPos b) {
        if (a.getX() != b.getX()) return Integer.compare(a.getX(), b.getX());
        if (a.getY() != b.getY()) return Integer.compare(a.getY(), b.getY());
        return Integer.compare(a.getZ(), b.getZ());
    }

    /**
     * True if a BlockPos currently contains a PowerCrystalTile.
     */
    private static boolean isCrystal(ServerLevel level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof PowerCrystalTile;
    }

    /**
     * Two tiles are considered connected if either side references the other in their connections list.
     */
    private static boolean areConnected(ServerLevel level, BlockPos a, BlockPos b) {
        var beA = level.getBlockEntity(a);
        var beB = level.getBlockEntity(b);
        if (!(beA instanceof PowerCrystalTile tileA) || !(beB instanceof PowerCrystalTile tileB)) return false;
        return tileA.getConnections().contains(b) || tileB.getConnections().contains(a);
    }

    public Set<BlockPos> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    public boolean contains(BlockPos pos) {
        return members.contains(pos);
    }

    public String getId() {
        return id;
    }

    /**
     * Called when this network is created as the result of merging multiple previously separate networks.
     * Implementors can perform additional bookkeeping (like aggregating power, transferring state, etc.).
     *
     * @param others The other network instances that were merged into this one (may be empty but never null).
     * @param level  The server level where the merge happened.
     */
    public void onMergedWith(Collection<PowerCrystalNetwork> others, ServerLevel level) {
        this.power += others.stream().mapToLong(PowerCrystalNetwork::getPower).sum();
        this.power = Math.min(this.power, getMaxPower());
    }

    public long getPower() {
        return power;
    }

    public long getMaxPower() {
        return 10_000;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("Id", this.id);
        compoundTag.putLong("Power", this.power);
        compoundTag.putLongArray("Members", this.members.stream().mapToLong(BlockPos::asLong).toArray());
        return compoundTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        this.id = compoundTag.getString("Id");
        this.power = compoundTag.getLong("Power");
        this.members = new HashSet<>();
        for (long pos : compoundTag.getLongArray("Members")) {
            this.members.add(BlockPos.of(pos));
        }
    }
}

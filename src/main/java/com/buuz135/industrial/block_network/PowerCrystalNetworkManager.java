package com.buuz135.industrial.block_network;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Holds a single instance of each CustomCrystalNetwork per ServerLevel, keyed by stable network id.
 * Tiles should compute their current networkId and retrieve the instance from this manager.
 */
public class PowerCrystalNetworkManager {

    // One manager per ServerLevel, weakly referenced to avoid memory leaks across world unloads
    private static final Map<ServerLevel, PowerCrystalNetworkManager> BY_LEVEL = new WeakHashMap<>();

    private final Map<String, PowerCrystalNetwork> networks = new HashMap<>();

    private PowerCrystalNetworkManager() {
    }

    public static PowerCrystalNetworkManager get(ServerLevel level) {
        return BY_LEVEL.computeIfAbsent(level, k -> new PowerCrystalNetworkManager());
    }

    /**
     * Returns an existing network by id or null if not present in this manager.
     */
    public PowerCrystalNetwork getById(String id) {
        return networks.get(id);
    }

    /**
     * Returns the network instance for the component that contains pos. If it does not exist yet,
     * it will be built and stored by its stable id.
     */
    public PowerCrystalNetwork getOrBuild(ServerLevel level, BlockPos pos) {
        // Compute the full component and its stable id
        var component = PowerCrystalNetwork.getComponent(level, pos);
        String newId = component.stream()
                .min((a, b) -> {
                    if (a.getX() != b.getX()) return Integer.compare(a.getX(), b.getX());
                    if (a.getY() != b.getY()) return Integer.compare(a.getY(), b.getY());
                    return Integer.compare(a.getZ(), b.getZ());
                })
                .map(p -> p.getX() + ":" + p.getY() + ":" + p.getZ())
                .orElse(pos.getX() + ":" + pos.getY() + ":" + pos.getZ());

        // Find existing networks that intersect this component
        java.util.LinkedHashSet<PowerCrystalNetwork> intersecting = new java.util.LinkedHashSet<>();
        for (PowerCrystalNetwork net : networks.values()) {
            // Quick skip if ids match directly
            if (newId.equals(net.getId())) {
                intersecting.add(net);
                break;
            }
            for (net.minecraft.core.BlockPos p : component) {
                if (net.contains(p)) {
                    intersecting.add(net);
                    break;
                }
            }
        }

        if (intersecting.isEmpty()) {
            // No existing network covers this component: create a new one
            PowerCrystalNetwork created = new PowerCrystalNetwork(component);
            networks.put(created.getId(), created);
            return created;
        } else if (intersecting.size() == 1) {
            // Single existing network; ensure it reflects the full component
            PowerCrystalNetwork existing = intersecting.iterator().next();
            // If the id changed (seed moved) or membership changed, replace the instance in the map
            boolean sameMembers = existing.getMembers().equals(component);
            if (sameMembers && existing.getId().equals(newId)) {
                return existing;
            }
            PowerCrystalNetwork updated = new PowerCrystalNetwork(component);
            // Replace map key if needed
            networks.remove(existing.getId());
            networks.put(updated.getId(), updated);
            // Not a merge of multiple networks, so we don't call onMergedWith
            return updated;
        } else {
            // Merge multiple networks into one
            java.util.HashSet<net.minecraft.core.BlockPos> mergedMembers = new java.util.HashSet<>(component);
            // Build the new unified network
            PowerCrystalNetwork unified = new PowerCrystalNetwork(mergedMembers);
            // Call the merge hook with the old networks
            unified.onMergedWith(intersecting, level);
            // Remove all old networks from the manager map
            for (PowerCrystalNetwork old : intersecting) {
                networks.remove(old.getId());
            }
            // Put the new unified network under its (possibly new) id
            networks.put(unified.getId(), unified);
            return unified;
        }
    }

    /**
     * Put/replace a network instance by its id.
     */
    public void put(PowerCrystalNetwork network) {
        networks.put(network.getId(), network);
    }

    /**
     * Remove a network by id.
     */
    public void remove(String id) {
        networks.remove(id);
    }
}

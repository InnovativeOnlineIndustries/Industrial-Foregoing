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

package com.buuz135.industrial.utils;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.config.server.ServerLoadBalancerConfig;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks server TPS and provides adaptive tick skipping intervals
 * for performance-intensive tile entities like HydroponicBed.
 * Supports both global server TPS and per-world TPS tracking.
 */
public class ServerLoadBalancer {

    private static final double TARGET_TPS = 20.0;

    // Global TPS tracking
    private static double currentTPS = TARGET_TPS;
    private static long lastSampleTime = System.nanoTime();
    private static int ticksSinceLastSample = 0;
    private static int cachedSkipInterval = 1;

    // Per-world TPS tracking
    private static final Map<ResourceKey<Level>, WorldTpsData> worldTpsMap = new ConcurrentHashMap<>();

    /**
     * Data class for per-world TPS tracking
     */
    private static class WorldTpsData {
        double currentTPS = TARGET_TPS;
        long lastSampleTime = System.nanoTime();
        int ticksSinceLastSample = 0;
        int cachedSkipInterval = 1;
    }

    /**
     * Called on every server tick to track global TPS.
     * Should be registered with ServerTickEvent.Pre.
     */
    public static void onServerTick(ServerTickEvent.Pre event) {
        // Always track global TPS as fallback even when per-world mode is enabled
        ticksSinceLastSample++;

        if (ticksSinceLastSample >= ServerLoadBalancerConfig.tpsSampleInterval) {
            long now = System.nanoTime();
            double elapsedSeconds = (now - lastSampleTime) / 1_000_000_000.0;

            if (elapsedSeconds > 0) {
                currentTPS = ticksSinceLastSample / elapsedSeconds;
                if (currentTPS > TARGET_TPS) {
                    currentTPS = TARGET_TPS;
                }
            }

            // Only calculate skip interval if system is enabled
            if (ServerLoadBalancerConfig.enabled) {
                int newInterval = calculateSkipInterval(currentTPS);
                cachedSkipInterval = newInterval;
            } else {
                cachedSkipInterval = 1;
            }

            ticksSinceLastSample = 0;
            lastSampleTime = now;
        }
    }

    /**
     * Called on every level tick to track per-world TPS.
     * Should be registered with LevelTickEvent.Pre.
     */
    public static void onLevelTick(LevelTickEvent.Pre event) {
        if (!ServerLoadBalancerConfig.enabled || !ServerLoadBalancerConfig.perWorldTps) {
            return;
        }

        if (event.getLevel().isClientSide()) {
            return;
        }

        ResourceKey<Level> dimension = event.getLevel().dimension();
        WorldTpsData data = worldTpsMap.computeIfAbsent(dimension, k -> new WorldTpsData());

        data.ticksSinceLastSample++;

        if (data.ticksSinceLastSample >= ServerLoadBalancerConfig.tpsSampleInterval) {
            long now = System.nanoTime();
            double elapsedSeconds = (now - data.lastSampleTime) / 1_000_000_000.0;

            if (elapsedSeconds > 0) {
                data.currentTPS = data.ticksSinceLastSample / elapsedSeconds;
                if (data.currentTPS > TARGET_TPS) {
                    data.currentTPS = TARGET_TPS;
                }
            }

            data.cachedSkipInterval = calculateSkipInterval(data.currentTPS);
            data.ticksSinceLastSample = 0;
            data.lastSampleTime = now;
        }
    }

    /**
     * Calculates the tick skip interval based on TPS value.
     */
    private static int calculateSkipInterval(double tps) {
        // If min equals max, skipping is effectively disabled
        if (ServerLoadBalancerConfig.minSkippedTicks >= ServerLoadBalancerConfig.maxSkippedTicks) {
            return ServerLoadBalancerConfig.minSkippedTicks;
        }

        // TPS >= normalTps: No skipping (normal operation)
        if (tps >= ServerLoadBalancerConfig.normalTps) {
            return ServerLoadBalancerConfig.minSkippedTicks;
        }

        // Gradual increase/decrease of skip interval
        if (ServerLoadBalancerConfig.criticalGradualProgressiveSkippedTicks ||
            ServerLoadBalancerConfig.nonCriticalGradualRegressiveSkippedTicks) {

            // Linear interpolation between min and max based on TPS
            double tpsRange = ServerLoadBalancerConfig.normalTps - ServerLoadBalancerConfig.criticalLoadTps;
            double currentOffset = ServerLoadBalancerConfig.normalTps - tps;
            double ratio = Math.min(1.0, Math.max(0.0, currentOffset / tpsRange));

            int skipRange = ServerLoadBalancerConfig.maxSkippedTicks - ServerLoadBalancerConfig.minSkippedTicks;
            return ServerLoadBalancerConfig.minSkippedTicks + (int) Math.round(ratio * skipRange);
        }

        // Step mode (no gradual change)
        // TPS highLoadTps-normalTps: Skip every other tick
        if (tps >= ServerLoadBalancerConfig.highLoadTps) {
            return 2;
        }

        // TPS criticalLoadTps-highLoadTps: Process every 4th tick
        if (tps >= ServerLoadBalancerConfig.criticalLoadTps) {
            return 4;
        }

        // TPS < criticalLoadTps: Maximum skipping
        return ServerLoadBalancerConfig.maxSkippedTicks;
    }

    /**
     * Returns the tick skip interval for a specific level.
     * Uses per-world TPS if enabled, otherwise falls back to global TPS.
     *
     * @param level The level to get skip interval for
     * @return Skip interval (1 = every tick, 2 = every other tick, etc.)
     */
    public static int getTickSkipInterval(Level level) {
        if (!ServerLoadBalancerConfig.enabled) {
            return 1;
        }

        if (level == null || !ServerLoadBalancerConfig.perWorldTps) {
            return cachedSkipInterval;
        }

        WorldTpsData data = worldTpsMap.get(level.dimension());
        if (data == null) {
            return cachedSkipInterval;
        }

        return data.cachedSkipInterval;
    }

    /**
     * Returns the current tick skip interval (global).
     * Tiles should only process when (gameTime % getTickSkipInterval() == 0).
     *
     * @return Skip interval (1 = every tick, 2 = every other tick, etc.)
     */
    public static int getTickSkipInterval() {
        if (!ServerLoadBalancerConfig.enabled) {
            return 1;
        }
        return cachedSkipInterval;
    }

    /**
     * Calculates tick skip interval with custom thresholds for a specific level.
     *
     * @param level The level to check TPS for
     * @param enabled Whether adaptive tick skipping is enabled
     * @param highLoadThreshold TPS threshold for light skipping (every 2nd tick)
     * @param criticalLoadThreshold TPS threshold for heavy skipping (every 4th tick)
     * @param maxSkip Maximum skip interval when TPS is critically low
     * @return Skip interval (1 = every tick, 2 = every other tick, etc.)
     */
    public static int getTickSkipInterval(Level level, boolean enabled, int highLoadThreshold, int criticalLoadThreshold, int maxSkip) {
        if (!ServerLoadBalancerConfig.enabled || !enabled) {
            return 1;
        }

        double tps = getCurrentTPS(level);

        if (tps >= 19.0) {
            return 1;
        }

        if (tps >= highLoadThreshold) {
            return 2;
        }

        if (tps >= criticalLoadThreshold) {
            return 4;
        }

        return maxSkip;
    }

    /**
     * Calculates tick skip interval with custom thresholds (legacy, uses global TPS).
     */
    public static int getTickSkipInterval(boolean enabled, int highLoadThreshold, int criticalLoadThreshold, int maxSkip) {
        return getTickSkipInterval(null, enabled, highLoadThreshold, criticalLoadThreshold, maxSkip);
    }

    /**
     * Returns the current measured TPS for a specific level.
     *
     * @param level The level to get TPS for (null for global TPS)
     * @return Current TPS (0-20)
     */
    public static double getCurrentTPS(Level level) {
        if (level == null || !ServerLoadBalancerConfig.perWorldTps) {
            return currentTPS;
        }

        WorldTpsData data = worldTpsMap.get(level.dimension());
        if (data == null) {
            return currentTPS;
        }

        return data.currentTPS;
    }

    /**
     * Returns the current measured global TPS.
     *
     * @return Current TPS (0-20)
     */
    public static double getCurrentTPS() {
        return currentTPS;
    }

    /**
     * Checks if adaptive tick skipping is currently active for a level.
     *
     * @param level The level to check (null for global)
     * @return true if ticks are being skipped
     */
    public static boolean isSkippingActive(Level level) {
        return getTickSkipInterval(level) > 1;
    }

    /**
     * Checks if adaptive tick skipping is currently active (global).
     *
     * @return true if ticks are being skipped
     */
    public static boolean isSkippingActive() {
        return cachedSkipInterval > 1;
    }

    /**
     * Resets the TPS tracker. Called on server start.
     */
    public static void reset() {
        currentTPS = TARGET_TPS;
        lastSampleTime = System.nanoTime();
        ticksSinceLastSample = 0;
        cachedSkipInterval = ServerLoadBalancerConfig.enabled ? ServerLoadBalancerConfig.minSkippedTicks : 1;
        worldTpsMap.clear();

        IndustrialForegoing.LOGGER.info("[ServerLoadBalancer] Initialized. Enabled: {}, Mode: {}, Skip: {}-{}, TPS thresholds: {}/{}/{}",
            ServerLoadBalancerConfig.enabled,
            ServerLoadBalancerConfig.perWorldTps ? "PER_WORLD" : "GLOBAL",
            ServerLoadBalancerConfig.minSkippedTicks,
            ServerLoadBalancerConfig.maxSkippedTicks,
            ServerLoadBalancerConfig.normalTps,
            ServerLoadBalancerConfig.highLoadTps,
            ServerLoadBalancerConfig.criticalLoadTps);
    }

    /**
     * Removes tracking data for a specific level.
     * Should be called when a level is unloaded.
     *
     * @param dimension The dimension key to remove
     */
    public static void removeLevel(ResourceKey<Level> dimension) {
        worldTpsMap.remove(dimension);
    }
}

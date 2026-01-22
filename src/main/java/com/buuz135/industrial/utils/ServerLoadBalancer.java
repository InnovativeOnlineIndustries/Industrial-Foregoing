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
import com.buuz135.industrial.config.machine.resourceproduction.HydroponicBedConfig;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Tracks server TPS and provides adaptive tick skipping intervals
 * for performance-intensive tile entities like HydroponicBed.
 */
public class ServerLoadBalancer {

    private static final int TPS_SAMPLE_INTERVAL = 20; // Update TPS every second (20 ticks)
    private static final double TARGET_TPS = 20.0;

    private static double currentTPS = TARGET_TPS;
    private static long lastSampleTime = System.nanoTime();
    private static int ticksSinceLastSample = 0;

    // Cached skip interval to avoid recalculation every tick
    private static int cachedSkipInterval = 1;

    /**
     * Called on every server tick to track TPS.
     * Should be registered with ServerTickEvent.Pre.
     */
    public static void onServerTick(ServerTickEvent.Pre event) {
        ticksSinceLastSample++;

        if (ticksSinceLastSample >= TPS_SAMPLE_INTERVAL) {
            long now = System.nanoTime();
            double elapsedSeconds = (now - lastSampleTime) / 1_000_000_000.0;

            // Calculate TPS: how many ticks occurred per second
            if (elapsedSeconds > 0) {
                currentTPS = ticksSinceLastSample / elapsedSeconds;
                // Clamp to max 20 TPS
                if (currentTPS > TARGET_TPS) {
                    currentTPS = TARGET_TPS;
                }
            }

            // Update cached skip interval
            int newInterval = calculateSkipInterval();
            if (newInterval != cachedSkipInterval) {
                /*IndustrialForegoing.LOGGER.info("[ServerLoadBalancer] TPS: {}, Skip interval: {} -> {}",
                    String.format("%.1f", currentTPS), cachedSkipInterval, newInterval);*/
                cachedSkipInterval = newInterval;
            }

            // Reset for next sample
            ticksSinceLastSample = 0;
            lastSampleTime = now;
        }
    }

    /**
     * Calculates the tick skip interval based on current TPS.
     */
    private static int calculateSkipInterval() {
        if (!HydroponicBedConfig.adaptiveTickSkipping) {
            return 1;
        }

        // TPS >= 19: No skipping (normal operation)
        if (currentTPS >= 19.0) {
            return 1;
        }

        // TPS 15-19: Skip every other tick
        if (currentTPS >= HydroponicBedConfig.highLoadThresholdTPS) {
            return 2;
        }

        // TPS 10-15: Process every 4th tick
        if (currentTPS >= HydroponicBedConfig.criticalLoadThresholdTPS) {
            return 4;
        }

        // TPS < 10: Maximum skipping
        return HydroponicBedConfig.maxTickSkip;
    }

    /**
     * Returns the current tick skip interval.
     * Tiles should only process when (gameTime % getTickSkipInterval() == 0).
     *
     * @return Skip interval (1 = every tick, 2 = every other tick, etc.)
     */
    public static int getTickSkipInterval() {
        return cachedSkipInterval;
    }

    /**
     * Calculates tick skip interval with custom thresholds.
     * Use this for machines with their own config settings.
     *
     * @param enabled Whether adaptive tick skipping is enabled
     * @param highLoadThreshold TPS threshold for light skipping (every 2nd tick)
     * @param criticalLoadThreshold TPS threshold for heavy skipping (every 4th tick)
     * @param maxSkip Maximum skip interval when TPS is critically low
     * @return Skip interval (1 = every tick, 2 = every other tick, etc.)
     */
    public static int getTickSkipInterval(boolean enabled, int highLoadThreshold, int criticalLoadThreshold, int maxSkip) {
        if (!enabled) {
            return 1;
        }

        if (currentTPS >= 19.0) {
            return 1;
        }

        if (currentTPS >= highLoadThreshold) {
            return 2;
        }

        if (currentTPS >= criticalLoadThreshold) {
            return 4;
        }

        return maxSkip;
    }

    /**
     * Returns the current measured TPS.
     *
     * @return Current TPS (0-20)
     */
    public static double getCurrentTPS() {
        return currentTPS;
    }

    /**
     * Checks if adaptive tick skipping is currently active (i.e., TPS is low).
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
        cachedSkipInterval = 1;
        IndustrialForegoing.LOGGER.info("[ServerLoadBalancer] Initialized. Adaptive tick skipping: {}",
            HydroponicBedConfig.adaptiveTickSkipping ? "ENABLED" : "DISABLED");
    }
}

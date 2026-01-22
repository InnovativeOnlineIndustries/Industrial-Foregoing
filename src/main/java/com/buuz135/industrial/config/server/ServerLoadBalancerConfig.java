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

package com.buuz135.industrial.config.server;

import com.buuz135.industrial.config.ServerConfig;
import com.hrznstudio.titanium.annotation.config.ConfigFile;
import com.hrznstudio.titanium.annotation.config.ConfigVal;

@ConfigFile.Child(ServerConfig.class)
public class ServerLoadBalancerConfig {

    @ConfigVal(comment = "Enable adaptive tick skipping system. When enabled, machines will skip ticks when server TPS is low - Default: [true]")
    public static boolean enabled = true;

    @ConfigVal(comment = "TPS tracking mode: false = global server TPS, true = per-world TPS (useful for servers with separate worlds per player) - Default: [false]")
    public static boolean perWorldTps = false;

    @ConfigVal(comment = "TPS sampling interval in ticks - Default: [20] (1 second)")
    public static int tpsSampleInterval = 20;

    @ConfigVal(comment = "TPS threshold for normal operation (no tick skipping) [0.0 ~ 20.0] - Default: [19.0]")
    public static double normalTps = 19.0;

    @ConfigVal(comment = "TPS threshold for medium load (skip every 2nd tick) [0.0 ~ 20.0] - Default: [15.0]")
    public static double highLoadTps = 15.0;

    @ConfigVal(comment = "TPS threshold for high load (skip every 4th tick) [0.0 ~ 20.0] - Default: [10.0]")
    public static double criticalLoadTps = 10.0;

    @ConfigVal(comment = "Minimum tick skip interval (1 = no skipping) [1 ~ 200] - Default: [1]")
    public static int minSkippedTicks = 1;

    @ConfigVal(comment = "Maximum tick skip interval (must be >= minSkippedTicks) [1 ~ 200] - Default: [8]")
    public static int maxSkippedTicks = 8;

    @ConfigVal(comment = "Gradually increase tick skipping from min to max when load increases - Default: [true]")
    public static boolean criticalGradualProgressiveSkippedTicks = true;

    @ConfigVal(comment = "Gradually decrease tick skipping from max to min when load decreases - Default: [true]")
    public static boolean nonCriticalGradualRegressiveSkippedTicks = true;

}

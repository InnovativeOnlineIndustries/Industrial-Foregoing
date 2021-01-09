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
package com.buuz135.industrial.config.machine.core;

import com.buuz135.industrial.config.MachineCoreConfig;
import com.hrznstudio.titanium.annotation.config.ConfigFile;
import com.hrznstudio.titanium.annotation.config.ConfigVal;

@ConfigFile.Child(MachineCoreConfig.class)
public class LatexProcessingUnitConfig {

    @ConfigVal(comment = "Cooldown Time in Ticks [20 Ticks per Second] - Default: [100 (5s)]")
    public static int maxProgress = 100;

    @ConfigVal(comment = "Amount of Power Consumed per Tick - Default: [400FE]")
    public static int powerPerTick = 20;

    @ConfigVal(comment = "Max Stored Power [FE] - Default: [10000 FE]")
    public static int maxStoredPower = 10000;

    @ConfigVal(comment = "Max Amount of Stored Fluid [Latex] - Default: [8000mB]")
    public static int maxLatexTankSize = 16000;

    @ConfigVal(comment = "Max Amount of Stored Fluid [Water] - Default: [8000mB]")
    public static int maxWaterTankSize = 16000;

}

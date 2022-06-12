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

package com.buuz135.industrial.config.machine.resourceproduction;

import com.buuz135.industrial.config.MachineResourceProductionConfig;
import com.hrznstudio.titanium.annotation.config.ConfigFile;
import com.hrznstudio.titanium.annotation.config.ConfigVal;

@ConfigFile.Child(MachineResourceProductionConfig.class)
public class FermentationStationConfig {

    @ConfigVal(comment = "Amount of Power Consumed per Tick - Default: [40FE]")
    public static int powerPerTick = 40;

    @ConfigVal(comment = "Max Stored Power [FE] - Default: [10000 FE]")
    public static int maxStoredPower = 10000;

    @ConfigVal(comment = "Amount of ticks for 2x production - Default: [20 * 5 FE]")
    public static int ticksFor2XProduction = 20 * 5;

    @ConfigVal(comment = "Amount of ticks for 3x production - Default: [20 * 45 FE]")
    public static int ticksFor3XProduction = 20 * 45;

    @ConfigVal(comment = "Amount of ticks for 4x production - Default: [20 * 60 * 2 FE]")
    public static int ticksFor4XProduction = 20 * 60 * 2;

    @ConfigVal(comment = "Amount of ticks for 5x production - Default: [20 * 60 * 5 FE]")
    public static int ticksFor5XProduction = 20 * 60 * 5;
}

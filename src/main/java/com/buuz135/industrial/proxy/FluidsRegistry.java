/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2018, Buuz135
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
package com.buuz135.industrial.proxy;

import com.buuz135.industrial.fluid.IFCustomFluid;
import com.buuz135.industrial.fluid.IFOreFluid;
import net.minecraft.fluid.Fluid;

import java.awt.*;


public class FluidsRegistry {

    public static Fluid ESSENCE;
    public static Fluid MILK;
    public static Fluid MEAT;
    public static Fluid LATEX;
    public static Fluid SEWAGE;
    public static Fluid SLUDGE;
    public static Fluid BIOFUEL;
    public static Fluid PINK_SLIME;
    public static Fluid PROTEIN;
    public static Fluid ORE_FLUID_RAW;
    public static Fluid ORE_FLUID_FERMENTED;

    public static void registerFluids() {
        /*
        (ESSENCE = (IFCustomFluid) new IFCustomFluid("essence", 300, Color.decode("0x408000")).setLuminosity(15)).register();
        (MILK = new IFCustomFluid("milk", 300, Color.WHITE)).register();
        (MEAT = new IFCustomFluid("meat", 375, Color.decode("0x996633"))).register();
        (LATEX = new IFCustomFluid("latex", 375, Color.decode("0xc2c2a3"))).register();
        (SEWAGE = new IFCustomFluid("sewage", 300, Color.decode("0x993300"))).register();
        (SLUDGE = new IFCustomFluid("sludge", 600, Color.decode("0x330033"))).register();
        (BIOFUEL = new IFCustomFluid("biofuel", 600, Color.decode("0x993399"))).register();
        (PINK_SLIME = new IFCustomFluid("if.pink_slime", 300, Color.decode("0xff66cc"))).register();
        (PROTEIN = new IFCustomFluid("if.protein", 600, Color.decode("0xff5050"))).register();
        (ORE_FLUID_RAW = new IFOreFluid("raw")).register();
        (ORE_FLUID_FERMENTED = new IFOreFluid("fermented")).register();*/
    }


}

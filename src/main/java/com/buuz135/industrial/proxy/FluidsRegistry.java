package com.buuz135.industrial.proxy;

import com.buuz135.industrial.fluid.IFCustomFluid;

import java.awt.*;


public class FluidsRegistry {

    public static IFCustomFluid ESSENCE;
    public static IFCustomFluid MILK;
    public static IFCustomFluid MEAT;
    public static IFCustomFluid LATEX;
    public static IFCustomFluid SEWAGE;
    public static IFCustomFluid SLUDGE;
    public static IFCustomFluid BIOFUEL;
    public static IFCustomFluid PINK_SLIME;
    public static IFCustomFluid PROTEIN;

    public static void registerFluids() {
        (ESSENCE = (IFCustomFluid) new IFCustomFluid("essence", 300, Color.decode("0x408000")).setLuminosity(15)).register();
        (MILK = new IFCustomFluid("milk", 300, Color.WHITE)).register();
        (MEAT = new IFCustomFluid("meat", 375, Color.decode("0x996633"))).register();
        (LATEX = new IFCustomFluid("latex", 375, Color.decode("0xc2c2a3"))).register();
        (SEWAGE = new IFCustomFluid("sewage", 300, Color.decode("0x993300"))).register();
        (SLUDGE = new IFCustomFluid("sludge", 600, Color.decode("0x330033"))).register();
        (BIOFUEL = new IFCustomFluid("biofuel", 600, Color.decode("0x993399"))).register();
        (PINK_SLIME = new IFCustomFluid("if.pink_slime", 300, Color.decode("0xff66cc"))).register();
        (PROTEIN = new IFCustomFluid("if.protein", 600, Color.decode("0xff5050"))).register();
    }


}

package com.buuz135.industrial.proxy;

import com.buuz135.industrial.fluid.IFCustomFluid;

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
        (ESSENCE = new IFCustomFluid("essence", 300)).register();
        (MILK = new IFCustomFluid("milk", 300)).register();
        (MEAT = new IFCustomFluid("meat", 600)).register();
        (LATEX = new IFCustomFluid("latex", 600)).register();
        (SEWAGE = new IFCustomFluid("sewage", 600)).register();
        (SLUDGE = new IFCustomFluid("sludge", 600)).register();
        (BIOFUEL = new IFCustomFluid("biofuel", 600)).register();
        (PINK_SLIME = new IFCustomFluid("if.pink_slime", 300)).register();
        (PROTEIN = new IFCustomFluid("if.protein", 600)).register();
    }


}

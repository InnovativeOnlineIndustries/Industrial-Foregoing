package com.buuz135.industrial.proxy;

import com.buuz135.industrial.fluid.IFCustomFluid;

public class FluidsRegistry {

    public static IFCustomFluid XP;
    public static IFCustomFluid MILK;
    public static IFCustomFluid MEAT;
    public static IFCustomFluid LATEX;

    public static void registerFluids() {
        (XP = new IFCustomFluid("xpfluid")).register();
        (MILK = new IFCustomFluid("milk")).register();
        (MEAT = new IFCustomFluid("meat")).register();
        (LATEX = new IFCustomFluid("latex")).register();
    }


}

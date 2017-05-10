package com.buuz135.industrial.proxy;

import com.buuz135.industrial.fluid.IFCustomFluid;
import com.buuz135.industrial.item.IFCustomItem;

public class FluidsRegistry {

    public static IFCustomFluid ESSENCE;
    public static IFCustomFluid MILK;
    public static IFCustomFluid MEAT;
    public static IFCustomFluid LATEX;
    public static IFCustomFluid SEWAGE;
    public static IFCustomFluid SLUDGE;

    public static void registerFluids() {
        (ESSENCE = new IFCustomFluid("essence")).register();
        (MILK = new IFCustomFluid("milk")).register();
        (MEAT = new IFCustomFluid("meat")).register();
        (LATEX = new IFCustomFluid("latex")).register();
        (SEWAGE = new IFCustomFluid("sewage")).register();
        (SLUDGE = new IFCustomFluid("sludge")).register();
    }


}

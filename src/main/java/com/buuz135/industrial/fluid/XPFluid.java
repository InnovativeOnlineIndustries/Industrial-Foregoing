package com.buuz135.industrial.fluid;


import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class XPFluid extends Fluid {

    public XPFluid() {
        super("xp_fluid", new ResourceLocation("blocks/water_still"), new ResourceLocation("blocks/water_flow"));
    }

}

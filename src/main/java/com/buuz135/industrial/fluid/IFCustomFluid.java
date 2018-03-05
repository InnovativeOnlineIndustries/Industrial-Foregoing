package com.buuz135.industrial.fluid;

import com.buuz135.industrial.utils.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.awt.*;

public class IFCustomFluid extends Fluid {

    public IFCustomFluid(String fluidName, int temperature) {
        super(fluidName, new ResourceLocation(Reference.MOD_ID, "blocks/fluids/" + fluidName + "_still"), new ResourceLocation(Reference.MOD_ID, "blocks/fluids/" + fluidName + "_flow"));
        this.setDensity(3000).setViscosity(6000).setTemperature(temperature);
    }

    public IFCustomFluid(String fluidName, int temperature, Color color) {
        this(fluidName, temperature);
        this.setColor(color);
    }

    public void register() {
        FluidRegistry.registerFluid(this);
        FluidRegistry.addBucketForFluid(this);
    }
}

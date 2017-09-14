package com.buuz135.industrial.utils;

import com.buuz135.industrial.api.straw.StrawHandler;
import com.buuz135.industrial.registry.IFRegistries;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.Optional;

public class StrawUtils {

    @Nonnull
    public static Optional<StrawHandler> getStrawHandler(@Nonnull FluidStack stack) {
        for(StrawHandler handler : IFRegistries.STRAW_HANDLER_REGISTRY) {
            if(handler.validFluid(stack))
                return Optional.of(handler);
        }
        return Optional.empty();
    }
}

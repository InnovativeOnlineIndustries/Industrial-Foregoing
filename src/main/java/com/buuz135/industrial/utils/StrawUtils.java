package com.buuz135.industrial.utils;

import com.buuz135.industrial.api.straw.StrawHandler;
import com.buuz135.industrial.registry.IFRegistries;
import com.google.common.collect.Lists;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class StrawUtils {

    @Nonnull
    public static Optional<StrawHandler> getStrawHandler(@Nonnull FluidStack stack) {

        List<StrawHandler> current = Lists.newArrayList(IFRegistries.STRAW_HANDLER_REGISTRY);
        current.sort(Comparator.comparingInt(StrawHandler::getPriority));
        for (StrawHandler handler : current) {
            if (handler.validFluid(stack))
                return Optional.of(handler);
        }
        return Optional.empty();
    }
}
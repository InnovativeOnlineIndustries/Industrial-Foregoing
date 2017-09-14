package com.buuz135.industrial.api.straw;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class StrawHandler extends IForgeRegistryEntry.Impl<StrawHandler> {
    public abstract boolean validFluid(FluidStack stack);

    public abstract void onDrink(World world, BlockPos pos, FluidStack stack, EntityPlayer player, boolean fromFluidContainer);

    public int getPriority() {
        return 0;
    }
}
package com.buuz135.industrial.api.fluid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public interface IFluidDrinkHandler {
    /**
     *
     * @param world World of where drinking
     * @param pos BlockPos of where drinking from
     * @param stack FluidStack drank
     * @param player EntityPlayer drinking
     * @param fromFluidContainer true if drinking from a tank
     */
    void onDrink(World world, BlockPos pos, FluidStack stack, EntityPlayer player, boolean fromFluidContainer);
}

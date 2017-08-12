package com.buuz135.industrial.utils.drinkhandlers;

import com.buuz135.industrial.api.IFluidDrinkHandler;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class DrinkHandlerEssence implements IFluidDrinkHandler {
    @Override
    public void onDrink(World world, BlockPos pos, FluidStack stack, EntityPlayer player, boolean fromFluidContainer) {
        world.spawnEntity(new EntityXPOrb(world, player.posX, player.posY, player.posZ, world.rand.nextInt(10) + 8));
    }
}
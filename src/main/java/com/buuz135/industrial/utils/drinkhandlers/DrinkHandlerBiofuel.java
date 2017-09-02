package com.buuz135.industrial.utils.drinkhandlers;

import com.buuz135.industrial.api.fluid.IFluidDrinkHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class DrinkHandlerBiofuel implements IFluidDrinkHandler {
    @Override
    public void onDrink(World world, BlockPos pos, FluidStack stack, EntityPlayer player, boolean fromFluidContainer) {
        player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 800, 0));
        player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 800, 0));
    }
}
package com.buuz135.industrial.utils.drinkhandlers;

import com.buuz135.industrial.api.fluid.IFluidDrinkHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class DrinkHandlerLatex implements IFluidDrinkHandler {
    @Override
    public void onDrink(World world, BlockPos pos, FluidStack stack, EntityPlayer player, boolean fromFluidContainer) {
        player.addPotionEffect(new PotionEffect(MobEffects.POISON, 1200,2));
        player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 1200,2));
    }
}
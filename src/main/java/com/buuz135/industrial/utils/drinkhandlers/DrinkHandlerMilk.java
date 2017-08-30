package com.buuz135.industrial.utils.drinkhandlers;

import com.buuz135.industrial.api.fluid.IFluidDrinkHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class DrinkHandlerMilk implements IFluidDrinkHandler {
    @Override
    public void onDrink(World world, BlockPos pos, FluidStack stack, EntityPlayer player, boolean fromFluidContainer) {
        player.curePotionEffects(new ItemStack(Items.MILK_BUCKET));
    }
}
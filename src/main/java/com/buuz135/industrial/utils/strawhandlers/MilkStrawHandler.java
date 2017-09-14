package com.buuz135.industrial.utils.strawhandlers;

import com.buuz135.industrial.proxy.FluidsRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class MilkStrawHandler extends StrawHandlerBase {
    public MilkStrawHandler() {
        super(FluidsRegistry.MILK.getName());
        setRegistryName("milk");
    }

    @Override
    public void onDrink(World world, BlockPos pos, FluidStack stack, EntityPlayer player, boolean fromFluidContainer) {
        player.curePotionEffects(new ItemStack(Items.MILK_BUCKET));
    }
}
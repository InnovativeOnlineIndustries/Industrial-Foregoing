package com.buuz135.industrial.utils.apihandlers.straw;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class LavaStrawHandler extends StrawHandlerBase {
    public LavaStrawHandler() {
        super("lava");
        setRegistryName("lava");
    }

    @Override
    public void onDrink(World world, BlockPos pos, FluidStack stack, EntityPlayer player, boolean fromFluidContainer) {
        player.attackEntityFrom(DamageSource.LAVA, 7);
        player.setFire(30);
        NBTTagCompound tag = player.getEntityData();
        tag.setLong("lavaDrink", world.getTotalWorldTime());
    }
}
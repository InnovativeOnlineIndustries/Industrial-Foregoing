package com.buuz135.industrial.utils.apihandlers.straw;

import com.buuz135.industrial.proxy.FluidsRegistry;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class EssenceStrawHandler extends StrawHandlerBase {
    public EssenceStrawHandler() {
        super(FluidsRegistry.ESSENCE.getName());
        setRegistryName("essence");
    }

    @Override
    public void onDrink(World world, BlockPos pos, FluidStack stack, EntityPlayer player, boolean fromFluidContainer) {
        world.spawnEntity(new EntityXPOrb(world, player.posX, player.posY, player.posZ, world.rand.nextInt(10) + 8));
    }
}
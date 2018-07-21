package com.buuz135.industrial.utils.apihandlers.straw;

import com.buuz135.industrial.utils.Triple;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class PotionStrawHandler extends StrawHandlerBase {
    private List<Triple<Potion, Integer, Integer>> potions = new ArrayList<>();

    public PotionStrawHandler(String fluidName) {
        super(fluidName);
    }

    public PotionStrawHandler(Fluid fluid) {
        super(fluid.getName());
    }

    public PotionStrawHandler addPotion(PotionEffect effect) {
        return addPotion(effect.getPotion(), effect.getDuration(), effect.getAmplifier());
    }

    public PotionStrawHandler addPotion(Potion potion, Integer duration, Integer amplifier) {
        potions.add(new Triple<>(potion, duration, amplifier));
        return this;
    }

    @Override
    public void onDrink(World world, BlockPos pos, FluidStack stack, EntityPlayer player, boolean fromFluidContainer) {
        for (Triple<Potion, Integer, Integer> triple : potions) {
            player.addPotionEffect(new PotionEffect(triple.getA(), triple.getB(), triple.getC()));
        }
    }
}

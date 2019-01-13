/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
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

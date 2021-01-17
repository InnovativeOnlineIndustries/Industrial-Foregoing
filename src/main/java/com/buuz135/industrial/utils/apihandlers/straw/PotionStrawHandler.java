/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class PotionStrawHandler extends StrawHandlerBase {
    private List<Triple<Effect, Integer, Integer>> potions = new ArrayList<>();

    public PotionStrawHandler(Fluid fluid) {
        super(() -> fluid);
    }

    public PotionStrawHandler addPotion(EffectInstance effect) {
        return addPotion(effect.getPotion(), effect.getDuration(), effect.getAmplifier());
    }

    public PotionStrawHandler addPotion(Effect potion, Integer duration, Integer amplifier) {
        potions.add(new Triple<>(potion, duration, amplifier));
        return this;
    }

    @Override
    public void onDrink(World world, BlockPos pos, Fluid stack, PlayerEntity player, boolean fromFluidContainer) {
        for (Triple<Effect, Integer, Integer> triple : potions) {
            player.addPotionEffect(new EffectInstance(triple.getA(), triple.getB(), triple.getC()));
        }
    }
}

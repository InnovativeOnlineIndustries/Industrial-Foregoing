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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class PotionStrawHandler extends StrawHandlerBase {
    private List<Triple<MobEffect, Integer, Integer>> potions = new ArrayList<>();

    public PotionStrawHandler(Fluid fluid) {
        super(() -> fluid);
    }

    public PotionStrawHandler addPotion(MobEffectInstance effect) {
        return addPotion(effect.getEffect(), effect.getDuration(), effect.getAmplifier());
    }

    public PotionStrawHandler addPotion(MobEffect potion, Integer duration, Integer amplifier) {
        potions.add(new Triple<>(potion, duration, amplifier));
        return this;
    }

    @Override
    public void onDrink(Level world, BlockPos pos, Fluid stack, Player player, boolean fromFluidContainer) {
        for (Triple<MobEffect, Integer, Integer> triple : potions) {
            player.addEffect(new MobEffectInstance(triple.getA(), triple.getB(), triple.getC()));
        }
    }
}

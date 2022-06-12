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

/**
 * MIT License
 * <p>
 * Copyright (c) 2018
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.buuz135.industrial.proxy.client.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TickeableSound extends AbstractSoundInstance implements TickableSoundInstance {

    private boolean done;
    private Level world;
    private int distance;
    private float tickIncrease;

    public TickeableSound(Level world, BlockPos pos, SoundEvent soundIn, int distance, int soundTime) {
        super(soundIn, SoundSource.BLOCKS);
        this.world = world;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.looping = false;
        this.done = false;
        this.volume = 0.8f;
        this.pitch = 0f;
        this.relative = false;
        this.distance = distance;
        this.tickIncrease = 1 / (soundTime * 20F);
    }

    @Override
    public boolean isStopped() {
        return done;
    }

    public void setDone() {
        done = true;
    }

    public void increase() {
        if (this.pitch < 2) {
            this.pitch += tickIncrease;
        }
    }

    public void decrease() {
        if (this.pitch > 0) {
            this.pitch -= tickIncrease;
        }
    }

    @Override
    public void tick() {
        double distance = Minecraft.getInstance().player.blockPosition().distManhattan(new BlockPos(this.x, this.y, this.z));
        if (distance > this.distance) {
            this.volume = 0;
        } else {
            if (distance == 0) distance = 1;
            this.volume = (float) (0.8 * (1 - distance / (double) this.distance));
        }
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}

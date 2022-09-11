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
package com.buuz135.industrial.proxy.network;

import com.buuz135.industrial.entity.client.InfinityLauncherProjectileArmorLayer;
import com.hrznstudio.titanium.network.Message;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public class PlungerPlayerHitMessage extends Message {


    public UUID entity;

    public PlungerPlayerHitMessage(UUID entity) {
        this.entity = entity;
    }

    public PlungerPlayerHitMessage() {
    }

    @Override
    protected void handleMessage(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            int amount = InfinityLauncherProjectileArmorLayer.PROJECTILE_AMOUNT.computeIfAbsent(entity.toString(), s -> 0);
            InfinityLauncherProjectileArmorLayer.PROJECTILE_AMOUNT.put(entity.toString(), amount + 1);
            new Thread(() -> {
                try {
                    Thread.sleep(20 * 1000);
                    int amountFuture = InfinityLauncherProjectileArmorLayer.PROJECTILE_AMOUNT.computeIfAbsent(entity.toString(), s -> 0);
                    InfinityLauncherProjectileArmorLayer.PROJECTILE_AMOUNT.put(entity.toString(), Math.max(0, amountFuture - 1));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }


}

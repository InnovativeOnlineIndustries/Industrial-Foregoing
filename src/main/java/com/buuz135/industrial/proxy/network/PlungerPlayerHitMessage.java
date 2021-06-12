package com.buuz135.industrial.proxy.network;

import com.buuz135.industrial.entity.client.InfinityLauncherProjectileArmorLayer;
import com.hrznstudio.titanium.network.Message;
import net.minecraftforge.fml.network.NetworkEvent;

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

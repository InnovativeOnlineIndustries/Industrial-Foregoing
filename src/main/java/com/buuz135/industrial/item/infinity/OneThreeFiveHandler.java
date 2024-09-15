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

package com.buuz135.industrial.item.infinity;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.proxy.client.particle.ParticleVex;
import com.buuz135.industrial.proxy.network.SpecialParticleMessage;
import com.buuz135.industrial.utils.IFAttachments;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@EventBusSubscriber(modid = Reference.MOD_ID)
public class OneThreeFiveHandler {

    private static final String SPECIAL = "135135";

    public static HashMap<UUID, Long> SPECIAL_ENTITIES = new HashMap<>();

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.level() != null && !Minecraft.getInstance().isPaused() && Minecraft.getInstance().player.level().getGameTime() % 5 == 0) {
            BlockPos pos = new BlockPos(Minecraft.getInstance().player.blockPosition().getX(), Minecraft.getInstance().player.blockPosition().getY(), Minecraft.getInstance().player.blockPosition().getZ());
            Minecraft.getInstance().player.level().getEntitiesOfClass(LivingEntity.class, new AABB(pos.offset(32, 32, 32).getCenter(), pos.offset(-32, -32, -32).getCenter()),
                            input -> input.getUUID().toString().contains(SPECIAL)).
                    forEach(living -> Minecraft.getInstance().particleEngine.add(new ParticleVex(living)));
            Minecraft.getInstance().player.level().getEntitiesOfClass(Player.class, new AABB(pos.offset(32, 32, 32).getCenter(), pos.offset(-32, -32, -32).getCenter()),
                            input -> SPECIAL_ENTITIES.containsKey(input.getUUID())).
                    forEach(living -> Minecraft.getInstance().particleEngine.add(new ParticleVex(living)));
        }
        List<UUID> toRemove = new ArrayList<>();
        for (UUID uuid : SPECIAL_ENTITIES.keySet()) {
            if (System.currentTimeMillis() >= SPECIAL_ENTITIES.get(uuid) + 1000) {
                toRemove.add(uuid);
            }
        }
        toRemove.forEach(uuid -> SPECIAL_ENTITIES.remove(uuid));
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity().level().getGameTime() % 20 == 0) {
            for (ItemStack stack : event.getEntity().getInventory().items) {
                if (stack.getItem() instanceof ItemInfinity && ((ItemInfinity) stack.getItem()).isSpecial(stack) && ((ItemInfinity) stack.getItem()).isSpecialEnabled(stack)) {
                    IndustrialForegoing.NETWORK.sendToNearby(event.getEntity().level(), new BlockPos(event.getEntity().blockPosition().getX(), event.getEntity().blockPosition().getY(), event.getEntity().blockPosition().getZ()), 64, new SpecialParticleMessage(event.getEntity().getUUID()));
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityKill(LivingDeathEvent event) {
        if (event.getEntity().getUUID().toString().contains(SPECIAL) && event.getSource().getEntity() instanceof Player && !(event.getSource().getEntity() instanceof FakePlayer)) {
            Player player = (Player) event.getSource().getEntity();
            if (player.getMainHandItem().getItem() instanceof ItemInfinity) {
                player.getMainHandItem().set(IFAttachments.INFINITY_ITEM_SPECIAL, true);
            }
        }
    }

}
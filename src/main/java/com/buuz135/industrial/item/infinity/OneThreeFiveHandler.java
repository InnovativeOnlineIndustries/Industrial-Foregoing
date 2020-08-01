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
package com.buuz135.industrial.item.infinity;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.proxy.client.particle.ParticleVex;
import com.buuz135.industrial.proxy.network.SpecialParticleMessage;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class OneThreeFiveHandler {

    private static final String SPECIAL = "135135";

    public static HashMap<UUID, Long> SPECIAL_ENTITIES = new HashMap<>();

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.world != null && !Minecraft.getInstance().isGamePaused() && Minecraft.getInstance().player.world.getGameTime() % 4 == 0) {
            BlockPos pos = new BlockPos(Minecraft.getInstance().player.func_233580_cy_().getX(), Minecraft.getInstance().player.func_233580_cy_().getY(), Minecraft.getInstance().player.func_233580_cy_().getZ());
            Minecraft.getInstance().player.world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(pos.add(32, 32, 32), pos.add(-32, -32, -32)),
                    input -> input.getUniqueID().toString().contains(SPECIAL)).
                    forEach(living -> Minecraft.getInstance().particles.addEffect(new ParticleVex(living)));
            Minecraft.getInstance().player.world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(pos.add(32, 32, 32), pos.add(-32, -32, -32)),
                    input -> SPECIAL_ENTITIES.containsKey(input.getUniqueID())).
                    forEach(living -> Minecraft.getInstance().particles.addEffect(new ParticleVex(living)));
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
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) return;
        if (event.player.world.getGameTime() % 20 == 0) {
            for (ItemStack stack : event.player.inventory.mainInventory) {
                if (stack.getItem() instanceof ItemInfinity && ((ItemInfinity) stack.getItem()).isSpecial(stack)) {
                    IndustrialForegoing.NETWORK.sendToNearby(event.player.world, new BlockPos(event.player.func_233580_cy_().getX(), event.player.func_233580_cy_().getY(), event.player.func_233580_cy_().getZ()), 64, new SpecialParticleMessage(event.player.getUniqueID()));
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityKill(LivingDeathEvent event) {
        if (event.getEntityLiving().getUniqueID().toString().contains(SPECIAL) && event.getSource().getTrueSource() instanceof PlayerEntity && !(event.getSource().getTrueSource() instanceof FakePlayer)) {
            PlayerEntity player = (PlayerEntity) event.getSource().getTrueSource();
            if (player.getHeldItemMainhand().getItem() instanceof ItemInfinity) {
                player.getHeldItemMainhand().getTag().putBoolean("Special", true);
            }
        }
    }

}
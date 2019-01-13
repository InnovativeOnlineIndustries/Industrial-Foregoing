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
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.proxy.client.particle.ParticleVex;
import com.buuz135.industrial.proxy.network.SpecialParticleMessage;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class OneThreeFiveHandler {

    public static final String SPECIAL = "135135";

    public static HashMap<UUID, Long> SPECIAL_ENTITIES = new HashMap<>();

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().player.world != null && !Minecraft.getMinecraft().isGamePaused() && Minecraft.getMinecraft().player.world.getTotalWorldTime() % 2 == 0) {
            BlockPos pos = new BlockPos(Minecraft.getMinecraft().player.posX, Minecraft.getMinecraft().player.posY, Minecraft.getMinecraft().player.posZ);
            Minecraft.getMinecraft().player.world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(pos.add(32, 32, 32), pos.add(-32, -32, -32)),
                    input -> input.getUniqueID().toString().contains(SPECIAL)).
                    forEach(living -> Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleVex(living)));
            Minecraft.getMinecraft().player.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.add(32, 32, 32), pos.add(-32, -32, -32)),
                    input -> SPECIAL_ENTITIES.containsKey(input.getUniqueID())).
                    forEach(living -> Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleVex(living)));
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
        if (event.player.world.getTotalWorldTime() % 20 == 0) {
            for (ItemStack stack : event.player.inventory.mainInventory) {
                if (stack.getItem().equals(ItemRegistry.itemInfinityDrill) && ItemRegistry.itemInfinityDrill.isSpecial(stack)) {
                    IndustrialForegoing.NETWORK.sendToAllAround(new SpecialParticleMessage(event.player.getUniqueID()), new NetworkRegistry.TargetPoint(event.player.dimension,
                            event.player.posX, event.player.posY, event.player.posZ, 64));
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityKill(LivingDeathEvent event) {
        if (event.getEntityLiving().getUniqueID().toString().contains(SPECIAL) && event.getSource().getTrueSource() instanceof EntityPlayer && !(event.getSource().getTrueSource() instanceof FakePlayer)) {
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
            if (player.getHeldItemMainhand().getItem().equals(ItemRegistry.itemInfinityDrill)) {
                player.getHeldItemMainhand().getTagCompound().setBoolean("Special", true);
            }
        }
    }

}

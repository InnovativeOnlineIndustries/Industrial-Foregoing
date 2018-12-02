package com.buuz135.industrial.item.infinity;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.proxy.client.particle.ParticleVex;
import com.buuz135.industrial.proxy.network.SpecialParticleMessage;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
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

    public static HashMap<UUID, Long> SPECIAL_ENTITIES = new HashMap<>();

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().player.world != null && !Minecraft.getMinecraft().isGamePaused() && Minecraft.getMinecraft().player.world.getTotalWorldTime() % 2 == 0) {
            BlockPos pos = new BlockPos(Minecraft.getMinecraft().player.posX, Minecraft.getMinecraft().player.posY, Minecraft.getMinecraft().player.posZ);
            Minecraft.getMinecraft().player.world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(pos.add(32, 32, 32), pos.add(-32, -32, -32)),
                    input -> input.getUniqueID().toString().contains("135")).
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
    public static void onEntityKill(LivingDropsEvent event) {
        if (event.getEntityLiving().getUniqueID().toString().contains("135")) {
            event.getDrops().add(new EntityItem(event.getEntityLiving().world, event.getEntityLiving().posX, event.getEntityLiving().posY, event.getEntityLiving().posZ, ItemRegistry.itemInfinityDrill.createStack(Long.MAX_VALUE, 1_000_000, true)));
        }
    }

}

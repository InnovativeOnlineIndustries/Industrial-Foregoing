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
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.world != null && !Minecraft.getInstance().isGamePaused() && Minecraft.getInstance().player.world.getTotalWorldTime() % 2 == 0) {
            BlockPos pos = new BlockPos(Minecraft.getInstance().player.posX, Minecraft.getInstance().player.posY, Minecraft.getInstance().player.posZ);
            Minecraft.getInstance().player.world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(pos.add(32, 32, 32), pos.add(-32, -32, -32)),
                    input -> input.getUniqueID().toString().contains(SPECIAL)).
                    forEach(living -> Minecraft.getInstance().effectRenderer.addEffect(new ParticleVex(living)));
            Minecraft.getInstance().player.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.add(32, 32, 32), pos.add(-32, -32, -32)),
                    input -> SPECIAL_ENTITIES.containsKey(input.getUniqueID())).
                    forEach(living -> Minecraft.getInstance().effectRenderer.addEffect(new ParticleVex(living)));
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

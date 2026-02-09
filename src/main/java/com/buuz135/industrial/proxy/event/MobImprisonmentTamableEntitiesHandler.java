package com.buuz135.industrial.proxy.event;

import com.buuz135.industrial.item.MobImprisonmentToolItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.npc.Villager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class MobImprisonmentTamableEntitiesHandler {
    /**
     * This event is used to intercept entities that normally perform special vanilla
     * interactions on right-click (such as villagers opening a trade GUI, animals sitting,
     * or horses being mounted). When the MobImprisonmentTool is used, the vanilla interaction is cancelled
     * and replaced by the capture logic.
     *
     * @param event
     */
    @SubscribeEvent
    public void onPlayerInteractEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide) return;

        if ((!event.getItemStack().isEmpty() && event.getItemStack().getItem() instanceof MobImprisonmentToolItem item)) {
            var player = event.getEntity();
            var livingEntity = event.getTarget();

            if (livingEntity instanceof Villager
                    || livingEntity instanceof TamableAnimal
                    || livingEntity instanceof AbstractHorse) {
                item.capture(event.getItemStack(), (LivingEntity) livingEntity, player);

                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }
}

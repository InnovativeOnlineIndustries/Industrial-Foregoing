package com.buuz135.industrial.proxy.event;

import com.buuz135.industrial.item.MeatFeederItem;
import com.buuz135.industrial.proxy.ItemRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MeatFeederTickHandler {

    @SubscribeEvent
    public void onTick(LivingEvent.LivingUpdateEvent event) {
        if (!event.getEntityLiving().getEntityWorld().isRemote && event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (player.getFoodStats().needFood() || player.getFoodStats().getSaturationLevel() < 10) {
                for (ItemStack stack : player.inventory.mainInventory) {
                    if (stack.getItem().equals(ItemRegistry.meatFeederItem)) {
                        int filledAmount = ((MeatFeederItem) stack.getItem()).getFilledAmount(stack);
                        if (filledAmount >= 400 && (player.getFoodStats().getSaturationLevel() < 20 || player.getFoodStats().getFoodLevel() < 20)) {
                            ((MeatFeederItem) stack.getItem()).drain(stack, 400);
                            player.getFoodStats().addStats(1, 1);
                            return;
                        }
                    }
                }
            }
        }
    }
}

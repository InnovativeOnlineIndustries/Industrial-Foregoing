package com.buuz135.industrial.proxy.event;

import com.buuz135.industrial.tile.world.TreeFluidExtractorTile;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class WorldTickHandler {


    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {

        TreeFluidExtractorTile.WoodLodProgress.woodLodProgressList.stream().filter(woodLodProgress -> woodLodProgress.getProgress() > 0).forEach(woodLodProgress -> woodLodProgress.getWorld().sendBlockBreakProgress(woodLodProgress.getBreakID(), woodLodProgress.getBlockPos(), woodLodProgress.getProgress() - 1));

    }

}

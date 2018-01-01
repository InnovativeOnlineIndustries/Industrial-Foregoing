package com.buuz135.industrial.proxy.event;

import com.buuz135.industrial.tile.world.TreeFluidExtractorTile;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class WorldTickHandler {

    private int tick = 0;

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.type == TickEvent.Type.WORLD && tick >= 20) {
            TreeFluidExtractorTile.WoodLodProgress.woodLodProgressList.stream().filter(woodLodProgress -> woodLodProgress.getProgress() > 0).forEach(woodLodProgress -> woodLodProgress.getWorld().sendBlockBreakProgress(woodLodProgress.getBreakID(), woodLodProgress.getBlockPos(), woodLodProgress.getProgress() - 1));
            tick = 0;
        }
        ++tick;
    }

}

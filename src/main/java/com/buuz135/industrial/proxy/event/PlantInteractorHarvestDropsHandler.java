package com.buuz135.industrial.proxy.event;

import com.buuz135.industrial.tile.agriculture.PlantInteractorTile;
import com.buuz135.industrial.utils.BlockUtils;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlantInteractorHarvestDropsHandler {

    @SubscribeEvent
    public void onHarvest(BlockEvent.HarvestDropsEvent event) {
        for (PlantInteractorTile tile : PlantInteractorTile.WORKING_TILES) {
            if (tile.getWorld().equals(event.getWorld()) && BlockUtils.getBlockPosInAABB(tile.getWorkingArea()).contains(event.getPos())) {
                tile.harvestDrops(event);
                return;
            }
        }
    }
}

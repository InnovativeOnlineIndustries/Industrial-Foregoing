package com.buuz135.industrial.utils.explosion;

import net.minecraftforge.event.TickEvent;

import java.util.ArrayList;
import java.util.List;

public class ExplosionTickHandler {

    public static List<ExplosionHelper.RemovalProcess> removalProcessList = new ArrayList<>();
    public static List<ProcessExplosion> processExplosionList = new ArrayList<>();

    public static void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            for (ExplosionHelper.RemovalProcess removalProcess : new ArrayList<>(removalProcessList)) {
                if (removalProcess.isDead()) {
                    removalProcessList.remove(removalProcess);
                } else {
                    removalProcess.updateProcess();
                }
            }
            for (ProcessExplosion processExplosion : new ArrayList<>(processExplosionList)) {
                if (processExplosion.isDead()) {
                    processExplosionList.remove(processExplosion);
                } else {
                    processExplosion.updateProcess();
                }
            }
        }
    }

}

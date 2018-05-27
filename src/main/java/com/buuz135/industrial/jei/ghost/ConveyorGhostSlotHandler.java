package com.buuz135.industrial.jei.ghost;

import com.buuz135.industrial.gui.conveyor.GuiConveyor;
import com.buuz135.industrial.proxy.block.filter.IFilter;
import mezz.jei.api.gui.IGhostIngredientHandler;

import java.util.ArrayList;
import java.util.List;

public class ConveyorGhostSlotHandler implements IGhostIngredientHandler<GuiConveyor> {

    @Override
    public <I> List<Target<I>> getTargets(GuiConveyor gui, I ingredient, boolean doStart) {
        List<Target<I>> list = new ArrayList<>();
        for (IFilter.GhostSlot ghostSlot : gui.getGhostSlots()) {
            list.add((Target<I>) ghostSlot);
        }
        return list;
    }

    @Override
    public void onComplete() {

    }

    @Override
    public boolean shouldHighlightTargets() {
        return false;
    }
}

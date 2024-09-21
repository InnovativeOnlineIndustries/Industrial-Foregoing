package com.buuz135.industrial.plugin.emi;

import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;

public abstract class EmiDrawableWidget extends Widget {
    @Override
    public Bounds getBounds() {
        return Bounds.EMPTY;
    }
}

package com.buuz135.industrial.block.resourceproduction.tile;

import com.hrznstudio.titanium.block.tile.ActiveTile;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;

public interface ILaserBase<T extends ActiveTile<T>> {

    ProgressBarComponent<T> getBar();

}

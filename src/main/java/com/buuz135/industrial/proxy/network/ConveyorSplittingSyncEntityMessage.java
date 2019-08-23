/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.industrial.proxy.network;

import com.buuz135.industrial.api.conveyor.ConveyorUpgrade;
import com.buuz135.industrial.block.transport.conveyor.ConveyorSplittingUpgrade;
import com.buuz135.industrial.block.transport.tile.ConveyorTile;
import com.hrznstudio.titanium.network.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class ConveyorSplittingSyncEntityMessage extends Message {

    private BlockPos pos;
    private int entityID;
    private String facingCurrent;

    public ConveyorSplittingSyncEntityMessage(BlockPos pos, int entityID, Direction facingCurrent) {
        this.pos = pos;
        this.entityID = entityID;
        this.facingCurrent = facingCurrent.getName();
    }

    public ConveyorSplittingSyncEntityMessage() {
    }

    @Override
    protected void handleMessage(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            TileEntity entity = Minecraft.getInstance().player.world.getTileEntity(pos);
            Direction facingDirection = Direction.byName(facingCurrent);
            if (entity instanceof ConveyorTile) {
                if (((ConveyorTile) entity).hasUpgrade(facingDirection)) {
                    ConveyorUpgrade upgrade = ((ConveyorTile) entity).getUpgradeMap().get(facingDirection);
                    if (upgrade instanceof ConveyorSplittingUpgrade) {
                        ((ConveyorSplittingUpgrade) upgrade).handlingEntities.add(entityID);
                    }
                    ((ConveyorTile) entity).getEntityFilter().add(entityID);
                }
            }
        });
    }


}

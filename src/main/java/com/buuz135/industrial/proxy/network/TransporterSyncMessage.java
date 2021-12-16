/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
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

import com.buuz135.industrial.block.transportstorage.tile.TransporterTile;
import com.hrznstudio.titanium.network.Message;
import com.hrznstudio.titanium.util.TileUtil;
import net.minecraftforge.network.NetworkEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

public class TransporterSyncMessage extends Message {

    private BlockPos pos;
    private CompoundTag sync;
    private int direction;
    private int originDirection;

    public TransporterSyncMessage() {
    }

    public TransporterSyncMessage(BlockPos pos, CompoundTag sync, int direction, int originDirection) {
        this.pos = pos;
        this.sync = sync;
        this.direction = direction;
        this.originDirection = originDirection;
    }

    @Override
    protected void handleMessage(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            TileUtil.getTileEntity(Minecraft.getInstance().level, pos, TransporterTile.class).ifPresent(tileEntity -> {
                tileEntity.getTransporterTypeMap().get(Direction.from3DDataValue(direction)).handleRenderSync(Direction.from3DDataValue(originDirection), sync);
            });
        });
    }
}

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
import com.buuz135.industrial.proxy.block.tile.TileEntityConveyor;
import com.buuz135.industrial.proxy.block.upgrade.ConveyorSplittingUpgrade;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ConveyorSplittingSyncEntityMessage implements IMessage {

    private BlockPos pos;
    private int entityID;
    private EnumFacing facingCurrent;

    public ConveyorSplittingSyncEntityMessage(BlockPos pos, int entityID, EnumFacing facingCurrent) {
        this.pos = pos;
        this.entityID = entityID;
        this.facingCurrent = facingCurrent;
    }

    public ConveyorSplittingSyncEntityMessage() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        this.pos = buffer.readBlockPos();
        this.entityID = buffer.readInt();
        this.facingCurrent = buffer.readEnumValue(EnumFacing.class);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeBlockPos(pos);
        buffer.writeInt(entityID);
        buffer.writeEnumValue(facingCurrent);
    }

    public static class Handler implements IMessageHandler<ConveyorSplittingSyncEntityMessage, IMessage> {

        @Override
        public IMessage onMessage(ConveyorSplittingSyncEntityMessage message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                TileEntity entity = Minecraft.getMinecraft().player.world.getTileEntity(message.pos);
                if (entity instanceof TileEntityConveyor) {
                    if (((TileEntityConveyor) entity).hasUpgrade(message.facingCurrent)) {
                        ConveyorUpgrade upgrade = ((TileEntityConveyor) entity).getUpgradeMap().get(message.facingCurrent);
                        if (upgrade instanceof ConveyorSplittingUpgrade) {
                            ((ConveyorSplittingUpgrade) upgrade).handlingEntities.add(message.entityID);
                        }
                        ((TileEntityConveyor) entity).getEntityFilter().add(message.entityID);
                    }
                }
            });
            return null;
        }
    }
}

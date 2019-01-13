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

import com.buuz135.industrial.proxy.block.tile.TileEntityConveyor;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.IOException;

public class ConveyorButtonInteractMessage implements IMessage {

    private BlockPos pos;
    private int buttonId;
    private EnumFacing facing;
    private NBTTagCompound compound;

    public ConveyorButtonInteractMessage(BlockPos pos, int buttonId, EnumFacing facing, NBTTagCompound compound) {
        this.pos = pos;
        this.buttonId = buttonId;
        this.facing = facing;
        this.compound = compound;
    }

    public ConveyorButtonInteractMessage() {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        pos = buffer.readBlockPos();
        buttonId = buffer.readInt();
        facing = buffer.readEnumValue(EnumFacing.class);
        try {
            compound = buffer.readCompoundTag();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeBlockPos(pos);
        buffer.writeInt(buttonId);
        buffer.writeEnumValue(facing);
        buffer.writeCompoundTag(compound);
    }

    public static class Handler implements IMessageHandler<ConveyorButtonInteractMessage, IMessage> {

        @Override
        public IMessage onMessage(ConveyorButtonInteractMessage message, MessageContext ctx) {
            ctx.getServerHandler().player.getServer().addScheduledTask(() -> {
                BlockPos pos = message.pos;
                TileEntity entity = ctx.getServerHandler().player.getServerWorld().getTileEntity(pos);
                if (entity instanceof TileEntityConveyor) {
                    if (((TileEntityConveyor) entity).hasUpgrade(message.facing)) {
                        ((TileEntityConveyor) entity).getUpgradeMap().get(message.facing).handleButtonInteraction(message.buttonId, message.compound);
                    }
                }
            });
            return null;
        }
    }
}

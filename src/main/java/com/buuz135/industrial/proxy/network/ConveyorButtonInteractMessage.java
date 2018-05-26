package com.buuz135.industrial.proxy.network;

import com.buuz135.industrial.proxy.block.TileEntityConveyor;
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

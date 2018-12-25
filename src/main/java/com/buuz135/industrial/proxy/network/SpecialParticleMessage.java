package com.buuz135.industrial.proxy.network;

import com.buuz135.industrial.item.infinity.OneThreeFiveHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class SpecialParticleMessage implements IMessage {

    public UUID uuid;

    public SpecialParticleMessage(UUID uuid) {
        this.uuid = uuid;
    }

    public SpecialParticleMessage() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        uuid = buffer.readUniqueId();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeUniqueId(uuid);
    }

    public static class Handler implements IMessageHandler<SpecialParticleMessage, IMessage> {

        @Override
        public IMessage onMessage(SpecialParticleMessage message, MessageContext ctx) {
            OneThreeFiveHandler.SPECIAL_ENTITIES.put(message.uuid, System.currentTimeMillis());
            return null;
        }
    }
}

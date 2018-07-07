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

package com.buuz135.industrial.proxy.network;

import com.buuz135.industrial.block.transportstorage.tile.TransporterTile;
import com.hrznstudio.titanium.network.Message;
import com.hrznstudio.titanium.util.TileUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

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

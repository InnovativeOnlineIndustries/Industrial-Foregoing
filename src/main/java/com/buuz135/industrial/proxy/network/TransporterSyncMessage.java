package com.buuz135.industrial.proxy.network;

import com.buuz135.industrial.block.transportstorage.tile.TransporterTile;
import com.hrznstudio.titanium.network.Message;
import com.hrznstudio.titanium.util.TileUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class TransporterSyncMessage extends Message {

    private BlockPos pos;
    private CompoundNBT sync;
    private int direction;
    private int originDirection;

    public TransporterSyncMessage() {
    }

    public TransporterSyncMessage(BlockPos pos, CompoundNBT sync, int direction, int originDirection) {
        this.pos = pos;
        this.sync = sync;
        this.direction = direction;
        this.originDirection = originDirection;
    }

    @Override
    protected void handleMessage(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            TileUtil.getTileEntity(Minecraft.getInstance().world, pos, TransporterTile.class).ifPresent(tileEntity -> {
                tileEntity.getTransporterTypeMap().get(Direction.byIndex(direction)).handleRenderSync(Direction.byIndex(originDirection), sync);
            });
        });
    }
}

package com.buuz135.industrial.proxy.network;

import com.buuz135.industrial.worlddata.BackpackDataManager;
import com.hrznstudio.titanium.network.Message;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.NetworkEvent;

public class BackpackSyncMessage extends Message {

    public String id;
    public CompoundNBT backpack;

    public BackpackSyncMessage(String id, CompoundNBT backpack) {
        this.id = id;
        this.backpack = backpack;
    }

    public BackpackSyncMessage() {
    }

    @Override
    protected void handleMessage(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            BackpackDataManager.BackpackItemHandler handler = new BackpackDataManager.BackpackItemHandler(null);
            handler.deserializeNBT(backpack);
            BackpackDataManager.CLIENT_SIDE_BACKPACKS.put(id, handler);
        });
    }
}

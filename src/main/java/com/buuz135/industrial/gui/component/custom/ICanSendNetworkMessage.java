package com.buuz135.industrial.gui.component.custom;

import net.minecraft.nbt.CompoundTag;

public interface ICanSendNetworkMessage {

    public void sendMessage(int id, CompoundTag compound);
}

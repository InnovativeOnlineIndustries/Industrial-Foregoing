package com.buuz135.industrial.gui.component.custom;

import net.minecraft.nbt.CompoundNBT;

public interface ICanSendNetworkMessage {

    public void sendMessage(int id, CompoundNBT compound);
}

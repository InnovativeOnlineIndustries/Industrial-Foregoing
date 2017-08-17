package com.buuz135.industrial.proxy.client.infopiece;

import net.minecraft.item.ItemStack;

public interface IHasDisplayStack {

    public ItemStack getItemStack();

    public int getAmount();

    public String getDisplayNameUnlocalized();
}



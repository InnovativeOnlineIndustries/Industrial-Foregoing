/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
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

import java.util.UUID;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.item.infinity.item.ItemInfinityBackpack;
import com.buuz135.industrial.module.ModuleTool;
import com.buuz135.industrial.worlddata.BackpackDataManager;
import com.hrznstudio.titanium.network.Message;
import com.hrznstudio.titanium.network.locator.LocatorFactory;
import com.hrznstudio.titanium.network.locator.instance.InventoryStackLocatorInstance;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class BackpackOpenMessage extends Message {

    private boolean forceDisable;

    public BackpackOpenMessage(boolean forceDisable) {
        this.forceDisable = forceDisable;
    }

    public BackpackOpenMessage() {
    }

    @Override
    protected void handleMessage(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer entity = context.getSender();
                ItemInfinityBackpack.findFirstBackpack(entity).ifPresent(target -> {
                    ItemStack stack = target.getFinder().getStackGetter().apply(entity, target.getSlot());
                    if (stack.getItem() instanceof ItemInfinityBackpack) {
                        if (!stack.hasTag() || !stack.getTag().contains("Id")){
                            UUID id = UUID.randomUUID();
                            CompoundTag nbt = stack.getOrCreateTag();
                            nbt.putString("Id", id.toString());
//                            BackpackDataManager.getData(entity.level).createBackPack(id);
                            stack.setTag(nbt);
                        }
                        String id = stack.getTag().getString("Id");
                        if (forceDisable) {
                            ItemInfinityBackpack.setPickUpMode(stack, 3);
                            entity.displayClientMessage(new TranslatableComponent("tooltip.industrialforegoing.backpack.pickup_disabled").withStyle(ChatFormatting.RED), true);
                        } else if (entity.isShiftKeyDown()){
                            int mode = (ItemInfinityBackpack.getPickUpMode(stack) + 1) % 4;
                            ItemInfinityBackpack.setPickUpMode(stack, mode);
                            switch (mode){
                                case 0:
                                    entity.displayClientMessage(new TranslatableComponent("tooltip.industrialforegoing.backpack.pickup_all").withStyle(ChatFormatting.GREEN), true);
                                    return;
                                case 1:
                                    entity.displayClientMessage(new TranslatableComponent("tooltip.industrialforegoing.backpack.item_pickup_enabled").withStyle(ChatFormatting.GREEN), true);
                                    return;
                                case 2:
                                    entity.displayClientMessage(new TranslatableComponent("tooltip.industrialforegoing.backpack.xp_pickup_enabled").withStyle(ChatFormatting.GREEN), true);
                                    return;
                                default:
                                    entity.displayClientMessage(new TranslatableComponent("tooltip.industrialforegoing.backpack.pickup_disabled").withStyle(ChatFormatting.RED), true);
                            }
                        } else {
                            ItemInfinityBackpack.sync(entity.level, id, entity);
                            IndustrialForegoing.NETWORK.get().sendTo(new BackpackOpenedMessage(target.getSlot(), target.getName()), entity.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                            NetworkHooks.openGui(entity, ModuleTool.INFINITY_BACKPACK, buffer ->
                                    LocatorFactory.writePacketBuffer(buffer, new InventoryStackLocatorInstance(target.getName(), target.getSlot())));
                            return;
                        }
                    }
                });
        });
    }
}

package com.buuz135.industrial.proxy.network;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.item.infinity.item.ItemInfinityBackpack;
import com.buuz135.industrial.module.ModuleTool;
import com.buuz135.industrial.worlddata.BackpackDataManager;
import com.hrznstudio.titanium.network.Message;
import com.hrznstudio.titanium.network.locator.LocatorFactory;
import com.hrznstudio.titanium.network.locator.instance.InventoryStackLocatorInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.commons.lang3.tuple.Pair;

import java.util.UUID;

public class BackpackOpenMessage extends Message {

    @Override
    protected void handleMessage(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayerEntity entity = context.getSender();
                Pair<Integer, ItemStack> pair = ItemInfinityBackpack.findFirstBackpack(Minecraft.getInstance().player);
                ItemStack stack = pair.getRight();
                if (stack.getItem() instanceof ItemInfinityBackpack) {
                    if (!stack.hasTag() || !stack.getTag().contains("Id")){
                        UUID id = UUID.randomUUID();
                        CompoundNBT nbt = stack.getOrCreateTag();
                        nbt.putString("Id", id.toString());
                        BackpackDataManager.getData(entity.world).createBackPack(id);
                        stack.setTag(nbt);
                    }
                    String id = stack.getTag().getString("Id");
                    ItemInfinityBackpack.sync(entity.world, id, entity);
                    int finalI = pair.getLeft();
                    IndustrialForegoing.NETWORK.get().sendTo(new BackpackOpenedMessage(pair.getLeft()), entity.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
                    NetworkHooks.openGui(entity, ModuleTool.INFINITY_BACKPACK, buffer ->
                            LocatorFactory.writePacketBuffer(buffer, new InventoryStackLocatorInstance(finalI)));
                    return;
                }

        });
    }
}

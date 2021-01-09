package com.buuz135.industrial.proxy.network;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.item.infinity.item.ItemInfinityBackpack;
import com.buuz135.industrial.module.ModuleTool;
import com.buuz135.industrial.worlddata.BackpackDataManager;
import com.hrznstudio.titanium.network.Message;
import com.hrznstudio.titanium.network.locator.LocatorFactory;
import com.hrznstudio.titanium.network.locator.PlayerInventoryFinder;
import com.hrznstudio.titanium.network.locator.instance.InventoryStackLocatorInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.UUID;

public class BackpackOpenMessage extends Message {

    @Override
    protected void handleMessage(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayerEntity entity = context.getSender();
                PlayerInventoryFinder.Target target = ItemInfinityBackpack.findFirstBackpack(Minecraft.getInstance().player);
                ItemStack stack = target.getFinder().getStackGetter().apply(entity, target.getSlot());
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
                    IndustrialForegoing.NETWORK.get().sendTo(new BackpackOpenedMessage(target.getSlot(), target.getName()), entity.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
                    NetworkHooks.openGui(entity, ModuleTool.INFINITY_BACKPACK, buffer ->
                            LocatorFactory.writePacketBuffer(buffer, new InventoryStackLocatorInstance(target.getName(), target.getSlot())));
                    return;
                }

        });
    }
}

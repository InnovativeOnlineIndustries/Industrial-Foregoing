package com.buuz135.industrial.api;

import com.hrznstudio.titanium.component.inventory.InventoryComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface IMachineSettings {

    public static CompoundTag writeInventory(InventoryComponent component) {
        CompoundTag tag = new CompoundTag();
        for (int i = 0; i < component.getSlots(); i++) {
            var stack = component.getStackInSlot(i);
            if (!stack.isEmpty()) {
                tag.put(i + "", stack.save(new CompoundTag()));
            }
        }
        return tag;
    }

    public static List<ItemStack> readInventory(CompoundTag tag) {
        List<ItemStack> stacks = new ArrayList<>();
        for (String allKey : tag.getAllKeys()) {
            stacks.add(ItemStack.of(tag.getCompound(allKey)));
        }
        return stacks;
    }

    void loadSettings(Player player, CompoundTag tag);

    void saveSettings(Player player, CompoundTag tag);
}

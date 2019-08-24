package com.buuz135.industrial.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class MobEssenceToolItem extends IFCustomItem {

    public MobEssenceToolItem(ItemGroup group) {
        super("mob_essence_tool", group, new Properties().maxStackSize(1), null);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (isInGroup(group)) {
            ItemStack stack = new ItemStack(this);
            addNBT(stack);
            items.add(stack);
        }
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn) {
        super.onCreated(stack, worldIn, playerIn);
        addNBT(stack);
    }

    private void addNBT(ItemStack stack) {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putInt("Kills", 0);
        stack.setTag(compoundNBT);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {

        if (stack.hasTag() && stack.getTag().getInt("Kills") == 0) {
            CompoundNBT compoundNBT = new CompoundNBT();
            compoundNBT.putString("Entity", target.getType().getRegistryName().toString());
            compoundNBT.putInt("Kills", 1);
            stack.setTag(compoundNBT);
            playerIn.setHeldItem(hand, stack);
            target.remove(true);
            return true;
        }
        return super.itemInteractionForEntity(stack, playerIn, target, hand);
    }
}

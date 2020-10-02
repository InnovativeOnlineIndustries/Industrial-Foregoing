package com.buuz135.industrial.item.bucket;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class MilkBucketItem extends BucketItem {

    private final Item realBucket;

    public MilkBucketItem(Supplier<? extends Fluid> p_i51817_1_, Properties p_i51817_2_) {
        super(p_i51817_1_, p_i51817_2_);
        this.realBucket = Items.MILK_BUCKET;
    }

    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        return realBucket.onItemUseFinish(stack, worldIn, entityLiving);
    }

    public int getUseDuration(ItemStack stack) {
        return realBucket.getUseDuration(stack);
    }

    public UseAction getUseAction(ItemStack stack) {
        return realBucket.getUseAction(stack);
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ActionResult<ItemStack> result = super.onItemRightClick(worldIn, playerIn, handIn);
        if (result.getType().isSuccess()) {
            return result;
        }
        return realBucket.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new FluidBucketWrapper(stack);
    }

}

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
package com.buuz135.industrial.item;


import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.world.item.Item.Properties;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class OreBucketItem extends BucketItem {

    private static final String NBT_TAG = "Tag";

    public OreBucketItem(Supplier<? extends Fluid> supplier, Properties builder) {
        super(supplier, builder);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.hasTag() && stack.getTag().contains(NBT_TAG)) return InteractionResultHolder.pass(stack);
        return super.use(world, player, hand);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new FluidBucketWrapper(stack) {
            @Nonnull
            @Override
            public FluidStack getFluid() {
                FluidStack stack = new FluidStack(OreBucketItem.this.getFluid(), FluidAttributes.BUCKET_VOLUME);
                if (container.getOrCreateTag().contains(NBT_TAG)) {
                    String tag = container.getOrCreateTag().getString(NBT_TAG);
                    stack.getOrCreateTag().putString(NBT_TAG, tag);
                }
                return stack;
            }
        };
    }

    @Override
    public Component getName(ItemStack stack) {
        TranslatableComponent displayName = new TranslatableComponent(this.getDescriptionId(stack));
        // TODO: 26/07/2021 Fix
//        if (stack.hasTag() && stack.getTag().contains(NBT_TAG)) {
//            String tag = stack.getTag().getString(NBT_TAG);
//            List<Item> items = SerializationTags.getInstance().getItems().getTagOrEmpty(new ResourceLocation(tag)).getValues();
//            if (items.size() > 0) {
//                TranslatableComponent oreDisplayName = new TranslatableComponent(items.get(0).getDescriptionId());
//                return displayName.append(" (").append(oreDisplayName).append(")");
//            }
//        }
        return displayName;
    }

    /*
    This will not work  due to problems with how forge tags interact with the creative search initialization.
    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            items.add(new ItemStack(this));
            ITagCollection<Item> tags = TagCollectionManager.getManager().getItemTags();
            for (ResourceLocation loc : tags.getIDTagMap().keySet()) {
                String tagName = loc.toString();
                if (!tagName.startsWith("forge:ores/"))
                    continue;
                ItemStack stack = new ItemStack(this);
                stack.getOrCreateTag().putString(NBT_TAG, tagName);
                items.add(stack);
            }
        }
    }
     */
}

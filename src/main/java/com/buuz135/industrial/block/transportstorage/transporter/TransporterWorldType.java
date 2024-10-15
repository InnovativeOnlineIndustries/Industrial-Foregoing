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
package com.buuz135.industrial.block.transportstorage.transporter;

import com.buuz135.industrial.api.IBlockContainer;
import com.buuz135.industrial.api.transporter.FilteredTransporterType;
import com.buuz135.industrial.api.transporter.TransporterType;
import com.buuz135.industrial.api.transporter.TransporterTypeFactory;
import com.buuz135.industrial.block.transportstorage.tile.TransporterTile;
import com.buuz135.industrial.block.transportstorage.transporter.filter.ItemFilter;
import com.buuz135.industrial.proxy.block.filter.RegulatorFilter;
import com.buuz135.industrial.utils.IndustrialTags;
import com.buuz135.industrial.utils.Reference;
import com.google.common.collect.Sets;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.Set;

public class TransporterWorldType extends FilteredTransporterType<ItemStack, IItemHandler> {

    private int extractSlot;

    public TransporterWorldType(IBlockContainer<?> container, TransporterTypeFactory factory, Direction side, TransporterTypeFactory.TransporterAction action) {
        super(container, factory, side, action);
        this.extractSlot = 0;
    }

    @Override
    public RegulatorFilter<ItemStack, IItemHandler> createFilter() {
        return new ItemFilter(20, 20, 5, 3, 16, 64, 1024 * 8, "");
    }

    @Override
    public void update() {
        super.update();
        float speed = getSpeed();
        if (getLevel().isClientSide || getLevel().getGameTime() % (Math.max(1, 4 - speed)) != 0) return;
        IBlockContainer<?> container = getContainer();

        if (!(container instanceof TransporterTile)) return;

        var origin = getLevel().getCapability(Capabilities.ItemHandler.BLOCK, getPos().relative(this.getSide()), getSide().getOpposite());
        if (origin == null) return;

        if (getAction() == TransporterTypeFactory.TransporterAction.EXTRACT) {
            this.handleExtract(origin);
        } else {
            this.handleInsert(origin);
        }
    }

    private void handleExtract(@NotNull IItemHandler origin) {
        if (origin.getSlots() <= 0) return;

        if (extractSlot >= origin.getSlots()
            || origin.getStackInSlot(extractSlot).isEmpty()
            || !this.getFilter().canExtract(origin.getStackInSlot(extractSlot), origin)
        ) findSlot(origin);

        ItemStack extractSlotStack = origin.getStackInSlot(extractSlot);
        if (extractSlotStack.isEmpty() || !this.getFilter().canExtract(extractSlotStack, origin)) return;

        int amount = Math.min((int) Math.ceil(1 * getEfficiency()), this.getFilter().getExtractAmount(extractSlotStack, origin));
        ItemStack extracted = origin.extractItem(extractSlot, amount, false);
        if (extracted.isEmpty()) return;

        ItemEntity item = new ItemEntity(getLevel(), getPos().getX() + 0.5, getPos().getY() + 0.2, getPos().getZ() + 0.5, extracted);
        item.setDeltaMovement(0, 0, 0);
        item.setPickUpDelay(4);
        item.setItem(extracted);
        getLevel().addFreshEntity(item);
    }

    private void handleInsert(IItemHandler origin)  {
        AABB aabb = new AABB(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), this.getPos().getX() + 1, this.getPos().getY() + 1, this.getPos().getZ() + 1);
        for (ItemEntity item : this.getLevel().getEntitiesOfClass(ItemEntity.class, aabb, EntitySelector.ENTITY_STILL_ALIVE)) {
            ItemStack stack = item.getItem().copy();
            ItemStack originStack = item.getItem().copy();
            int possibleAmount = Math.min((int) Math.ceil(1 * getEfficiency()), getFilter().getInsertAmount(stack, origin));
            if (possibleAmount <= 0) continue;

            stack.setCount(Math.min(stack.getCount(), possibleAmount));

            int preInsertionCount = stack.getCount();
            ItemStack insertItem = ItemHandlerHelper.insertItem(origin, stack, false);
            originStack.shrink(preInsertionCount - insertItem.getCount());
            if (originStack.isEmpty()) {
                item.setItem(ItemStack.EMPTY);
                item.discard();
            } else {
                item.setItem(originStack);
            }
        }
    }

    private void findSlot(@NotNull IItemHandler itemHandler) {
        for (int i = this.extractSlot; i < itemHandler.getSlots(); i++) {
            if (!itemHandler.getStackInSlot(i).isEmpty() && getFilter().canExtract(itemHandler.getStackInSlot(i), itemHandler)) {
                this.extractSlot = i;
                return;
            }
        }
        this.extractSlot = 0;
    }

    @Override
    public void updateClient() {
        super.updateClient();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderTransfer(Vector3f pos, Direction direction, int step, PoseStack stack, int combinedOverlayIn, MultiBufferSource buffer, float frame, Level level) {
        super.renderTransfer(pos, direction, step, stack, combinedOverlayIn, buffer, frame, level);
    }

    public static class Factory extends TransporterTypeFactory {

        public Factory() {
            super("world");
        }

        @Override
        public TransporterType create(IBlockContainer container, Direction face, TransporterAction action) {
            return new TransporterWorldType(container, this, face, action);
        }

        @Override
        @Nonnull
        public ResourceLocation getModel(Direction upgradeSide, TransporterAction action) {
            return ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/transporters/world_transporter_" + action.name().toLowerCase() + "_" + upgradeSide.getSerializedName().toLowerCase());
        }

        @Override
        public Set<ResourceLocation> getTextures() {
            return Sets.newHashSet(ResourceLocation.parse("industrialforegoing:block/transporters/world"), ResourceLocation.parse("industrialforegoing:block/base/bottom"));
        }

        @Override
        public boolean canBeAttachedAgainst(Level world, BlockPos pos, Direction face) {
            return world.getCapability(Capabilities.ItemHandler.BLOCK, pos, face) != null;
        }

        @Nonnull
        @Override
        public ResourceLocation getItemModel() {
            return ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/transporters/world_transporter_" + TransporterAction.EXTRACT.name().toLowerCase() + "_" + Direction.NORTH.getSerializedName().toLowerCase());
        }

        @Override
        public void registerRecipe(RecipeOutput consumer) {
            TitaniumShapedRecipeBuilder.shapedRecipe(getUpgradeItem(), 2)
                    .pattern("IPI").pattern("GMG").pattern("ICI")
                    .define('I', Tags.Items.DUSTS_REDSTONE)
                    .define('P', Items.ENDER_PEARL)
                    .define('G', Items.HOPPER)
                    .define('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                    .define('C', Items.DROPPER)
                    .save(consumer);
        }
    }
}

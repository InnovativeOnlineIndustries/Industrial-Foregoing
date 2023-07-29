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

package com.buuz135.industrial.block.transportstorage;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.transportstorage.tile.BlackHoleUnitTile;
import com.buuz135.industrial.capability.BLHBlockItemHandlerItemStack;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleTransportStorage;
import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.datagenerator.loot.block.BasicBlockLootTables;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import com.hrznstudio.titanium.util.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BlackHoleUnitBlock extends IndustrialBlock<BlackHoleUnitTile> {

    private Rarity rarity;

    public BlackHoleUnitBlock(Rarity rarity) {
        super(rarity.name().toLowerCase() + "_black_hole_unit", Properties.copy(Blocks.IRON_BLOCK), BlackHoleUnitTile.class, ModuleTransportStorage.TAB_TRANSPORT);
        this.rarity = rarity;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<BlackHoleUnitTile> getTileEntityFactory() {
        return (p_155268_, p_155269_) -> new BlackHoleUnitTile(this, getRarityType(), rarity, p_155268_, p_155269_);
    }

    /*
    @Override
    public void addAlternatives(DeferredRegistryHelper registry) {
        setItem(registry.register(ForgeRegistries.ITEMS.getRegistryKey(), rarity.name().toLowerCase() + "_black_hole_unit", this.getItemBlockFactory()));
        NBTManager.getInstance().scanTileClassForAnnotations(BlackHoleUnitTile.class);
        tileEntityType = BlockEntityType.Builder.of(this.getTileEntityFactory()::create, new Block[]{this}).build((Type) null);
        registry.registerBlockEntityType(rarity.name().toLowerCase()  + "_black_hole_unit", () -> tileEntityType);
    }*/

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
            if (iItemHandler instanceof BLHBlockItemHandlerItemStack) {
                ItemStack contain = ((BLHBlockItemHandlerItemStack) iItemHandler).getStack();
                if (!contain.isEmpty()) {
                    tooltip.add(Component.literal(ChatFormatting.GOLD + LangUtil.getString("text.industrialforegoing.tooltip.item") + ": " + ChatFormatting.WHITE + contain.getHoverName().getString()));
                    tooltip.add(Component.literal(ChatFormatting.GOLD + LangUtil.getString("text.industrialforegoing.tooltip.contains") + ": " + ChatFormatting.WHITE + new DecimalFormat().format(((BLHBlockItemHandlerItemStack) iItemHandler).getAmount()) + ChatFormatting.DARK_AQUA + " " + LangUtil.getString("text.industrialforegoing.tooltip.items")));
                }
            }
        });
        tooltip.add(Component.literal(ChatFormatting.GOLD + LangUtil.getString("text.industrialforegoing.tooltip.can_hold") + ": " + ChatFormatting.WHITE + new DecimalFormat().format(BlockUtils.getStackAmountByRarity(rarity)) + ChatFormatting.DARK_AQUA + " " + LangUtil.getString("text.industrialforegoing.tooltip.items")));
        if (stack.hasTag() && stack.getTag().contains("BlockEntityTag") && stack.getTag().getCompound("BlockEntityTag").contains("voidItems") && stack.getTag().getCompound("BlockEntityTag").getBoolean("voidItems")) {
            tooltip.add(Component.literal(ChatFormatting.GOLD + LangUtil.getString("text.industrialforegoing.tooltip.void_items")));
        }
    }

    @Override
    public RotatableBlock.RotationType getRotationType() {
        return RotatableBlock.RotationType.FOUR_WAY;
    }

    @Override
    public void attack(BlockState state, Level worldIn, BlockPos pos, Player player) {
        getTile(worldIn, pos).ifPresent(tile -> tile.onClicked(player));
    }

    @Override
    public LootTable.Builder getLootTable(@Nonnull BasicBlockLootTables blockLootTables) {
        CopyNbtFunction.Builder nbtBuilder = CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY);
        nbtBuilder.copy("stored", "BlockEntityTag.stored");
        nbtBuilder.copy("blStack", "BlockEntityTag.blStack");
        nbtBuilder.copy("voidItems", "BlockEntityTag.voidItems");
        nbtBuilder.copy("hasNBT", "BlockEntityTag.hasNBT");
        return blockLootTables.droppingSelfWithNbt(this, nbtBuilder);
    }

    @Override
    public NonNullList<ItemStack> getDynamicDrops(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        return NonNullList.create();
    }

    @Override
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {
        if (rarity == Rarity.COMMON) {
            TitaniumShapedRecipeBuilder.shapedRecipe(this)
                    .pattern("PPP").pattern("CEC").pattern("CMC")
                    .define('P', Tags.Items.INGOTS_IRON)
                    .define('E', IndustrialTags.Items.GEAR_IRON)
                    .define('C', Tags.Items.CHESTS_WOODEN)
                    .define('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                    .save(consumer);
        } else {
            TagKey<Item> tag = IndustrialTags.Items.MACHINE_FRAME_PITY;
            if (rarity == ModuleCore.SIMPLE_RARITY) tag = IndustrialTags.Items.MACHINE_FRAME_SIMPLE;
            if (rarity == ModuleCore.ADVANCED_RARITY) tag = IndustrialTags.Items.MACHINE_FRAME_ADVANCED;
            if (rarity == ModuleCore.SUPREME_RARITY) tag = IndustrialTags.Items.MACHINE_FRAME_SUPREME;
            TitaniumShapedRecipeBuilder.shapedRecipe(this)
                    .pattern("PPP").pattern("NEN").pattern("CMC")
                    .define('P', IndustrialTags.Items.PLASTIC)
                    .define('N', Items.ENDER_EYE)
                    .define('E', Items.ENDER_PEARL)
                    .define('C', Tags.Items.CHESTS_WOODEN)
                    .define('M', tag)
                    .save(consumer);
        }
    }

    private BlockEntityType getRarityType() {
        if (rarity == Rarity.COMMON) return ModuleTransportStorage.BLACK_HOLE_UNIT_COMMON.getRight().get();
        if (rarity == ModuleCore.SIMPLE_RARITY) return ModuleTransportStorage.BLACK_HOLE_UNIT_SIMPLE.getRight().get();
        if (rarity == ModuleCore.ADVANCED_RARITY)
            return ModuleTransportStorage.BLACK_HOLE_UNIT_ADVANCED.getRight().get();
        if (rarity == ModuleCore.SUPREME_RARITY) return ModuleTransportStorage.BLACK_HOLE_UNIT_SUPREME.getRight().get();
        return ModuleTransportStorage.BLACK_HOLE_UNIT_PITY.getRight().get();
    }

    public static class BlackHoleUnitItem extends BlockItem {

        private Rarity rarity;

        public BlackHoleUnitItem(Block blockIn, Properties builder, Rarity rarity) {
            super(blockIn, builder);
            this.rarity = rarity;
        }

        @Nullable
        @Override
        public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
            return new BlackHoleUnitBlock.BlackHoleUnitCapabilityProvider(stack, this.rarity);
        }

        @Nullable
        @Override
        public String getCreatorModId(ItemStack itemStack) {
            return Component.translatable("itemGroup.industrialforegoing_" + ModuleTransportStorage.TAB_TRANSPORT.getResourceLocation().getPath()).getString();
        }
    }

    public static class BlackHoleUnitCapabilityProvider implements ICapabilityProvider {

        private final ItemStack stack;
        private LazyOptional<BLHBlockItemHandlerItemStack> optional;

        public BlackHoleUnitCapabilityProvider(ItemStack stack, Rarity rarity) {
            this.stack = stack;
            this.optional = LazyOptional.of(() -> new BLHBlockItemHandlerItemStack(stack, BlockUtils.getStackAmountByRarity(rarity)));
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap != null && cap.equals(ForgeCapabilities.ITEM_HANDLER)) return optional.cast();
            return LazyOptional.empty();
        }
    }
}

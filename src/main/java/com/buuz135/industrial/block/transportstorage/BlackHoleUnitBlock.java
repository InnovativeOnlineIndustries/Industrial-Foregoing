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
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.datagenerator.loot.block.BasicBlockLootTables;
import com.hrznstudio.titanium.module.api.RegistryManager;
import com.hrznstudio.titanium.nbthandler.NBTManager;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import com.hrznstudio.titanium.util.LangUtil;
import com.mojang.datafixers.types.Type;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.functions.CopyNbt;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Consumer;

public class BlackHoleUnitBlock extends IndustrialBlock<BlackHoleUnitTile> {

    private Rarity rarity;
    private TileEntityType tileEntityType;

    public BlackHoleUnitBlock(Rarity rarity) {
        super(rarity.name().toLowerCase() + "_black_hole_unit",  Properties.from(Blocks.IRON_BLOCK), BlackHoleUnitTile.class, ModuleTransportStorage.TAB_TRANSPORT);
        this.rarity = rarity;
    }

    @Override
    public IFactory<BlackHoleUnitTile> getTileEntityFactory() {
        return () -> new BlackHoleUnitTile(this, rarity);
    }

    @Override
    public void addAlternatives(RegistryManager<?> registry) {
        BlockItem item = this.getItemBlockFactory().create();
        setItem(item);
        registry.content(Item.class, item);
        NBTManager.getInstance().scanTileClassForAnnotations(BlackHoleUnitTile.class);
        tileEntityType = TileEntityType.Builder.create(this.getTileEntityFactory()::create, new Block[]{this}).build((Type) null);
        tileEntityType.setRegistryName(new ResourceLocation(Reference.MOD_ID, rarity.name().toLowerCase()  + "_black_hole_unit"));
        registry.content(TileEntityType.class, tileEntityType);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iItemHandler -> {
            if (iItemHandler instanceof BLHBlockItemHandlerItemStack){
                ItemStack contain = ((BLHBlockItemHandlerItemStack) iItemHandler).getStack();
                if (!contain.isEmpty()){
                    tooltip.add(new StringTextComponent(TextFormatting.GOLD + LangUtil.getString("text.industrialforegoing.tooltip.item") + ": " + TextFormatting.WHITE+ contain.getDisplayName().getString()));
                    tooltip.add(new StringTextComponent(TextFormatting.GOLD + LangUtil.getString("text.industrialforegoing.tooltip.contains") +": "  + TextFormatting.WHITE+ new DecimalFormat().format(((BLHBlockItemHandlerItemStack) iItemHandler).getAmount()) + TextFormatting.DARK_AQUA + " " + LangUtil.getString("text.industrialforegoing.tooltip.items")));
                }
            }
        });
        tooltip.add(new StringTextComponent(TextFormatting.GOLD +  LangUtil.getString("text.industrialforegoing.tooltip.can_hold") + ": " + TextFormatting.WHITE+ new DecimalFormat().format(BlockUtils.getStackAmountByRarity(rarity)) + TextFormatting.DARK_AQUA + " "  +LangUtil.getString("text.industrialforegoing.tooltip.items")));
        if (stack.hasTag() && stack.getTag().contains("BlockEntityTag") && stack.getTag().getCompound("BlockEntityTag").contains("voidItems") && stack.getTag().getCompound("BlockEntityTag").getBoolean("voidItems")){
            tooltip.add(new StringTextComponent(TextFormatting.GOLD + LangUtil.getString("text.industrialforegoing.tooltip.void_items")));
        }
    }

    @Override
    public RotatableBlock.RotationType getRotationType() {
        return RotatableBlock.RotationType.FOUR_WAY;
    }

    @Override
    public TileEntityType getTileEntityType() {
        return tileEntityType;
    }

    @Override
    public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        getTile(worldIn, pos).ifPresent(tile -> tile.onClicked(player));
    }

    @Override
    public LootTable.Builder getLootTable(@Nonnull BasicBlockLootTables blockLootTables) {
        CopyNbt.Builder nbtBuilder = CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY);
        nbtBuilder.replaceOperation("stored",  "BlockEntityTag.stored");
        nbtBuilder.replaceOperation("blStack",  "BlockEntityTag.blStack");
        nbtBuilder.replaceOperation("voidItems",  "BlockEntityTag.voidItems");
        nbtBuilder.replaceOperation("hasNBT",  "BlockEntityTag.hasNBT");
        return blockLootTables.droppingSelfWithNbt(this, nbtBuilder);
    }

    @Override
    public NonNullList<ItemStack> getDynamicDrops(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        return NonNullList.create();
    }

    @Override
    public IFactory<BlockItem> getItemBlockFactory() {
        return () -> (BlockItem) new BlackHoleUnitItem(this, new Item.Properties().group(this.getItemGroup()), rarity).setRegistryName(this.getRegistryName());
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        if (rarity == Rarity.COMMON){
            TitaniumShapedRecipeBuilder.shapedRecipe(this)
                    .patternLine("PPP").patternLine("CEC").patternLine("CMC")
                    .key('P', IndustrialTags.Items.PLASTIC)
                    .key('E', IndustrialTags.Items.GEAR_IRON)
                    .key('C', Tags.Items.CHESTS_WOODEN)
                    .key('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                    .build(consumer);
        } else {
            ITag tag = IndustrialTags.Items.MACHINE_FRAME_PITY;
            if (rarity == ModuleCore.SIMPLE_RARITY) tag = IndustrialTags.Items.MACHINE_FRAME_SIMPLE;
            if (rarity == ModuleCore.ADVANCED_RARITY) tag = IndustrialTags.Items.MACHINE_FRAME_ADVANCED;
            if (rarity == ModuleCore.SUPREME_RARITY) tag = IndustrialTags.Items.MACHINE_FRAME_SUPREME;
            TitaniumShapedRecipeBuilder.shapedRecipe(this)
                    .patternLine("PPP").patternLine("NEN").patternLine("CMC")
                    .key('P', IndustrialTags.Items.PLASTIC)
                    .key('N', Items.ENDER_EYE)
                    .key('E', Items.ENDER_PEARL)
                    .key('C', Tags.Items.CHESTS_WOODEN)
                    .key('M', tag)
                    .build(consumer);
        }
    }

    public class BlackHoleUnitItem extends BlockItem{

        private Rarity rarity;

        public BlackHoleUnitItem(Block blockIn, Properties builder, Rarity rarity) {
            super(blockIn, builder);
            this.rarity = rarity;
        }

        @Nullable
        @Override
        public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
            return new BlackHoleUnitBlock.BlackHoleUnitCapabilityProvider(stack, this.rarity);
        }
    }

    public class BlackHoleUnitCapabilityProvider implements ICapabilityProvider {

        private final ItemStack stack;
        private LazyOptional<BLHBlockItemHandlerItemStack> optional;

        public BlackHoleUnitCapabilityProvider(ItemStack stack, Rarity rarity) {
            this.stack = stack;
            this.optional = LazyOptional.of(() -> new BLHBlockItemHandlerItemStack(stack, BlockUtils.getStackAmountByRarity(rarity)));
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap != null && cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) return optional.cast();
            return LazyOptional.empty();
        }
    }
}

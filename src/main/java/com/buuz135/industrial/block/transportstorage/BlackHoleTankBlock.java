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
import com.buuz135.industrial.block.transportstorage.tile.BlackHoleTankTile;
import com.buuz135.industrial.capability.BlockFluidHandlerItemStack;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleTransportStorage;
import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.IndustrialTags;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.datagenerator.loot.block.BasicBlockLootTables;
import com.hrznstudio.titanium.module.api.RegistryManager;
import com.hrznstudio.titanium.nbthandler.NBTManager;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import com.hrznstudio.titanium.util.LangUtil;
import com.mojang.datafixers.types.Type;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Consumer;

import com.hrznstudio.titanium.block.RotatableBlock.RotationType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.item.Item.Properties;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;

public class BlackHoleTankBlock extends IndustrialBlock<BlackHoleTankTile> {

    private Rarity rarity;
    private BlockEntityType tileEntityType;

    public BlackHoleTankBlock(Rarity rarity) {
        super(rarity.name().toLowerCase() + "_black_hole_tank",  Properties.copy(Blocks.IRON_BLOCK), BlackHoleTankTile.class, ModuleTransportStorage.TAB_TRANSPORT);
        this.rarity = rarity;
    }

    @Override
    public IFactory<BlackHoleTankTile> getTileEntityFactory() {
        return () -> new BlackHoleTankTile(this, rarity);
    }

    @Override
    public void addAlternatives(RegistryManager<?> registry) {
        BlockItem item = this.getItemBlockFactory().create();
        setItem(item);
        registry.content(Item.class, item);
        NBTManager.getInstance().scanTileClassForAnnotations(BlackHoleTankTile.class);
        tileEntityType = BlockEntityType.Builder.of(this.getTileEntityFactory()::create, new Block[]{this}).build((Type) null);
        tileEntityType.setRegistryName(new ResourceLocation(Reference.MOD_ID, rarity.name().toLowerCase() + "_black_hole_tank"));
        registry.content(BlockEntityType.class, tileEntityType);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(iFluidHandlerItem -> {
            if (!iFluidHandlerItem.getFluidInTank(0).isEmpty()){
                tooltip.add(new TextComponent(ChatFormatting.GOLD + LangUtil.getString("text.industrialforegoing.tooltip.contains") +": " + ChatFormatting.WHITE + new DecimalFormat().format(iFluidHandlerItem.getFluidInTank(0).getAmount()) + ChatFormatting.YELLOW + LangUtil.getString("tooltip.industrialforegoing.mb_of",ChatFormatting.DARK_AQUA+ iFluidHandlerItem.getFluidInTank(0).getDisplayName().getString())));
            }
        });
        tooltip.add(new TextComponent(ChatFormatting.GOLD + LangUtil.getString("text.industrialforegoing.tooltip.can_hold") + ": " + ChatFormatting.WHITE+ new DecimalFormat().format(BlockUtils.getFluidAmountByRarity(rarity)) + ChatFormatting.DARK_AQUA + LangUtil.getString("text.industrialforegoing.tooltip.mb")));
    }

    @Override
    public IFactory<BlockItem> getItemBlockFactory() {
        return () -> (BlockItem) new BlackHoleTankItem(this, new Item.Properties().tab(this.getItemGroup()), rarity).setRegistryName(this.getRegistryName());
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public BlockEntityType getTileEntityType() {
        return tileEntityType;
    }

    @Override
    public void attack(BlockState state, Level worldIn, BlockPos pos, Player player) {
        //getTile(worldIn, pos).ifPresent(tile -> tile.onClicked(player));
    }

    @Override
    public LootTable.Builder getLootTable(@Nonnull BasicBlockLootTables blockLootTables) {
        CopyNbtFunction.Builder nbtBuilder = CopyNbtFunction.copyData(CopyNbtFunction.DataSource.BLOCK_ENTITY);
        nbtBuilder.copy("tank",  "BlockEntityTag.tank");
        nbtBuilder.copy("filter",  "BlockEntityTag.filter");
        return blockLootTables.droppingSelfWithNbt(this, nbtBuilder);
    }

    @Override
    public NonNullList<ItemStack> getDynamicDrops(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        return NonNullList.create();
    }

    @Override
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {
        if (rarity == Rarity.COMMON){
            TitaniumShapedRecipeBuilder.shapedRecipe(this)
                    .pattern("PPP").pattern("CEC").pattern("CMC")
                    .define('P', Tags.Items.INGOTS_IRON)
                    .define('E', IndustrialTags.Items.GEAR_IRON)
                    .define('C', Items.BUCKET)
                    .define('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                    .save(consumer);
        } else {
            Tag tag = IndustrialTags.Items.MACHINE_FRAME_PITY;
            if (rarity == ModuleCore.SIMPLE_RARITY) tag = IndustrialTags.Items.MACHINE_FRAME_SIMPLE;
            if (rarity == ModuleCore.ADVANCED_RARITY) tag = IndustrialTags.Items.MACHINE_FRAME_ADVANCED;
            if (rarity == ModuleCore.SUPREME_RARITY) tag = IndustrialTags.Items.MACHINE_FRAME_SUPREME;
            TitaniumShapedRecipeBuilder.shapedRecipe(this)
                    .pattern("PPP").pattern("NEN").pattern("CMC")
                    .define('P', IndustrialTags.Items.PLASTIC)
                    .define('N', Items.ENDER_EYE)
                    .define('E', Items.ENDER_PEARL)
                    .define('C', Items.BUCKET)
                    .define('M', tag)
                    .save(consumer);
        }
    }

    public class BlackHoleTankItem extends BlockItem{

        private Rarity rarity;

        public BlackHoleTankItem(Block blockIn, Properties builder, Rarity rarity) {
            super(blockIn, builder);
            this.rarity = rarity;
        }

        @Nullable
        @Override
        public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
            return new BlackHoleTankCapabilityProvider(stack, this.rarity);
        }

        @Nullable
        @Override
        public String getCreatorModId(ItemStack itemStack) {
            return new TranslatableComponent("itemGroup." + this.category.getRecipeFolderName()).getString();
        }
    }

    public class BlackHoleTankCapabilityProvider implements ICapabilityProvider {

        private final ItemStack stack;
        private LazyOptional<FluidHandlerItemStack> iFluidHandlerItemLazyOptional;

        public BlackHoleTankCapabilityProvider(ItemStack stack, Rarity rarity) {
            this.stack = stack;
            this.iFluidHandlerItemLazyOptional = LazyOptional.of(() -> new BlockFluidHandlerItemStack(stack, new ItemStack(stack.getItem()), BlockUtils.getFluidAmountByRarity(rarity), "tank"));
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap != null && cap.equals(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)) return iFluidHandlerItemLazyOptional.cast();
            return LazyOptional.empty();
        }
    }
}

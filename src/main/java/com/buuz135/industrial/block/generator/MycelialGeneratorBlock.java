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
package com.buuz135.industrial.block.generator;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.generator.mycelial.IMycelialGeneratorType;
import com.buuz135.industrial.block.generator.tile.MycelialGeneratorTile;
import com.buuz135.industrial.module.ModuleGenerator;
import com.buuz135.industrial.utils.Reference;
import com.buuz135.industrial.worlddata.MycelialDataManager;
import com.hrznstudio.titanium.module.api.RegistryManager;
import com.hrznstudio.titanium.nbthandler.NBTManager;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import com.mojang.datafixers.types.Type;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class MycelialGeneratorBlock extends IndustrialBlock<MycelialGeneratorTile> {

    private BlockEntityType tileEntityType;
    private final IMycelialGeneratorType type;

    public MycelialGeneratorBlock(IMycelialGeneratorType type) {
        super("mycelial_" + type.getName(), Properties.copy(Blocks.IRON_BLOCK), MycelialGeneratorTile.class, ModuleGenerator.TAB_GENERATOR);
        this.type = type;
    }

    @Override
    public void addAlternatives(RegistryManager<?> registry) {
        BlockItem item = (BlockItem) this.getItemBlockFactory().create();
        setItem(item);
        registry.content(Item.class, item);
        NBTManager.getInstance().scanTileClassForAnnotations(MycelialGeneratorTile.class);
        tileEntityType = BlockEntityType.Builder.of(this.getTileEntityFactory()::create, new Block[]{this}).build((Type) null);
        tileEntityType.setRegistryName(new ResourceLocation(Reference.MOD_ID, "mycelial_generator_"+type.getName()));
        registry.content(BlockEntityType.class, tileEntityType);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<MycelialGeneratorTile> getTileEntityFactory() {
        return (BlockEntityType.BlockEntitySupplier<MycelialGeneratorTile>) new MycelialGeneratorTile(this, type);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    public IMycelialGeneratorType getType() {
        return type;
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        BlockEntity entity = worldIn.getBlockEntity(pos);
        if (entity instanceof MycelialGeneratorTile && placer != null){
            ((MycelialGeneratorTile) entity).setOwner(placer.getUUID().toString());
        }
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        BlockEntity entity = worldIn.getBlockEntity(pos);
        if (entity instanceof  MycelialGeneratorTile){
            MycelialDataManager.removeGeneratorInfo(((MycelialGeneratorTile) entity).getOwner(), worldIn, pos, type);
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder recipe = TitaniumShapedRecipeBuilder.shapedRecipe(this).pattern("BBB").pattern("BCB").pattern("RMR")
                .define('R', Items.REDSTONE);
        type.addIngredients(recipe).save(consumer);
    }

    @Override
    public BlockEntityType getTileEntityType() {
        return tileEntityType;
    }
}

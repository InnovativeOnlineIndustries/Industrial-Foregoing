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
import com.buuz135.industrial.block.generator.tile.MycelialReactorTile;
import com.buuz135.industrial.module.ModuleGenerator;
import com.buuz135.industrial.worlddata.MycelialDataManager;
import com.hrznstudio.titanium.api.IFactory;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import com.hrznstudio.titanium.block.RotatableBlock.RotationType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class MycelialReactorBlock extends IndustrialBlock<MycelialReactorTile> {

    public MycelialReactorBlock() {
        super("mycelial_reactor", Properties.copy(Blocks.IRON_BLOCK), MycelialReactorTile.class, ModuleGenerator.TAB_GENERATOR);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<MycelialReactorTile> getTileEntityFactory() {
        return MycelialReactorTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        BlockEntity entity = worldIn.getBlockEntity(pos);
        if (entity instanceof MycelialReactorTile && placer != null){
            ((MycelialReactorTile) entity).setOwner(placer.getUUID().toString());
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray) {
        BlockEntity tileEntity = worldIn.getBlockEntity(pos);
        if (player.isShiftKeyDown() && !worldIn.isClientSide && tileEntity instanceof MycelialReactorTile){
//            List<String> available = MycelialDataManager.getReactorAvailable(((MycelialReactorTile) tileEntity).getOwner(), worldIn, false);
//            if (available.size() != IMycelialGeneratorType.TYPES.size()){
//                player.sendMessage(new TextComponent("Generators not running:").withStyle(ChatFormatting.RED), player.getUUID());
//            }
//            for (IMycelialGeneratorType type : IMycelialGeneratorType.TYPES) {
//                if (!available.contains(type.getName())){
//                    player.sendMessage(new TranslatableComponent("block.industrialforegoing.mycelial_" + type.getName()).withStyle(ChatFormatting.RED), player.getUUID());
//                }
//            }
        }
        return super.use(state, worldIn, pos, player, hand, ray);
    }

}

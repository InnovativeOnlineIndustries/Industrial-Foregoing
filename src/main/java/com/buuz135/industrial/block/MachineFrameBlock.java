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
package com.buuz135.industrial.block;

import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.block.BasicBlock;
import com.hrznstudio.titanium.datagenerator.loot.block.BasicBlockLootTables;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.loot.LootTable;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class MachineFrameBlock extends BasicBlock {

    private MachineFrameItem item;
    private Rarity rarity;

    public MachineFrameBlock(Rarity rarity, ItemGroup group) {
        super(Properties.from(Blocks.IRON_BLOCK));
        this.setItemGroup(group);
        this.rarity = rarity;
    }

    @Override
    public Item asItem() {
        return item;
    }

    @Override
    public IFactory<BlockItem> getItemBlockFactory() {
        return () -> item = new MachineFrameItem(this, rarity, this.getItemGroup());
    }

    @Override
    public LootTable.Builder getLootTable(BasicBlockLootTables blockLootTables) {
        return blockLootTables.droppingNothing();
    }

    public class MachineFrameItem extends BlockItem {

        public MachineFrameItem(BasicBlock blockIn, Rarity rarity, ItemGroup group) {
            super(blockIn, new Item.Properties().group(group).rarity(rarity));
            this.setRegistryName(blockIn.getRegistryName());
        }

        @Override
        protected boolean canPlace(BlockItemUseContext p_195944_1_, BlockState p_195944_2_) {
            return false;
        }

        @Nullable
        @Override
        public String getCreatorModId(ItemStack itemStack) {
            return new TranslationTextComponent("itemGroup." + this.group.getPath()).getString();
        }

    }
}

package com.buuz135.industrial.block;

import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.block.BlockBase;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.*;

public class MachineFrameBlock extends BlockBase {

    private MachineFrameItem item;
    private Rarity rarity;

    public MachineFrameBlock(String name, Rarity rarity, ItemGroup group) {
        super("machine_frame_" + name, Properties.from(Blocks.IRON_BLOCK));
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

    public class MachineFrameItem extends BlockItem {

        public MachineFrameItem(BlockBase blockIn, Rarity rarity, ItemGroup group) {
            super(blockIn, new Item.Properties().group(group).rarity(rarity));
            this.setRegistryName(blockIn.getRegistryName());
        }

        @Override
        protected boolean canPlace(BlockItemUseContext p_195944_1_, BlockState p_195944_2_) {
            return false;
        }
    }
}

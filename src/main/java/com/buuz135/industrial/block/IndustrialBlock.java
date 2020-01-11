package com.buuz135.industrial.block;

import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.block.tile.BasicTile;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public abstract class IndustrialBlock<T extends BasicTile<T>> extends RotatableBlock<T> {

    public IndustrialBlock(String name, Properties properties, Class<T> tileClass, ItemGroup group) {
        super(name, properties, tileClass);
        setItemGroup(group);
    }

    @Override
    public IFactory<BlockItem> getItemBlockFactory() {
        return () -> new IndustrialBlockItem(this, this.getItemGroup());
    }

    public class IndustrialBlockItem extends BlockItem {

        public IndustrialBlockItem(Block blockIn, ItemGroup group) {
            super(blockIn, new Properties().group(group));
            this.setRegistryName(blockIn.getRegistryName());
        }

        @Nullable
        @Override
        public String getCreatorModId(ItemStack itemStack) {
            return new TranslationTextComponent(this.group.getTranslationKey()).getFormattedText();
        }

    }
}

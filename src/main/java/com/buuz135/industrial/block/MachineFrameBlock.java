package com.buuz135.industrial.block;

import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.block.BasicBlock;
import com.hrznstudio.titanium.recipe.generator.TitaniumLootTableProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class MachineFrameBlock extends BasicBlock {

    public static Rarity PITY_RARITY = Rarity.create("pity", TextFormatting.GREEN);
    public static Rarity SIMPLE_RARITY = Rarity.create("simple", TextFormatting.AQUA);
    public static Rarity ADVANCED_RARITY = Rarity.create("advanced", TextFormatting.LIGHT_PURPLE);
    public static Rarity SUPREME_RARITY = Rarity.create("supreme", TextFormatting.GOLD);

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

    @Override
    public void createLootTable(TitaniumLootTableProvider provider) {
        provider.createEmpty(this);
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
            return new TranslationTextComponent("itemGroup." + this.group.getPath()).getFormattedText();
        }

    }
}

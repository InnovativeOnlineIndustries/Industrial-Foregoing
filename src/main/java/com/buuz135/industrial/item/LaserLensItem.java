package com.buuz135.industrial.item;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.utils.RecipeUtils;
import lombok.Getter;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IForgeRegistry;

public class LaserLensItem extends IFCustomItem {

    @Getter
    private boolean inverted;

    public LaserLensItem(boolean inverted) {
        super("laser_lens" + (inverted ? "_inverted" : ""));
        setMaxStackSize(1);
        setHasSubtypes(true);
        this.inverted = inverted;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (isInCreativeTab(tab))
            for (int i = 0; i < 16; ++i) subItems.add(new ItemStack(this, 1, i));
    }

    @Override
    public void register(IForgeRegistry<Item> items) {
        super.register(items);
    }

    public void createRecipe() {
        for (int i = 0; i < 16; ++i)
            RecipeUtils.addShapedRecipe(new ItemStack(this, 1, i), " i ", "ipi", " i ", 'i', inverted ? new ItemStack(Items.IRON_INGOT) : new ItemStack(ItemRegistry.pinkSlime),
                    'p', new ItemStack(Blocks.STAINED_GLASS_PANE, 1, i));
    }

    @Override
    public void registerRender() {
        for (int i = 0; i < 16; ++i)
            ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(this.getRegistryName().toString() + i, "inventory"));
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return new TextComponentTranslation("item.fireworksCharge." + EnumDyeColor.byMetadata(stack.getMetadata()).getUnlocalizedName().replaceAll("_", "")).getFormattedText() + " " + super.getItemStackDisplayName(stack);
    }
}

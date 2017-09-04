package com.buuz135.industrial.item.addon;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.ndrei.teslacorelib.tileentities.SidedTileEntity;

import javax.annotation.Nullable;
import java.util.List;

public class RangeAddonItem extends CustomAddon {

    public RangeAddonItem() {
        super("range_addon");
        setMaxStackSize(1);
        setHasSubtypes(true);
    }

    @Override
    public boolean canBeAddedTo(SidedTileEntity machine) {
        return !machine.getAddons().contains(this) && machine instanceof WorkingAreaElectricMachine && ((WorkingAreaElectricMachine) machine).canAcceptRangeUpgrades();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) for (int i = 0; i < 12; ++i) items.add(new ItemStack(this, 1, i));
    }


    @Override
    public void registerRenderer() {
        for (int i = 0; i < 12; ++i)
            ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(this.getRegistryName().toString() + i, "inventory"));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add("Tier: " + stack.getMetadata());
    }

    public void createRecipe() {
        ItemStack[] items = new ItemStack[]{new ItemStack(Blocks.COBBLESTONE), new ItemStack(Items.DYE, 1, 4), new ItemStack(Items.COAL), new ItemStack(Items.QUARTZ), new ItemStack(Blocks.RED_SANDSTONE), new ItemStack(Blocks.GLOWSTONE), new ItemStack(Items.IRON_INGOT), new ItemStack(Items.GOLD_INGOT), new ItemStack(Blocks.IRON_BLOCK), new ItemStack(Items.DIAMOND), new ItemStack(Items.PRISMARINE_SHARD), new ItemStack(Items.EMERALD)};
        for (int i = 0; i < 12; ++i) {
            RecipeUtils.addShapedRecipe(new ItemStack(this, 1, i), "ipi", "igi", "ipi",
                    'i', items[i],
                    'p', ItemRegistry.plastic,
                    'g', "paneGlass");
        }

    }
}

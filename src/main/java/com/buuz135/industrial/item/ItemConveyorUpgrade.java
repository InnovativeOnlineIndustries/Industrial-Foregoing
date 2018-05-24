package com.buuz135.industrial.item;

import com.buuz135.industrial.api.conveyor.ConveyorUpgradeFactory;
import com.buuz135.industrial.api.conveyor.IConveyorContainer;
import com.buuz135.industrial.registry.IFRegistries;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;

public class ItemConveyorUpgrade extends IFCustomItem {
    public ItemConveyorUpgrade() {
        super("conveyor_upgrade");
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof IConveyorContainer) {
            ConveyorUpgradeFactory factory = IFRegistries.CONVEYOR_UPGRADE_REGISTRY.getValue(player.getHeldItem(hand).getMetadata()+1);
            if (factory != null&&!((IConveyorContainer) tile).hasUpgrade(player.getHorizontalFacing())) {
                ((IConveyorContainer) tile).addUpgrade(player.getHorizontalFacing(), factory);
                return EnumActionResult.SUCCESS;
            }
        }
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        ConveyorUpgradeFactory factory = IFRegistries.CONVEYOR_UPGRADE_REGISTRY.getValue(stack.getMetadata() + 1);
        if (factory == null)
            return "conveyor.upgrade.error";
        return String.format("conveyor.upgrade.%s.%s", factory.getRegistryName().getResourceDomain(), factory.getRegistryName().getResourcePath());
    }

    @Override
    public void registerRender() {
        for (ConveyorUpgradeFactory factory : IFRegistries.CONVEYOR_UPGRADE_REGISTRY.getValuesCollection()) {
            ModelLoader.setCustomModelResourceLocation(this, IFRegistries.CONVEYOR_UPGRADE_REGISTRY.getID(factory) - 1, new ModelResourceLocation(factory.getItemModel(), "inventory"));
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (ConveyorUpgradeFactory factory : IFRegistries.CONVEYOR_UPGRADE_REGISTRY.getValuesCollection()) {
            items.add(new ItemStack(this, 1, IFRegistries.CONVEYOR_UPGRADE_REGISTRY.getID(factory)-1));
        }
    }
}

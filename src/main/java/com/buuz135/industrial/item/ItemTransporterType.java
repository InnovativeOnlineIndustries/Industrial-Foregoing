package com.buuz135.industrial.item;

import com.buuz135.industrial.api.IBlockContainer;
import com.buuz135.industrial.api.transporter.TransporterTypeFactory;
import com.buuz135.industrial.module.ModuleTransportStorage;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.function.Consumer;

public class ItemTransporterType extends IFCustomItem {

    public TransporterTypeFactory factory;

    public ItemTransporterType(TransporterTypeFactory transporterTypeFactory, ItemGroup group) {
        super(transporterTypeFactory.getRegistryName().getPath() + "_transporter_type", group);
        this.factory = transporterTypeFactory;
        this.factory.setUpgradeItem(this);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        BlockPos pos = context.getPos().offset(context.getFace());
        if (!context.getWorld().getBlockState(context.getPos().offset(context.getFace())).isIn(ModuleTransportStorage.TRANSPORTER)) { //TODO Chekc if air
            context.getWorld().setBlockState(context.getPos().offset(context.getFace()), ModuleTransportStorage.TRANSPORTER.getDefaultState());
            pos = context.getPos().offset(context.getFace());
        }
        TileEntity tile = context.getWorld().getTileEntity(pos);
        if (tile instanceof IBlockContainer) {
            Direction side = context.getFace().getOpposite();
            if (!((IBlockContainer) tile).hasUpgrade(side) && factory.canBeAttachedAgainst(context.getWorld(), context.getPos(), side)) {
                ((IBlockContainer) tile).addUpgrade(side, factory);
                if (!context.getPlayer().isCreative()) context.getItem().shrink(1);
                return ActionResultType.SUCCESS;
            }
        }

        return ActionResultType.PASS;
    }


    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {

    }
}

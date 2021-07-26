package com.buuz135.industrial.item;

import com.buuz135.industrial.api.IBlockContainer;
import com.buuz135.industrial.api.transporter.TransporterTypeFactory;
import com.buuz135.industrial.module.ModuleTransportStorage;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import java.util.function.Consumer;

public class ItemTransporterType extends IFCustomItem {

    public TransporterTypeFactory factory;

    public ItemTransporterType(TransporterTypeFactory transporterTypeFactory, CreativeModeTab group) {
        super(transporterTypeFactory.getRegistryName().getPath() + "_transporter_type", group);
        this.factory = transporterTypeFactory;
        this.factory.setUpgradeItem(this);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        Direction side = context.getClickedFace().getOpposite();
        if (factory.canBeAttachedAgainst(context.getLevel(), context.getClickedPos(), side.getOpposite())) {
            if (!context.getLevel().getBlockState(context.getClickedPos().relative(context.getClickedFace())).is(ModuleTransportStorage.TRANSPORTER) && context.getLevel().getBlockState(context.getClickedPos().relative(context.getClickedFace())).isAir()) {
                context.getLevel().setBlockAndUpdate(context.getClickedPos().relative(context.getClickedFace()), ModuleTransportStorage.TRANSPORTER.defaultBlockState());
                pos = context.getClickedPos().relative(context.getClickedFace());
            }
            BlockEntity tile = context.getLevel().getBlockEntity(pos);
            if (tile instanceof IBlockContainer) {
                if (!((IBlockContainer) tile).hasUpgrade(side) && factory.canBeAttachedAgainst(context.getLevel(), context.getClickedPos(), side.getOpposite())) {
                    ((IBlockContainer) tile).addUpgrade(side, factory);
                    if (!context.getPlayer().isCreative()) context.getItemInHand().shrink(1);
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.PASS;
    }


    @Override
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {

    }
}

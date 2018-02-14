package com.buuz135.industrial.item.addon.movility;


import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ITransferAction {

    boolean actionTransfer(World world, BlockPos pos, EnumFacing facing, int transferAmount);

}

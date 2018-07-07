package com.buuz135.industrial.gui;

import com.buuz135.industrial.book.GUIBookMain;
import com.buuz135.industrial.gui.conveyor.ContainerConveyor;
import com.buuz135.industrial.gui.conveyor.GuiConveyor;
import com.buuz135.industrial.proxy.block.BlockBase;
import com.buuz135.industrial.proxy.block.Cuboid;
import com.buuz135.industrial.proxy.block.tile.TileEntityConveyor;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {

    public static final int BOOK = 0;
    public static final int CONVEYOR = 1;

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == CONVEYOR) {
            BlockPos pos = new BlockPos(x, y, z);
            Block block = world.getBlockState(pos).getBlock();
            TileEntity entity = world.getTileEntity(pos);
            if (block instanceof BlockBase && entity instanceof TileEntityConveyor) {
                Cuboid hit = ((BlockBase) block).getCuboidHit(world, pos, player);
                if (hit != null) {
                    EnumFacing facing = EnumFacing.getFront(hit.identifier);
                    return new ContainerConveyor((TileEntityConveyor) entity, facing, player.inventory);
                }
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == BOOK) return new GUIBookMain(world, x, y, z);
        if (ID == CONVEYOR) {
            return new GuiConveyor((Container) getServerGuiElement(ID, player, world, x, y, z));
        }
        return null;
    }
}

package com.buuz135.industrial.tile.world;

import com.buuz135.industrial.proxy.client.render.LaserDrillSpecialRender;
import com.buuz135.industrial.tile.CustomElectricMachine;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.List;


public class LaserDrillTile extends CustomElectricMachine {

    public LaserDrillTile() {
        super(LaserDrillTile.class.getName().hashCode());

    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
    }

    @Override
    protected float performWork() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;

        BlockPos pos = getLaserBasePos();
        if (pos != null) {
            LaserBaseTile tile = (LaserBaseTile) this.world.getTileEntity(pos);
            tile.increaseWork();
            return 1;
        }
        return 0;
    }

    public BlockPos getLaserBasePos() {
        BlockPos pos = this.pos.offset(this.getFacing().getOpposite(), 2);
        if (this.world.getTileEntity(pos) != null && this.world.getTileEntity(pos) instanceof LaserBaseTile) return pos;
        return null;
    }


    @Override
    public List<TileEntitySpecialRenderer<TileEntity>> getRenderers() {
        List<TileEntitySpecialRenderer<TileEntity>> render = super.getRenderers();
        render.add(new LaserDrillSpecialRender());
        return render;
    }
}

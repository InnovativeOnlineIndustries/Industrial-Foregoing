/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
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

package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.magic.PotionEnervatorTile;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionEnervatorBlock extends CustomOrientedBlock<PotionEnervatorTile> {
    public PotionEnervatorBlock() {
        super("potion_enervator", PotionEnervatorTile.class, Material.ROCK);
    }

}

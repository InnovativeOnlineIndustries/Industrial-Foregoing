package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.agriculture.SewageCompostSolidifierTile;
import net.minecraft.block.material.Material;

public class SewageCompostSolidiferBlock extends CustomOrientedBlock<SewageCompostSolidifierTile> {

    public SewageCompostSolidiferBlock() {
        super("sewage_composter_solidifier", SewageCompostSolidifierTile.class, Material.ROCK, 1000, 10);
    }
}

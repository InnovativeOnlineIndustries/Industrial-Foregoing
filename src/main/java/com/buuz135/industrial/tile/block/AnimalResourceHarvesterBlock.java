package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.agriculture.AnimalResourceHarvesterTile;
import net.minecraft.block.material.Material;

public class AnimalResourceHarvesterBlock extends CustomOrientedBlock<AnimalResourceHarvesterTile> {

    public AnimalResourceHarvesterBlock() {
        super("animal_resource_harvester", AnimalResourceHarvesterTile.class, Material.ROCK,400,20);
    }
}

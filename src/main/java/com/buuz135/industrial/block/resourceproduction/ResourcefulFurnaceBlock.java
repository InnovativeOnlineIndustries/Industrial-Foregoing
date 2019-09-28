package com.buuz135.industrial.block.resourceproduction;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.resourceproduction.tile.ResourcefulFurnaceTile;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.hrznstudio.titanium.api.IFactory;
import net.minecraft.block.Blocks;

import javax.annotation.Nonnull;

public class ResourcefulFurnaceBlock extends IndustrialBlock<ResourcefulFurnaceTile> {

    public ResourcefulFurnaceBlock() {
        super("resourceful_furnace", Properties.from(Blocks.IRON_BLOCK), ResourcefulFurnaceTile.class, ModuleResourceProduction.TAB_RESOURCE);
    }

    @Override
    public IFactory<ResourcefulFurnaceTile> getTileEntityFactory() {
        return ResourcefulFurnaceTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }
}

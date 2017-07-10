package com.buuz135.industrial.fluid;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.registries.IForgeRegistry;

public class IFCustomFluidBlock extends BlockFluidClassic {


    public IFCustomFluidBlock(IFCustomFluid fluid, Material material) {
        super(fluid, material);
        setRegistryName(new ResourceLocation(Reference.MOD_ID, fluid.getName()));
        setUnlocalizedName(fluidName);
        setCreativeTab(IndustrialForegoing.creativeTab);
    }

    public void register(IForgeRegistry<Block> block) {
        block.register(this);
    }

    public String getName() {
        return fluidName;
    }


}

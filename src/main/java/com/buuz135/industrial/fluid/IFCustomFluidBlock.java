package com.buuz135.industrial.fluid;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class IFCustomFluidBlock extends BlockFluidClassic {


    public IFCustomFluidBlock(IFCustomFluid fluid, Material material) {
        super(fluid, material);
        setRegistryName(new ResourceLocation(Reference.MOD_ID, fluid.getName()));
        setUnlocalizedName(fluidName);
        setCreativeTab(IndustrialForegoing.creativeTab);
    }

    public void register() {
        GameRegistry.register(this);
    }

    public String getName() {
        return fluidName;
    }


}

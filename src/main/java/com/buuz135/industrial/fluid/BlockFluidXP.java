package com.buuz135.industrial.fluid;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockFluidXP extends BlockFluidClassic {


    public BlockFluidXP(Fluid fluid, String name) {
        super(fluid, Material.WATER);
        setRegistryName(new ResourceLocation(Reference.MOD_ID, name));
        setUnlocalizedName(name);
        setCreativeTab(IndustrialForegoing.creativeTab);
    }

    public void register() {
        GameRegistry.register(this);

    }
}

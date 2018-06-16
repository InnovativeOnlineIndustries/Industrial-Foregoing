package com.buuz135.industrial.fluid;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Consumer;

public class IFCustomFluidBlock extends BlockFluidClassic {

    private Consumer<EntityLivingBase> consumer;

    public IFCustomFluidBlock(IFCustomFluid fluid, Material material, Consumer<EntityLivingBase> consumer) {
        super(fluid, material);
        setRegistryName(new ResourceLocation(Reference.MOD_ID, fluid.getName()));
        setUnlocalizedName(fluidName);
        setCreativeTab(IndustrialForegoing.creativeTab);
        this.consumer = consumer;
    }

    public void register(IForgeRegistry<Block> block) {
        block.register(this);
    }

    public String getName() {
        return fluidName;
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        super.onEntityCollidedWithBlock(worldIn, pos, state, entityIn);
        if (worldIn.isRemote) return;
        if (entityIn instanceof EntityLivingBase) {
            consumer.accept((EntityLivingBase) entityIn);
        }
    }


}

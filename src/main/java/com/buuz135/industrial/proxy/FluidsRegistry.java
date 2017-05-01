package com.buuz135.industrial.proxy;

import com.buuz135.industrial.fluid.XPFluid;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class FluidsRegistry {

    public static Fluid XP;

    public static void registerFluids() {
        XP = new XPFluid();
        FluidRegistry.registerFluid(XP);
        FluidRegistry.addBucketForFluid(XP);

    }


    public static void registerRender() {
        Item fluid = Item.getItemFromBlock(BlockRegistry.XP);

        ModelBakery.registerItemVariants(fluid);
        ModelLoader.setCustomMeshDefinition(fluid, stack -> new ModelResourceLocation(Reference.MOD_ID, "fluid"));
        ModelLoader.setCustomStateMapper(BlockRegistry.XP, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation(Reference.MOD_ID, "fluid");
            }
        });
    }
}

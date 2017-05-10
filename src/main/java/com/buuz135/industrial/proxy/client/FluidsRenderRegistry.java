package com.buuz135.industrial.proxy.client;

import com.buuz135.industrial.fluid.IFCustomFluidBlock;
import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;

public class FluidsRenderRegistry {

    public static void registerRender() {
        register(BlockRegistry.BLOCK_ESSENCE);
        register(BlockRegistry.BLOCK_MILK);
        register(BlockRegistry.BLOCK_MEAT);
        register(BlockRegistry.BLOCK_LATEX);
        register(BlockRegistry.BLOCK_SEWAGE);
        register(BlockRegistry.BLOCK_SLUDGE);
    }

    public static void register(IFCustomFluidBlock base) {
        Item fluid = Item.getItemFromBlock(base);

        ModelBakery.registerItemVariants(fluid);
        FluidStateMapper mapper = new FluidStateMapper(base.getName());
        ModelLoader.setCustomMeshDefinition(fluid, mapper);
        ModelLoader.setCustomStateMapper(base, mapper);
    }

    public static class FluidStateMapper extends StateMapperBase implements ItemMeshDefinition {
        public final ModelResourceLocation location;

        public FluidStateMapper(String name) {
            location = new ModelResourceLocation(Reference.MOD_ID + ":fluids", name);
        }

        protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
            return location;
        }

        public ModelResourceLocation getModelLocation(ItemStack stack) {
            return location;
        }
    }
}

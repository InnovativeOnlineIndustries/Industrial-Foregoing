package com.buuz135.industrial.utils.data;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.block.BlockBase;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.DirectionProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;

public class IndustrialBlockstateProvider extends BlockStateProvider {

    public IndustrialBlockstateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, "industrialforegoing", exFileHelper);
    }

    public static ResourceLocation getModel(Block block) {
        return new ResourceLocation(block.getRegistryName().getNamespace(), "block/" + block.getRegistryName().getPath());
    }

    @Override
    protected void registerStatesAndModels() {
        BlockBase.BLOCKS.stream().filter(blockBase -> blockBase.getRegistryName().getNamespace().equals(Reference.MOD_ID) && blockBase instanceof IndustrialBlock)
                .map(blockBase -> (IndustrialBlock) blockBase)
                .forEach(industrialBlock -> {
                    VariantBlockStateBuilder builder = getVariantBuilder(industrialBlock);
                    for (DirectionProperty property : industrialBlock.getRotationType().getProperties()) {
                        for (Direction allowedValue : property.getAllowedValues()) {
                            builder.partialState().with(property, allowedValue)
                                    .addModels(new ConfiguredModel(new ModelFile.UncheckedModelFile(getModel(industrialBlock)), allowedValue.getHorizontalIndex() == -1 ? allowedValue.getAxisDirection().getOffset() * 90 : 0, (int) allowedValue.getOpposite().getHorizontalAngle(), false));
                        }
                    }
                });

    }
}

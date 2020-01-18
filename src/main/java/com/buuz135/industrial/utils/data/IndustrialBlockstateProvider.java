package com.buuz135.industrial.utils.data;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.transport.ConveyorBlock;
import com.buuz135.industrial.module.ModuleTransport;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.block.BasicBlock;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.DirectionProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;

public class IndustrialBlockstateProvider extends BlockStateProvider {

    private ExistingFileHelper helper;

    public IndustrialBlockstateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, "industrialforegoing", exFileHelper);
        this.helper = exFileHelper;
    }

    public static ResourceLocation getModel(Block block) {
        return new ResourceLocation(block.getRegistryName().getNamespace(), "block/" + block.getRegistryName().getPath());
    }

    @Override
    protected void registerStatesAndModels() {
        BasicBlock.BLOCKS.stream().filter(blockBase -> blockBase.getRegistryName().getNamespace().equals(Reference.MOD_ID) && blockBase instanceof IndustrialBlock)
                .map(blockBase -> (IndustrialBlock) blockBase)
                .forEach(industrialBlock -> {
                    VariantBlockStateBuilder builder = getVariantBuilder(industrialBlock);
                    for (DirectionProperty property : industrialBlock.getRotationType().getProperties()) {
                        for (Direction allowedValue : property.getAllowedValues()) {
                            builder.partialState().with(property, allowedValue)
                                    .addModels(new ConfiguredModel(new ModelFile.UncheckedModelFile(getModel(industrialBlock)), allowedValue.getHorizontalIndex() == -1 ? allowedValue.getOpposite().getAxisDirection().getOffset() * 90 : 0, (int) allowedValue.getOpposite().getHorizontalAngle(), false));
                        }
                    }
                });
        VariantBlockStateBuilder conveyor = getVariantBuilder(ModuleTransport.CONVEYOR);
        for (ConveyorBlock.EnumType type : ConveyorBlock.TYPE.getAllowedValues()) {
            for (Direction direction : ConveyorBlock.FACING.getAllowedValues()) {
                conveyor.partialState().with(ConveyorBlock.TYPE, type).with(ConveyorBlock.FACING, direction)
                        .addModels(new ConfiguredModel(new BlockModelBuilder(new ResourceLocation(Reference.MOD_ID, "block/conveyor_" + type.getName().toLowerCase() + "_" + direction.getName().toLowerCase()), helper).parent(new ModelFile.UncheckedModelFile(type.getModel())).texture("2", type.getTexture()), 0, (int) direction.getOpposite().getHorizontalAngle(), false));
            }
        }
    }
}

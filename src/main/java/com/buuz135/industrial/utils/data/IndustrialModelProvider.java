package com.buuz135.industrial.utils.data;

import com.buuz135.industrial.utils.Reference;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelProvider;

public class IndustrialModelProvider extends ModelProvider<BlockModelBuilder> {

    public IndustrialModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, Reference.MOD_ID, "block", BlockModelBuilder::new, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        //for (ConveyorBlock.EnumType type : ConveyorBlock.TYPE.getAllowedValues()) {
        //    for (Direction direction : ConveyorBlock.FACING.getAllowedValues()) {
        //        getBuilder(Reference.MOD_ID + ":conveyor_" + type.getName().toLowerCase() + "_" + direction.getName().toLowerCase()).parent(new ModelFile.UncheckedModelFile(type.getModel())).texture("2", type.getTexture());
        //    }
        //}
    }

    @Override
    public String getName() {
        return "Industrial Foregoing Model Provider";
    }
}

package com.buuz135.industrial.block.generator;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.generator.mycelial.IMycelialGeneratorType;
import com.buuz135.industrial.block.generator.tile.MycelialGeneratorTile;
import com.buuz135.industrial.module.ModuleGenerator;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.module.api.RegistryManager;
import com.hrznstudio.titanium.nbthandler.NBTManager;
import com.mojang.datafixers.types.Type;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;

public class MycelialGeneratorBlock extends IndustrialBlock<MycelialGeneratorTile> {

    private TileEntityType tileEntityType;
    private final IMycelialGeneratorType type;

    public MycelialGeneratorBlock(IMycelialGeneratorType type) {
        super("mycelial_" + type.getName(), Properties.from(Blocks.IRON_BLOCK), MycelialGeneratorTile.class, ModuleGenerator.TAB_GENERATOR);
        this.type = type;
    }

    @Override
    public void addAlternatives(RegistryManager<?> registry) {
        BlockItem item = (BlockItem) this.getItemBlockFactory().create();
        setItem(item);
        registry.content(Item.class, item);
        NBTManager.getInstance().scanTileClassForAnnotations(MycelialGeneratorTile.class);
        tileEntityType = TileEntityType.Builder.create(this.getTileEntityFactory()::create, new Block[]{this}).build((Type) null);
        tileEntityType.setRegistryName(new ResourceLocation(Reference.MOD_ID, "mycelial_generator_"+type.getName()));
        registry.content(TileEntityType.class, tileEntityType);
    }

    @Override
    public IFactory<MycelialGeneratorTile> getTileEntityFactory() {
        return () -> new MycelialGeneratorTile(this, type, tileEntityType);
    }
}

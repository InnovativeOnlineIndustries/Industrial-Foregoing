package com.buuz135.industrial.block.resourceproduction;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.resourceproduction.tile.LaserDrillTile;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;
import java.util.function.Consumer;

public class LaserDrillBlock extends IndustrialBlock<LaserDrillTile> {

    public LaserDrillBlock() {
        super("laser_drill", Properties.from(Blocks.IRON_BLOCK), LaserDrillTile.class, ModuleResourceProduction.TAB_RESOURCE);
    }

    @Override
    public IFactory<LaserDrillTile> getTileEntityFactory() {
        return LaserDrillTile::new;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.SIX_WAY;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, World world, BlockPos pos, Random rand) {
        if (world.getTileEntity(pos) instanceof LaserDrillTile){
            LaserDrillTile tile = (LaserDrillTile) world.getTileEntity(pos);
            if (!tile.getTarget().equals(BlockPos.ZERO)){
                BlockPos target = tile.getTarget();
                Vector3i vector =  tile.getFacingDirection().getOpposite().getDirectionVec();
                Vector3d vec = new Vector3d(pos.getX(), pos.getY(), pos.getZ());
                vec = vec.add(0.5, 0.5, 0.5).add(vector.getX() /2D, vector.getY()/2D, vector.getZ() / 2D);
                Vector3d velocity = vec.subtractReverse(new Vector3d(target.getX(), target.getY(), target.getZ()).add(0.5, 0.5, 0.5));
                double vel = 10;
                world.addParticle(ParticleTypes.END_ROD, vec.getX(), vec.getY(), vec.getZ(), velocity.x /vel ,velocity.y/vel,velocity.z /vel);
            }
        }
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this).patternLine("pfp").patternLine("bmb").patternLine("grg")
                .key('p', IndustrialTags.Items.PLASTIC)
                .key('f', IndustrialTags.Items.GEAR_DIAMOND)
                .key('b', Items.PISTON)
                .key('m', IndustrialTags.Items.MACHINE_FRAME_SIMPLE)
                .key('g', IndustrialTags.Items.GEAR_GOLD)
                .key('r', Items.REDSTONE)
                .build(consumer);
    }
}

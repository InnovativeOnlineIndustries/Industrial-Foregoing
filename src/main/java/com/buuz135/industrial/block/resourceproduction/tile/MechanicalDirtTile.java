package com.buuz135.industrial.block.resourceproduction.tile;

import com.buuz135.industrial.block.tile.IndustrialWorkingTile;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import net.minecraft.entity.*;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.Difficulty;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class MechanicalDirtTile extends IndustrialWorkingTile<MechanicalDirtTile> {

    @Save
    private SidedFluidTankComponent<MechanicalDirtTile> meat;

    public MechanicalDirtTile() {
        super(ModuleResourceProduction.MECHANICAL_DIRT);
        addTank(meat = (SidedFluidTankComponent<MechanicalDirtTile>) new SidedFluidTankComponent<MechanicalDirtTile>("meat", 4000, 43, 20, 0).
                setColor(DyeColor.BROWN).
                setComponentHarness(this).
                setTankAction(FluidTankComponent.Action.FILL).
                setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(ModuleCore.MEAT.getSourceFluid()))
        );
    }

    @Override
    public WorkAction work() {
        if (world.rand.nextDouble() > 0.1
                || world.getDifficulty() == Difficulty.PEACEFUL
                || (world.isDaytime() && world.getBrightness(pos.up()) > 0.5f && world.canBlockSeeSky(pos.up()))
                || world.getEntitiesWithinAABB(MobEntity.class, new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(pos).grow(3)).size() > 10) {
            if (hasEnergy(100)) return new WorkAction(0.5f, 100);
            return new WorkAction(1, 0);
        }
        if (meat.getFluidAmount() > 100) {
            MobEntity entity = getMobToSpawn();
            if (entity != null) {
                world.addEntity(entity);
                meat.drainForced(100, IFluidHandler.FluidAction.EXECUTE);
                if (hasEnergy(1000)) return new WorkAction(0.5f, 1000);
            }
        }
        return new WorkAction(1, 0);
    }

    private MobEntity getMobToSpawn() {
        List<Biome.SpawnListEntry> spawnListEntries = ((ServerChunkProvider) world.getChunkProvider()).getChunkGenerator().getPossibleCreatures(EntityClassification.MONSTER, pos);
        if (spawnListEntries.size() == 0) return null;
        Biome.SpawnListEntry spawnListEntry = spawnListEntries.get(world.rand.nextInt(spawnListEntries.size()));
        if (!EntitySpawnPlacementRegistry.func_223515_a(spawnListEntry.entityType, this.world, SpawnReason.NATURAL, pos, world.rand))
            return null;
        Entity entity = spawnListEntry.entityType.create(world);
        if (entity instanceof MobEntity) {
            ((MobEntity) entity).onInitialSpawn(world, world.getDifficultyForLocation(pos), SpawnReason.NATURAL, null, null);
            entity.setPosition(pos.getX() + 0.5, pos.getY() + 1.0626, pos.getZ() + 0.5);
            if (world.func_226669_j_(entity) && world.checkNoEntityCollision(entity, world.getBlockState(pos.up()).getShape(world, pos.up()))) { //doesNotCollide
                return (MobEntity) entity;
            }
        }
        return null;
    }

    public SidedFluidTankComponent<MechanicalDirtTile> getMeat() {
        return meat;
    }

    @Override
    public void tick() {
        super.tick();
        if (isServer() && this.world.getGameTime() % 5 == 0) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                TileEntity tile = world.getTileEntity(pos.offset(direction));
                if (tile instanceof MechanicalDirtTile) {
                    int difference = meat.getFluidAmount() - ((MechanicalDirtTile) tile).getMeat().getFluidAmount();
                    if (difference > 0) {
                        if (difference <= 25) difference = difference / 2;
                        else difference = 25;
                        if (meat.getFluidAmount() >= difference) {
                            meat.drainForced(((MechanicalDirtTile) tile).getMeat().fill(new FluidStack(ModuleCore.MEAT.getSourceFluid(), meat.drainForced(difference, IFluidHandler.FluidAction.SIMULATE).getAmount()), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                        }
                    }
                    difference = getEnergyStorage().getEnergyStored() - ((MechanicalDirtTile) tile).getEnergyStorage().getEnergyStored();
                    if (difference > 0) {
                        if (difference <= 1000 && difference > 1) difference = difference / 2;
                        if (difference > 1000) difference = 1000;
                        if (getEnergyStorage().getEnergyStored() >= difference) {
                            getEnergyStorage().extractEnergyForced(((MechanicalDirtTile) tile).getEnergyStorage().receiveEnergy(difference, false));
                        }
                    }
                }
            }
        }
    }

    @Nonnull
    @Override
    public MechanicalDirtTile getSelf() {
        return this;
    }
}

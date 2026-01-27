package com.buuz135.industrial.block.agriculturehusbandry.tile;

import com.buuz135.industrial.api.plant.PlantRecollectable;
import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.IndustrialWorkingTile;
import com.buuz135.industrial.config.machine.resourceproduction.HydroponicBedConfig;
import com.buuz135.industrial.item.HydroponicSimulationProcessorItem;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.registry.IFRegistries;
import com.buuz135.industrial.utils.IFAttachments;
import com.buuz135.industrial.utils.IndustrialTags;
import com.buuz135.industrial.utils.ServerLoadBalancer;
import com.buuz135.industrial.utils.apihandlers.plant.TreePlantRecollectable;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.SpecialPlantable;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class HydroponicBedTile extends IndustrialWorkingTile<HydroponicBedTile> {

    // Cached values for performance
    private static final int BALANCE_INTERVAL = 10; // Ticks between neighbor balancing
    private static final int NEIGHBOR_CACHE_INTERVAL = 100; // Ticks between neighbor cache refresh

    @Save
    private SidedFluidTankComponent<HydroponicBedTile> water;
    @Save
    private SidedFluidTankComponent<HydroponicBedTile> ether;
    @Save
    private ProgressBarComponent<HydroponicBedTile> etherBuffer;
    @Save
    private SidedInventoryComponent<HydroponicBedTile> output;
    @Save
    private SidedInventoryComponent<HydroponicBedTile> simulation_slot;

    // Cached positions and neighbors
    private BlockPos cachedAbovePos;
    private HydroponicBedTile[] cachedNeighbors;
    private int neighborCacheCounter = 0;

    // Cached simulation processor with deferred NBT saving
    private static final int SIMULATION_SAVE_INTERVAL = 20;
    private HydroponicSimulationProcessorItem.Simulation cachedSimulation;
    private ItemStack lastSimulationItem = ItemStack.EMPTY;
    private boolean simulationDirty = false;
    private int simulationSaveCounter = 0;


    public HydroponicBedTile(BlockPos blockPos, BlockState blockState) {
        super(ModuleAgricultureHusbandry.HYDROPONIC_BED, HydroponicBedConfig.powerPerOperation, blockPos, blockState);
        addTank(this.water = (SidedFluidTankComponent<HydroponicBedTile>) new SidedFluidTankComponent<HydroponicBedTile>("water", 1000, 43, 20, 0)
                .setColor(DyeColor.BLUE)
                .setTankType(FluidTankComponent.Type.SMALL)
                .setTankAction(FluidTankComponent.Action.FILL)
                .setValidator(fluidStack -> fluidStack.getFluid().isSame(Fluids.WATER) || fluidStack.getFluid().isSame(Fluids.LAVA))
        );
        addTank(this.ether = (SidedFluidTankComponent<HydroponicBedTile>) new SidedFluidTankComponent<HydroponicBedTile>("ether", 10, 43, 57, 1)
                .setColor(DyeColor.CYAN)
                .setTankType(FluidTankComponent.Type.SMALL)
                .setTankAction(FluidTankComponent.Action.FILL)
                .setValidator(fluidStack -> fluidStack.getFluid().isSame(ModuleCore.ETHER.getSourceFluid().get()))
        );
        addProgressBar(this.etherBuffer = new ProgressBarComponent<HydroponicBedTile>(63, 20, 200)
                .setColor(DyeColor.CYAN)
                .setCanReset(hydroponicBedTile -> false)
                .setCanIncrease(hydroponicBedTile -> false) // Disable auto-tick, progress is managed manually
        );
        addInventory(this.output = (SidedInventoryComponent<HydroponicBedTile>) new SidedInventoryComponent<HydroponicBedTile>("output", 79, 22, 5 * 3, 2)
                .setColor(DyeColor.ORANGE)
                .setRange(5, 3)
                .setInputFilter((stack, integer) -> false)
        );
        addInventory(this.simulation_slot = (SidedInventoryComponent<HydroponicBedTile>) new SidedInventoryComponent<HydroponicBedTile>("simulation", 104, 80, 1, 3)
                .setColor(DyeColor.LIME)
                .setInputFilter((stack, integer) -> stack.getItem().equals(ModuleAgricultureHusbandry.HYDROPONIC_SIMULATION_PROCESSOR.get()))
                .setOutputFilter((stack, integer) -> false)
        );
    }

    private PlantRecollectable cachedRecollectable = null;
    private int errorAttempts = 0;

    /**
     * Gets the BlockState with maximum age for the given block.
     * Used in virtual growth mode to simulate fully grown crops.
     */
    private BlockState getMaxAgeState(Block block) {
        if (block instanceof CropBlock crop) {
            return crop.getStateForAge(crop.getMaxAge());
        }
        if (block instanceof NetherWartBlock) {
            return block.defaultBlockState().setValue(NetherWartBlock.AGE, 3);
        }
        // Fallback for other blocks (e.g., BushBlock)
        return block.defaultBlockState();
    }

    private BlockPos getAbovePos() {
        if (cachedAbovePos == null) {
            cachedAbovePos = this.worldPosition.above();
        }
        return cachedAbovePos;
    }

    private static final Direction[] HORIZONTAL_DIRECTIONS = {Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

    private HydroponicBedTile[] getNeighborCache() {
        if (cachedNeighbors == null || ++neighborCacheCounter >= NEIGHBOR_CACHE_INTERVAL) {
            neighborCacheCounter = 0;
            if (cachedNeighbors == null) {
                cachedNeighbors = new HydroponicBedTile[4];
            }
            for (int i = 0; i < HORIZONTAL_DIRECTIONS.length; i++) {
                BlockEntity tile = this.level.getBlockEntity(worldPosition.relative(HORIZONTAL_DIRECTIONS[i]));
                cachedNeighbors[i] = tile instanceof HydroponicBedTile ? (HydroponicBedTile) tile : null;
            }
        }
        return cachedNeighbors;
    }

    public static boolean tryToHarvestAndReplant(Level level, BlockPos up, BlockState state, IItemHandler output, ProgressBarComponent<?> etherBuffer, IndustrialWorkingTile tile, Supplier<PlantRecollectable> plantSupplier, ItemStack simulationOutput) {
        return tryToHarvestAndReplantInternal(level, up, state, output, etherBuffer, tile, plantSupplier, simulationOutput, null);
    }

    /**
     * Overload that accepts a cached Simulation to avoid NBT parsing overhead.
     * The simulation is modified in-place and saved back to the ItemStack.
     */
    public static boolean tryToHarvestAndReplant(Level level, BlockPos up, BlockState state, IItemHandler output, ProgressBarComponent<?> etherBuffer, IndustrialWorkingTile tile, Supplier<PlantRecollectable> plantSupplier, ItemStack simulationOutput, HydroponicSimulationProcessorItem.Simulation cachedSim) {
        return tryToHarvestAndReplantInternal(level, up, state, output, etherBuffer, tile, plantSupplier, simulationOutput, cachedSim);
    }

    private static boolean tryToHarvestAndReplantInternal(Level level, BlockPos up, BlockState state, IItemHandler output, ProgressBarComponent<?> etherBuffer, IndustrialWorkingTile tile, Supplier<PlantRecollectable> plantSupplier, ItemStack simulationOutput, HydroponicSimulationProcessorItem.Simulation cachedSim) {
        var cachedRecollectable = plantSupplier.get();
        if (cachedRecollectable != null) {
            List<ItemStack> drops = new ArrayList<>();
            if (cachedRecollectable instanceof TreePlantRecollectable) {
                while (cachedRecollectable.canBeHarvested(level, up, state)) {
                    drops.addAll(cachedRecollectable.doHarvestOperation(level, up, state));
                }
            } else {
                drops.addAll(cachedRecollectable.doHarvestOperation(level, up, state));
            }
            var planted = ItemStack.EMPTY;
            if (level.isEmptyBlock(up)) {
                for (ItemStack drop : drops) {
                    if (!drop.isEmpty() && drop.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof BushBlock) {
                        planted = drop.copyWithCount(1);
                        BlockState blockstate1 = blockItem.getBlock().defaultBlockState();
                        // Use UPDATE_CLIENTS | UPDATE_KNOWN_SHAPE to skip neighbor updates
                        level.setBlock(up, blockstate1, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
                        drop.shrink(1);
                        break;
                    }
                    if (drop.getItem() instanceof SpecialPlantable specialPlantable && specialPlantable.canPlacePlantAtPosition(drop, level, up, null)) {
                        planted = drop.copyWithCount(1);
                        specialPlantable.spawnPlantAtPosition(drop, level, up, null);
                        drop.shrink(1);
                        break;
                    }
                }
            }
            if (planted.isEmpty()) {
                planted = cachedRecollectable.getSeedDrop(level, up, state);
            }
            if (!planted.is(IndustrialTags.Items.HYDROPONIC_SIMULATION_BLACKLIST) && !simulationOutput.isEmpty() && simulationOutput.getItem() instanceof HydroponicSimulationProcessorItem) {
                // Use cached simulation if provided, otherwise create new one
                var sim = cachedSim != null ? cachedSim : new HydroponicSimulationProcessorItem.Simulation(simulationOutput.get(IFAttachments.HYDROPONIC_SIMULATION_PROCESSOR));
                sim.acceptExecution(planted, drops);
                simulationOutput.set(IFAttachments.HYDROPONIC_SIMULATION_PROCESSOR, sim.toNBT(level.registryAccess()));
            }
            // Use regular for-loop to avoid lambda allocation
            for (int i = 0, size = drops.size(); i < size; i++) {
                ItemStack stack = drops.get(i);
                if (!stack.isEmpty()) {
                    ItemHandlerHelper.insertItem(output, stack, false);
                }
            }
            if (tile instanceof IndustrialAreaWorkingTile<?> && cachedRecollectable.shouldCheckNextPlant(level, up, level.getBlockState(up))) {
                ((IndustrialAreaWorkingTile<?>) tile).increasePointer();
            }
            etherBuffer.setProgress(etherBuffer.getProgress() - 1);
            return true;
        }

        return false;
    }

    private void findRecollectable(Level level, BlockPos up, BlockState state) {
        for (PlantRecollectable plantRecollectable : IFRegistries.PLANT_RECOLLECTABLES_REGISTRY) {
            if (plantRecollectable.canBeHarvested(level, up, state)) {
                cachedRecollectable = plantRecollectable;
                return;
            }
        }
    }

    @Override
    public WorkAction work() {
        if (this.etherBuffer.getProgress() <= 0 && this.ether.getFluidAmount() > 0) {
            this.ether.drainForced(1, IFluidHandler.FluidAction.EXECUTE);
            this.etherBuffer.setProgress(this.etherBuffer.getMaxProgress());
        }

        return workVirtual();
    }

    /**
     * Virtual growth mode: uses planted crop above the block, generates drops via Block.getDrops()
     * for max-age state without physically growing the crop.
     * Seeds/plantable items are filtered out since the crop is not physically harvested.
     * Supports Simulation Processor for recording crop data.
     */
    private WorkAction workVirtual() {
        if (!hasEnergy(HydroponicBedConfig.powerPerOperation)) {
            return new WorkAction(1, 0);
        }
        if (this.water.getFluidAmount() < 10) {
            return new WorkAction(1, 0);
        }

        BlockPos up = getAbovePos();
        BlockState state = this.level.getBlockState(up);
        Block block = state.getBlock();

        // Check if there's a valid crop planted above
        if (state.isAir() || (!(block instanceof CropBlock) && !(block instanceof NetherWartBlock) && !(block instanceof BushBlock))) {
            return new WorkAction(1, 0);
        }

        // Consume water
        this.water.drainForced(10, IFluidHandler.FluidAction.EXECUTE);

        // Consume ether buffer if available (gives bonus via faster progress in base class)
        if (this.etherBuffer.getProgress() > 0) {
            this.etherBuffer.setProgress(this.etherBuffer.getProgress() - 1);
        }

        // Generate drops from max-age state (not the current state)
        BlockState maxAgeState = getMaxAgeState(block);

        if (this.level instanceof ServerLevel serverLevel) {
            // Build loot context parameters
            LootParams.Builder builder = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.worldPosition))
                    .withParameter(LootContextParams.TOOL, ItemStack.EMPTY);

            // Get drops from the max-age state
            List<ItemStack> drops = maxAgeState.getDrops(builder);

            // Determine the seed/planted item for simulation processor
            ItemStack planted = ItemStack.EMPTY;
            for (int i = 0; i < drops.size(); i++) {
                ItemStack drop = drops.get(i);
                if (!drop.isEmpty() && drop.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof BushBlock) {
                    planted = drop.copyWithCount(1);
                    break;
                }
            }
            // Fallback: use PlantRecollectable to get seed
            if (planted.isEmpty()) {
                PlantRecollectable recollectable = getPlantRecollectable(up, state);
                if (recollectable != null) {
                    planted = recollectable.getSeedDrop(this.level, up, state);
                }
            }

            // Record to Simulation Processor if present (deferred NBT saving for performance)
            ItemStack simulationOutput = this.simulation_slot.getStackInSlot(0);
            if (!planted.isEmpty() && !planted.is(IndustrialTags.Items.HYDROPONIC_SIMULATION_BLACKLIST)
                    && !simulationOutput.isEmpty() && simulationOutput.getItem() instanceof HydroponicSimulationProcessorItem) {
                var sim = getCachedSimulation();
                if (sim != null) {
                    sim.acceptExecution(planted, drops);
                    simulationDirty = true;
                }
            }

            // Add drops to output, filtering out one seed/plantable item (crop is not physically harvested)
            boolean seedFiltered = planted.isEmpty(); // Skip filtering if no seed identified
            for (int i = 0, size = drops.size(); i < size; i++) {
                ItemStack drop = drops.get(i);
                if (!drop.isEmpty()) {
                    // Filter out one seed item only - keep other drops and extra seeds
                    if (!seedFiltered && ItemStack.isSameItem(drop, planted)) {
                        if (drop.getCount() > 1) {
                            // Multiple items in stack - remove one, keep the rest
                            drop.shrink(1);
                            ItemHandlerHelper.insertItem(this.output, drop, false);
                        }
                        // Single item - just skip it entirely
                        seedFiltered = true;
                        continue;
                    }
                    ItemHandlerHelper.insertItem(this.output, drop, false);
                }
            }
        }

        return new WorkAction(1, HydroponicBedConfig.powerPerOperation);
    }

    private PlantRecollectable getPlantRecollectable() {
        BlockPos up = getAbovePos();
        BlockState state = this.level.getBlockState(up);
        return getPlantRecollectable(up, state);
    }

    private PlantRecollectable getPlantRecollectable(BlockPos up, BlockState state) {
        if (errorAttempts >= 15) {
            findRecollectable(level, up, state);
            errorAttempts = 0;
        }
        if (cachedRecollectable == null) {
            findRecollectable(level, up, state);
        } else if (!cachedRecollectable.canBeHarvested(level, up, state)) {
            ++errorAttempts;
            return null;
        }
        return cachedRecollectable;
    }

    /**
     * Returns a cached Simulation object, creating it only when the item in the slot changes.
     * Uses isSameItem instead of isSameItemSameComponents to avoid re-parsing NBT
     * every tick when we update the simulation data.
     */
    private HydroponicSimulationProcessorItem.Simulation getCachedSimulation() {
        ItemStack currentStack = this.simulation_slot.getStackInSlot(0);

        // Check if slot item changed (ignore component changes - we update them ourselves)
        boolean slotChanged = (currentStack.isEmpty() != lastSimulationItem.isEmpty())
                || (!currentStack.isEmpty() && !ItemStack.isSameItem(currentStack, lastSimulationItem));

        if (slotChanged) {
            lastSimulationItem = currentStack.copyWithCount(1);
            if (currentStack.isEmpty() || !(currentStack.getItem() instanceof HydroponicSimulationProcessorItem)) {
                cachedSimulation = null;
            } else {
                cachedSimulation = new HydroponicSimulationProcessorItem.Simulation(currentStack.get(IFAttachments.HYDROPONIC_SIMULATION_PROCESSOR));
            }
        }
        return cachedSimulation;
    }

    public SidedFluidTankComponent<HydroponicBedTile> getWater() {
        return water;
    }

    public SidedFluidTankComponent<HydroponicBedTile> getEther() {
        return ether;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, HydroponicBedTile blockEntity) {
        // Adaptive tick skipping: skip ticks when TPS is low (supports per-world TPS)
        int skipInterval = ServerLoadBalancer.getTickSkipInterval(level);
        if (skipInterval > 1 && level.getGameTime() % skipInterval != 0) {
            return; // Skip this tick
        }

        // Deferred simulation NBT saving - save every N ticks instead of every work cycle
        if (simulationDirty && ++simulationSaveCounter >= SIMULATION_SAVE_INTERVAL) {
            simulationSaveCounter = 0;
            simulationDirty = false;
            ItemStack simulationOutput = this.simulation_slot.getStackInSlot(0);
            if (cachedSimulation != null && !simulationOutput.isEmpty()) {
                simulationOutput.set(IFAttachments.HYDROPONIC_SIMULATION_PROCESSOR, cachedSimulation.toNBT(this.level.registryAccess()));
            }
        }

        super.serverTick(level, pos, state, blockEntity);
        if (this.level.getGameTime() % BALANCE_INTERVAL == 0) {
            var thisEnergy = getEnergyStorage();
            HydroponicBedTile[] neighbors = getNeighborCache();
            for (HydroponicBedTile neighbor : neighbors) {
                if (neighbor == null || neighbor.isRemoved()) continue;

                // Balance water
                var neighborWater = neighbor.water;
                int difference = water.getFluidAmount() - neighborWater.getFluidAmount();
                if (difference > 0 && (water.getFluid().is(neighborWater.getFluid().getFluid()) || neighborWater.isEmpty())) {
                    difference = difference <= 25 ? difference / 2 : 25;
                    if (water.getFluidAmount() >= difference) {
                        int transferred = neighborWater.fill(new FluidStack(Fluids.WATER, difference), IFluidHandler.FluidAction.EXECUTE);
                        water.drainForced(transferred, IFluidHandler.FluidAction.EXECUTE);
                    }
                }

                // Balance ether
                var neighborEther = neighbor.ether;
                difference = ether.getFluidAmount() - neighborEther.getFluidAmount();
                if (difference > 0 && ether.getFluidAmount() >= 1) {
                    int transferred = neighborEther.fill(new FluidStack(ModuleCore.ETHER.getSourceFluid().get(), 1), IFluidHandler.FluidAction.EXECUTE);
                    ether.drainForced(transferred, IFluidHandler.FluidAction.EXECUTE);
                }

                // Balance energy
                var neighborEnergy = neighbor.getEnergyStorage();
                difference = thisEnergy.getEnergyStored() - neighborEnergy.getEnergyStored();
                if (difference > 0) {
                    difference = difference <= 1000 ? (difference > 1 ? difference / 2 : difference) : 1000;
                    if (thisEnergy.getEnergyStored() >= difference) {
                        thisEnergy.extractEnergy(neighborEnergy.receiveEnergy(difference, false), false);
                    }
                }
            }
        }
    }

    @Override
    public int getMaxProgress() {
        return HydroponicBedConfig.maxProgress * HydroponicBedConfig.progressMultiplier;
    }

    @Nonnull
    @Override
    public HydroponicBedTile getSelf() {
        return this;
    }

    @Override
    protected EnergyStorageComponent<HydroponicBedTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(HydroponicBedConfig.maxStoredPower, 10, 20);
    }

    @Override
    public void setRemoved() {
        // Save pending simulation data before removal
        if (simulationDirty && cachedSimulation != null && this.level != null) {
            ItemStack simulationOutput = this.simulation_slot.getStackInSlot(0);
            if (!simulationOutput.isEmpty()) {
                simulationOutput.set(IFAttachments.HYDROPONIC_SIMULATION_PROCESSOR, cachedSimulation.toNBT(this.level.registryAccess()));
            }
        }
        super.setRemoved();
    }
}

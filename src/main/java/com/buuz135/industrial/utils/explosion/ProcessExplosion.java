package com.buuz135.industrial.utils.explosion;

import com.buuz135.industrial.IndustrialForegoing;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.gen.SimplexNoiseGenerator;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.IFluidBlock;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Class copied and adapted from Draconic Evolution https://github.com/brandon3055/Draconic-Evolution/blob/master/src/main/java/com/brandon3055/draconicevolution/blocks/reactor/ProcessExplosion.java
 */
public class ProcessExplosion {

    public static DamageSource fusionExplosion = new DamageSource("damage.de.fusionExplode").setExplosion().setDamageBypassesArmor();

    /**
     * The origin of the explosion.
     */
    public final Vector3f origin;
    private final ServerWorld world;
    private final MinecraftServer server;
    private final int minimumDelay;
    public double[] angularResistance;
    public boolean isDead = false;
    public int radius = 0;
    public int maxRadius;
    public double circumference = 0;
    public double meanResistance = 0;
    public boolean enableEffect = true;
    /**
     * Set this to false to disable the laval dropped by the explosion.
     */
    public boolean lava = true;
    public HashSet<Integer> blocksToUpdate = new HashSet<>();
    public LinkedList<HashSet<Integer>> destroyedBlocks = new LinkedList<>();
    public HashSet<Integer> lavaPositions = new HashSet<>();
    public HashSet<Integer> destroyedCache = new HashSet<>();
    public HashSet<Integer> scannedCache = new HashSet<>();
    public ShortPos shortPos;
    public Consumer<Double> progressMon = null;
    protected boolean calculationComplete = false;
    protected boolean detonated = false;
    protected long startTime = -1;
    protected long calcWait = 0;
    private BlockState lavaState;

    /**
     * This process is responsible for handling some extremely large explosions as efficiently as possible.
     * The absolute max recommended radius is 500 (that's 1000 across) However this has only been tested on a high end system and
     * may crash on systems with less ram and processing power. Recommended max is 350 - 400;
     *
     * @param origin           The origin of the explosion.
     * @param radius           The radius of the explosion (Note depending on terrain the actual destruction radius will be slightly less then this)
     * @param world            The server world.
     * @param minimumDelayTime The minimum delay in seconds before detonation.
     *                         If the explosion calculation completes before this time is up the process will wait till this amount of time has based before detonating.
     *                         Use -1 for manual detonation.
     */
    public ProcessExplosion(BlockPos origin, int radius, ServerWorld world, int minimumDelayTime) {
        this.origin = new Vector3f(origin.getX() + 0.5f, origin.getY() + 0.5f, origin.getZ() + 0.5f);
        this.shortPos = new ShortPos(origin);
        this.world = world;
        this.server = world.getServer();
        this.minimumDelay = minimumDelayTime;
        this.angularResistance = new double[121];
        Arrays.fill(angularResistance, 100);

        IndustrialForegoing.LOGGER.info("Explosion Calculation Started for " + radius + " Block radius detonation!");
        maxRadius = radius;
        lavaState = Blocks.LAVA.getDefaultState();

    }

    public void updateProcess() {
        long serverTime = Util.milliTime();
        if (startTime == -1) {
            startTime = System.currentTimeMillis();
        }

        if (calcWait > 0) {
            calcWait--;
            return;
        }

        if (!calculationComplete) {
            long t = System.currentTimeMillis();
            updateCalculation();
            t = System.currentTimeMillis() - t;
            calcWait = t / 40;
            IndustrialForegoing.LOGGER.debug("Calculation Progress: " + MathHelper.floor((((double) radius / (double) maxRadius) * 100D)) + "% " + (Runtime.getRuntime().freeMemory() / 1000000));
            if (calcWait > 0) {
                IndustrialForegoing.LOGGER.debug("Explosion Calc loop took " + t + "ms! Waiting " + calcWait + " ticks before continuing");
            }
            if (progressMon != null) {
                progressMon.accept((double) radius / (double) maxRadius);
            }
        } else if (minimumDelay == -1) {
            isDead = true;
        } else {
            if ((System.currentTimeMillis() - startTime) / 1000 >= minimumDelay) {
                detonate();
            }
        }
    }

    public void updateCalculation() {
        BlockPos originPos = new BlockPos(origin.getX(), origin.getY(), origin.getZ());

        double maxCoreHeight = 20D * (maxRadius / 150D);

        Vector3f posVecUp = new Vector3f();
        Vector3f posVecDown = new Vector3f();
        SimplexNoiseGenerator noise = new SimplexNoiseGenerator(world.getRandom());
        for (int x = originPos.getX() - radius; x < originPos.getX() + radius; x++) {
            for (int z = originPos.getZ() - radius; z < originPos.getZ() + radius; z++) {
                double dist = calculateDistanceBetweenPoints(x, z, originPos.getX(), originPos.getZ());
                if (dist < radius && dist >= radius - 1) {
                    posVecUp.set(x + 0.5f, origin.getY(), z + 0.5f);
                    double radialAngle = getRadialAngle(posVecUp);
                    double radialResistance = getRadialResistance(radialAngle);
                    double angularLoad = (meanResistance / radialResistance) * 1;
                    double radialPos = 1D - (radius / (double) maxRadius);
                    double coreFalloff = Math.max(0, (radialPos - 0.8) * 5);
                    coreFalloff = 1 - ((1 - coreFalloff) * (1 - coreFalloff) * (1 - coreFalloff));
                    double coreHeight = coreFalloff * maxCoreHeight;
                    double edgeNoise = Math.max(0, (-radialPos + 0.2) * 5);
                    double edgeScatter = edgeNoise * world.getRandom().nextInt(10);
                    double sim = noise.getValue(x / 50D, z / 50D);
                    edgeNoise = 1 + (Math.abs(sim) * edgeNoise * 8);

                    double power = (10000 * radialPos * radialPos * radialPos * angularLoad * edgeNoise) + edgeScatter;
                    double heightUp = 20 + ((5D + (radius / 10D)) * angularLoad);
                    double heightDown = coreHeight + ((5D + (radius / 10D)) * angularLoad * (1 - coreFalloff));
                    heightDown += (Math.abs(sim) * 4) + world.getRandom().nextDouble();
                    heightUp += (Math.abs(sim) * 4) + world.getRandom().nextDouble();

                    posVecDown.set(posVecUp.getX(), posVecUp.getY(), posVecUp.getZ());
                    double resist = trace(posVecUp, power * (1 + 8 * radialPos), (int) heightUp * 3, 1, 0, 0);
                    posVecDown.sub(new Vector3f(0, 1, 0));
                    resist += trace(posVecDown, power, (int) heightDown, -1, 0, 0);
                    resist *= 1 / angularLoad;

                    if (radialPos < 0.8) {
                        addRadialResistance(radialAngle, resist);
                    }
                }
            }
        }

        recalcResist();
        radius++;
        circumference = 2 * Math.PI * radius;

        destroyedBlocks.add(destroyedCache);
        destroyedCache = new HashSet<>();
        scannedCache = new HashSet<>();

        if (radius >= maxRadius) {
            IndustrialForegoing.LOGGER.debug("Explosion Calculation Completed!");
            calculationComplete = true;
        }
    }

    public double calculateDistanceBetweenPoints(double x1, double y1, double x2, double y2) {
        return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }

    //region Math Stuff

    private void recalcResist() {
        double total = 0;
        for (double resist : angularResistance) {
            total += resist;
        }

        meanResistance = total / angularResistance.length;
    }

    public double getRadialAngle(Vector3f pos) {
        double theta = Math.atan2(pos.getX() - origin.getX(), origin.getZ() - pos.getZ());

        if (theta < 0.0) {
            theta += Math.PI * 2;
        }

        return ((theta / (Math.PI * 2)) * (double) angularResistance.length);
    }

    public double getRadialResistance(double radialPos) {
        int min = MathHelper.floor(radialPos);
        if (min >= angularResistance.length) {
            min -= angularResistance.length;
        }
        int max = MathHelper.ceil(radialPos);
        if (max >= angularResistance.length) {
            max -= angularResistance.length;
        }

        double delta = radialPos - min;

        return (angularResistance[min] * (1 - delta)) + (angularResistance[max] * delta);
    }

    public void addRadialResistance(double radialPos, double power) {
        int min = MathHelper.floor(radialPos);
        if (min >= angularResistance.length) {
            min -= angularResistance.length;
        }

        int max = MathHelper.ceil(radialPos);
        if (max >= angularResistance.length) {
            max -= angularResistance.length;
        }

        double delta = radialPos - min;
        angularResistance[min] += power * (1 - delta);
        angularResistance[max] += power * delta;
    }

    //endregion

    private double trace(Vector3f posVec, double power, int dist, int traceDir, double totalResist, int travel) {
        if (dist > 100) {
            dist = 100;
        }
        if (dist <= 0 || power <= 0 || posVec.getY() < 0 || posVec.getY() > 255) {
            return totalResist;
        }

        dist--;
        travel++;
        Integer iPos = shortPos.getIntPos(posVec);

        if (scannedCache.contains(iPos) || destroyedCache.contains(iPos)) {
            posVec.add(0, traceDir, 0);
            return trace(posVec, power, dist, traceDir, totalResist, travel);
        }

        BlockPos pos = new BlockPos(posVec.getX(), posVec.getY(), posVec.getZ());

        double r = 1;

        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (!block.isAir(state, world, pos)) {
            Material mat = state.getMaterial();
            double effectivePower = (power / 10) * ((double) dist / (dist + travel));

            r = block.getExplosionResistance();

            if (effectivePower >= r) {
                destroyedCache.add(iPos);
            } else if (mat == Material.WATER || mat == Material.LAVA) {
                if (effectivePower > 5) {
                    destroyedCache.add(iPos);
                } else {
                    blocksToUpdate.add(iPos);
                }
                r = 10;
            } else {
                if (block instanceof IFluidBlock || block instanceof FallingBlock) {
                    blocksToUpdate.add(iPos);
                }
                scannedCache.add(iPos);
            }

            if (r > 1000) {
                r = 1000;
            }
        } else {
            scannedCache.add(iPos);
        }

        r = (r / radius) / travel;//?

        totalResist += r;
        power -= r;

        if (dist == 1 && traceDir == -1 && lava && world.getRandom().nextInt(250) == 0 && !world.isAirBlock(pos.down())) {
            dist = 0;
            if (destroyedCache.contains(iPos)) {
                destroyedCache.remove(iPos);
            }
            lavaPositions.add(iPos);
            blocksToUpdate.add(iPos);
            scannedCache.add(iPos);
        }

        posVec.add(0, traceDir, 0);
        return trace(posVec, power, dist, traceDir, totalResist, travel);
    }

    /**
     * @return true if explosion calculation is complete.
     */
    public boolean isCalculationComplete() {
        return calculationComplete;
    }

    /**
     * Call this once the explosion calculation has completed to manually detonate.
     *
     * @return false if calculation is not yet complete or detonation has already occurred.
     */
    public boolean detonate() {
        if (!isCalculationComplete() || detonated) {
            return false;
        }

        long l = System.currentTimeMillis();

        IndustrialForegoing.LOGGER.debug("Removing Blocks!");
        //LogHelper.startTimer("Adding Blocks For Removal");

        ExplosionHelper removalHelper = new ExplosionHelper(world, new BlockPos(origin.getX(), origin.getY(), origin.getZ()), shortPos);
        int i = 0;

        removalHelper.setBlocksForRemoval(destroyedBlocks);

        //LogHelper.stopTimer();
        //LogHelper.startTimer("Adding Lava");

        for (Integer pos : lavaPositions) {
            world.setBlockState(shortPos.getActualPos(pos), lavaState);
        }

        //LogHelper.stopTimer();
        //LogHelper.startTimer("Adding update Blocks");
        removalHelper.addBlocksForUpdate(blocksToUpdate);
        IndustrialForegoing.LOGGER.debug("Blocks Removed: " + i);
        //LogHelper.stopTimer();

        removalHelper.finish();

        isDead = true;
        detonated = true;

        final BlockPos pos = new BlockPos(origin.getX(), origin.getY(), origin.getZ());
        if (enableEffect) {
            //DraconicNetwork.sendExplosionEffect(world.dimension(), pos, radius * 4, false);
        }

        new Thread(() -> {
            try {
                Thread.sleep(30);
                List<Entity> list = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)).grow(radius * 2.5, radius * 2.5, radius * 2.5));
                for (Entity e : list) {
                    double dist = pos.distanceSq(e.getPosition());
                    float dmg = 10000F * (1F - (float) (dist / (radius * 1.2D)));
                    e.attackEntityFrom(fusionExplosion, dmg);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        IndustrialForegoing.LOGGER.debug("Total explosion time: " + (System.currentTimeMillis() - l) / 1000D + "s");
        return true;
    }


    public boolean isDead() {
        return isDead;
    }

}
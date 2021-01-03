package com.buuz135.industrial.worlddata;

import com.buuz135.industrial.block.generator.mycelial.IMycelialGeneratorType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MycelialDataManager extends WorldSavedData {

    public static final String NAME = "IFMycelial";
    public HashMap<String, HashMap<String, List<GeneratorInfo>>> infos;

    public MycelialDataManager() {
        super(NAME);
        infos = new HashMap();
    }

    public static void setGeneratorInfo(String uuid, World world, BlockPos pos, IMycelialGeneratorType type){
        if (world instanceof ServerWorld){
            MycelialDataManager dataManager = getData(world);
            List<GeneratorInfo> generatorInfos = dataManager.getInfos().computeIfAbsent(uuid, s -> new HashMap<>()).computeIfAbsent(type.getName(), s -> new ArrayList<>());
            boolean updated = false;
            for (GeneratorInfo generatorInfo : generatorInfos) {
                if (generatorInfo.pos.equals(pos) && generatorInfo.world.equals(world.getDimensionKey())){
                    updated = true;
                    generatorInfo.lastRun = world.getGameTime();
                }
            }
            if (!updated){
                generatorInfos.add(new GeneratorInfo(world.getGameTime(), 0 , pos, world.getDimensionKey()));
            }
            dataManager.markDirty();
        }
    }

    public static void removeGeneratorInfo(String uuid, World world, BlockPos pos, IMycelialGeneratorType type){
        MycelialDataManager dataManager = getData(world);
        List<GeneratorInfo> generatorInfos = dataManager.getInfos().computeIfAbsent(uuid, s -> new HashMap<>()).computeIfAbsent(type.getName(), s -> new ArrayList<>());
        generatorInfos.removeIf(generatorInfo -> generatorInfo.world.equals(world.getDimensionKey()) && generatorInfo.pos.equals(pos));
        dataManager.markDirty();
    }

    public static List<String> getReactorAvailable(String uuid, World world, boolean execute){
        List<String> names = new ArrayList<>();
        if (world instanceof ServerWorld){
            MycelialDataManager dataManager = getData(world);
            HashMap<String, List<GeneratorInfo>> generators = dataManager.getInfos().computeIfAbsent(uuid, s -> new HashMap<>());
            for (IMycelialGeneratorType type : IMycelialGeneratorType.TYPES) {
                for (GeneratorInfo generatorInfo : generators.computeIfAbsent(type.getName() , s -> new ArrayList<>())) {
                    if (generatorInfo.lastTracked + 5 <= world.getGameTime()  && generatorInfo.lastRun + 5 >= world.getGameTime()){
                        names.add(type.getName());
                        if (execute) generatorInfo.lastTracked = world.getGameTime();
                        dataManager.markDirty();
                        break;
                    }
                }
            }
        }
        return names;
    }

    public HashMap<String, HashMap<String, List<GeneratorInfo>>> getInfos() {
        return infos;
    }

    @Override
    public void read(CompoundNBT nbt) {
        infos = new HashMap<>();
        for (String uuid : nbt.getCompound("values").keySet()) {
            CompoundNBT uuidNbt = nbt.getCompound("values").getCompound(uuid);
            HashMap<String, List<GeneratorInfo>> generators = new HashMap<>();
            for (String genName : uuidNbt.keySet()) {
                CompoundNBT genNbt = uuidNbt.getCompound(genName);
                for (String s : genNbt.keySet()) {
                    GeneratorInfo generatorInfo = new GeneratorInfo();
                    generatorInfo.deserializeNBT(genNbt.getCompound(s));
                    generators.computeIfAbsent(genName, s1 -> new ArrayList<>()).add(generatorInfo);
                }
            }
            infos.put(uuid, generators);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        CompoundNBT values = new CompoundNBT();
        for (String uuid : infos.keySet()) {
            CompoundNBT uuidNbt = new CompoundNBT();
            for (String genName : infos.get(uuid).keySet()) {
                int i = 0;
                CompoundNBT genNbt = new CompoundNBT();
                for (GeneratorInfo generatorInfo : infos.get(uuid).get(genName)) {
                    genNbt.put(i +"", generatorInfo.serializeNBT());
                    ++i;
                }
                uuidNbt.put(genName, genNbt);
            }
            values.put(uuid, uuidNbt);
        }
        compound.put("values", values);
        return compound;
    }

    @Nullable
    public static MycelialDataManager getData(IWorld world) {
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = ((ServerWorld) world).getServer().getWorld(World.OVERWORLD);
            MycelialDataManager data = serverWorld.getSavedData().getOrCreate(MycelialDataManager::new, NAME);
            return data;
        }
        return null;
    }

    public static class GeneratorInfo implements INBTSerializable<CompoundNBT> {

        private long lastRun;
        private long lastTracked;
        private BlockPos pos;
        private RegistryKey<World> world;

        public GeneratorInfo(long lastRun, long lastTracked, BlockPos pos, RegistryKey<World> world) {
            this.lastRun = lastRun;
            this.lastTracked = lastTracked;
            this.pos = pos;
            this.world = world;
        }

        public GeneratorInfo() {
        }

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT compoundNBT = new CompoundNBT();
            compoundNBT.putLong("Run", lastRun);
            compoundNBT.putLong("Track", lastTracked);
            compoundNBT.putLong("Pos", pos.toLong());
            compoundNBT.putString("RKValue", world.getLocation().toString());
            return compoundNBT;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            lastRun = nbt.getLong("Run");
            lastTracked = nbt.getLong("Track");
            pos = BlockPos.fromLong(nbt.getLong("Pos"));
            this.world = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(nbt.getString("RKValue")));
        }
    }
}

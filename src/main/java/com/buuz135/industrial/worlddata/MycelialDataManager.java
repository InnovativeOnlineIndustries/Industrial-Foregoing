/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.buuz135.industrial.worlddata;

import com.buuz135.industrial.block.generator.mycelial.IMycelialGeneratorType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MycelialDataManager extends SavedData {

    public static final String NAME = "IFMycelial";
    public HashMap<String, HashMap<String, List<GeneratorInfo>>> infos = new HashMap();

    public MycelialDataManager() {
    }

    public static void setGeneratorInfo(String uuid, Level world, BlockPos pos, IMycelialGeneratorType type) {
        if (world instanceof ServerLevel) {
            MycelialDataManager dataManager = getData(world);
            List<GeneratorInfo> generatorInfos = dataManager.getInfos().computeIfAbsent(uuid, s -> new HashMap<>()).computeIfAbsent(type.getName(), s -> new ArrayList<>());
            boolean updated = false;
            for (GeneratorInfo generatorInfo : generatorInfos) {
                if (generatorInfo.pos.equals(pos) && generatorInfo.world.equals(world.dimension())) {
                    updated = true;
                    generatorInfo.lastRun = world.getGameTime();
                }
            }
            if (!updated) {
                generatorInfos.add(new GeneratorInfo(world.getGameTime(), 0, pos, world.dimension()));
            }
            dataManager.setDirty();
        }
    }

    public static void removeGeneratorInfo(String uuid, Level world, BlockPos pos, IMycelialGeneratorType type) {
        MycelialDataManager dataManager = getData(world);
        List<GeneratorInfo> generatorInfos = dataManager.getInfos().computeIfAbsent(uuid, s -> new HashMap<>()).computeIfAbsent(type.getName(), s -> new ArrayList<>());
        generatorInfos.removeIf(generatorInfo -> generatorInfo.world.equals(world.dimension()) && generatorInfo.pos.equals(pos));
        dataManager.setDirty();
    }

    public static List<String> getReactorAvailable(String uuid, Level world, boolean execute) {
        List<String> names = new ArrayList<>();
        if (world instanceof ServerLevel) {
            MycelialDataManager dataManager = getData(world);
            HashMap<String, List<GeneratorInfo>> generators = dataManager.getInfos().computeIfAbsent(uuid, s -> new HashMap<>());
            for (IMycelialGeneratorType type : IMycelialGeneratorType.TYPES) {
                for (GeneratorInfo generatorInfo : generators.computeIfAbsent(type.getName(), s -> new ArrayList<>())) {
                    if (generatorInfo.lastTracked + 5 <= world.getGameTime() && generatorInfo.lastRun + 5 >= world.getGameTime()) {
                        names.add(type.getName());
                        if (execute) generatorInfo.lastTracked = world.getGameTime();
                        dataManager.setDirty();
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

    public static MycelialDataManager load(CompoundTag nbt) {
        MycelialDataManager mycelialDataManager = new MycelialDataManager();
        for (String uuid : nbt.getCompound("values").getAllKeys()) {
            CompoundTag uuidNbt = nbt.getCompound("values").getCompound(uuid);
            HashMap<String, List<GeneratorInfo>> generators = new HashMap<>();
            for (String genName : uuidNbt.getAllKeys()) {
                CompoundTag genNbt = uuidNbt.getCompound(genName);
                for (String s : genNbt.getAllKeys()) {
                    GeneratorInfo generatorInfo = new GeneratorInfo();
                    generatorInfo.deserializeNBT(genNbt.getCompound(s));
                    generators.computeIfAbsent(genName, s1 -> new ArrayList<>()).add(generatorInfo);
                }
            }
            mycelialDataManager.infos.put(uuid, generators);
        }
        return mycelialDataManager;
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        CompoundTag values = new CompoundTag();
        for (String uuid : infos.keySet()) {
            CompoundTag uuidNbt = new CompoundTag();
            for (String genName : infos.get(uuid).keySet()) {
                int i = 0;
                CompoundTag genNbt = new CompoundTag();
                for (GeneratorInfo generatorInfo : infos.get(uuid).get(genName)) {
                    genNbt.put(i + "", generatorInfo.serializeNBT());
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
    public static MycelialDataManager getData(LevelAccessor world) {
        if (world instanceof ServerLevel) {
            ServerLevel serverWorld = ((ServerLevel) world).getServer().getLevel(Level.OVERWORLD);
            MycelialDataManager data = serverWorld.getDataStorage().computeIfAbsent(MycelialDataManager::load, MycelialDataManager::new, NAME);
            return data;
        }
        return null;
    }

    public static class GeneratorInfo implements INBTSerializable<CompoundTag> {

        private long lastRun;
        private long lastTracked;
        private BlockPos pos;
        private ResourceKey<Level> world;

        public GeneratorInfo(long lastRun, long lastTracked, BlockPos pos, ResourceKey<Level> world) {
            this.lastRun = lastRun;
            this.lastTracked = lastTracked;
            this.pos = pos;
            this.world = world;
        }

        public GeneratorInfo() {
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag compoundNBT = new CompoundTag();
            compoundNBT.putLong("Run", lastRun);
            compoundNBT.putLong("Track", lastTracked);
            compoundNBT.putLong("Pos", pos.asLong());
            compoundNBT.putString("RKValue", world.location().toString());
            return compoundNBT;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            lastRun = nbt.getLong("Run");
            lastTracked = nbt.getLong("Track");
            pos = BlockPos.of(nbt.getLong("Pos"));
            this.world = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(nbt.getString("RKValue")));
        }
    }
}

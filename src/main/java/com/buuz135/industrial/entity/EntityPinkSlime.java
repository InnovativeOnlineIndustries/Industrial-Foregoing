/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
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
package com.buuz135.industrial.entity;

import com.buuz135.industrial.proxy.CommonProxy;
import com.buuz135.industrial.proxy.ItemRegistry;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.item.Item;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nullable;

public class EntityPinkSlime extends EntitySlime {


    public EntityPinkSlime(World worldIn) {
        super(worldIn);
    }

    @Override
    public boolean getCanSpawnHere() {
        return false;
    }

    @Override
    protected EntityPinkSlime createInstance() {
        return new EntityPinkSlime(this.world);
    }

    @Override
    protected Item getDropItem() {
        return this.getSlimeSize() == 1 ? ItemRegistry.pinkSlime : null;
    }

    @Nullable
    @Override
    protected ResourceLocation getLootTable() {
        return this.getSlimeSize() == 1 ? CommonProxy.PINK_SLIME_LOOT : LootTableList.EMPTY;
    }

    @Override
    protected EnumParticleTypes getParticleType() {
        return EnumParticleTypes.END_ROD;
    }

}

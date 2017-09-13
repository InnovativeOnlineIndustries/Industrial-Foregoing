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

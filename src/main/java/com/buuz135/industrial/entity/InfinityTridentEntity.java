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

package com.buuz135.industrial.entity;

import com.buuz135.industrial.item.infinity.ItemInfinity;
import com.buuz135.industrial.item.infinity.item.ItemInfinityTrident;
import com.buuz135.industrial.module.ModuleTool;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

public class InfinityTridentEntity extends AbstractArrow {

    private static final EntityDataAccessor<Integer> LOYALTY_LEVEL = SynchedEntityData.defineId(InfinityTridentEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> CHANNELING = SynchedEntityData.defineId(InfinityTridentEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TIER = SynchedEntityData.defineId(InfinityTridentEntity.class, EntityDataSerializers.INT);

    public static int DAMAGE = 8;

    private ItemStack thrownStack;
    private boolean dealtDamage;
    public int returningTicks;

    public InfinityTridentEntity(EntityType<? extends InfinityTridentEntity> type, Level worldIn) {
        super(type, worldIn);
        this.thrownStack = new ItemStack(ModuleTool.INFINITY_TRIDENT.get());
    }

    public InfinityTridentEntity(Level worldIn, LivingEntity thrower, ItemStack thrownStackIn) {
        super((EntityType<? extends AbstractArrow>) ModuleTool.TRIDENT_ENTITY_TYPE.get(), thrower, worldIn);
        this.thrownStack = new ItemStack(ModuleTool.INFINITY_TRIDENT.get());
        this.thrownStack = thrownStackIn.copy();
        this.entityData.set(LOYALTY_LEVEL, ((ItemInfinityTrident) ModuleTool.INFINITY_TRIDENT.get()).getCurrentLoyalty(thrownStack));
        this.entityData.set(CHANNELING, ((ItemInfinityTrident) ModuleTool.INFINITY_TRIDENT.get()).getCurrentChanneling(thrownStack));
        this.entityData.set(TIER, ItemInfinity.getSelectedTier(thrownStack).getRadius());
    }

    @OnlyIn(Dist.CLIENT)
    public InfinityTridentEntity(Level worldIn, double x, double y, double z) {
        super((EntityType<? extends AbstractArrow>) ModuleTool.TRIDENT_ENTITY_TYPE.get(), x, y, z, worldIn);
        this.thrownStack = new ItemStack(ModuleTool.INFINITY_TRIDENT.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LOYALTY_LEVEL, 0);
        this.entityData.define(CHANNELING, false);
        this.entityData.define(TIER, 1);
    }

    @Override
    public void tick() {
        if (this.inGroundTime > 4 || this.getY() < 0) {
            this.dealtDamage = true;
        }

        Entity entity = this.getOwner();
        if ((this.dealtDamage || this.isNoPhysics()) && entity != null) {
            int loyaltyLevel = this.entityData.get(LOYALTY_LEVEL);
            if (!this.shouldReturnToThrower()) {
                if (!this.level.isClientSide && this.pickup == AbstractArrow.Pickup.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1F);
                }
                this.onClientRemoval();
            } else if (loyaltyLevel > 0) {
                this.setNoPhysics(true);
                Vec3 vector3d = new Vec3(entity.getX() - this.getX(), entity.getEyeY() - this.getY(), entity.getZ() - this.getZ());
                this.setPosRaw(this.getX(), this.getY() + vector3d.y * 0.015D * (double) loyaltyLevel, this.getZ());
                if (this.level.isClientSide) {
                    this.yOld = this.getY();
                }

                double d0 = 0.05D * (double) loyaltyLevel;
                this.setDeltaMovement(this.getDeltaMovement().scale(0.95D).add(vector3d.normalize().scale(d0)));
                if (this.returningTicks == 0) {
                    this.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
                }

                ++this.returningTicks;
            }
        }

        super.tick();
    }

    protected void onHitEntity(EntityHitResult p_213868_1_) {
        Entity target = p_213868_1_.getEntity();
        float damageHit = (float) (DAMAGE + Math.pow(2, this.entityData.get(TIER))) * 0.5f;
        if (target instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity) target;
            damageHit += EnchantmentHelper.getDamageBonus(this.thrownStack, livingentity.getMobType());
        }
        Entity entity1 = this.getOwner();
        DamageSource damagesource = DamageSource.trident(this, (Entity) (entity1 == null ? this : entity1));
        this.dealtDamage = true;
        SoundEvent soundevent = SoundEvents.TRIDENT_HIT;
        if (target.hurt(damagesource, damageHit)) {
            if (target.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (target instanceof LivingEntity) {
                LivingEntity livingentity1 = (LivingEntity) target;
                if (entity1 instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(livingentity1, entity1);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity) entity1, livingentity1);
                }

                this.doPostHurtEffects(livingentity1);
            }
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
        float f1 = 1.0F;
        AABB area = new AABB(target.getX(), target.getY(), target.getZ(), target.getX(), target.getY(), target.getZ()).inflate(this.entityData.get(TIER));
        List<Mob> mobs = this.getCommandSenderWorld().getEntitiesOfClass(Mob.class, area);
        if (entity1 instanceof Player) {
            mobs.forEach(mobEntity -> {
                float damage = (float) (DAMAGE + Math.pow(2, this.entityData.get(TIER))) * 0.5f;
                if (target instanceof LivingEntity) {
                    LivingEntity livingentity = (LivingEntity) target;
                    damage += EnchantmentHelper.getDamageBonus(this.thrownStack, livingentity.getMobType());
                }
                mobEntity.hurt(DamageSource.playerAttack((Player) entity1), damage);
            });
            this.getCommandSenderWorld().getEntitiesOfClass(ItemEntity.class, area.inflate(1)).forEach(itemEntity -> {
                itemEntity.setNoPickUpDelay();
                itemEntity.teleportTo(entity1.blockPosition().getX(), entity1.blockPosition().getY() + 1, entity1.blockPosition().getZ());
            });
            this.getCommandSenderWorld().getEntitiesOfClass(ExperienceOrb.class, area.inflate(1)).forEach(entityXPOrb -> entityXPOrb.teleportTo(entity1.blockPosition().getX(), entity1.blockPosition().getY(), entity1.blockPosition().getZ()));
        }
        if (this.level instanceof ServerLevel && this.entityData.get(CHANNELING)) {
            BlockPos blockpos = target.blockPosition();
            if (this.level.canSeeSky(blockpos)) {
                LightningBolt lightningboltentity = EntityType.LIGHTNING_BOLT.create(this.level);
                lightningboltentity.moveTo(Vec3.atBottomCenterOf(blockpos));
                lightningboltentity.setCause(entity1 instanceof ServerPlayer ? (ServerPlayer) entity1 : null);
                this.level.addFreshEntity(lightningboltentity);
                soundevent = SoundEvents.TRIDENT_THUNDER;
                f1 = 5.0F;
                mobs.forEach(mobEntity -> {
                    if (this.level.canSeeSky(mobEntity.blockPosition())) {
                        LightningBolt lightningboltentity1 = EntityType.LIGHTNING_BOLT.create(this.level);
                        lightningboltentity1.moveTo(Vec3.atBottomCenterOf(mobEntity.blockPosition()));
                        lightningboltentity1.setCause(entity1 instanceof ServerPlayer ? (ServerPlayer) entity1 : null);
                        this.level.addFreshEntity(lightningboltentity1);
                    }
                });
            }
        }
        this.playSound(soundevent, f1, 1.0F);
    }

    private boolean shouldReturnToThrower() {
        Entity entity = this.getOwner();
        if (entity != null && entity.isAlive()) {
            return !(entity instanceof ServerPlayer) || !entity.isSpectator();
        } else {
            return false;
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        return this.thrownStack.copy();
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isEnchanted() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double dist) {
        return dist <= 64;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void tickDespawn() {

    }

    @Override
    protected float getWaterInertia() {
        return 0.99f;
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Trident", 10)) {
            this.thrownStack = ItemStack.of(compound.getCompound("Trident"));
        }

        this.dealtDamage = compound.getBoolean("DealtDamage");
        this.entityData.set(LOYALTY_LEVEL, ((ItemInfinityTrident) ModuleTool.INFINITY_TRIDENT.get()).getCurrentLoyalty(thrownStack));
        this.entityData.set(CHANNELING, ((ItemInfinityTrident) ModuleTool.INFINITY_TRIDENT.get()).getCurrentChanneling(thrownStack));
        this.entityData.set(TIER, ItemInfinity.getSelectedTier(thrownStack).getRadius());
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.put("Trident", this.thrownStack.save(new CompoundTag()));
        compound.putBoolean("DealtDamage", this.dealtDamage);
    }

}

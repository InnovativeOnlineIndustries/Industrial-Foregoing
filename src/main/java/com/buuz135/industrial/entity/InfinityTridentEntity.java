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
import com.buuz135.industrial.module.ModuleTool;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.List;

public class InfinityTridentEntity extends AbstractArrowEntity {

    private static final DataParameter<Integer> LOYALTY_LEVEL = EntityDataManager.createKey(InfinityTridentEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> CHANNELING = EntityDataManager.createKey(InfinityTridentEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> TIER = EntityDataManager.createKey(InfinityTridentEntity.class, DataSerializers.VARINT);

    public static int DAMAGE = 8;

    private ItemStack thrownStack;
    private boolean dealtDamage;
    public int returningTicks;

    public InfinityTridentEntity(EntityType<? extends InfinityTridentEntity> type, World worldIn) {
        super(type, worldIn);
        this.thrownStack = new ItemStack(ModuleTool.INFINITY_TRIDENT);
    }

    public InfinityTridentEntity(World worldIn, LivingEntity thrower, ItemStack thrownStackIn) {
        super(ModuleTool.TRIDENT_ENTITY_TYPE, thrower, worldIn);
        this.thrownStack = new ItemStack(ModuleTool.INFINITY_TRIDENT);
        this.thrownStack = thrownStackIn.copy();
        this.dataManager.set(LOYALTY_LEVEL, ModuleTool.INFINITY_TRIDENT.getCurrentLoyalty(thrownStack));
        this.dataManager.set(CHANNELING, ModuleTool.INFINITY_TRIDENT.getCurrentChanneling(thrownStack));
        this.dataManager.set(TIER, ItemInfinity.getSelectedTier(thrownStack).getRadius());
    }

    @OnlyIn(Dist.CLIENT)
    public InfinityTridentEntity(World worldIn, double x, double y, double z) {
        super(ModuleTool.TRIDENT_ENTITY_TYPE, x, y, z, worldIn);
        this.thrownStack = new ItemStack(ModuleTool.INFINITY_TRIDENT);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(LOYALTY_LEVEL, 0);
        this.dataManager.register(CHANNELING, false);
        this.dataManager.register(TIER, 1);
    }

    @Override
    public void tick() {
        if (this.timeInGround > 4 || this.getPosY() < 0) {
            this.dealtDamage = true;
        }

        Entity entity = this.func_234616_v_();
        if ((this.dealtDamage || this.getNoClip()) && entity != null) {
            int loyaltyLevel = this.dataManager.get(LOYALTY_LEVEL);
            if (!this.shouldReturnToThrower()) {
                if (!this.world.isRemote && this.pickupStatus == AbstractArrowEntity.PickupStatus.ALLOWED) {
                    this.entityDropItem(this.getArrowStack(), 0.1F);
                }
                this.remove();
            } else if (loyaltyLevel > 0){
                this.setNoClip(true);
                Vector3d vector3d = new Vector3d(entity.getPosX() - this.getPosX(), entity.getPosYEye() - this.getPosY(), entity.getPosZ() - this.getPosZ());
                this.setRawPosition(this.getPosX(), this.getPosY() + vector3d.y * 0.015D * (double)loyaltyLevel, this.getPosZ());
                if (this.world.isRemote) {
                    this.lastTickPosY = this.getPosY();
                }

                double d0 = 0.05D * (double)loyaltyLevel;
                this.setMotion(this.getMotion().scale(0.95D).add(vector3d.normalize().scale(d0)));
                if (this.returningTicks == 0) {
                    this.playSound(SoundEvents.ITEM_TRIDENT_RETURN, 10.0F, 1.0F);
                }

                ++this.returningTicks;
            }
        }

        super.tick();
    }

    protected void onEntityHit(EntityRayTraceResult p_213868_1_) {
        Entity target = p_213868_1_.getEntity();
        float damageHit = (float) (DAMAGE + Math.pow(2, this.dataManager.get(TIER))) * 0.5f;
        if (target instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)target;
            damageHit += EnchantmentHelper.getModifierForCreature(this.thrownStack, livingentity.getCreatureAttribute());
        }
        Entity entity1 = this.func_234616_v_();
        DamageSource damagesource = DamageSource.causeTridentDamage(this, (Entity)(entity1 == null ? this : entity1));
        this.dealtDamage = true;
        SoundEvent soundevent = SoundEvents.ITEM_TRIDENT_HIT;
        if (target.attackEntityFrom(damagesource, damageHit)) {
            if (target.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (target instanceof LivingEntity) {
                LivingEntity livingentity1 = (LivingEntity)target;
                if (entity1 instanceof LivingEntity) {
                    EnchantmentHelper.applyThornEnchantments(livingentity1, entity1);
                    EnchantmentHelper.applyArthropodEnchantments((LivingEntity)entity1, livingentity1);
                }

                this.arrowHit(livingentity1);
            }
        }

        this.setMotion(this.getMotion().mul(-0.01D, -0.1D, -0.01D));
        float f1 = 1.0F;
        AxisAlignedBB area = new AxisAlignedBB(target.getPosX(), target.getPosY(), target.getPosZ(), target.getPosX(), target.getPosY(), target.getPosZ()).grow(this.dataManager.get(TIER));
        List<MobEntity> mobs = this.getEntityWorld().getEntitiesWithinAABB(MobEntity.class, area);
        if (entity1 instanceof PlayerEntity){
            mobs.forEach(mobEntity -> {
                float damage = (float) (DAMAGE + Math.pow(2, this.dataManager.get(TIER))) * 0.5f;
                if (target instanceof LivingEntity) {
                    LivingEntity livingentity = (LivingEntity)target;
                    damage += EnchantmentHelper.getModifierForCreature(this.thrownStack, livingentity.getCreatureAttribute());
                }
                mobEntity.attackEntityFrom(DamageSource.causePlayerDamage((PlayerEntity) entity1), damage);
            });
            this.getEntityWorld().getEntitiesWithinAABB(ItemEntity.class, area.grow(1)).forEach(itemEntity -> {
                itemEntity.setNoPickupDelay();
                itemEntity.setPositionAndUpdate(entity1.getPosition().getX(), entity1.getPosition().getY() + 1, entity1.getPosition().getZ());
            });
            this.getEntityWorld().getEntitiesWithinAABB(ExperienceOrbEntity.class, area.grow(1)).forEach(entityXPOrb -> entityXPOrb.setPositionAndUpdate(entity1.getPosition().getX(), entity1.getPosition().getY(), entity1.getPosition().getZ()));
        }
        if (this.world instanceof ServerWorld && this.dataManager.get(CHANNELING)) {
            BlockPos blockpos = target.getPosition();
            if (this.world.canSeeSky(blockpos)) {
                LightningBoltEntity lightningboltentity = EntityType.LIGHTNING_BOLT.create(this.world);
                lightningboltentity.moveForced(Vector3d.copyCenteredHorizontally(blockpos));
                lightningboltentity.setCaster(entity1 instanceof ServerPlayerEntity ? (ServerPlayerEntity)entity1 : null);
                this.world.addEntity(lightningboltentity);
                soundevent = SoundEvents.ITEM_TRIDENT_THUNDER;
                f1 = 5.0F;
                mobs.forEach(mobEntity -> {
                    if (this.world.canSeeSky(mobEntity.getPosition())) {
                        LightningBoltEntity lightningboltentity1 = EntityType.LIGHTNING_BOLT.create(this.world);
                        lightningboltentity1.moveForced(Vector3d.copyCenteredHorizontally(mobEntity.getPosition()));
                        lightningboltentity1.setCaster(entity1 instanceof ServerPlayerEntity ? (ServerPlayerEntity)entity1 : null);
                        this.world.addEntity(lightningboltentity1);
                    }
                });
            }
        }
        this.playSound(soundevent, f1, 1.0F);
    }

    private boolean shouldReturnToThrower() {
        Entity entity = this.func_234616_v_();
        if (entity != null && entity.isAlive()) {
            return !(entity instanceof ServerPlayerEntity) || !entity.isSpectator();
        } else {
            return false;
        }
    }

    @Override
    protected ItemStack getArrowStack() {
        return this.thrownStack.copy();
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isEnchanted(){
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean isInRangeToRender3d(double x, double y, double z) {
        return true;
    }

    @Override
    public boolean isInRangeToRenderDist(double dist) {
        return dist <= 64;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void func_225516_i_() {

    }

    @Override
    protected float getWaterDrag() {
        return 0.99f;
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.contains("Trident", 10)) {
            this.thrownStack = ItemStack.read(compound.getCompound("Trident"));
        }

        this.dealtDamage = compound.getBoolean("DealtDamage");
        this.dataManager.set(LOYALTY_LEVEL, ModuleTool.INFINITY_TRIDENT.getCurrentLoyalty(thrownStack));
        this.dataManager.set(CHANNELING, ModuleTool.INFINITY_TRIDENT.getCurrentChanneling(thrownStack));
        this.dataManager.set(TIER, ItemInfinity.getSelectedTier(thrownStack).getRadius());
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.put("Trident", this.thrownStack.write(new CompoundNBT()));
        compound.putBoolean("DealtDamage", this.dealtDamage);
    }

}

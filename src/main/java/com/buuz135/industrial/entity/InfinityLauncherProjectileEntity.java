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

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.item.MobImprisonmentToolItem;
import com.buuz135.industrial.item.infinity.item.ItemInfinityLauncher;
import com.buuz135.industrial.module.ModuleTool;
import com.buuz135.industrial.proxy.network.PlungerPlayerHitMessage;
import com.buuz135.industrial.utils.IFAttachments;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class InfinityLauncherProjectileEntity extends AbstractArrow {

    private static final EntityDataAccessor<Integer> PLUNGER_ACTION = SynchedEntityData.defineId(InfinityLauncherProjectileEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TIER = SynchedEntityData.defineId(InfinityLauncherProjectileEntity.class, EntityDataSerializers.INT);

    public InfinityLauncherProjectileEntity(EntityType<? extends InfinityLauncherProjectileEntity> type, Level world) {
        super(type, world);
    }

    public InfinityLauncherProjectileEntity(Level worldIn, LivingEntity thrower, ItemInfinityLauncher.PlungerAction plungerAction, int tier, ItemStack stack) {
        super((EntityType<? extends AbstractArrow>) ModuleTool.INFINITY_LAUNCHER_PROJECTILE_ENTITY_TYPE.value(), thrower, worldIn, ItemStack.EMPTY, stack);
        this.entityData.set(PLUNGER_ACTION, plungerAction.getId());
        this.entityData.set(TIER, tier);
    }

    @OnlyIn(Dist.CLIENT)
    public InfinityLauncherProjectileEntity(Level worldIn, double x, double y, double z) {
        super((EntityType<? extends AbstractArrow>) ModuleTool.INFINITY_LAUNCHER_PROJECTILE_ENTITY_TYPE.value(), x, y, z, worldIn, ItemStack.EMPTY, ItemStack.EMPTY);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        Entity player = this.getOwner();
        ItemInfinityLauncher.PlungerAction action = ItemInfinityLauncher.PlungerAction.getFromId(this.entityData.get(PLUNGER_ACTION));
        if (player instanceof Player && action == ItemInfinityLauncher.PlungerAction.RELEASE) {
            for (ItemStack itemStack : ((Player) player).getInventory().items) {
                if (itemStack.getItem() instanceof MobImprisonmentToolItem && itemStack.has(IFAttachments.MOB_IMPRISONMENT_TOOL)) {
                    ItemStack copy = itemStack.copy();
                    if (((MobImprisonmentToolItem) itemStack.getItem()).release((Player) player, result.getBlockPos(), result.getDirection(), this.level(), copy)) {
                        ((Player) player).getInventory().removeItem(itemStack);
                        ((Player) player).getInventory().add(copy);
                        break;
                    }

                }
            }
            this.onClientRemoval();
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PLUNGER_ACTION, 0);
        builder.define(TIER, 0);
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return ItemStack.EMPTY;
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


    /*@Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }*/



    @Override
    protected void onHitEntity(EntityHitResult p_213868_1_) {
        Entity entity = p_213868_1_.getEntity();
        float f = (float) this.getDeltaMovement().length();
        int i = Mth.ceil(Mth.clamp((double) f * 1 + this.entityData.get(TIER), 0.0D, 2.147483647E9D));
        ItemInfinityLauncher.PlungerAction action = ItemInfinityLauncher.PlungerAction.getFromId(this.entityData.get(PLUNGER_ACTION));

        if (action == ItemInfinityLauncher.PlungerAction.DAMAGE) {
            if (this.isCritArrow()) {
                long j = (long) this.random.nextInt(i / 2 + 2);
                i = (int) Math.min(j + (long) i, 2147483647L);
            }
            Entity entity1 = this.getOwner();
            DamageSource damagesource;
            if (entity1 == null) {
                damagesource = level().damageSources().arrow(this, this);
            } else {
                damagesource = level().damageSources().arrow(this, entity1);
                if (entity1 instanceof LivingEntity) {
                    ((LivingEntity) entity1).setLastHurtMob(entity);
                }
            }
            boolean flag = entity.getType() == EntityType.ENDERMAN;
            int k = entity.getRemainingFireTicks();
            if (this.isOnFire() && !flag) {
                entity.igniteForSeconds(5);
            }
            if (entity.hurt(damagesource, (float) i)) {
                if (flag) {
                    return;
                }
                if (entity instanceof LivingEntity) {
                    LivingEntity livingentity = (LivingEntity) entity;
                    if (!this.level().isClientSide && livingentity instanceof Player) {
                        IndustrialForegoing.NETWORK.sendToNearby(this.level(), this.blockPosition(), 256, new PlungerPlayerHitMessage(livingentity.getUUID()));
                    }
                    if (1 > 0) {
                        Vec3 vector3d = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale((double) 1 * 0.6D);
                        if (vector3d.lengthSqr() > 0.0D) {
                            livingentity.push(vector3d.x, 0.1D, vector3d.z);
                        }
                    }
                    if (this.level() instanceof ServerLevel serverLevel && entity1 instanceof LivingEntity) {
                        EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, livingentity, damagesource, this.getWeaponItem());
                    }
                    this.doPostHurtEffects(livingentity);
                    if (entity1 != null && livingentity != entity1 && livingentity instanceof Player && entity1 instanceof ServerPlayer && !this.isSilent()) {
                        ((ServerPlayer) entity1).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
                    }
                }
                this.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
                if (this.getPierceLevel() <= 0) {
                    this.onClientRemoval();
                }
            } else {
                entity.setRemainingFireTicks(k);
                this.setDeltaMovement(this.getDeltaMovement().scale(-0.1D));
                this.setYRot(180.0F);
                this.yRotO += 180.0F;
                if (!this.level().isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
                    if (this.pickup == AbstractArrow.Pickup.ALLOWED) {
                        this.spawnAtLocation(this.getPickupItem(), 0.1F);
                    }

                    this.onClientRemoval();
                }
            }
        }
        Entity player = this.getOwner();
        if (player instanceof Player && entity instanceof LivingEntity && action == ItemInfinityLauncher.PlungerAction.CAPTURE) {
            for (ItemStack itemStack : ((Player) player).getInventory().items) {
                if (itemStack.getItem() instanceof MobImprisonmentToolItem && !itemStack.has(IFAttachments.MOB_IMPRISONMENT_TOOL)) {
                    ItemStack copy = itemStack.copy();
                    if (((MobImprisonmentToolItem) itemStack.getItem()).capture(copy, (LivingEntity) entity)) {
                        ((Player) player).getInventory().removeItem(itemStack);
                        ((Player) player).getInventory().add(copy);
                        break;
                    }

                }
            }
            this.onClientRemoval();
        }
    }


}

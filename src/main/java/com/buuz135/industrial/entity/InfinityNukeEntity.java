package com.buuz135.industrial.entity;

import com.buuz135.industrial.item.infinity.item.ItemInfinityNuke;
import com.buuz135.industrial.module.ModuleTool;
import com.buuz135.industrial.proxy.client.ClientProxy;
import com.buuz135.industrial.proxy.client.sound.TickeableSound;
import com.buuz135.industrial.utils.explosion.ExplosionTickHandler;
import com.buuz135.industrial.utils.explosion.ProcessExplosion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

public class InfinityNukeEntity extends Entity {

    private static final EntityDataAccessor<Integer> RADIUS = SynchedEntityData.defineId(InfinityNukeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> EXPLODING = SynchedEntityData.defineId(InfinityNukeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ARMED = SynchedEntityData.defineId(InfinityNukeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TICKS = SynchedEntityData.defineId(InfinityNukeEntity.class, EntityDataSerializers.INT);

    @Nullable
    private LivingEntity placedBy;
    private ItemStack original;
    private boolean exploding = false;
    private boolean armed = false;
    private int radius = 1;
    private int ticksExploding = 1;
    private ProcessExplosion explosionHelper;
    @OnlyIn(Dist.CLIENT)
    private TickeableSound chargingSound;
    @OnlyIn(Dist.CLIENT)
    private TickeableSound explodingSound;

    public InfinityNukeEntity(EntityType<? extends InfinityNukeEntity> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.original = new ItemStack(ModuleTool.INFINITY_NUKE);
        this.blocksBuilding = true;
    }

    public InfinityNukeEntity(Level worldIn, LivingEntity owner, ItemStack original) {
        this(ModuleTool.INFINITY_NUKE_ENTITY_TYPE, worldIn);
        this.placedBy = owner;
        this.original = original;
        this.radius = ItemInfinityNuke.getRadius(original);
        this.entityData.set(RADIUS, this.radius);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
        if (this.onGround) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
        }

        if (exploding) {
            if (level instanceof ServerLevel && explosionHelper == null) {
                explosionHelper = new ProcessExplosion(this.blockPosition(), ItemInfinityNuke.getRadius(original), (ServerLevel) this.level, 39, placedBy != null ? placedBy.getDisplayName().getString() : "");
                ExplosionTickHandler.processExplosionList.add(explosionHelper);
            }
            setTicksExploding(this.getTicksExploding() + 1);
            this.updateInWaterStateAndDoFluidPushing();
        }
        if (this.level.isClientSide && this.getEntityData().get(EXPLODING)) {
            if (this.level.isClientSide) {
                this.level.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 1.1D, this.getZ(), 0.0D, 0.0D, 0.0D);
                this.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, this.level.getBlockState(this.blockPosition().below())), this.getX() + this.level.getRandom().nextDouble() - 0.5, this.getY(), this.getZ() + this.level.getRandom().nextDouble() - 0.5, 0.0D, 0.0D, 0.0D);
            }
        }
        if (explosionHelper != null && explosionHelper.isDead) {
            this.onClientRemoval();
        }
        if (level.isClientSide) {
            tickClient();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void tickClient() {
        if (chargingSound == null && this.getEntityData().get(EXPLODING)) {
            Minecraft.getInstance().getSoundManager().play(chargingSound = new TickeableSound(this.level, this.blockPosition(), ModuleTool.NUKE_CHARGING, this.getEntityData().get(RADIUS), 10));
        }
        if (chargingSound != null) {
            chargingSound.increase();
            if (!Minecraft.getInstance().getSoundManager().isActive(chargingSound) && explodingSound == null) {
                explodingSound = new TickeableSound(this.level, this.blockPosition(), ClientProxy.NUKE_EXPLOSION, this.getEntityData().get(RADIUS), 10);
                explodingSound.setPitch(1);
                Minecraft.getInstance().getSoundManager().play(explodingSound);
            }
        }
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();

    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(RADIUS, 1);
        this.entityData.define(EXPLODING, false);
        this.entityData.define(ARMED, false);
        this.entityData.define(TICKS, 1);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.put("Original", this.getOriginal().serializeNBT());
        compound.putBoolean("Exploding", this.isExploding());
        compound.putBoolean("Armed", this.isArmed());
        compound.putInt("TicksExploding", this.getTicksExploding());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.setArmed(compound.getBoolean("Armed"));
        this.setExploding(compound.getBoolean("Exploding"));
        this.setOriginal(ItemStack.of(compound.getCompound("Original")));
        this.setTicksExploding(compound.getInt("TicksExploding"));
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (this.isExploding()) return InteractionResult.SUCCESS;
        if (player.level.isClientSide && hand == InteractionHand.MAIN_HAND && player.getItemInHand(hand).isEmpty()) {
            arm();
        }
        if (!player.level.isClientSide && hand == InteractionHand.MAIN_HAND) {
            if (player.getItemInHand(hand).isEmpty()) {
                if (player.isShiftKeyDown()) {
                    ItemHandlerHelper.giveItemToPlayer(player, this.original);
                    this.onClientRemoval();
                    return InteractionResult.SUCCESS;
                } else {
                    this.setArmed(!isArmed());
                    return InteractionResult.SUCCESS;
                }
            }
            if (!this.isExploding() && this.isArmed() && player.getItemInHand(hand).getItem().equals(Items.FLINT_AND_STEEL)) {
                this.setExploding(true);
                player.getItemInHand(hand).hurtAndBreak(1, player, (playerEntity) -> {
                    playerEntity.broadcastBreakEvent(hand);
                });
                return InteractionResult.SUCCESS;
            }
        }
        return super.interact(player, hand);
    }

    @OnlyIn(Dist.CLIENT)
    private void arm() {
        Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(ClientProxy.NUKE_ARMING, SoundSource.BLOCKS, 1, 1, this.blockPosition()));
    }

    @Override
    protected MovementEmission getMovementEmission() {
        return MovementEmission.EVENTS;
    }

    @Override
    public boolean isPickable() {
        return !this.isAlive();
    }

    public ItemStack getOriginal() {
        return original;
    }

    public void setOriginal(ItemStack original) {
        this.original = original;
    }

    public boolean isExploding() {
        return exploding;
    }

    public void setExploding(boolean exploding) {
        this.exploding = exploding;
        this.getEntityData().set(EXPLODING, exploding);
    }

    public boolean isArmed() {
        return armed;
    }

    public void setArmed(boolean armed) {
        this.armed = armed;
        this.getEntityData().set(ARMED, armed);
    }

    public boolean isDataArmed() {
        return this.getEntityData().get(ARMED);
    }

    public boolean isDataExploding() {
        return this.getEntityData().get(EXPLODING);
    }

    public int getTicksExploding() {
        return ticksExploding;
    }

    public void setTicksExploding(int ticksExploding) {
        this.ticksExploding = ticksExploding;
        this.getEntityData().set(TICKS, ticksExploding);
    }

    public int getDataTicksExploding() {
        return this.getEntityData().get(TICKS);
    }
}

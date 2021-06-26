package com.buuz135.industrial.entity;

import com.buuz135.industrial.item.infinity.item.ItemInfinityNuke;
import com.buuz135.industrial.module.ModuleTool;
import com.buuz135.industrial.proxy.client.sound.TickeableSound;
import com.buuz135.industrial.utils.explosion.ExplosionTickHandler;
import com.buuz135.industrial.utils.explosion.ProcessExplosion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

public class InfinityNukeEntity extends Entity {

    private static final DataParameter<Integer> RADIUS = EntityDataManager.createKey(InfinityNukeEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> EXPLODING = EntityDataManager.createKey(InfinityNukeEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> ARMED = EntityDataManager.createKey(InfinityNukeEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> TICKS = EntityDataManager.createKey(InfinityNukeEntity.class, DataSerializers.VARINT);

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

    public InfinityNukeEntity(EntityType<? extends InfinityNukeEntity> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
        this.original = new ItemStack(ModuleTool.INFINITY_NUKE);
        this.preventEntitySpawning = true;
    }

    public InfinityNukeEntity(World worldIn, LivingEntity owner, ItemStack original) {
        this(ModuleTool.INFINITY_NUKE_ENTITY_TYPE, worldIn);
        this.placedBy = owner;
        this.original = original;
        this.radius = ItemInfinityNuke.getRadius(original);
        this.dataManager.set(RADIUS, this.radius);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.hasNoGravity()) {
            this.setMotion(this.getMotion().add(0.0D, -0.04D, 0.0D));
        }
        this.move(MoverType.SELF, this.getMotion());
        this.setMotion(this.getMotion().scale(0.98D));
        if (this.onGround) {
            this.setMotion(this.getMotion().mul(0.7D, -0.5D, 0.7D));
        }

        if (exploding) {
            if (world instanceof ServerWorld && explosionHelper == null) {
                explosionHelper = new ProcessExplosion(this.getPosition(), ItemInfinityNuke.getRadius(original), (ServerWorld) this.world, 39, placedBy.getDisplayName().getString());
                ExplosionTickHandler.processExplosionList.add(explosionHelper);
            }
            setTicksExploding(this.getTicksExploding() + 1);
            this.func_233566_aG_();
        }
        if (this.world.isRemote && this.getDataManager().get(EXPLODING)) {
            if (this.world.isRemote) {
                this.world.addParticle(ParticleTypes.SMOKE, this.getPosX(), this.getPosY() + 1.1D, this.getPosZ(), 0.0D, 0.0D, 0.0D);
                this.world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, this.world.getBlockState(this.getPosition().down())), this.getPosX() + this.world.getRandom().nextDouble() - 0.5, this.getPosY(), this.getPosZ() + this.world.getRandom().nextDouble() - 0.5, 0.0D, 0.0D, 0.0D);
            }
        }
        if (explosionHelper != null && explosionHelper.isDead) {
            this.remove();
        }
        if (world.isRemote) {
            tickClient();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void tickClient() {
        if (chargingSound == null && this.getDataManager().get(EXPLODING)) {
            Minecraft.getInstance().getSoundHandler().play(chargingSound = new TickeableSound(this.world, this.getPosition(), ModuleTool.NUKE_CHARGING, this.getDataManager().get(RADIUS), 10));
        }
        if (chargingSound != null) {
            chargingSound.increase();
            if (!Minecraft.getInstance().getSoundHandler().isPlaying(chargingSound) && explodingSound == null) {
                explodingSound = new TickeableSound(this.world, this.getPosition(), ModuleTool.NUKE_EXPLOSION, this.getDataManager().get(RADIUS), 10);
                explodingSound.setPitch(1);
                Minecraft.getInstance().getSoundHandler().play(explodingSound);
            }
        }
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();

    }

    @Override
    protected void registerData() {
        this.dataManager.register(RADIUS, 1);
        this.dataManager.register(EXPLODING, false);
        this.dataManager.register(ARMED, false);
        this.dataManager.register(TICKS, 1);
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.put("Original", this.getOriginal().serializeNBT());
        compound.putBoolean("Exploding", this.isExploding());
        compound.putBoolean("Armed", this.isArmed());
        compound.putInt("TicksExploding", this.getTicksExploding());
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        this.setArmed(compound.getBoolean("Armed"));
        this.setExploding(compound.getBoolean("Exploding"));
        this.setOriginal(ItemStack.read(compound.getCompound("Original")));
        this.setTicksExploding(compound.getInt("TicksExploding"));
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
        if (this.isExploding()) return ActionResultType.SUCCESS;
        if (player.world.isRemote && hand == Hand.MAIN_HAND && player.getHeldItem(hand).isEmpty()) {
            Minecraft.getInstance().getSoundHandler().play(new SimpleSound(ModuleTool.NUKE_ARMING, SoundCategory.BLOCKS, 1, 1, this.getPosition()));
        }
        if (!player.world.isRemote && hand == Hand.MAIN_HAND) {
            if (player.getHeldItem(hand).isEmpty()) {
                if (player.isSneaking()) {
                    ItemHandlerHelper.giveItemToPlayer(player, this.original);
                    this.remove();
                    return ActionResultType.SUCCESS;
                } else {
                    this.setArmed(!isArmed());
                    return ActionResultType.SUCCESS;
                }
            }
            if (!this.isExploding() && this.isArmed() && player.getHeldItem(hand).getItem().equals(Items.FLINT_AND_STEEL)) {
                this.setExploding(true);
                player.getHeldItem(hand).damageItem(1, player, (playerEntity) -> {
                    playerEntity.sendBreakAnimation(hand);
                });
                return ActionResultType.SUCCESS;
            }
        }
        return super.processInitialInteract(player, hand);
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.removed;
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
        this.getDataManager().set(EXPLODING, exploding);
    }

    public boolean isArmed() {
        return armed;
    }

    public void setArmed(boolean armed) {
        this.armed = armed;
        this.getDataManager().set(ARMED, armed);
    }

    public boolean isDataArmed() {
        return this.getDataManager().get(ARMED);
    }

    public boolean isDataExploding() {
        return this.getDataManager().get(EXPLODING);
    }

    public int getTicksExploding() {
        return ticksExploding;
    }

    public void setTicksExploding(int ticksExploding) {
        this.ticksExploding = ticksExploding;
        this.getDataManager().set(TICKS, ticksExploding);
    }

    public int getDataTicksExploding() {
        return this.getDataManager().get(TICKS);
    }
}

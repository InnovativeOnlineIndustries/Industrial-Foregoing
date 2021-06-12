package com.buuz135.industrial.entity;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.item.MobImprisonmentToolItem;
import com.buuz135.industrial.item.infinity.item.ItemInfinityLauncher;
import com.buuz135.industrial.module.ModuleTool;
import com.buuz135.industrial.proxy.network.PlungerPlayerHitMessage;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class InfinityLauncherProjectileEntity extends AbstractArrowEntity {

    private static final DataParameter<Integer> PLUNGER_ACTION = EntityDataManager.createKey(InfinityLauncherProjectileEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TIER = EntityDataManager.createKey(InfinityLauncherProjectileEntity.class, DataSerializers.VARINT);

    public InfinityLauncherProjectileEntity(EntityType<? extends InfinityLauncherProjectileEntity> type, World world) {
        super(type, world);
    }

    public InfinityLauncherProjectileEntity(World worldIn, LivingEntity thrower, ItemInfinityLauncher.PlungerAction plungerAction, int tier) {
        super(ModuleTool.INFINITY_LAUNCHER_PROJECTILE_ENTITY_TYPE, thrower, worldIn);
        this.dataManager.set(PLUNGER_ACTION, plungerAction.getId());
        this.dataManager.set(TIER, tier);
    }

    @OnlyIn(Dist.CLIENT)
    public InfinityLauncherProjectileEntity(World worldIn, double x, double y, double z) {
        super(ModuleTool.INFINITY_LAUNCHER_PROJECTILE_ENTITY_TYPE, x, y, z, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void func_230299_a_(BlockRayTraceResult result) {
        super.func_230299_a_(result);
        Entity player = this.func_234616_v_();
        ItemInfinityLauncher.PlungerAction action = ItemInfinityLauncher.PlungerAction.getFromId(this.dataManager.get(PLUNGER_ACTION));
        if (player instanceof PlayerEntity && action == ItemInfinityLauncher.PlungerAction.RELEASE) {
            for (ItemStack itemStack : ((PlayerEntity) player).inventory.mainInventory) {
                if (itemStack.getItem() instanceof MobImprisonmentToolItem && itemStack.hasTag()) {
                    ItemStack copy = itemStack.copy();
                    if (((MobImprisonmentToolItem) itemStack.getItem()).release((PlayerEntity) player, result.getPos(), result.getFace(), this.world, copy)) {
                        ((PlayerEntity) player).inventory.deleteStack(itemStack);
                        ((PlayerEntity) player).inventory.addItemStackToInventory(copy);
                        break;
                    }

                }
            }
            this.remove();
        }
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(PLUNGER_ACTION, 0);
        this.dataManager.register(TIER, 0);
    }

    @Override
    protected ItemStack getArrowStack() {
        return new ItemStack(Blocks.STONE);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isEnchanted() {
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
    protected void onEntityHit(EntityRayTraceResult p_213868_1_) {
        Entity entity = p_213868_1_.getEntity();
        float f = (float) this.getMotion().length();
        int i = MathHelper.ceil(MathHelper.clamp((double) f * 1 + this.dataManager.get(TIER), 0.0D, 2.147483647E9D));
        ItemInfinityLauncher.PlungerAction action = ItemInfinityLauncher.PlungerAction.getFromId(this.dataManager.get(PLUNGER_ACTION));

        if (action == ItemInfinityLauncher.PlungerAction.DAMAGE) {
            if (this.getIsCritical()) {
                long j = (long) this.rand.nextInt(i / 2 + 2);
                i = (int) Math.min(j + (long) i, 2147483647L);
            }
            Entity entity1 = this.func_234616_v_();
            DamageSource damagesource;
            if (entity1 == null) {
                damagesource = DamageSource.causeArrowDamage(this, this);
            } else {
                damagesource = DamageSource.causeArrowDamage(this, entity1);
                if (entity1 instanceof LivingEntity) {
                    ((LivingEntity) entity1).setLastAttackedEntity(entity);
                }
            }
            boolean flag = entity.getType() == EntityType.ENDERMAN;
            int k = entity.getFireTimer();
            if (this.isBurning() && !flag) {
                entity.setFire(5);
            }
            if (entity.attackEntityFrom(damagesource, (float) i)) {
                if (flag) {
                    return;
                }
                if (entity instanceof LivingEntity) {
                    LivingEntity livingentity = (LivingEntity) entity;
                    if (!this.world.isRemote && livingentity instanceof PlayerEntity) {
                        IndustrialForegoing.NETWORK.sendToNearby(this.world, this.getPosition(), 256, new PlungerPlayerHitMessage(livingentity.getUniqueID()));
                    }
                    if (1 > 0) {
                        Vector3d vector3d = this.getMotion().mul(1.0D, 0.0D, 1.0D).normalize().scale((double) 1 * 0.6D);
                        if (vector3d.lengthSquared() > 0.0D) {
                            livingentity.addVelocity(vector3d.x, 0.1D, vector3d.z);
                        }
                    }
                    if (!this.world.isRemote && entity1 instanceof LivingEntity) {
                        EnchantmentHelper.applyThornEnchantments(livingentity, entity1);
                        EnchantmentHelper.applyArthropodEnchantments((LivingEntity) entity1, livingentity);
                    }
                    this.arrowHit(livingentity);
                    if (entity1 != null && livingentity != entity1 && livingentity instanceof PlayerEntity && entity1 instanceof ServerPlayerEntity && !this.isSilent()) {
                        ((ServerPlayerEntity) entity1).connection.sendPacket(new SChangeGameStatePacket(SChangeGameStatePacket.field_241770_g_, 0.0F));
                    }
                }
                this.playSound(SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                if (this.getPierceLevel() <= 0) {
                    this.remove();
                }
            } else {
                entity.forceFireTicks(k);
                this.setMotion(this.getMotion().scale(-0.1D));
                this.rotationYaw += 180.0F;
                this.prevRotationYaw += 180.0F;
                if (!this.world.isRemote && this.getMotion().lengthSquared() < 1.0E-7D) {
                    if (this.pickupStatus == AbstractArrowEntity.PickupStatus.ALLOWED) {
                        this.entityDropItem(this.getArrowStack(), 0.1F);
                    }

                    this.remove();
                }
            }
        }
        Entity player = this.func_234616_v_();
        if (player instanceof PlayerEntity && entity instanceof LivingEntity && action == ItemInfinityLauncher.PlungerAction.CAPTURE) {
            for (ItemStack itemStack : ((PlayerEntity) player).inventory.mainInventory) {
                if (itemStack.getItem() instanceof MobImprisonmentToolItem && !itemStack.hasTag()) {
                    ItemStack copy = itemStack.copy();
                    if (((MobImprisonmentToolItem) itemStack.getItem()).capture(copy, (LivingEntity) entity)) {
                        ((PlayerEntity) player).inventory.deleteStack(itemStack);
                        ((PlayerEntity) player).inventory.addItemStackToInventory(copy);
                        break;
                    }

                }
            }
            this.remove();
        }
    }


}

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

package com.buuz135.industrial.item.infinity.item;

import com.buuz135.industrial.item.infinity.InfinityTier;
import com.buuz135.industrial.item.infinity.ItemInfinity;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import com.buuz135.industrial.utils.IFAttachments;
import com.buuz135.industrial.utils.IndustrialTags;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.ArrowButtonScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.TextScreenAddon;
import com.hrznstudio.titanium.component.button.ArrowButtonComponent;
import com.hrznstudio.titanium.item.BasicItem;
import com.hrznstudio.titanium.tab.TitaniumTab;
import com.hrznstudio.titanium.util.FacingUtil;
import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class ItemInfinityHammer extends ItemInfinity {

    public static HashMap<Class<?>, Function<Entity, ItemStack>> HEADS = new HashMap<>();
    public static int POWER_CONSUMPTION = 10000;
    public static int FUEL_CONSUMPTION = 3;
    public static int DAMAGE = 10;
    public static float ATTACK_SPEED = -2.0f;
    public static String BEHEADING_NBT = "Beheading";

    static {
        HEADS.put(WitherSkeleton.class, (entity) -> new ItemStack(Blocks.WITHER_SKELETON_SKULL));
        HEADS.put(Skeleton.class, (entity) -> new ItemStack(Blocks.SKELETON_SKULL));
        HEADS.put(Zombie.class, (entity) -> new ItemStack(Blocks.ZOMBIE_HEAD));
        HEADS.put(Creeper.class, (entity) -> new ItemStack(Blocks.CREEPER_HEAD));
        HEADS.put(EnderDragon.class, (entity) -> new ItemStack(Blocks.DRAGON_HEAD));
    }

    public ItemInfinityHammer(TitaniumTab group) {
        //.addToolType(ToolType.get("sword"), 1)
        super("infinity_hammer", group, new Properties().stacksTo(1), POWER_CONSUMPTION, FUEL_CONSUMPTION, true);
    }

    public static ItemStack createHead(GameProfile gameProfile) {
        ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
        stack.set(DataComponents.PROFILE, new ResolvableProfile(gameProfile));
        return stack;
    }

    @Override
    public void addNbt(ItemStack stack, long power, int fuel, boolean special) {
        super.addNbt(stack, power, fuel, special);
        stack.set(IFAttachments.INFINITY_HAMMER_BEHEADING, 0);
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return Items.DIAMOND_SWORD.supportsEnchantment(new ItemStack(Items.DIAMOND_SWORD), enchantment);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level worldIn, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        return false;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.is(Blocks.COBWEB)) {
            return 15.0F;
        } else {
            return 1.25f;
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        InfinityTier infinityTier = getSelectedTier(stack);
        if (infinityTier.getRadius() > 1 && attacker instanceof Player) {
            AABB area = new AABB(target.getX(), target.getY(), target.getZ(), target.getX(), target.getY(), target.getZ()).inflate(infinityTier.getRadius());
            List<Mob> mobs = attacker.getCommandSenderWorld().getEntitiesOfClass(Mob.class, new AABB(target.getX(), target.getY(), target.getZ(), target.getX(), target.getY(), target.getZ()).inflate(infinityTier.getRadius()));
            mobs.forEach(mobEntity -> {
                if (enoughFuel(stack)) {
                    mobEntity.hurt(target.damageSources().playerAttack((Player) attacker), (float) (DAMAGE + Math.pow(2, infinityTier.getRadius())) * 0.8f);
                    consumeFuel(stack);
                    if (mobEntity.getHealth() <= 0 && attacker.getCommandSenderWorld().random.nextDouble() <= getCurrentBeheading(stack) * 0.15) {
                        ItemStack head = HEADS.getOrDefault(mobEntity.getClass(), (entity) -> ItemStack.EMPTY).apply(mobEntity);
                        Block.popResource(attacker.level(), attacker.blockPosition(), head);
                    }
                }
            });
            attacker.getCommandSenderWorld().getEntitiesOfClass(ItemEntity.class, area.inflate(1)).forEach(itemEntity -> {
                itemEntity.setNoPickUpDelay();
                itemEntity.teleportTo(attacker.blockPosition().getX(), attacker.blockPosition().getY() + 1, attacker.blockPosition().getZ());
            });
            attacker.getCommandSenderWorld().getEntitiesOfClass(ExperienceOrb.class, area.inflate(1)).forEach(entityXPOrb -> entityXPOrb.teleportTo(attacker.blockPosition().getX(), attacker.blockPosition().getY(), attacker.blockPosition().getZ()));
        }
        if (target.getHealth() <= 0 && target instanceof Player) {
            Block.popResource(attacker.level(), attacker.blockPosition(), createHead(((Player) target).getGameProfile()));
        }
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        InfinityTier infinityTier = getSelectedTier(stack);
        Player player = context.getPlayer();
        if (infinityTier.getRadius() > 1) {
            Vec3 looking = player.getLookAngle();
            Vec3[] all = new Vec3[]{looking, looking.yRot(0.22f), looking.yRot(-0.22f)};
            for (Vec3 vector3d : all) {
                float f = (float) Mth.atan2(vector3d.z, vector3d.x);
                for (int i = 0; i < infinityTier.getRadius() * 1.5 + 1; i++) {
                    double d2 = 1.25D * (double) (i + 1);
                    int j = 1;
                    this.spawnFangs(player, player.getX() + (double) Mth.cos(f) * d2, player.getZ() + (double) Mth.sin(f) * d2, player.getY() - 1, player.getY() + 1, f, j);
                    consumeFuel(stack);
                }
            }
            player.getCooldowns().addCooldown(this, 12);
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }

    private void spawnFangs(LivingEntity caster, double x, double z, double minY, double maxY, float rotation, int delay) {
        BlockPos blockpos = new BlockPos((int) x, (int) maxY, (int) z);
        boolean flag = false;
        double d0 = 0.0D;
        do {
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = caster.level().getBlockState(blockpos1);
            if (blockstate.isFaceSturdy(caster.level(), blockpos1, Direction.UP)) {
                if (!caster.level().isEmptyBlock(blockpos)) {
                    BlockState blockstate1 = caster.level().getBlockState(blockpos);
                    VoxelShape voxelshape = blockstate1.getCollisionShape(caster.level(), blockpos);
                    if (!voxelshape.isEmpty()) {
                        d0 = voxelshape.max(Direction.Axis.Y);
                    }
                }
                flag = true;
                break;
            }
            blockpos = blockpos.below();
        } while (blockpos.getY() >= Mth.floor(minY) - 1);
        if (flag) {
            caster.level().addFreshEntity(new EvokerFangs(caster.level(), x, (double) blockpos.getY() + d0, z, rotation, delay, caster));
        }
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (state.getDestroySpeed(worldIn, pos) != 0.0F) {
            stack.hurtAndBreak(1, entityLiving, EquipmentSlot.MAINHAND);
        }
        return true;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState blockIn) {
        return blockIn.is(Blocks.COBWEB);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        var attributes = super.getDefaultAttributeModifiers(stack);
        InfinityTier infinityTier = InfinityTier.getTierBraquet(getPowerFromStack(stack)).getLeft();
        attributes = attributes.withModifierAdded(Attributes.ATTACK_DAMAGE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "base_attack_damage"), DAMAGE + Math.pow(2, infinityTier.getRadius()), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        attributes = attributes.withModifierAdded(Attributes.ATTACK_SPEED, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "base_attack_speed"), ATTACK_SPEED, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        return attributes;
    }


    public int getCurrentBeheading(ItemStack stack) {
        return stack.getOrDefault(IFAttachments.INFINITY_HAMMER_BEHEADING, 0);
    }

    public int getMaxBeheading(ItemStack stack) {
        InfinityTier infinityTier = InfinityTier.getTierBraquet(getPowerFromStack(stack)).getLeft();
        return Math.min(Math.max(0, infinityTier.getRadius() - 2), 3);
    }

    public void setBeheading(ItemStack stack, int level) {
        stack.set(IFAttachments.INFINITY_HAMMER_BEHEADING, level);
    }

    @Override
    public void addTooltipDetails(BasicItem.Key key, ItemStack stack, List<Component> tooltip, boolean advanced) {
        super.addTooltipDetails(key, stack, tooltip, advanced);
        String level = "0";
        switch (getCurrentBeheading(stack)) {
            case 1:
                level = "I";
                break;
            case 2:
                level = "II";
                break;
            case 3:
                level = "III";
                break;
        }
        tooltip.add(Component.translatable("text.industrialforegoing.display.beheading").append(" " + level).withStyle(ChatFormatting.GRAY));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public List<IFactory<? extends IScreenAddon>> getScreenAddons(Supplier<ItemStack> stack) {
        List<IFactory<? extends IScreenAddon>> factory = super.getScreenAddons(stack);
        factory.add(() -> new ArrowButtonScreenAddon((ArrowButtonComponent) new ArrowButtonComponent(154, 20 + 16 * 2, 14, 14, FacingUtil.Sideness.RIGHT).setId(4)));
        factory.add(() -> new ArrowButtonScreenAddon((ArrowButtonComponent) new ArrowButtonComponent(54, 20 + 16 * 2, 14, 14, FacingUtil.Sideness.LEFT).setId(5)));
        factory.add(() -> new TextScreenAddon("", 54 + 14 + 4, 24 + 16 * 2, false) {
            @Override
            public String getText() {
                return ChatFormatting.DARK_GRAY + Component.translatable("text.industrialforegoing.display.beheading").append(": ").append(getCurrentBeheading(stack.get()) + "/" + getMaxBeheading(stack.get())).getString();
            }
        });
        return factory;
    }

    @Override
    public void handleButtonMessage(int id, Player playerEntity, CompoundTag compound) {
        super.handleButtonMessage(id, playerEntity, compound);
        ItemStack stack = playerEntity.getItemInHand(InteractionHand.MAIN_HAND);
        int currentBeheading = getCurrentBeheading(stack);
        int maxBeheading = getMaxBeheading(stack);
        if (id == 5 && currentBeheading > 0) {
            setBeheading(stack, Math.max(currentBeheading - 1, 0));
        }
        if (id == 4 && currentBeheading < maxBeheading) {
            setBeheading(stack, Math.min(3, currentBeheading + 1));
        }
    }

    @Override
    public void registerRecipe(RecipeOutput consumer) {
        DissolutionChamberRecipe.createRecipe(consumer, "infinity_hammer", new DissolutionChamberRecipe(List.of(
                Ingredient.of(new ItemStack(Items.DIAMOND_BLOCK)),
                Ingredient.of(new ItemStack(Items.DIAMOND_SWORD)),
                Ingredient.of(new ItemStack(Items.DIAMOND_BLOCK)),
                Ingredient.of(new ItemStack(Items.DIAMOND_AXE)),
                Ingredient.of(new ItemStack(ModuleCore.RANGE_ADDONS[11].get())),
                Ingredient.of(IndustrialTags.Items.GEAR_GOLD),
                Ingredient.of(IndustrialTags.Items.GEAR_GOLD),
                Ingredient.of(IndustrialTags.Items.GEAR_GOLD)
        ), new FluidStack(ModuleCore.PINK_SLIME.getSourceFluid().get(), 2000), 400, Optional.of(new ItemStack(this)), Optional.empty()));

    }
}

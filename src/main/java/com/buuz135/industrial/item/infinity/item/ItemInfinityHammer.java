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
import com.buuz135.industrial.utils.IndustrialTags;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.ArrowButtonScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.TextScreenAddon;
import com.hrznstudio.titanium.component.button.ArrowButtonComponent;
import com.hrznstudio.titanium.item.BasicItem;
import com.hrznstudio.titanium.util.FacingUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.EvokerFangsEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
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
        HEADS.put(WitherSkeletonEntity.class, (entity) -> new ItemStack(Blocks.WITHER_SKELETON_SKULL));
        HEADS.put(SkeletonEntity.class, (entity) -> new ItemStack(Blocks.SKELETON_SKULL));
        HEADS.put(ZombieEntity.class, (entity) -> new ItemStack(Blocks.ZOMBIE_HEAD));
        HEADS.put(CreeperEntity.class, (entity) -> new ItemStack(Blocks.CREEPER_HEAD));
        HEADS.put(EnderDragonEntity.class, (entity) -> new ItemStack(Blocks.DRAGON_HEAD));
    }

    public ItemInfinityHammer(ItemGroup group) {
        super("infinity_hammer", group, new Properties().addToolType(ToolType.get("sword"), 1).maxStackSize(1), POWER_CONSUMPTION, FUEL_CONSUMPTION, true);
    }

    public static ItemStack createHead(String name) {
        ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
        stack.getOrCreateTag().putString("SkullOwner", name);
        return stack;
    }

    @Override
    public void addNbt(ItemStack stack, long power, int fuel, boolean special) {
        super.addNbt(stack, power, fuel, special);
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putInt(BEHEADING_NBT, 0);
        stack.setTag(nbt);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return Items.DIAMOND_SWORD.canApplyAtEnchantingTable(stack, enchantment) ;
    }

    @Override
    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return !player.isCreative();
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        return false;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.isIn(Blocks.COBWEB)) {
            return 15.0F;
        } else {
            Material material = state.getMaterial();
            return material != Material.PLANTS && material != Material.TALL_PLANTS && material != Material.CORAL && !state.isIn(BlockTags.LEAVES) && material != Material.GOURD ? 1.0F : 1.5F;
        }
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        InfinityTier infinityTier = getSelectedTier(stack);
        if (infinityTier.getRadius() > 1 && attacker instanceof PlayerEntity) {
            AxisAlignedBB area = new AxisAlignedBB(target.getPosX(), target.getPosY(), target.getPosZ(), target.getPosX(), target.getPosY(), target.getPosZ()).grow(infinityTier.getRadius());
            List<MobEntity> mobs = attacker.getEntityWorld().getEntitiesWithinAABB(MobEntity.class, new AxisAlignedBB(target.getPosX(), target.getPosY(), target.getPosZ(), target.getPosX(), target.getPosY(), target.getPosZ()).grow(infinityTier.getRadius()));
            mobs.forEach(mobEntity -> {
                if (enoughFuel(stack)) {
                    mobEntity.attackEntityFrom(DamageSource.causePlayerDamage((PlayerEntity) attacker), (float) (DAMAGE + Math.pow(2, infinityTier.getRadius())) * 0.8f);
                    consumeFuel(stack);
                    if (mobEntity.getHealth() <= 0 && attacker.getEntityWorld().rand.nextDouble() <= getCurrentBeheading(stack) * 0.15) {
                        ItemStack head = HEADS.getOrDefault(mobEntity.getClass(), (entity) -> ItemStack.EMPTY).apply(mobEntity);
                        Block.spawnAsEntity(attacker.world, attacker.getPosition(), head);
                    }
                }
            });
            attacker.getEntityWorld().getEntitiesWithinAABB(ItemEntity.class, area.grow(1)).forEach(itemEntity -> {
                itemEntity.setNoPickupDelay();
                itemEntity.setPositionAndUpdate(attacker.getPosition().getX(), attacker.getPosition().getY() + 1, attacker.getPosition().getZ());
            });
            attacker.getEntityWorld().getEntitiesWithinAABB(ExperienceOrbEntity.class, area.grow(1)).forEach(entityXPOrb -> entityXPOrb.setPositionAndUpdate(attacker.getPosition().getX(), attacker.getPosition().getY(), attacker.getPosition().getZ()));
        }
        if (target.getHealth() <= 0 && target instanceof PlayerEntity) {
            Block.spawnAsEntity(attacker.world, attacker.getPosition(), createHead(target.getDisplayName().getString()));
        }
        return true;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        ItemStack stack = context.getItem();
        InfinityTier infinityTier = getSelectedTier(stack);
        PlayerEntity player = context.getPlayer();
        if (infinityTier.getRadius() > 1) {
            Vector3d looking = player.getLookVec();
            Vector3d[] all = new Vector3d[]{looking, looking.rotateYaw(0.22f), looking.rotateYaw(-0.22f)};
            for (Vector3d vector3d : all) {
                float f = (float) MathHelper.atan2(vector3d.z, vector3d.x);
                for (int i = 0; i < infinityTier.getRadius() * 1.5 + 1; i++) {
                    double d2 = 1.25D * (double) (i + 1);
                    int j = 1;
                    this.spawnFangs(player, player.getPosX() + (double) MathHelper.cos(f) * d2, player.getPosZ() + (double) MathHelper.sin(f) * d2, player.getPosY() - 1, player.getPosY() + 1, f, j);
                    consumeFuel(stack);
                }
            }
            player.getCooldownTracker().setCooldown(this, 12);
            return ActionResultType.SUCCESS;
        }
        return super.onItemUse(context);
    }

    private void spawnFangs(LivingEntity caster, double x, double z, double minY, double maxY, float rotation, int delay) {
        BlockPos blockpos = new BlockPos(x, maxY, z);
        boolean flag = false;
        double d0 = 0.0D;
        do {
            BlockPos blockpos1 = blockpos.down();
            BlockState blockstate = caster.world.getBlockState(blockpos1);
            if (blockstate.isSolidSide(caster.world, blockpos1, Direction.UP)) {
                if (!caster.world.isAirBlock(blockpos)) {
                    BlockState blockstate1 = caster.world.getBlockState(blockpos);
                    VoxelShape voxelshape = blockstate1.getCollisionShape(caster.world, blockpos);
                    if (!voxelshape.isEmpty()) {
                        d0 = voxelshape.getEnd(Direction.Axis.Y);
                    }
                }
                flag = true;
                break;
            }
            blockpos = blockpos.down();
        } while (blockpos.getY() >= MathHelper.floor(minY) - 1);
        if (flag) {
            caster.world.addEntity(new EvokerFangsEntity(caster.world, x, (double) blockpos.getY() + d0, z, rotation, delay, caster));
        }
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (state.getBlockHardness(worldIn, pos) != 0.0F) {
            entityLiving.sendBreakAnimation(EquipmentSlotType.MAINHAND);
        }
        return true;
    }

    @Override
    public boolean canHarvestBlock(BlockState blockIn) {
        return blockIn.isIn(Blocks.COBWEB);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = MultimapBuilder.hashKeys().arrayListValues().build();
        if (slot == EquipmentSlotType.MAINHAND) {
            InfinityTier infinityTier = InfinityTier.getTierBraquet(getPowerFromStack(stack)).getLeft();
            multimap.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", DAMAGE + Math.pow(2, infinityTier.getRadius()), AttributeModifier.Operation.ADDITION)); //AttackDamage
            multimap.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", ATTACK_SPEED, AttributeModifier.Operation.ADDITION)); //AttackSpeed
        }
        return multimap;
    }

    public int getCurrentBeheading(ItemStack stack) {
        return stack.getOrCreateTag().getInt(BEHEADING_NBT);
    }

    public int getMaxBeheading(ItemStack stack) {
        InfinityTier infinityTier = InfinityTier.getTierBraquet(getPowerFromStack(stack)).getLeft();
        return Math.min(Math.max(0, infinityTier.getRadius() - 2), 3);
    }

    public void setBeheading(ItemStack stack, int level) {
        stack.getOrCreateTag().putInt(BEHEADING_NBT, level);
    }

    @Override
    public void addTooltipDetails(BasicItem.Key key, ItemStack stack, List<ITextComponent> tooltip, boolean advanced) {
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
        tooltip.add(new TranslationTextComponent("text.industrialforegoing.display.beheading").appendString(" " + level).mergeStyle(TextFormatting.GRAY));
    }

    @Override
    public List<IFactory<? extends IScreenAddon>> getScreenAddons(Supplier<ItemStack> stack) {
        List<IFactory<? extends IScreenAddon>> factory = super.getScreenAddons(stack);
        factory.add(() -> new ArrowButtonScreenAddon((ArrowButtonComponent) new ArrowButtonComponent(154, 20 + 16 * 2, 14, 14, FacingUtil.Sideness.RIGHT).setId(4)));
        factory.add(() -> new ArrowButtonScreenAddon((ArrowButtonComponent) new ArrowButtonComponent(54, 20 + 16 * 2, 14, 14, FacingUtil.Sideness.LEFT).setId(5)));
        factory.add(() -> new TextScreenAddon("", 54 + 14 + 4, 24 + 16 * 2, false) {
            @Override
            public String getText() {
                return TextFormatting.DARK_GRAY + new TranslationTextComponent("text.industrialforegoing.display.beheading").appendString(": ").appendString(getCurrentBeheading(stack.get()) + "/" + getMaxBeheading(stack.get())).getString();
            }
        });
        return factory;
    }

    @Override
    public void handleButtonMessage(int id, PlayerEntity playerEntity, CompoundNBT compound) {
        super.handleButtonMessage(id, playerEntity, compound);
        ItemStack stack = playerEntity.getHeldItem(Hand.MAIN_HAND);
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
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        new DissolutionChamberRecipe(this.getRegistryName(),
                new Ingredient.IItemList[]{
                        new Ingredient.SingleItemList(new ItemStack(Items.DIAMOND_BLOCK)),
                        new Ingredient.SingleItemList(new ItemStack(Items.DIAMOND_SWORD)),
                        new Ingredient.SingleItemList(new ItemStack(Items.DIAMOND_BLOCK)),
                        new Ingredient.SingleItemList(new ItemStack(Items.DIAMOND_AXE)),
                        new Ingredient.SingleItemList(new ItemStack(ModuleCore.RANGE_ADDONS[11])),
                        new Ingredient.TagList(IndustrialTags.Items.GEAR_GOLD),
                        new Ingredient.TagList(IndustrialTags.Items.GEAR_GOLD),
                        new Ingredient.TagList(IndustrialTags.Items.GEAR_GOLD),
                },
                new FluidStack(ModuleCore.PINK_SLIME.getSourceFluid(), 2000), 400, new ItemStack(this), FluidStack.EMPTY);
    }
}

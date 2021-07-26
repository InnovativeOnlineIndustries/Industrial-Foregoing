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

import com.buuz135.industrial.entity.InfinityTridentEntity;
import com.buuz135.industrial.item.infinity.InfinityTier;
import com.buuz135.industrial.item.infinity.ItemInfinity;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.ArrowButtonScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.StateButtonAddon;
import com.hrznstudio.titanium.client.screen.addon.StateButtonInfo;
import com.hrznstudio.titanium.client.screen.addon.TextScreenAddon;
import com.hrznstudio.titanium.component.button.ArrowButtonComponent;
import com.hrznstudio.titanium.component.button.ButtonComponent;
import com.hrznstudio.titanium.item.BasicItem;
import com.hrznstudio.titanium.util.FacingUtil;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.world.item.Item.Properties;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;

public class ItemInfinityTrident extends ItemInfinity {

    private static String LOYALTY_NBT = "Loyalty";
    private static String RIPTIDE_NBT = "Riptide";
    private static String CHANNELING_NBT = "Channeling";

    private static int LOYALTY_MAX = 5;
    private static int RIPTIDE_MAX = 5;


    public static int POWER_CONSUMPTION = 100000;
    public static int FUEL_CONSUMPTION = 30;

    public ItemInfinityTrident(CreativeModeTab group) {
        super("infinity_trident", group, new Properties().stacksTo(1), POWER_CONSUMPTION, FUEL_CONSUMPTION, true);
    }

    @Override
    public void addNbt(ItemStack stack, long power, int fuel, boolean special) {
        super.addNbt(stack, power, fuel, special);
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putInt(LOYALTY_NBT, 0);
        nbt.putInt(RIPTIDE_NBT, 0);
        nbt.putBoolean(CHANNELING_NBT, false);
        stack.setTag(nbt);
    }


    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000 / 2;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (EnchantmentHelper.getRiptide(itemstack) > 0 && !playerIn.isInWaterOrRain()) {
            return InteractionResultHolder.fail(itemstack);
        } else if (!playerIn.isShiftKeyDown()) {
            playerIn.startUsingItem(handIn);
            return InteractionResultHolder.consume(itemstack);
        }
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (enchantment.equals(Enchantments.LOYALTY) || enchantment.equals(Enchantments.RIPTIDE) || enchantment.equals(Enchantments.CHANNELING)) return false;
        return Items.TRIDENT.canApplyAtEnchantingTable(new ItemStack(Items.TRIDENT), enchantment) ;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player) {
            Player playerentity = (Player) entityLiving;
            int i = this.getUseDuration(stack) - timeLeft;
            if (i >= 10) {
                int riptideModifier = getCurrentRiptide(stack);
                if (riptideModifier <= 0 && enoughFuel(stack)) {
                    if (!worldIn.isClientSide) {
                        stack.hurtAndBreak(1, playerentity, (player) -> {
                            player.broadcastBreakEvent(entityLiving.getUsedItemHand());
                        });
                        if (riptideModifier == 0) {
                            consumeFuel(stack);
                            InfinityTridentEntity tridententity = new InfinityTridentEntity(worldIn, playerentity, stack);
                            tridententity.shootFromRotation(playerentity, playerentity.xRot, playerentity.yRot, 0.0F, 2.5F + (float) riptideModifier * 0.5F, 1.0F);
                            if (playerentity.abilities.instabuild) {
                                tridententity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                            }

                            worldIn.addFreshEntity(tridententity);
                            worldIn.playSound((Player) null, tridententity, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
                            if (!playerentity.abilities.instabuild) {
                                playerentity.inventory.removeItem(stack);
                            }
                        }
                    }
                }
                playerentity.awardStat(Stats.ITEM_USED.get(this));
                if (riptideModifier > 0 && enoughFuel(stack)) {
                    float f7 = playerentity.yRot;
                    float f = playerentity.xRot;
                    float f1 = -Mth.sin(f7 * ((float) Math.PI / 180F)) * Mth.cos(f * ((float) Math.PI / 180F));
                    float f2 = -Mth.sin(f * ((float) Math.PI / 180F));
                    float f3 = Mth.cos(f7 * ((float) Math.PI / 180F)) * Mth.cos(f * ((float) Math.PI / 180F));
                    float f4 = Mth.sqrt(f1 * f1 + f2 * f2 + f3 * f3);
                    float f5 = 3.0F * ((1.0F + (float) riptideModifier * 2) / 4.0F);
                    f1 = f1 * (f5 / f4);
                    f2 = f2 * (f5 / f4);
                    f3 = f3 * (f5 / f4);
                    playerentity.push((double) f1, (double) f2, (double) f3);
                    playerentity.startAutoSpinAttack(20);
                    if (playerentity.isOnGround()) {
                        playerentity.move(MoverType.SELF, new Vec3(0.0D, (double) 1.1999999F, 0.0D));
                    }

                    SoundEvent soundevent;
                    if (riptideModifier >= 3) {
                        soundevent = SoundEvents.TRIDENT_RIPTIDE_3;
                    } else if (riptideModifier == 2) {
                        soundevent = SoundEvents.TRIDENT_RIPTIDE_2;
                    } else {
                        soundevent = SoundEvents.TRIDENT_RIPTIDE_1;
                    }
                    worldIn.playSound((Player) null, playerentity, soundevent, SoundSource.PLAYERS, 1.0F, 1.0F);
                    consumeFuel(stack);
                }
            }
        }
    }

    //Trident stuff

    public int getCurrentLoyalty(ItemStack stack) {
        return stack.getOrCreateTag().getInt(LOYALTY_NBT);
    }

    public int getMaxLoyalty(ItemStack stack) {
        InfinityTier infinityTier = InfinityTier.getTierBraquet(getPowerFromStack(stack)).getLeft();
        return Math.min(Math.max(0, infinityTier.getRadius() - 1), LOYALTY_MAX);
    }

    public void setLoyalty(ItemStack stack, int level) {
        stack.getOrCreateTag().putInt(LOYALTY_NBT, level);
    }

    public int getCurrentRiptide(ItemStack stack) {
        return stack.getOrCreateTag().getInt(RIPTIDE_NBT);
    }

    public int getMaxRiptide(ItemStack stack) {
        InfinityTier infinityTier = InfinityTier.getTierBraquet(getPowerFromStack(stack)).getLeft();
        return Math.min(Math.max(0, infinityTier.getRadius() - 1), RIPTIDE_MAX);
    }

    public void setRiptide(ItemStack stack, int level) {
        stack.getOrCreateTag().putInt(RIPTIDE_NBT, level);
    }

    public boolean getCurrentChanneling(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(CHANNELING_NBT);
    }

    public boolean canChanneling(ItemStack stack) {
        return InfinityTier.getTierBraquet(getPowerFromStack(stack)).getLeft().getRadius() >= 4;
    }

    public void setChanneling(ItemStack stack, boolean enabled) {
        stack.getOrCreateTag().putBoolean(CHANNELING_NBT, enabled);
    }

    //GUI Stuff

    @Override
    public List<IFactory<? extends IScreenAddon>> getScreenAddons(Supplier<ItemStack> stack) {
        List<IFactory<? extends IScreenAddon>> factory = super.getScreenAddons(stack);
        factory.add(() -> new ArrowButtonScreenAddon((ArrowButtonComponent) new ArrowButtonComponent(154, 20 + 16 * 2, 14, 14, FacingUtil.Sideness.RIGHT).setId(4)));
        factory.add(() -> new ArrowButtonScreenAddon((ArrowButtonComponent) new ArrowButtonComponent(54, 20 + 16 * 2, 14, 14, FacingUtil.Sideness.LEFT).setId(5)));
        factory.add(() -> new TextScreenAddon("", 54 + 14 + 4, 24 + 16 * 2, false) {
            @Override
            public String getText() {
                return ChatFormatting.DARK_GRAY + new TranslatableComponent("enchantment.minecraft.loyalty").append(": ").append(getCurrentLoyalty(stack.get()) + "/" + getMaxLoyalty(stack.get())).getString();
            }
        });
        int y = 16;
        factory.add(() -> new ArrowButtonScreenAddon((ArrowButtonComponent) new ArrowButtonComponent(154, 20 + 16 * 2 + y , 14, 14, FacingUtil.Sideness.RIGHT).setId(6)));
        factory.add(() -> new ArrowButtonScreenAddon((ArrowButtonComponent) new ArrowButtonComponent(54, 20 + 16 * 2 + y, 14, 14, FacingUtil.Sideness.LEFT).setId(7)));
        factory.add(() -> new TextScreenAddon("", 54 + 14 + 4, 24 + 16 * 2 + y, false) {
            @Override
            public String getText() {
                return ChatFormatting.DARK_GRAY + new TranslatableComponent("enchantment.minecraft.riptide").append(": ").append(getCurrentRiptide(stack.get()) + "/" + getMaxRiptide(stack.get())).getString();
            }
        });
        factory.add(() -> new StateButtonAddon(new ButtonComponent(54, 20 + 16 * 2 + y * 2, 14, 14).setId(8), new StateButtonInfo(0, AssetTypes.BUTTON_SIDENESS_ENABLED), new StateButtonInfo(1, AssetTypes.BUTTON_SIDENESS_DISABLED)) {
            @Override
            public int getState() {
                return getCurrentChanneling(stack.get()) ? 0 : 1;
            }
        });
        factory.add(() -> new TextScreenAddon(ChatFormatting.DARK_GRAY + new TranslatableComponent("enchantment.minecraft.channeling").getString(), 54 + 14 + 4, 24 + 16 * 2 + y * 2, false));
        return factory;
    }

    @Override
    public void handleButtonMessage(int id, Player playerEntity, CompoundTag compound) {
        super.handleButtonMessage(id, playerEntity, compound);
        ItemStack stack = playerEntity.getItemInHand(InteractionHand.MAIN_HAND);
        int current = getCurrentLoyalty(stack);
        int max = getMaxLoyalty(stack);
        if (id == 5 && current > 0) {
            setLoyalty(stack, Math.max(current - 1, 0));
        }
        if (id == 4 && current < max) {
            setRiptide(stack, 0);
            setLoyalty(stack, Math.min(max, current + 1));
        }
        current = getCurrentRiptide(stack);
        max = getMaxRiptide(stack);
        if (id == 7 && current > 0) {
            setRiptide(stack, Math.max(current - 1, 0));
        }
        if (id == 6 && current < max) {
            setLoyalty(stack, 0);
            setRiptide(stack, Math.min(max, current + 1));
        }
        if (id == 8 && canChanneling(stack)) {
            setChanneling(stack, !getCurrentChanneling(stack));
        }
    }

    @Override
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {
        new DissolutionChamberRecipe(this.getRegistryName(),
                new Ingredient.Value[]{
                        new Ingredient.ItemValue(new ItemStack(Items.DIAMOND_BLOCK)),
                        new Ingredient.ItemValue(new ItemStack(Items.TRIDENT)),
                        new Ingredient.ItemValue(new ItemStack(Items.DIAMOND_BLOCK)),
                        new Ingredient.ItemValue(new ItemStack(Items.DIAMOND_HOE)),
                        new Ingredient.ItemValue(new ItemStack(ModuleCore.RANGE_ADDONS[11])),
                        new Ingredient.TagValue(IndustrialTags.Items.GEAR_GOLD),
                        new Ingredient.TagValue(IndustrialTags.Items.GEAR_GOLD),
                        new Ingredient.TagValue(IndustrialTags.Items.GEAR_GOLD),
                },
                new FluidStack(ModuleCore.PINK_SLIME.getSourceFluid(), 2000), 400, new ItemStack(this), FluidStack.EMPTY);
    }

    @Override
    public void addTooltipDetails(BasicItem.Key key, ItemStack stack, List<Component> tooltip, boolean advanced) {
        super.addTooltipDetails(key, stack, tooltip, advanced);
        addTooltip(tooltip, "enchantment.minecraft.loyalty", getCurrentLoyalty(stack));
        addTooltip(tooltip, "enchantment.minecraft.riptide", getCurrentRiptide(stack));
        addTooltip(tooltip, "enchantment.minecraft.channeling", getCurrentChanneling(stack) ? 1 : 0);
    }

    private void addTooltip(List<Component> tooltip, String type, int value){
        if (value > 0){
            String level = "0";
            switch (value) {
                case 1:
                    level = "I";
                    break;
                case 2:
                    level = "II";
                    break;
                case 3:
                    level = "III";
                    break;
                case 4:
                    level = "IV";
                    break;
                case 5:
                    level = "V";
                    break;
            }
            tooltip.add(new TranslatableComponent(type).append(" " + level).withStyle(ChatFormatting.GRAY));
        }

    }

}

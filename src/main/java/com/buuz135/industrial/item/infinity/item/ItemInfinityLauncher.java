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

import com.buuz135.industrial.entity.InfinityLauncherProjectileEntity;
import com.buuz135.industrial.item.infinity.ItemInfinity;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleTool;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import com.buuz135.industrial.utils.IFAttachments;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.ArrowButtonScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.TextScreenAddon;
import com.hrznstudio.titanium.component.button.ArrowButtonComponent;
import com.hrznstudio.titanium.item.BasicItem;
import com.hrznstudio.titanium.tab.TitaniumTab;
import com.hrznstudio.titanium.util.FacingUtil;
import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

public class ItemInfinityLauncher extends ItemInfinity {

    public static int POWER_CONSUMPTION = 100000;
    public static int FUEL_CONSUMPTION = 30;

    public static String PLUNGER_NBT = "Plunger";

    public ItemInfinityLauncher(TitaniumTab group) {
        super("infinity_launcher", group, new Properties().stacksTo(1), POWER_CONSUMPTION, FUEL_CONSUMPTION, false);
        this.disableArea();
    }

    public static float getArrowVelocity(int charge) {
        float f = (float) charge / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    @Override
    public void addNbt(ItemStack stack, long power, int fuel, boolean special) {
        super.addNbt(stack, power, fuel, special);
        stack.set(IFAttachments.PLUNGER_ACTION, PlungerAction.RELEASE);
    }

    @Override
    public void verifyComponentsAfterLoad(ItemStack stack) {
        super.verifyComponentsAfterLoad(stack);
        if (!stack.has(IFAttachments.PLUNGER_ACTION)) stack.set(IFAttachments.PLUNGER_ACTION, PlungerAction.RELEASE);
    }

    @Override
    public void addTooltipDetails(BasicItem.Key key, ItemStack stack, List<Component> tooltip, boolean advanced) {
        super.addTooltipDetails(key, stack, tooltip, advanced);
        PlungerAction action = getPlungerAction(stack);
        tooltip.add(Component.translatable("text.industrialforegoing.action").withStyle(ChatFormatting.GRAY).append(Component.translatable("text.industrialforegoing.launcher." + action.name().toLowerCase(Locale.ROOT)).withStyle(action.getColor())));
    }

    public PlungerAction getPlungerAction(ItemStack stack) {
        return stack.get(IFAttachments.PLUNGER_ACTION);
    }

    public void setPlungerAction(ItemStack stack, PlungerAction plungerAction) {
        stack.set(IFAttachments.PLUNGER_ACTION, plungerAction);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }


    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 72000 / 2;
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return enchantment.is(Enchantments.UNBREAKING);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (!playerIn.isShiftKeyDown()) {
            playerIn.startUsingItem(handIn);
            return InteractionResultHolder.consume(itemstack);
        }
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player) {
            int time = this.getUseDuration(stack, entityLiving) - timeLeft;
            float velo = getArrowVelocity(time);
            if (!((double) velo < 0.1D) && enoughFuel(stack)) {
                Player playerentity = (Player) entityLiving;
                playerentity.getCooldowns().addCooldown(this, 20);
                if (!worldIn.isClientSide) {
                    InfinityLauncherProjectileEntity abstractarrowentity = new InfinityLauncherProjectileEntity(worldIn, playerentity, getPlungerAction(stack), getSelectedTier(stack).getRadius(), stack);
                    abstractarrowentity.shootFromRotation(playerentity, playerentity.xRotO, playerentity.yRotO, 0.0F, velo * 3.0F, 1.0F);
                    if (velo == 1.0F) {
                        abstractarrowentity.setCritArrow(true);
                    }
                    if (EnchantmentHelper.getItemEnchantmentLevel(worldIn.registryAccess().holderOrThrow(Enchantments.FLAME), stack) > 0) {
                        abstractarrowentity.setSharedFlagOnFire(true);
                    }
                    consumeFuel(stack);
                    abstractarrowentity.pickup = AbstractArrow.Pickup.DISALLOWED;
                    worldIn.addFreshEntity(abstractarrowentity);
                }
                worldIn.playSound((Player) null, playerentity.getX(), playerentity.getY(), playerentity.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (worldIn.random.nextFloat() * 0.4F + 1.2F) + velo * 0.5F);
                playerentity.awardStat(Stats.ITEM_USED.get(this));
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<IFactory<? extends IScreenAddon>> getScreenAddons(Supplier<ItemStack> stack) {
        List<IFactory<? extends IScreenAddon>> factory = super.getScreenAddons(stack);
        factory.add(() -> new ArrowButtonScreenAddon((ArrowButtonComponent) new ArrowButtonComponent(154, 20 + 16 * 2, 14, 14, FacingUtil.Sideness.RIGHT).setId(4)));
        factory.add(() -> new ArrowButtonScreenAddon((ArrowButtonComponent) new ArrowButtonComponent(54, 20 + 16 * 2, 14, 14, FacingUtil.Sideness.LEFT).setId(5)));
        factory.add(() -> new TextScreenAddon("", 54 + 14 + 4, 24 + 16 * 2, false) {
            @Override
            public String getText() {
                PlungerAction action = getPlungerAction(stack.get());
                return ChatFormatting.DARK_GRAY + Component.translatable("text.industrialforegoing.action").withStyle(ChatFormatting.GRAY).getString() + action.getColor() + Component.translatable("text.industrialforegoing.launcher." + action.name().toLowerCase(Locale.ROOT)).withStyle(action.getColor()).getString();
            }
        });
        return factory;
    }

    @Override
    public void handleButtonMessage(int id, Player playerEntity, CompoundTag compound) {
        super.handleButtonMessage(id, playerEntity, compound);
        ItemStack stack = playerEntity.getItemInHand(InteractionHand.MAIN_HAND);
        PlungerAction plungerAction = getPlungerAction(stack);
        if (id == 4) {
            setPlungerAction(stack, PlungerAction.getFromId(plungerAction.getId() + 1));
        }
        if (id == 5) {
            setPlungerAction(stack, PlungerAction.getFromId(plungerAction.getId() - 1));
        }

    }

    @Override
    public void registerRecipe(RecipeOutput consumer) {
        DissolutionChamberRecipe.createRecipe(consumer, "infinity_launcher", new DissolutionChamberRecipe(
                List.of(
                        Ingredient.of(new ItemStack(Items.DIAMOND_BLOCK)),
                        Ingredient.of(new ItemStack(Items.BOW)),
                        Ingredient.of(new ItemStack(Items.DIAMOND_BLOCK)),
                        Ingredient.of(new ItemStack(ModuleTool.MOB_IMPRISONMENT_TOOL.get())),
                        Ingredient.of(new ItemStack(ModuleCore.RANGE_ADDONS[11].get())),
                        Ingredient.of(IndustrialTags.Items.GEAR_GOLD),
                        Ingredient.of(IndustrialTags.Items.GEAR_GOLD),
                        Ingredient.of(IndustrialTags.Items.GEAR_GOLD)
                ), new FluidStack(ModuleCore.PINK_SLIME.getSourceFluid().get(), 2000), 400, Optional.of(new ItemStack(this)), Optional.empty()));
    }

    public enum PlungerAction implements StringRepresentable {
        RELEASE(0, ChatFormatting.GREEN), CAPTURE(1, ChatFormatting.GOLD), DAMAGE(2, ChatFormatting.RED);

        public static final Codec<PlungerAction> CODEC = StringRepresentable.fromValues(PlungerAction::values);

        private final int id;
        private final ChatFormatting color;

        PlungerAction(int id, ChatFormatting color) {
            this.id = id;
            this.color = color;
        }

        public static PlungerAction getFromId(int id) {
            if (id < 0) return DAMAGE;
            for (PlungerAction value : PlungerAction.values()) {
                if (value.id == id) {
                    return value;
                }
            }
            return RELEASE;
        }

        public int getId() {
            return id;
        }

        public ChatFormatting getColor() {
            return color;
        }

        @Override
        public String getSerializedName() {
            return this.name();
        }
    }

}

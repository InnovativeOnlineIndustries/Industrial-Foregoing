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
package com.buuz135.industrial.item.infinity;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.item.IFCustomItem;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.proxy.CommonProxy;
import com.buuz135.industrial.proxy.network.BackpackOpenedMessage;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.capability.FluidHandlerScreenProviderItemStack;
import com.hrznstudio.titanium.client.screen.addon.*;
import com.hrznstudio.titanium.component.button.ArrowButtonComponent;
import com.hrznstudio.titanium.component.button.ButtonComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.container.BasicAddonContainer;
import com.hrznstudio.titanium.itemstack.ItemStackHarnessRegistry;
import com.hrznstudio.titanium.network.IButtonHandler;
import com.hrznstudio.titanium.network.locator.LocatorFactory;
import com.hrznstudio.titanium.network.locator.PlayerInventoryFinder;
import com.hrznstudio.titanium.network.locator.instance.HeldStackLocatorInstance;
import com.hrznstudio.titanium.util.FacingUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkHooks;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemInfinity extends IFCustomItem implements MenuProvider, IButtonHandler, IInfinityDrillScreenAddons {

    private final int powerConsumption;
    private final int biofuelConsumption;
    private final boolean usesDepth;
    private boolean usesArea;

    public ItemInfinity(String name, CreativeModeTab group, Properties builder, int powerConsumption, int biofuelConsumption, boolean usesDepth) {
        super(name, group, builder);
        this.powerConsumption = powerConsumption;
        this.biofuelConsumption = biofuelConsumption;
        this.usesDepth = usesDepth;
        this.usesArea = true;
    }

    public static long getPowerFromStack(ItemStack stack) {
        long power = 0;
        if (stack.hasTag() && stack.getTag().contains("Energy")) {
            power = stack.getTag().getLong("Energy");
        }
        return power;
    }

    public void disableArea(){
        this.usesArea = false;
    }

    public String getFormattedArea(ItemStack stack, InfinityTier tier, int radius, boolean usesDepth) {
        int diameter = radius * 2 + 1;
        return diameter + "x" + diameter + "x" + (tier == InfinityTier.ARTIFACT || usesDepth ? diameter : 1);
    }

    public static InfinityTier getSelectedTier(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains("Selected") ? InfinityTier.valueOf(stack.getTag().getString("Selected")) : InfinityTier.getTierBraquet(getPowerFromStack(stack)).getLeft();
    }

    public static boolean canCharge(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("CanCharge")) return stack.getTag().getBoolean("CanCharge");
        return true;
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level worldIn, Player playerIn) {
        super.onCraftedBy(stack, worldIn, playerIn);
        addNbt(stack, 0, 0, false);
    }

    public void addNbt(ItemStack stack, long power, int fuel, boolean special) {
        CompoundTag tagCompound = new CompoundTag();
        tagCompound.putLong("Energy", power);
        CompoundTag fluid = new CompoundTag();
        fluid.putString("FluidName", "biofuel");
        fluid.putInt("Amount", fuel);
        tagCompound.put("Fluid", fluid);
        tagCompound.putBoolean("Special", special);
        tagCompound.putString("Selected", InfinityTier.getTierBraquet(power).getLeft().name());
        tagCompound.putBoolean("CanCharge", true);
        stack.setTag(tagCompound);
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (allowdedIn(group)) {
            for (InfinityTier value : InfinityTier.values()) {
                items.add(createStack(value.getPowerNeeded(), 0, false));
            }
            items.add(createStack(InfinityTier.ARTIFACT.getPowerNeeded(), 1_000_000, true));
        }
    }

    public ItemStack createStack(long power, int fuel, boolean special) {
        ItemStack stack = new ItemStack(this);
        addNbt(stack, power, fuel, special);
        return stack;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue() {
        return 50;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack p_77661_1_) {
        return UseAnim.BOW;
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged && !oldStack.equals(newStack);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return enoughFuel(stack) ? 10F : 0;
    }

    @Override
    public boolean isBarVisible(ItemStack p_150899_) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (!DistExecutor.safeRunForDist(() ->  Screen::hasShiftDown, () -> Boolean.FALSE::booleanValue)) { //hasShiftDown
            int fuel = getFuelFromStack(stack);
            return (int) Math.round(fuel* 13D / 1_000_000D);
        } else {
            long power = getPowerFromStack(stack);
            return (int) Math.round(power * 13D / (double) InfinityTier.getTierBraquet(power).getRight().getPowerNeeded());
        }
    }

    @Override
    public int getBarColor(ItemStack p_150901_) {
        return !DistExecutor.safeRunForDist(() -> Screen::hasShiftDown, () -> Boolean.FALSE::booleanValue) ? 0xcb00ff /*Purple*/ : 0x00d0ff /*Cyan*/;
    }

    @Override
    public boolean hasTooltipDetails(@Nullable Key key) {
        return key == null;
    }

    public int getFuelFromStack(ItemStack stack) {
        int fuelAmount = 0;
        if (stack.hasTag() && stack.getTag().contains("Fluid") && stack.getTag().getCompound("Fluid").contains("Amount")) {
            fuelAmount = stack.getTag().getCompound("Fluid").getInt("Amount");
        }
        return fuelAmount;
    }

    public boolean isSpecial(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains("Special") && stack.getTag().getBoolean("Special");
    }

    public boolean isSpecialEnabled(ItemStack stack) {
        return isSpecial(stack) && stack.getTag().contains("SpecialEnabled") && stack.getTag().getBoolean("SpecialEnabled");
    }

    public void setSpecialEnabled(ItemStack stack, boolean special) {
        if (isSpecial(stack)) stack.getTag().putBoolean("SpecialEnabled", special);
    }

    public boolean enoughFuel(ItemStack stack) {
        int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack);
        return getFuelFromStack(stack) >= biofuelConsumption * ( 1 / (i + 1)) || getPowerFromStack(stack) >= powerConsumption * ( 1 / (i + 1));
    }

    public void consumeFuel(ItemStack stack) {
        double i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack);
        if (getFuelFromStack(stack) >= biofuelConsumption * ( 1 / (i + 1))) {
            stack.getTag().getCompound("Fluid").putInt("Amount", (int) Math.max(0, stack.getTag().getCompound("Fluid").getInt("Amount") - biofuelConsumption * (1 / (i + 1))));
        } else {
            stack.getTag().putLong("Energy", (long) (stack.getTag().getLong("Energy") - powerConsumption * (1 / (i + 1))));
        }
    }

    public void setCanCharge(ItemStack stack, boolean canCharge) {
        stack.getTag().putBoolean("CanCharge", canCharge);
    }

    public void setSelectedDrillTier(ItemStack stack, InfinityTier tier) {
        stack.getTag().putString("Selected", tier.name());
    }

    @Override
    public void addTooltipDetails(@Nullable Key key, ItemStack stack, List<Component> tooltip, boolean advanced) {
        long power = getPowerFromStack(stack);
        Pair<InfinityTier, InfinityTier> braquet = InfinityTier.getTierBraquet(power);
        InfinityTier current = getSelectedTier(stack);
        if (usesArea)
            tooltip.add(new TranslatableComponent("text.industrialforegoing.display.current_area").append(" ").append(getFormattedArea(stack, current, current.getRadius(), this.usesDepth)).withStyle(ChatFormatting.GRAY));
        tooltip.add(new TranslatableComponent("text.industrialforegoing.display.tier").append(" " + braquet.getLeft().getColor() + braquet.getLeft().getLocalizedName()).withStyle(ChatFormatting.GRAY));
        tooltip.add(new TranslatableComponent("text.industrialforegoing.display.power").append(" ").append(ChatFormatting.RED + NumberFormat.getNumberInstance(Locale.ROOT).format(power) + ChatFormatting.GREEN).append("/").append(NumberFormat.getNumberInstance(Locale.ROOT).format(braquet.getRight().getPowerNeeded())).append("RF ").append(new TranslatableComponent("text.industrialforegoing.display.next_tier")).withStyle(ChatFormatting.GRAY));
        int fuelAmount = getFuelFromStack(stack);
        tooltip.add(new TranslatableComponent("text.industrialforegoing.display.fluid").append(" ").append(ChatFormatting.LIGHT_PURPLE + NumberFormat.getNumberInstance(Locale.ROOT).format(fuelAmount) + ChatFormatting.GRAY).append("/").append(NumberFormat.getNumberInstance(Locale.ROOT).format(1000000)).append(" mb of Biofuel").withStyle(ChatFormatting.GRAY));
        if (usesArea)
            tooltip.add(new TranslatableComponent("text.industrialforegoing.display.max_area").append(" ").append(getFormattedArea(stack, braquet.getLeft(), braquet.getLeft().getRadius(), this.usesDepth)).withStyle(ChatFormatting.GRAY));
        if (canCharge(stack)) {
            tooltip.add(new TranslatableComponent("text.industrialforegoing.display.charging").withStyle(ChatFormatting.GRAY).append(new TranslatableComponent("text.industrialforegoing.display.enabled").withStyle(ChatFormatting.GREEN)));
        } else {
            tooltip.add(new TranslatableComponent("text.industrialforegoing.display.charging").withStyle(ChatFormatting.GRAY).append(new TranslatableComponent("text.industrialforegoing.display.disabled").withStyle(ChatFormatting.RED)));
        }
        if (isSpecial(stack))
            tooltip.add(new TranslatableComponent("text.industrialforegoing.display.special").withStyle(ChatFormatting.GOLD));
    }

    public Pair<BlockPos, BlockPos> getArea(BlockPos pos, Direction facing, InfinityTier currentTier, boolean withDepth) {
        int radius = currentTier.getRadius();
        BlockPos bottomLeft = pos.relative(facing.getAxis() == Direction.Axis.Y ? Direction.SOUTH : Direction.DOWN, radius).relative(facing.getAxis() == Direction.Axis.Y ? Direction.WEST : facing.getCounterClockWise(), radius);
        BlockPos topRight = pos.relative(facing.getAxis() == Direction.Axis.Y ? Direction.NORTH : Direction.UP, radius).relative(facing.getAxis() == Direction.Axis.Y ? Direction.EAST : facing.getClockWise(), radius);
        if (facing.getAxis() != Direction.Axis.Y && radius > 0) {
            bottomLeft = bottomLeft.relative(Direction.UP, radius - 1);
            topRight = topRight.relative(Direction.UP, radius - 1);
        }
        if (currentTier == InfinityTier.ARTIFACT && withDepth) {
            topRight = topRight.relative(facing.getOpposite(), radius);
        }
        return Pair.of(bottomLeft, topRight);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = MultimapBuilder.hashKeys().arrayListValues().build();
        if (slot == EquipmentSlot.MAINHAND) {
            multimap.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", 3, AttributeModifier.Operation.ADDITION)); //AttackDamage
            multimap.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", -2.5D, AttributeModifier.Operation.ADDITION)); //AttackSpeed
        }
        return multimap;
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent(this.getDescriptionId()).withStyle(ChatFormatting.DARK_GRAY);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int menu, Inventory p_createMenu_2_, Player playerEntity) {
        return new BasicAddonContainer(ItemStackHarnessRegistry.createItemStackHarness(playerEntity.getMainHandItem()), new HeldStackLocatorInstance(true), new ContainerLevelAccess() {
            @Override
            public <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> p_221484_1_) {
                return Optional.empty();
            }
        }, playerEntity.inventory, menu);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player player, InteractionHand handIn) {
        if (player.isCrouching()) {
            if (player instanceof ServerPlayer) {
                IndustrialForegoing.NETWORK.get().sendTo(new BackpackOpenedMessage(player.inventory.selected, PlayerInventoryFinder.MAIN), ((ServerPlayer) player).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                NetworkHooks.openGui((ServerPlayer) player, this, buffer ->
                        LocatorFactory.writePacketBuffer(buffer, new HeldStackLocatorInstance(handIn == InteractionHand.MAIN_HAND)));
            }
            return InteractionResultHolder.success(player.getItemInHand(handIn));
        }
        if (CommonProxy.CONTRIBUTORS.contains(player.getUUID().toString())) {
            player.getItemInHand(handIn).getOrCreateTag().putBoolean("Special", true);
        }
        return super.use(worldIn, player, handIn);
    }

    @Override
    public boolean shouldOverrideMultiplayerNbt() {
        return true;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new InfinityCapabilityProvider(stack, getTankConstructor(stack), getEnergyConstructor(stack));
    }

    @Override
    public void handleButtonMessage(int id, Player playerEntity, CompoundTag compound) {
        ItemStack stack = playerEntity.getItemInHand(InteractionHand.MAIN_HAND);
        if (!(stack.getItem() instanceof ItemInfinity)) stack = playerEntity.getItemInHand(InteractionHand.OFF_HAND);
        if (stack.getItem() instanceof ItemInfinity) {
            if (id == 1) {
                InfinityTier prev = getSelectedTier(stack).getPrev(InfinityTier.getTierBraquet(getPowerFromStack(stack)).getLeft());
                setSelectedDrillTier(stack, prev);
            }
            if (id == 2) {
                InfinityTier next = getSelectedTier(stack).getNext(InfinityTier.getTierBraquet(getPowerFromStack(stack)).getLeft());
                setSelectedDrillTier(stack, next);
            }
            if (id == 3) {
                setCanCharge(stack, !canCharge(stack));
            }
            if (id == -10) {
                setSpecialEnabled(stack, !isSpecialEnabled(stack));
            }
        }
    }

    @Override
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {

    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public List<IFactory<? extends IScreenAddon>> getScreenAddons(Supplier<ItemStack> stack) {
        List<IFactory<? extends IScreenAddon>> factory = new ArrayList<>();
        factory.add(() -> new ArrowButtonScreenAddon((ArrowButtonComponent) new ArrowButtonComponent(154, 20, 14, 14, FacingUtil.Sideness.RIGHT).setId(2)));
        factory.add(() -> new ArrowButtonScreenAddon((ArrowButtonComponent) new ArrowButtonComponent(54, 20, 14, 14, FacingUtil.Sideness.LEFT).setId(1)));
        factory.add(() -> new TextScreenAddon("", 54 + 14 + 4, 24, false) {
            @Override
            public String getText() {
                InfinityTier current = ItemInfinity.getSelectedTier(stack.get());
                return ChatFormatting.DARK_GRAY + "Area: " + getFormattedArea(stack.get(), current, current.getRadius(), usesDepth);
            }
        });
        factory.add(() -> new StateButtonAddon(new ButtonComponent(54, 36, 14, 14).setId(3), new StateButtonInfo(0, AssetTypes.BUTTON_SIDENESS_ENABLED), new StateButtonInfo(1, AssetTypes.BUTTON_SIDENESS_DISABLED)) {
            @Override
            public int getState() {
                return ItemInfinity.canCharge(stack.get()) ? 0 : 1;
            }
        });
        factory.add(() -> new TextScreenAddon("", 54 + 14 + 4, 40, false) {
            @Override
            public String getText() {
                if (ItemInfinity.canCharge(stack.get())) {//setStyle
                    return ChatFormatting.DARK_GRAY + new TranslatableComponent("text.industrialforegoing.display.charging").getString() + ChatFormatting.GREEN + new TranslatableComponent("text.industrialforegoing.display.enabled").getString();
                } else {
                    return ChatFormatting.DARK_GRAY + new TranslatableComponent("text.industrialforegoing.display.charging").getString() + ChatFormatting.RED + new TranslatableComponent("text.industrialforegoing.display.disabled").getString();
                }
            }
        });
        if (isSpecial(stack.get())) {
            factory.add(() -> new StateButtonAddon(new ButtonComponent(12, 80, 14, 15).setId(-10), new StateButtonInfo(0, AssetTypes.BUTTON_SIDENESS_ENABLED), new StateButtonInfo(1, AssetTypes.BUTTON_SIDENESS_DISABLED)) {
                @Override
                public int getState() {
                    return ItemInfinity.this.isSpecialEnabled(stack.get()) ? 0 : 1;
                }
            });
            factory.add(() -> new TextScreenAddon(ChatFormatting.GOLD + new TranslatableComponent("text.industrialforegoing.display.special").getString(), 12 + 14 + 4, 84, false));
        }
        return factory;
    }

    public IFactory<? extends FluidHandlerScreenProviderItemStack> getTankConstructor(ItemStack stack){
        return () -> new FluidHandlerScreenProviderItemStack(stack, 1_000_000) {
            @Override
            public boolean canFillFluidType(FluidStack fluid) {
                return fluid != null && fluid.getFluid() != null && fluid.getFluid().equals(ModuleCore.BIOFUEL.getSourceFluid().get());
            }

            @Override
            public boolean canDrainFluidType(FluidStack fluid) {
                return false;
            }

            @Nonnull
            @Override
            @OnlyIn(Dist.CLIENT)
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                return Collections.singletonList(() -> new TankScreenAddon(30, 20, this, FluidTankComponent.Type.NORMAL));
            }
        };
    }

    public IFactory<InfinityEnergyStorage> getEnergyConstructor(ItemStack stack){
        return () ->  new InfinityEnergyStorage(InfinityTier.ARTIFACT.getPowerNeeded(), 10, 20) {
            @Override
            public long getLongEnergyStored() {
                if (stack.hasTag()) {
                    return Math.min(stack.getTag().getLong("Energy"), InfinityTier.ARTIFACT.getPowerNeeded());
                } else {
                    return 0;
                }
            }

            @Override
            public void setEnergyStored(long energy) {
                if (!stack.hasTag()) {
                    stack.setTag(new CompoundTag());
                }
                stack.getTag().putLong("Energy", Math.min(energy, InfinityTier.ARTIFACT.getPowerNeeded()));
            }

            @Override
            public boolean canReceive() {
                return ItemInfinity.canCharge(stack);
            }
        };
    }
}

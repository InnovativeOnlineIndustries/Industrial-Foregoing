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
import com.buuz135.industrial.proxy.network.BackpackOpenedMessage;
import com.buuz135.industrial.utils.IFAttachments;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.ISpecialCreativeTabItem;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.block.tile.IScreenInfoProvider;
import com.hrznstudio.titanium.client.screen.addon.ArrowButtonScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.StateButtonAddon;
import com.hrznstudio.titanium.client.screen.addon.StateButtonInfo;
import com.hrznstudio.titanium.client.screen.addon.TextScreenAddon;
import com.hrznstudio.titanium.component.button.ArrowButtonComponent;
import com.hrznstudio.titanium.component.button.ButtonComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.container.BasicAddonContainer;
import com.hrznstudio.titanium.itemstack.ItemStackHarnessRegistry;
import com.hrznstudio.titanium.network.IButtonHandler;
import com.hrznstudio.titanium.network.locator.LocatorFactory;
import com.hrznstudio.titanium.network.locator.PlayerInventoryFinder;
import com.hrznstudio.titanium.network.locator.instance.HeldStackLocatorInstance;
import com.hrznstudio.titanium.tab.TitaniumTab;
import com.hrznstudio.titanium.util.FacingUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class ItemInfinity extends IFCustomItem implements MenuProvider, IButtonHandler, IInfinityDrillScreenAddons, ISpecialCreativeTabItem, IScreenInfoProvider {

    private final int powerConsumption;
    private final int biofuelConsumption;
    private final boolean usesDepth;
    private boolean usesArea;

    public ItemInfinity(String name, TitaniumTab group, Properties builder, int powerConsumption, int biofuelConsumption, boolean usesDepth) {
        super(name, group, builder);
        this.powerConsumption = powerConsumption;
        this.biofuelConsumption = biofuelConsumption;
        this.usesDepth = usesDepth;
        this.usesArea = true;
    }

    public static long getPowerFromStack(ItemStack stack) {
        return stack.getOrDefault(IFAttachments.INFINITY_ITEM_POWER, 0L);
    }

    public void disableArea() {
        this.usesArea = false;
    }

    public String getFormattedArea(ItemStack stack, InfinityTier tier, int radius, boolean usesDepth) {
        int diameter = radius * 2 + 1;
        return diameter + "x" + diameter + "x" + (tier == InfinityTier.ARTIFACT || usesDepth ? diameter : 1);
    }

    public static InfinityTier getSelectedTier(ItemStack stack) {
        return stack.getOrDefault(IFAttachments.INFINITY_ITEM_SELECTED_TIER, InfinityTier.POOR);
    }

    public static boolean canCharge(ItemStack stack) {
        return stack.getOrDefault(IFAttachments.INFINITY_ITEM_CAN_CHARGE, false);
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level worldIn, Player playerIn) {
        super.onCraftedBy(stack, worldIn, playerIn);
        addNbt(stack, 0, 0, false);
    }

    public void addNbt(ItemStack stack, long power, int fuel, boolean special) {
        stack.set(IFAttachments.INFINITY_ITEM_POWER, power);
        stack.set(IFAttachments.INFINITY_ITEM_SPECIAL, special);
        stack.set(IFAttachments.INFINITY_ITEM_SELECTED_TIER, InfinityTier.getTierBraquet(power).getLeft());
        stack.set(IFAttachments.INFINITY_ITEM_CAN_CHARGE, true);
    }

    @Override
    public void addToTab(BuildCreativeModeTabContentsEvent event) {
        for (InfinityTier value : InfinityTier.values()) {
            event.accept(createStack(value.getPowerNeeded(), 0, false));
        }
        event.accept(createStack(InfinityTier.ARTIFACT.getPowerNeeded(), 1_000_000, true));
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
        Supplier<Supplier<Boolean>> checker = () -> () -> false;
        if (FMLEnvironment.dist.isClient()) checker = () -> Screen::hasShiftDown;
        if (!checker.get().get()) { //hasShiftDown
            int fuel = getFuelFromStack(stack);
            return (int) Math.round(fuel * 13D / 1_000_000D);
        } else {
            long power = getPowerFromStack(stack);
            return (int) Math.round(power * 13D / (double) InfinityTier.getTierBraquet(power).getRight().getPowerNeeded());
        }
    }

    @Override
    public int getBarColor(ItemStack p_150901_) {
        Supplier<Supplier<Boolean>> checker = () -> () -> false;
        if (FMLEnvironment.dist.isClient()) checker = () -> Screen::hasShiftDown;
        return !checker.get().get() ? 0xcb00ff /*Purple*/ : 0x00d0ff /*Cyan*/;
    }

    @Override
    public boolean hasTooltipDetails(@Nullable Key key) {
        return key == null;
    }

    public int getFuelFromStack(ItemStack stack) {
        int fuelAmount = 0;
        var capability = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (capability != null) {
            for (int i = 0; i < capability.getTanks(); i++) {
                if (capability.getFluidInTank(i).is(ModuleCore.BIOFUEL.getSourceFluid())) {
                    return capability.getFluidInTank(i).getAmount();
                }
            }
        }
        return fuelAmount;
    }

    public boolean isSpecial(ItemStack stack) {
        return stack.getOrDefault(IFAttachments.INFINITY_ITEM_SPECIAL, false);
    }

    public boolean isSpecialEnabled(ItemStack stack) {
        return isSpecial(stack) && stack.getOrDefault(IFAttachments.INFINITY_ITEM_SPECIAL_ENABLED, false);
    }

    public void setSpecialEnabled(ItemStack stack, boolean special) {
        if (isSpecial(stack)) stack.set(IFAttachments.INFINITY_ITEM_SPECIAL_ENABLED, special);
    }

    public boolean enoughFuel(ItemStack stack) {
        int level = EnchantmentHelper.getItemEnchantmentLevel(IFAttachments.registryAccess().holderOrThrow(Enchantments.UNBREAKING), stack);
        return getFuelFromStack(stack) >= biofuelConsumption * (1D / (level + 1)) || getPowerFromStack(stack) >= powerConsumption * (1D / (level + 1));
    }

    public void consumeFuel(ItemStack stack) {
        int level = EnchantmentHelper.getItemEnchantmentLevel(IFAttachments.registryAccess().holderOrThrow(Enchantments.UNBREAKING), stack);
        if (getFuelFromStack(stack) >= biofuelConsumption * (1D / (level + 1))) {
            var capability = stack.getCapability(Capabilities.FluidHandler.ITEM);
            if (capability != null) {
                for (int i = 0; i < capability.getTanks(); i++) {
                    if (capability.getFluidInTank(i).is(ModuleCore.BIOFUEL.getSourceFluid())) {
                        capability.drain(new FluidStack(ModuleCore.BIOFUEL.getSourceFluid(), biofuelConsumption * (1 / (level + 1))), IFluidHandler.FluidAction.EXECUTE);
                        return;
                    }
                }
            }
        } else {
            stack.set(IFAttachments.INFINITY_ITEM_POWER, (long) (stack.getOrDefault(IFAttachments.INFINITY_ITEM_POWER, 0L) - powerConsumption * (1D / (level + 1))));
        }
    }

    public void setCanCharge(ItemStack stack, boolean canCharge) {
        stack.set(IFAttachments.INFINITY_ITEM_CAN_CHARGE, canCharge);
    }

    public void setSelectedDrillTier(ItemStack stack, InfinityTier tier) {
        stack.set(IFAttachments.INFINITY_ITEM_SELECTED_TIER, tier);
    }

    @Override
    public void addTooltipDetails(@Nullable Key key, ItemStack stack, List<Component> tooltip, boolean advanced) {
        long power = getPowerFromStack(stack);
        Pair<InfinityTier, InfinityTier> braquet = InfinityTier.getTierBraquet(power);
        InfinityTier current = getSelectedTier(stack);
        if (usesArea)
            tooltip.add(Component.translatable("text.industrialforegoing.display.current_area").append(" ").append(getFormattedArea(stack, current, current.getRadius(), this.usesDepth)).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("text.industrialforegoing.display.tier").append(" " + braquet.getLeft().getColor() + braquet.getLeft().getLocalizedName()).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("text.industrialforegoing.display.power").append(" ").append(ChatFormatting.RED + NumberFormat.getNumberInstance(Locale.ROOT).format(power) + ChatFormatting.GREEN).append("/").append(NumberFormat.getNumberInstance(Locale.ROOT).format(braquet.getRight().getPowerNeeded())).append("RF ").append(Component.translatable("text.industrialforegoing.display.next_tier")).withStyle(ChatFormatting.GRAY));
        int fuelAmount = getFuelFromStack(stack);
        tooltip.add(Component.translatable("text.industrialforegoing.display.fluid").append(" ").append(ChatFormatting.LIGHT_PURPLE + NumberFormat.getNumberInstance(Locale.ROOT).format(fuelAmount) + ChatFormatting.GRAY).append("/").append(NumberFormat.getNumberInstance(Locale.ROOT).format(1000000)).append(" mb of Biofuel").withStyle(ChatFormatting.GRAY));
        if (usesArea)
            tooltip.add(Component.translatable("text.industrialforegoing.display.max_area").append(" ").append(getFormattedArea(stack, braquet.getLeft(), braquet.getLeft().getRadius(), this.usesDepth)).withStyle(ChatFormatting.GRAY));
        if (canCharge(stack)) {
            tooltip.add(Component.translatable("text.industrialforegoing.display.charging").withStyle(ChatFormatting.GRAY).append(Component.translatable("text.industrialforegoing.display.enabled").withStyle(ChatFormatting.GREEN)));
        } else {
            tooltip.add(Component.translatable("text.industrialforegoing.display.charging").withStyle(ChatFormatting.GRAY).append(Component.translatable("text.industrialforegoing.display.disabled").withStyle(ChatFormatting.RED)));
        }
        if (isSpecial(stack))
            tooltip.add(Component.translatable("text.industrialforegoing.display.special").withStyle(ChatFormatting.GOLD));
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
            topRight = topRight.relative(facing.getOpposite(), radius * 2);
        }
        return Pair.of(bottomLeft, topRight);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        var attributes = super.getDefaultAttributeModifiers(stack);
        attributes = attributes.withModifierAdded(Attributes.ATTACK_DAMAGE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "base_attack_damage"), 3, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ANY);
        attributes = attributes.withModifierAdded(Attributes.ATTACK_SPEED, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "base_attack_speed"), -2.5D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ANY);
        return attributes;
    }

    @Override
    public Component getDisplayName() {
        return getDescription().copy().withStyle(ChatFormatting.DARK_GRAY);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int menu, Inventory p_createMenu_2_, Player playerEntity) {
        return new BasicAddonContainer(ItemStackHarnessRegistry.createItemStackHarness(playerEntity.getMainHandItem()), new HeldStackLocatorInstance(true), new ContainerLevelAccess() {
            @Override
            public <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> p_221484_1_) {
                return Optional.empty();
            }
        }, playerEntity.getInventory(), menu);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player player, InteractionHand handIn) {
        if (player.isCrouching()) {
            if (player instanceof ServerPlayer) {
                IndustrialForegoing.NETWORK.sendTo(new BackpackOpenedMessage(player.getInventory().selected, PlayerInventoryFinder.MAIN), ((ServerPlayer) player));
                player.openMenu(this, buffer ->
                        LocatorFactory.writePacketBuffer(buffer, new HeldStackLocatorInstance(handIn == InteractionHand.MAIN_HAND)));
            }
            return InteractionResultHolder.success(player.getItemInHand(handIn));
        }
        if (IndustrialForegoing.CAT_EARS.getPlayers().contains(player.getUUID())) {
            player.getItemInHand(handIn).set(IFAttachments.INFINITY_ITEM_SPECIAL, true);
        }
        return super.use(worldIn, player, handIn);
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
    public void registerRecipe(RecipeOutput consumer) {

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
                    return ChatFormatting.DARK_GRAY + Component.translatable("text.industrialforegoing.display.charging").getString() + ChatFormatting.GREEN + Component.translatable("text.industrialforegoing.display.enabled").getString();
                } else {
                    return ChatFormatting.DARK_GRAY + Component.translatable("text.industrialforegoing.display.charging").getString() + ChatFormatting.RED + Component.translatable("text.industrialforegoing.display.disabled").getString();
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
            factory.add(() -> new TextScreenAddon(ChatFormatting.GOLD + Component.translatable("text.industrialforegoing.display.special").getString(), 12 + 14 + 4, 84, false));
        }
        return factory;
    }

    public IFactory<InfinityTankStorage> getTankConstructor(ItemStack stack) {
        return () -> new InfinityTankStorage(stack, new InfinityTankStorage.TankDefinition("biofuel", 1_000_000, 30, 20, fluidStack -> fluidStack.getFluid().isSame(ModuleCore.BIOFUEL.getSourceFluid().get()), true, true, FluidTankComponent.Type.NORMAL));
    }

    public IFactory<InfinityEnergyStorage> getEnergyConstructor(ItemStack stack) {
        return () -> new InfinityEnergyStorage(InfinityTier.ARTIFACT.getPowerNeeded(), 10, 20) {
            @Override
            public long getLongEnergyStored() {
                return Math.min(stack.getOrDefault(IFAttachments.INFINITY_ITEM_POWER, 0L), InfinityTier.ARTIFACT.getPowerNeeded());
            }

            @Override
            public void setEnergyStored(long energy) {
                stack.set(IFAttachments.INFINITY_ITEM_POWER, Math.min(energy, InfinityTier.ARTIFACT.getPowerNeeded()));
            }

            @Override
            public boolean canReceive() {
                return ItemInfinity.canCharge(stack);
            }
        };
    }
}
    
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
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemInfinity extends IFCustomItem implements INamedContainerProvider, IButtonHandler, IInfinityDrillScreenAddons {

    private final int powerConsumption;
    private final int biofuelConsumption;
    private final boolean usesDepth;
    private boolean usesArea;

    public ItemInfinity(String name, ItemGroup group, Properties builder, int powerConsumption, int biofuelConsumption, boolean usesDepth) {
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
    public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn) {
        super.onCreated(stack, worldIn, playerIn);
        addNbt(stack, 0, 0, false);
    }

    public void addNbt(ItemStack stack, long power, int fuel, boolean special) {
        CompoundNBT tagCompound = new CompoundNBT();
        tagCompound.putLong("Energy", power);
        CompoundNBT fluid = new CompoundNBT();
        fluid.putString("FluidName", "biofuel");
        fluid.putInt("Amount", fuel);
        tagCompound.put("Fluid", fluid);
        tagCompound.putBoolean("Special", special);
        tagCompound.putString("Selected", InfinityTier.getTierBraquet(power).getLeft().name());
        tagCompound.putBoolean("CanCharge", true);
        stack.setTag(tagCompound);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (isInGroup(group)) {
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
    public int getItemEnchantability() {
        return 50;
    }

    @Override
    public UseAction getUseAction(ItemStack p_77661_1_) {
        return UseAction.BOW;
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
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        if (!Screen.hasShiftDown()) { //hasShiftDown
            int fuel = getFuelFromStack(stack);
            return 1 - fuel / 1_000_000D;
        } else {
            long power = getPowerFromStack(stack);
            return 1 - power / (double) InfinityTier.getTierBraquet(power).getRight().getPowerNeeded();
        }
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return !Screen.hasShiftDown() ? 0xcb00ff /*Purple*/ : 0x00d0ff /*Cyan*/;
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
        int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);
        return getFuelFromStack(stack) >= biofuelConsumption * ( 1 / (i + 1)) || getPowerFromStack(stack) >= powerConsumption * ( 1 / (i + 1));
    }

    public void consumeFuel(ItemStack stack) {
        double i = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);
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
    public void addTooltipDetails(@Nullable Key key, ItemStack stack, List<ITextComponent> tooltip, boolean advanced) {
        long power = getPowerFromStack(stack);
        Pair<InfinityTier, InfinityTier> braquet = InfinityTier.getTierBraquet(power);
        InfinityTier current = getSelectedTier(stack);
        if (usesArea)
            tooltip.add(new TranslationTextComponent("text.industrialforegoing.display.current_area").appendString(" ").appendString(getFormattedArea(stack, current, current.getRadius(), this.usesDepth)).mergeStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("text.industrialforegoing.display.tier").appendString(" " + braquet.getLeft().getColor() + braquet.getLeft().getLocalizedName()).mergeStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("text.industrialforegoing.display.power").appendString(" ").appendString(TextFormatting.RED + NumberFormat.getNumberInstance(Locale.ROOT).format(power) + TextFormatting.GREEN).appendString("/").appendString(NumberFormat.getNumberInstance(Locale.ROOT).format(braquet.getRight().getPowerNeeded())).appendString("RF ").append(new TranslationTextComponent("text.industrialforegoing.display.next_tier")).mergeStyle(TextFormatting.GRAY));
        int fuelAmount = getFuelFromStack(stack);
        tooltip.add(new TranslationTextComponent("text.industrialforegoing.display.fluid").appendString(" ").appendString(TextFormatting.LIGHT_PURPLE + NumberFormat.getNumberInstance(Locale.ROOT).format(fuelAmount) + TextFormatting.GRAY).appendString("/").appendString(NumberFormat.getNumberInstance(Locale.ROOT).format(1000000)).appendString(" mb of Biofuel").mergeStyle(TextFormatting.GRAY));
        if (usesArea)
            tooltip.add(new TranslationTextComponent("text.industrialforegoing.display.max_area").appendString(" ").appendString(getFormattedArea(stack, braquet.getLeft(), braquet.getLeft().getRadius(), this.usesDepth)).mergeStyle(TextFormatting.GRAY));
        if (canCharge(stack)) {
            tooltip.add(new TranslationTextComponent("text.industrialforegoing.display.charging").mergeStyle(TextFormatting.GRAY).append(new TranslationTextComponent("text.industrialforegoing.display.enabled").mergeStyle(TextFormatting.GREEN)));
        } else {
            tooltip.add(new TranslationTextComponent("text.industrialforegoing.display.charging").mergeStyle(TextFormatting.GRAY).append(new TranslationTextComponent("text.industrialforegoing.display.disabled").mergeStyle(TextFormatting.RED)));
        }
        if (isSpecial(stack))
            tooltip.add(new TranslationTextComponent("text.industrialforegoing.display.special").mergeStyle(TextFormatting.GOLD));
    }

    public Pair<BlockPos, BlockPos> getArea(BlockPos pos, Direction facing, InfinityTier currentTier, boolean withDepth) {
        int radius = currentTier.getRadius();
        BlockPos bottomLeft = pos.offset(facing.getAxis() == Direction.Axis.Y ? Direction.SOUTH : Direction.DOWN, radius).offset(facing.getAxis() == Direction.Axis.Y ? Direction.WEST : facing.rotateYCCW(), radius);
        BlockPos topRight = pos.offset(facing.getAxis() == Direction.Axis.Y ? Direction.NORTH : Direction.UP, radius).offset(facing.getAxis() == Direction.Axis.Y ? Direction.EAST : facing.rotateY(), radius);
        if (facing.getAxis() != Direction.Axis.Y && radius > 0) {
            bottomLeft = bottomLeft.offset(Direction.UP, radius - 1);
            topRight = topRight.offset(Direction.UP, radius - 1);
        }
        if (currentTier == InfinityTier.ARTIFACT && withDepth) {
            topRight = topRight.offset(facing.getOpposite(), radius);
        }
        return Pair.of(bottomLeft, topRight);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = MultimapBuilder.hashKeys().arrayListValues().build();
        if (slot == EquipmentSlotType.MAINHAND) {
            multimap.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", 3, AttributeModifier.Operation.ADDITION)); //AttackDamage
            multimap.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -2.5D, AttributeModifier.Operation.ADDITION)); //AttackSpeed
        }
        return multimap;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(this.getTranslationKey()).mergeStyle(TextFormatting.DARK_GRAY);
    }

    @Nullable
    @Override
    public Container createMenu(int menu, PlayerInventory p_createMenu_2_, PlayerEntity playerEntity) {
        return new BasicAddonContainer(ItemStackHarnessRegistry.getHarnessCreators().get(this).apply(playerEntity.getHeldItemMainhand()), new HeldStackLocatorInstance(true), new IWorldPosCallable() {
            @Override
            public <T> Optional<T> apply(BiFunction<World, BlockPos, T> p_221484_1_) {
                return Optional.empty();
            }
        }, playerEntity.inventory, menu);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity player, Hand handIn) {
        if (player.isCrouching()) {
            if (player instanceof ServerPlayerEntity) {
                IndustrialForegoing.NETWORK.get().sendTo(new BackpackOpenedMessage(player.inventory.currentItem, PlayerInventoryFinder.MAIN), ((ServerPlayerEntity) player).connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
                NetworkHooks.openGui((ServerPlayerEntity) player, this, buffer ->
                        LocatorFactory.writePacketBuffer(buffer, new HeldStackLocatorInstance(handIn == Hand.MAIN_HAND)));
            }
            return ActionResult.resultSuccess(player.getHeldItem(handIn));
        }
        if (CommonProxy.CONTRIBUTORS.contains(player.getUniqueID().toString())) {
            player.getHeldItem(handIn).getOrCreateTag().putBoolean("Special", true);
        }
        return super.onItemRightClick(worldIn, player, handIn);
    }

    @Override
    public boolean shouldSyncTag() {
        return true;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new InfinityCapabilityProvider(stack, getTankConstructor(stack), getEnergyConstructor(stack));
    }

    @Override
    public void handleButtonMessage(int id, PlayerEntity playerEntity, CompoundNBT compound) {
        ItemStack stack = playerEntity.getHeldItem(Hand.MAIN_HAND);
        if (!(stack.getItem() instanceof ItemInfinity)) stack = playerEntity.getHeldItem(Hand.OFF_HAND);
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
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {

    }

    @Override
    public List<IFactory<? extends IScreenAddon>> getScreenAddons(Supplier<ItemStack> stack) {
        List<IFactory<? extends IScreenAddon>> factory = new ArrayList<>();
        factory.add(() -> new ArrowButtonScreenAddon((ArrowButtonComponent) new ArrowButtonComponent(154, 20, 14, 14, FacingUtil.Sideness.RIGHT).setId(2)));
        factory.add(() -> new ArrowButtonScreenAddon((ArrowButtonComponent) new ArrowButtonComponent(54, 20, 14, 14, FacingUtil.Sideness.LEFT).setId(1)));
        factory.add(() -> new TextScreenAddon("", 54 + 14 + 4, 24, false) {
            @Override
            public String getText() {
                InfinityTier current = ItemInfinity.getSelectedTier(stack.get());
                return TextFormatting.DARK_GRAY + "Area: " + getFormattedArea(stack.get(), current, current.getRadius(), usesDepth);
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
                    return TextFormatting.DARK_GRAY + new TranslationTextComponent("text.industrialforegoing.display.charging").getString() + TextFormatting.GREEN + new TranslationTextComponent("text.industrialforegoing.display.enabled").getString();
                } else {
                    return TextFormatting.DARK_GRAY + new TranslationTextComponent("text.industrialforegoing.display.charging").getString() + TextFormatting.RED + new TranslationTextComponent("text.industrialforegoing.display.disabled").getString();
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
            factory.add(() -> new TextScreenAddon(TextFormatting.GOLD + new TranslationTextComponent("text.industrialforegoing.display.special").getString(), 12 + 14 + 4, 84, false));
        }
        return factory;
    }

    public IFactory<? extends FluidHandlerScreenProviderItemStack> getTankConstructor(ItemStack stack){
        return () -> new FluidHandlerScreenProviderItemStack(stack, 1_000_000) {
            @Override
            public boolean canFillFluidType(FluidStack fluid) {
                return fluid != null && fluid.getFluid() != null && fluid.getFluid().equals(ModuleCore.BIOFUEL.getSourceFluid());
            }

            @Override
            public boolean canDrainFluidType(FluidStack fluid) {
                return false;
            }

            @Nonnull
            @Override
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
                    stack.setTag(new CompoundNBT());
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

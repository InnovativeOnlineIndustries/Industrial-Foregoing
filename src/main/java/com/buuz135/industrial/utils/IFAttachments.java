package com.buuz135.industrial.utils;

import com.buuz135.industrial.item.infinity.InfinityTier;
import com.buuz135.industrial.item.infinity.item.ItemInfinityLauncher;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.util.LogicalSidedProvider;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class IFAttachments {

    public static final DeferredRegister<DataComponentType<?>> DR = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Reference.MOD_ID);

    public static final Supplier<DataComponentType<ItemInfinityLauncher.PlungerAction>> PLUNGER_ACTION =
            register("configuration_action", () -> ItemInfinityLauncher.PlungerAction.RELEASE, builder -> builder.persistent(ItemInfinityLauncher.PlungerAction.CODEC));

    public static final Supplier<DataComponentType<Boolean>> INFINITY_ITEM_CAN_CHARGE = register("infinity_can_charge", () -> true, op -> op.persistent(Codec.BOOL));
    public static final Supplier<DataComponentType<Boolean>> INFINITY_MAGNET_ENABLED = register("infinity_backpack_magnet_enabled", () -> true, op -> op.persistent(Codec.BOOL));
    public static final Supplier<DataComponentType<Integer>> INFINITY_BACKPACK_PICKUP_MODE = register("infinity_backpack_pickup_mode", () -> 0, op -> op.persistent(Codec.INT));
    public static final Supplier<DataComponentType<Boolean>> INFINITY_ITEM_SPECIAL = register("infinity_item_special", () -> false, op -> op.persistent(Codec.BOOL));
    public static final Supplier<DataComponentType<Boolean>> INFINITY_ITEM_SPECIAL_ENABLED = register("infinity_item_special_enabled", () -> false, op -> op.persistent(Codec.BOOL));
    public static final Supplier<DataComponentType<InfinityTier>> INFINITY_ITEM_SELECTED_TIER =
            register("infinity_item_selected_tier", () -> InfinityTier.POOR, builder -> builder.persistent(InfinityTier.CODEC));
    public static final Supplier<DataComponentType<String>> INFINITY_BACKPACK_ID = register("infinity_backpack_id", () -> "", op -> op.persistent(Codec.STRING));
    public static final Supplier<DataComponentType<Long>> INFINITY_ITEM_POWER = register("infinity_item_power", () -> 0L, op -> op.persistent(Codec.LONG));
    public static final Supplier<DataComponentType<CompoundTag>> INFINITY_TANKS = register("infinity_tank", CompoundTag::new, op -> op.persistent(CompoundTag.CODEC));
    public static final Supplier<DataComponentType<Integer>> INFINITY_HAMMER_BEHEADING = register("infinity_hammer_beheading", () -> 0, op -> op.persistent(Codec.INT));
    public static final Supplier<DataComponentType<Integer>> INFINITY_TRIDENT_LOYALTY = register("infinity_trident_loyalty", () -> 0, op -> op.persistent(Codec.INT));
    public static final Supplier<DataComponentType<Integer>> INFINITY_TRIDENT_RIPTIDE = register("infinity_trident_riptide", () -> 0, op -> op.persistent(Codec.INT));
    public static final Supplier<DataComponentType<Boolean>> INFINITY_TRIDENT_CHANNELING = register("infinity_trident_channeling", () -> false, op -> op.persistent(Codec.BOOL));

    public static final Supplier<DataComponentType<CompoundTag>> MOB_IMPRISONMENT_TOOL = register("mob_imprisonment", CompoundTag::new, op -> op.persistent(CompoundTag.CODEC));

    public static final Supplier<DataComponentType<String>> ORE_FLUID_TAG = register("ore_fluid_nbt", () -> "", op -> op.persistent(Codec.STRING));

    public static final Supplier<DataComponentType<CompoundTag>> SETTINGS_COPIER = register("settings_copier", CompoundTag::new, op -> op.persistent(CompoundTag.CODEC));


    private static <T> ComponentSupplier<T> register(String name, Supplier<T> defaultVal, UnaryOperator<DataComponentType.Builder<T>> op) {
        var registered = DR.register(name, () -> op.apply(DataComponentType.builder()).build());
        return new ComponentSupplier<>(registered, defaultVal);
    }

    public static RegistryAccess registryAccess() {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            return ServerLifecycleHooks.getCurrentServer().registryAccess();
        }
        return LogicalSidedProvider.CLIENTWORLD.get(LogicalSide.CLIENT).orElseThrow().registryAccess();
    }

    public static class ComponentSupplier<T> implements Supplier<DataComponentType<T>> {
        private final Supplier<DataComponentType<T>> type;
        private final Supplier<T> defaultSupplier;

        public ComponentSupplier(Supplier<DataComponentType<T>> type, Supplier<T> defaultSupplier) {
            this.type = type;
            this.defaultSupplier = Suppliers.memoize(defaultSupplier::get);
        }

        public T get(ItemStack stack) {
            return stack.getOrDefault(type, defaultSupplier.get());
        }

        @Override
        public DataComponentType<T> get() {
            return type.get();
        }
    }
}

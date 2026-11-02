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


import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.item.infinity.InfinityTier;
import com.buuz135.industrial.item.infinity.ItemInfinity;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.proxy.event.InfinityDrillEventHandler;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.tab.TitaniumTab;
import com.hrznstudio.titanium.util.RayTraceUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ItemInfinityDrill extends ItemInfinity {

    public static int POWER_CONSUMPTION = 10000;
    public static int FUEL_CONSUMPTION = 30;

    /** Set while the drill breaks its own area, so the nested mineBlock calls don't expand again. */
    private static final ThreadLocal<Boolean> AREA_BREAKING = ThreadLocal.withInitial(() -> false);

    public ItemInfinityDrill(TitaniumTab group) {
        //.addToolType(ToolType.PICKAXE, 6).addToolType(ToolType.SHOVEL, 6)
        super("infinity_drill", group, new Properties().stacksTo(1), POWER_CONSUMPTION, FUEL_CONSUMPTION, false);
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        boolean netheriteInfinityTier = isNetheriteInfinityTier(stack);
        Item pickaxe = netheriteInfinityTier ? Items.NETHERITE_PICKAXE : Items.DIAMOND_PICKAXE;
        Item shovel = netheriteInfinityTier ? Items.NETHERITE_SHOVEL : Items.DIAMOND_SHOVEL;
        ItemStack pickaxeItemStack = new ItemStack(pickaxe);
        ItemStack shovelItemStack = new ItemStack(shovel);
        return pickaxe.supportsEnchantment(pickaxeItemStack, enchantment) || shovel.supportsEnchantment(shovelItemStack, enchantment);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState blockIn) {
        if (!isDrillMineable(blockIn)) {
            return false;
        }
        Tier tier = isNetheriteInfinityTier(stack) ? Tiers.NETHERITE : Tiers.DIAMOND;
        return !blockIn.is(tier.getIncorrectBlocksForDrops());
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        // Lets other mods (e.g. Apotheosis' "breaker" loot category) recognise the drill as a pickaxe and shovel.
        return itemAbility == ItemAbilities.PICKAXE_DIG || itemAbility == ItemAbilities.SHOVEL_DIG;
    }

    /**
     * Called for every block broken with the drill, whoever triggered the break:
     * <ul>
     *     <li>vanilla for the block the player mined</li>
     *     <li>drill's own area loop</li>
     *     <li>another mod's area mining (Apotheosis radial, FTB Ultimine, ...)</li>
     * </ul>
     * </br>
     * Only the player's direct break expands into the drill area.
     * </br>
     * Never called in creative mode, see {@link InfinityDrillEventHandler#onBreakEventEnd}.
     */
    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            if (isDrillMineable(state) && enoughFuel(stack)) {
                consumeFuel(stack);
            }
            if (shouldMineArea(player)) {
                mineArea(stack, level, pos, player);
            }
        }
        return super.mineBlock(stack, level, state, pos, entity);
    }

    /** Shared by survival and creative. Creative players do not need fuel. */
    public void mineArea(ItemStack stack, Level level, BlockPos origin, ServerPlayer player) {
        boolean creative = player.isCreative();
        HitResult rayTraceResult = RayTraceUtils.rayTraceSimple(level, player, 16, 0);
        if (rayTraceResult.getType() != HitResult.Type.BLOCK || !(rayTraceResult instanceof BlockHitResult blockResult)) {
            return;
        }
        Pair<BlockPos, BlockPos> area = getArea(origin, blockResult.getDirection(), getSelectedTier(stack), true);
        AREA_BREAKING.set(true);
        try {
            Runnable restoreRadialMining = ApotheosisRadialCompat.suppressRadialMining(player);
            try {
                for (BlockPos target : BlockPos.betweenClosed(area.getLeft(), area.getRight())) {
                    if (!creative && !enoughFuel(stack)) break;
                    if (target.equals(origin)) continue; // vanilla removes the origin itself afterwards
                    BlockState targetState = level.getBlockState(target);
                    if (targetState.isAir() || !isDrillMineable(targetState)) continue;
                    if (level.getBlockEntity(target) != null || targetState.getDestroySpeed(level, target) < 0) continue;
                    // A full player break: BreakEvent (protection, affixes), harvest checks, drops, XP, stats, mineBlock.
                    // In creative only the BreakEvent and the removal: no drops, no mineBlock.
                    player.gameMode.destroyBlock(target.immutable());
                }
            } finally {
                restoreRadialMining.run();
            }
        } finally {
            AREA_BREAKING.set(false);
        }
    }

    /** Survival only: by the time mineBlock runs, the player's own BreakEvent has already finished. */
    private static boolean shouldMineArea(ServerPlayer player) {
        // Nested call for a block the drill itself is breaking as part of its area.
        if (AREA_BREAKING.get()) return false;
        // Called from inside another mod's BreakEvent handler: that mod is doing its own area mining.
        // If it left the player's break alone (Apotheosis radial), the drill area still runs from the origin afterwards.
        // If it cancelled the break and took over (FTB Ultimine), the drill steps aside.
        return !InfinityDrillEventHandler.isInsideBreakEvent(player);
    }

    private static boolean isDrillMineable(BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL);
    }

    @Override
    public void registerRecipe(RecipeOutput consumer) {
        DissolutionChamberRecipe.createRecipe(consumer, "infinity_drill", new DissolutionChamberRecipe(List.of(
                Ingredient.of(new ItemStack(Items.DIAMOND_BLOCK)),
                Ingredient.of(new ItemStack(Items.DIAMOND_SHOVEL)),
                Ingredient.of(new ItemStack(Items.DIAMOND_BLOCK)),
                Ingredient.of(new ItemStack(Items.DIAMOND_BLOCK)),
                Ingredient.of(new ItemStack(ModuleCore.RANGE_ADDONS[11].get())),
                Ingredient.of(IndustrialTags.Items.GEAR_GOLD),
                Ingredient.of(IndustrialTags.Items.GEAR_GOLD),
                Ingredient.of(IndustrialTags.Items.GEAR_GOLD)
        ), new FluidStack(ModuleCore.PINK_SLIME.getSourceFluid().get(), 2000), 400, Optional.of(new ItemStack(this)), Optional.empty()));

    }

    private static boolean isNetheriteInfinityTier(ItemStack stack) {
        return getSelectedTier(stack).compareTo(InfinityTier.LEGENDARY) >= 0;
    }

//    public void configuration(Configuration config) {TODO
//        int i = 0;
//        for (DrillTier value : DrillTier.values()) {
//            value.setPowerNeeded(Long.parseLong(config.getString(i + "_" + value.name, Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "infinity_drill" + Configuration.CATEGORY_SPLITTER + "power_values", value.powerNeeded + "", "")));
//            value.setRadius(config.getInt(i + "_" + value.name, Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "infinity_drill" + Configuration.CATEGORY_SPLITTER + "radius", value.radius, 0, Integer.MAX_VALUE, ""));
//            ++i;
//        }
//    }

    /**
     * Keeps Apotheosis' radial mining affix from expanding again around blocks the Infinity Drill breaks as part of its area.
     * <p>
     * The drill switches the player's radial mining mode (the {@code apotheosis:radial_mining_mode} player attachment, an
     * enum) to DISABLED for its operation and restores it afterwards. It's looked up by name so no compile-time dependency
     * on Apotheosis is needed. Older Apotheosis releases (8.2.x and earlier) stored the mode differently and aren't
     * supported; with those a warning is logged and radial mining isn't suppressed.
     */
    private static final class ApotheosisRadialCompat {

        private static final ResourceLocation RADIAL_MINING_MODE = ResourceLocation.fromNamespaceAndPath("apotheosis", "radial_mining_mode");
        private static final String DISABLED = "DISABLED";
        private static final Runnable NO_OP = () -> {};

        /**
         * Looked up once when this class is initialised, which happens on the first drill area operation, long after
         * registries are frozen. Null when Apotheosis isn't installed, its radial mining mode isn't recognised, or
         * the DISABLED constant couldn't be resolved.
         * </br>
         * Server thread only.
         */
        private static @Nullable AttachmentType<Object> type = detectType();
        /**
         * The attachment's DISABLED constant.
         * </br>
         * Resolved from the first player value seen, because NeoForge doesn't
         * expose an attachment's default value. After that the lookup never runs again.
         * </br>
         * Server thread only.
         */
        private static @Nullable Object disabled;

        @SuppressWarnings("unchecked")
        private static @Nullable AttachmentType<Object> detectType() {
            if (!ModList.get().isLoaded("apotheosis")) return null;
            AttachmentType<?> attachmentType = NeoForgeRegistries.ATTACHMENT_TYPES.get(RADIAL_MINING_MODE);
            if (attachmentType == null) {
                warnUnsupported("the attachment isn't registered");
                return null;
            }
            // Safe: values are only ever read from and written back to this same attachment.
            return (AttachmentType<Object>) attachmentType;
        }

        /**
         * Disables Apotheosis radial mining for the player.
         *
         * @return an action that will restore the previous mining mode; will be a no-op if Apotheosis isn't installed
         * or its radial mining mode doesn't look as expected
         */
        public static Runnable suppressRadialMining(Player player) {
            // Local copy: the restore action must keep working even if the field is cleared later.
            AttachmentType<Object> type = ApotheosisRadialCompat.type;
            if (type == null) return NO_OP;
            boolean hadData = player.hasData(type);
            Object previous = player.getData(type);
            if (disabled == null && (disabled = resolveDisabled(previous)) == null) {
                ApotheosisRadialCompat.type = null; // Unsupported, already warned; makes every later call a no-op
                return NO_OP;
            }
            player.setData(type, disabled);
            return () -> {
                if (hadData) {
                    player.setData(type, previous);
                } else {
                    player.removeData(type); // getData attached the default; don't leave it behind
                }
            };
        }

        /** @return the DISABLED constant of the mode's enum, or null (with a warning) if it has none */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static @Nullable Object resolveDisabled(Object value) {
            if (value instanceof Enum<?> mode) {
                try {
                    return Enum.valueOf((Class) mode.getDeclaringClass(), DISABLED);
                } catch (IllegalArgumentException ignored) {
                    // Falls through to the warning
                }
            }
            warnUnsupported("it holds " + value);
            return null;
        }

        // Without a warning a future Apotheosis change would silently bring back radial mining around every drill block.
        private static void warnUnsupported(String detail) {
            IndustrialForegoing.LOGGER.warn("Unrecognised Apotheosis radial mining mode {} ({}); the Infinity Drill can't suppress radial mining around its area", RADIAL_MINING_MODE, detail);
        }

        private ApotheosisRadialCompat() {
        }
    }
}

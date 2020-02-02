/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
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


import com.buuz135.industrial.item.IFCustomItem;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.proxy.CommonProxy;
import com.google.common.collect.Multimap;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import com.hrznstudio.titanium.util.RayTraceUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Consumer;

public class ItemInfinityDrill extends IFCustomItem {

    public static Material[] mineableMaterials = {Material.ANVIL, Material.CLAY, Material.GLASS, Material.ICE, Material.IRON, Material.PACKED_ICE, Material.PISTON, Material.ROCK, Material.SAND, Material.SNOW};
    public static int POWER_CONSUMPTION = 10000;
    public static int FUEL_CONSUMPTION = 3;

    public ItemInfinityDrill(ItemGroup group) {
        super("infinity_drill", group, new Properties().maxStackSize(1).addToolType(ToolType.PICKAXE, 3).addToolType(ToolType.SHOVEL, 3));
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn) {
        super.onCreated(stack, worldIn, playerIn);
        addNbt(stack, 0, 0, CommonProxy.CONTRIBUTORS.contains(playerIn.getUniqueID().toString()));
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemEnchantability() {
        return 2;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.type == EnchantmentType.DIGGER;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (isInGroup(group)) {
            for (DrillTier value : DrillTier.values()) {
                items.add(createStack(value.getPowerNeeded(), 0, false));
            }
            items.add(createStack(DrillTier.ARTIFACT.getPowerNeeded(), 1_000_000, true));
        }
    }


    @Override
    public UseAction getUseAction(ItemStack p_77661_1_) {
        return UseAction.BOW;
    }

    @Override
    public boolean canHarvestBlock(BlockState blockIn) {
        return Items.DIAMOND_PICKAXE.canHarvestBlock(blockIn) || Items.DIAMOND_SHOVEL.canHarvestBlock(blockIn);
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

    private boolean isValidMaterial(Material material) {
        for (Material mineableMaterial : mineableMaterials) {
            if (mineableMaterial.equals(material)) return true;
        }
        return false;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new InfinityDrillCapabilityProvider(stack);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        if (Screen.hasShiftDown()) {
            int fuel = getFuelFromStack(stack);
            return 1 - fuel / 1_000_000D;
        } else {
            long power = getPowerFromStack(stack);
            return 1 - power / (double) DrillTier.getTierBraquet(power).getRight().getPowerNeeded();
        }
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return Screen.hasShiftDown() ? 0xcb00ff /*Purple*/ : 0x00d0ff /*Cyan*/;
    }

    @Override
    public boolean hasTooltipDetails(@Nullable Key key) {
        return key == null;
    }

    @Override
    public void addTooltipDetails(@Nullable Key key, ItemStack stack, List<ITextComponent> tooltip, boolean advanced) {
        long power = getPowerFromStack(stack);
        Pair<DrillTier, DrillTier> braquet = DrillTier.getTierBraquet(power);
        DrillTier current = getSelectedDrillTier(stack);
        tooltip.add(new TranslationTextComponent("text.industrialforegoing.display.current_area").appendText(" ").appendText(getFormattedArea(current, current.getRadius())).setStyle(new Style().setColor(TextFormatting.GRAY)));
        tooltip.add(new TranslationTextComponent("text.industrialforegoing.display.tier").appendText(" ").appendText(braquet.getLeft().getColor() + braquet.getLeft().getLocalizedName()).setStyle(new Style().setColor(TextFormatting.GRAY)));
        tooltip.add(new TranslationTextComponent("text.industrialforegoing.display.power").appendText(" ").appendText(new DecimalFormat().format(power)).appendText("/").appendText(new DecimalFormat().format(braquet.getRight().getPowerNeeded())).appendText("RF ").appendSibling(new TranslationTextComponent("text.industrialforegoing.display.next_tier")).setStyle(new Style().setColor(TextFormatting.GRAY)));
        int fuelAmount = getFuelFromStack(stack);
        tooltip.add(new TranslationTextComponent("text.industrialforegoing.display.fluid").appendText(" ").appendText(new DecimalFormat().format(fuelAmount)).appendText("/").appendText(new DecimalFormat().format(1000000)).appendText(" mb of Biofuel").setStyle(new Style().setColor(TextFormatting.GRAY)));
        tooltip.add(new TranslationTextComponent("text.industrialforegoing.display.max_area").appendText(" ").appendText(getFormattedArea(braquet.getLeft(), braquet.getLeft().getRadius())).setStyle(new Style().setColor(TextFormatting.GRAY)));
        if (isSpecial(stack))
            tooltip.add(new TranslationTextComponent("text.industrialforegoing.display.special").setStyle(new Style().setColor(TextFormatting.GRAY)));
    }

    public long getPowerFromStack(ItemStack stack) {
        long power = 0;
        if (stack.hasTag() && stack.getTag().contains("Energy")) {
            power = stack.getTag().getLong("Energy");
        }
        return power;
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

    public ItemStack createStack(long power, int fuel, boolean special) {
        ItemStack stack = new ItemStack(this);
        addNbt(stack, power, fuel, special);
        return stack;
    }

    public void addNbt(ItemStack stack, long power, int fuel, boolean special) {
        CompoundNBT tagCompound = new CompoundNBT();
        tagCompound.putLong("Energy", power);
        CompoundNBT fluid = new CompoundNBT();
        fluid.putString("FluidName", "biofuel");
        fluid.putInt("Amount", fuel);
        tagCompound.put("Fluid", fluid);
        tagCompound.putBoolean("Special", special);
        tagCompound.putString("Selected", DrillTier.getTierBraquet(power).getLeft().name());
        stack.setTag(tagCompound);
    }

    private String getFormattedArea(DrillTier tier, int radius) {
        int diameter = radius * 2 + 1;
        return diameter + "x" + diameter + "x" + (tier == DrillTier.ARTIFACT ? diameter : 1);
    }

    private boolean enoughFuel(ItemStack stack) {
        return getFuelFromStack(stack) >= FUEL_CONSUMPTION || getPowerFromStack(stack) >= POWER_CONSUMPTION;
    }

    private void consumeFuel(ItemStack stack) {
        if (getFuelFromStack(stack) >= FUEL_CONSUMPTION) {
            stack.getTag().getCompound("Fluid").putInt("Amount", Math.max(0, stack.getTag().getCompound("Fluid").getInt("Amount") - FUEL_CONSUMPTION));
        } else {
            stack.getTag().putLong("Energy", stack.getTag().getLong("Energy") - POWER_CONSUMPTION);
        }
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (entityLiving instanceof PlayerEntity) {
            RayTraceResult rayTraceResult = RayTraceUtils.rayTraceSimple(worldIn, entityLiving, 16, 0);
            if (rayTraceResult.getType() == RayTraceResult.Type.BLOCK) {
                BlockRayTraceResult blockResult = (BlockRayTraceResult) rayTraceResult;
                Direction facing = blockResult.getFace();
                DrillTier currentTier = getSelectedDrillTier(stack);
                Pair<BlockPos, BlockPos> area = getArea(pos, facing, currentTier, true);
                BlockPos.getAllInBoxMutable(area.getLeft(), area.getRight()).forEach(blockPos -> {
                    if (enoughFuel(stack) && worldIn.getTileEntity(blockPos) == null && entityLiving instanceof ServerPlayerEntity && !worldIn.isAirBlock(blockPos)) {
                        BlockState tempState = worldIn.getBlockState(blockPos);
                        Block block = tempState.getBlock();
                        if (block.getBlockHardness(tempState, worldIn, blockPos) < 0) return;
                        int xp = ForgeHooks.onBlockBreakEvent(worldIn, ((ServerPlayerEntity) entityLiving).interactionManager.getGameType(), (ServerPlayerEntity) entityLiving, blockPos);
                        if (xp >= 0 && block.removedByPlayer(tempState, worldIn, blockPos, (PlayerEntity) entityLiving, true, tempState.getFluidState())) {
                            block.onPlayerDestroy(worldIn, blockPos, tempState);
                            block.harvestBlock(worldIn, (PlayerEntity) entityLiving, blockPos, tempState, null, stack);
                            block.dropXpOnBlockBreak(worldIn, blockPos, xp);
                            consumeFuel(stack);
                        }
                    }
                });
                worldIn.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(area.getLeft(), area.getRight()).grow(1)).forEach(ItemEntity -> {
                    ItemEntity.setPositionAndUpdate(entityLiving.getPosition().getX(), entityLiving.getPosition().getY(), entityLiving.getPosition().getZ());
                    ItemEntity.setPickupDelay(0);
                });
                worldIn.getEntitiesWithinAABB(ExperienceOrbEntity.class, new AxisAlignedBB(area.getLeft(), area.getRight()).grow(1)).forEach(entityXPOrb -> entityXPOrb.setPositionAndUpdate(entityLiving.getPosition().getX(), entityLiving.getPosition().getY(), entityLiving.getPosition().getZ()));
            }
        }
        return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
    }

    public DrillTier getSelectedDrillTier(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains("Selected") ? DrillTier.valueOf(stack.getTag().getString("Selected")) : DrillTier.getTierBraquet(getPowerFromStack(stack)).getLeft();
    }

    public void setSelectedDrillTier(ItemStack stack, DrillTier tier) {
        stack.getTag().putString("Selected", tier.name());
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity player, Hand handIn) {
        if (player.isCrouching()) {
            player.playSound(SoundEvents.BLOCK_LEVER_CLICK, 0.5f, 0.5f);
            ItemStack stack = player.getHeldItem(handIn);
            DrillTier next = getSelectedDrillTier(stack).getNext(DrillTier.getTierBraquet(getPowerFromStack(stack)).getLeft());
            player.sendStatusMessage(new TranslationTextComponent("text.industrialforegoing.display.current_area").appendText(" ").appendText(getFormattedArea(next, next.getRadius())), true);
            setSelectedDrillTier(stack, next);
        }
        return super.onItemRightClick(worldIn, player, handIn);
    }

    public Pair<BlockPos, BlockPos> getArea(BlockPos pos, Direction facing, DrillTier currentTier, boolean withDepth) {
        int radius = currentTier.radius;
        BlockPos bottomLeft = pos.offset(facing.getAxis() == Direction.Axis.Y ? Direction.SOUTH : Direction.DOWN, radius).offset(facing.getAxis() == Direction.Axis.Y ? Direction.WEST : facing.rotateYCCW(), radius);
        BlockPos topRight = pos.offset(facing.getAxis() == Direction.Axis.Y ? Direction.NORTH : Direction.UP, radius).offset(facing.getAxis() == Direction.Axis.Y ? Direction.EAST : facing.rotateY(), radius);
        if (facing.getAxis() != Direction.Axis.Y && radius > 0) {
            bottomLeft = bottomLeft.offset(Direction.UP, radius - 1);
            topRight = topRight.offset(Direction.UP, radius - 1);
        }
        if (currentTier == DrillTier.ARTIFACT && withDepth) {
            topRight = topRight.offset(facing.getOpposite(), radius);
        }
        return Pair.of(bottomLeft, topRight);
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
        if (slot == EquipmentSlotType.MAINHAND) {
            DrillTier drillTier = DrillTier.getTierBraquet(getPowerFromStack(stack)).getLeft();
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", 2 + drillTier.getRadius(), AttributeModifier.Operation.ADDITION));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -2.5D, AttributeModifier.Operation.ADDITION));
        }
        return multimap;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine(" DD").patternLine(" ID").patternLine("I  ")
                .key('D', Blocks.DIAMOND_BLOCK)
                .key('I', Blocks.IRON_BLOCK)
                .build(consumer);
    }

//    public void configuration(Configuration config) {TODO
//        int i = 0;
//        for (DrillTier value : DrillTier.values()) {
//            value.setPowerNeeded(Long.parseLong(config.getString(i + "_" + value.name, Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "infinity_drill" + Configuration.CATEGORY_SPLITTER + "power_values", value.powerNeeded + "", "")));
//            value.setRadius(config.getInt(i + "_" + value.name, Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "infinity_drill" + Configuration.CATEGORY_SPLITTER + "radius", value.radius, 0, Integer.MAX_VALUE, ""));
//            ++i;
//        }
//    }

    public enum DrillTier {
        POOR("poor", 0, 0, TextFormatting.GRAY, 0x7c7c7a),//1x1
        COMMON("common", 4_000_000, 1, TextFormatting.WHITE, 0xFFFFFF), //3x3
        UNCOMMON("uncommon", 16_000_000, 2, TextFormatting.GREEN, 0x1ce819), //5x5
        RARE("rare", 80_000_000, 3, TextFormatting.BLUE, 0x0087ff), //7x7
        EPIC("epic", 480_000_000, 4, TextFormatting.DARK_PURPLE, 0xe100ff), //9x9
        LEGENDARY("legendary", 3_360_000_000L, 5, TextFormatting.GOLD, 0xffaa00), //11x11
        ARTIFACT("artifact", Long.MAX_VALUE, 6, TextFormatting.YELLOW, 0xfff887); //13x13

        private final String name;
        private final TextFormatting color;
        private final int textureColor;
        private long powerNeeded;
        private int radius;

        DrillTier(String name, long powerNeeded, int radius, TextFormatting color, int textureColor) {
            this.name = name;
            this.powerNeeded = powerNeeded;
            this.radius = radius;
            this.color = color;
            this.textureColor = textureColor;
        }

        public static Pair<DrillTier, DrillTier> getTierBraquet(long power) {
            DrillTier lastTier = POOR;
            for (DrillTier drillTier : DrillTier.values()) {
                if (power >= lastTier.getPowerNeeded() && power < drillTier.getPowerNeeded())
                    return Pair.of(lastTier, drillTier);
                lastTier = drillTier;
            }
            return Pair.of(ARTIFACT, ARTIFACT);
        }

        public String getLocalizedName() {
            return new TranslationTextComponent("text.industrialforegoing.tooltip.infinitydrill." + name).getUnformattedComponentText();
        }

        public String getName() {
            return name;
        }

        public long getPowerNeeded() {
            return powerNeeded;
        }

        public void setPowerNeeded(long powerNeeded) {
            this.powerNeeded = powerNeeded;
        }

        public int getRadius() {
            return radius;
        }

        public void setRadius(int radius) {
            this.radius = radius;
        }

        public TextFormatting getColor() {
            return color;
        }

        public int getTextureColor() {
            return textureColor;
        }

        public DrillTier getNext(DrillTier maxTier) {
            DrillTier lastTier = POOR;
            for (DrillTier drillTier : DrillTier.values()) {
                if (drillTier == POOR) continue;
                if (lastTier == maxTier) return POOR;
                if (this == lastTier) return drillTier;
                lastTier = drillTier;
            }
            return DrillTier.POOR;
        }
    }

    private class InfinityDrillCapabilityProvider implements ICapabilityProvider {

        private final FluidHandlerItemStack tank;
        private final InfinityDrillEnergyStorage energyStorage;
        private final LazyOptional<IEnergyStorage> energyStorageCap;
        private final LazyOptional<IFluidHandlerItem> tankCap;

        private InfinityDrillCapabilityProvider(ItemStack stack) {
            tank = new FluidHandlerItemStack(stack, 1_000_000) {
                @Override
                public boolean canFillFluidType(FluidStack fluid) {
                    return fluid != null && fluid.getFluid() != null && fluid.getFluid().equals(ModuleCore.BIOFUEL.getSourceFluid());
                }

                @Override
                public boolean canDrainFluidType(FluidStack fluid) {
                    return false;
                }
            };
            energyStorage = new InfinityDrillEnergyStorage() {
                @Override
                public long getLongEnergyStored() {
                    if (stack.hasTag()) {
                        return Math.min(stack.getTag().getLong("Energy"), DrillTier.ARTIFACT.getPowerNeeded());
                    } else {
                        return 0;
                    }
                }

                @Override
                public void setEnergyStored(long energy) {
                    if (!stack.hasTag()) {
                        stack.setTag(new CompoundNBT());
                    }
                    stack.getTag().putLong("Energy", Math.min(energy, DrillTier.ARTIFACT.getPowerNeeded()));
                }
            };
            this.tankCap = LazyOptional.of(() -> tank);
            this.energyStorageCap = LazyOptional.of(() -> energyStorage);
        }


        @Nullable
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) return tankCap.cast();
            if (capability == CapabilityEnergy.ENERGY) return energyStorageCap.cast();
            return LazyOptional.empty();
        }
    }

    private class InfinityDrillEnergyStorage implements IEnergyStorage {

        private final long capacity;
        private long energy;

        public InfinityDrillEnergyStorage() {
            this.energy = 0;
            this.capacity = DrillTier.ARTIFACT.getPowerNeeded();
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            long stored = getLongEnergyStored();
            int energyReceived = (int) Math.min(capacity - stored, Math.min(Long.MAX_VALUE, maxReceive));
            if (!simulate)
                setEnergyStored(stored + energyReceived);
            return energyReceived;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return 0;
        }

        @Override
        public int getEnergyStored() {
            return (int) energy;
        }

        public void setEnergyStored(long power) {
            this.energy = power;
        }

        @Override
        public int getMaxEnergyStored() {
            return (int) capacity;
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return true;
        }

        public long getLongEnergyStored() {
            return this.energy;
        }
    }
}

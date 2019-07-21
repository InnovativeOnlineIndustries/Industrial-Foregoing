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
import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.proxy.CommonProxy;
import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.utils.RayTraceUtils;
import com.buuz135.industrial.utils.RecipeUtils;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

public class ItemInfinityDrill extends IFCustomItem {

    public static Material[] mineableMaterials = {Material.ANVIL, Material.CLAY, Material.GLASS, Material.GRASS, Material.GROUND, Material.ICE, Material.IRON, Material.PACKED_ICE, Material.PISTON, Material.ROCK, Material.SAND, Material.SNOW};
    public static int POWER_CONSUMPTION = 10000;
    public static int FUEL_CONSUMPTION = 3;

    public ItemInfinityDrill() {
        super("infinity_drill");
        setMaxStackSize(1);
        setHasSubtypes(true);
        setHarvestLevel("pickaxe", Integer.MAX_VALUE);
        setHarvestLevel("shovel", Integer.MAX_VALUE);
    }

    @Override
    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), " ID", "PRI", "PT ",
                'I', Blocks.IRON_BLOCK,
                'D', Blocks.DIAMOND_BLOCK,
                'P', ItemRegistry.pinkSlimeIngot,
                'R', BlockRegistry.laserDrillBlock,
                'T', BlockRegistry.blackHoleTankBlock
        );
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
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
        return enchantment.type == EnumEnchantmentType.DIGGER;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            for (DrillTier value : DrillTier.values()) {
                items.add(createStack(value.getPowerNeeded(), 0, false));
            }
            items.add(createStack(DrillTier.ARTIFACT.getPowerNeeded(), 1_000_000, true));
        }
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public Set<String> getToolClasses(ItemStack stack) {
        return ImmutableSet.of("pickaxe", "shovel");
    }

    @Override
    public boolean canHarvestBlock(IBlockState blockIn) {
        return Items.DIAMOND_PICKAXE.canHarvestBlock(blockIn) || Items.DIAMOND_SHOVEL.canHarvestBlock(blockIn);
    }

    @Override
    public boolean isFull3D() {
        return true;
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
        return true;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged && !oldStack.equals(newStack);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 720000;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        return enoughFuel(stack) ? 10F : 0;
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return !oldStack.isItemEqual(newStack);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new InfinityDrillCapabilityProvider(stack);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        if (GuiScreen.isShiftKeyDown()) {
            int fuel = getFuelFromStack(stack);
            return 1 - fuel / 1_000_000D;
        } else {
            long power = getPowerFromStack(stack);
            return 1 - power / (double) DrillTier.getTierBraquet(power).getRight().getPowerNeeded();
        }
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return GuiScreen.isShiftKeyDown() ? 0xcb00ff /*Purple*/ : 0x00d0ff /*Cyan*/;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        long power = getPowerFromStack(stack);
        Pair<DrillTier, DrillTier> braquet = DrillTier.getTierBraquet(power);
        DrillTier current = getSelectedDrillTier(stack);
        tooltip.add(new TextComponentTranslation("text.industrialforegoing.display.current_area").getUnformattedText() + " " + getFormattedArea(current, current.getRadius()));
        tooltip.add(new TextComponentTranslation("text.industrialforegoing.display.tier").getUnformattedText() + " " + braquet.getLeft().getColor() + braquet.getLeft().getLocalizedName());
        tooltip.add(new TextComponentTranslation("text.industrialforegoing.display.power").getUnformattedText() + " " + new DecimalFormat().format(power) + "/" + new DecimalFormat().format(braquet.getRight().getPowerNeeded()) + "RF " + new TextComponentTranslation("text.industrialforegoing.display.next_tier").getUnformattedText());
        int fuelAmount = getFuelFromStack(stack);
        tooltip.add(new TextComponentTranslation("text.industrialforegoing.display.fluid").getUnformattedText() + " " + new DecimalFormat().format(fuelAmount) + "/" + new DecimalFormat().format(1000000) + " mb " + new TextComponentTranslation(FluidsRegistry.BIOFUEL.getUnlocalizedName()).getUnformattedComponentText());
        tooltip.add(new TextComponentTranslation("text.industrialforegoing.display.max_area").getUnformattedText() + " " + getFormattedArea(braquet.getLeft(), braquet.getLeft().getRadius()));
        if (isSpecial(stack))
            tooltip.add(new TextComponentTranslation("text.industrialforegoing.display.special").getFormattedText());
    }

    public long getPowerFromStack(ItemStack stack) {
        long power = 0;
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Energy")) {
            power = stack.getTagCompound().getLong("Energy");
        }
        return power;
    }

    public int getFuelFromStack(ItemStack stack) {
        int fuelAmount = 0;
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Fluid") && stack.getTagCompound().getCompoundTag("Fluid").hasKey("Amount")) {
            fuelAmount = stack.getTagCompound().getCompoundTag("Fluid").getInteger("Amount");
        }
        return fuelAmount;
    }

    public boolean isSpecial(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey("Special") && stack.getTagCompound().getBoolean("Special");
    }

    public ItemStack createStack(long power, int fuel, boolean special) {
        ItemStack stack = new ItemStack(this);
        addNbt(stack, power, fuel, special);
        return stack;
    }

    public void addNbt(ItemStack stack, long power, int fuel, boolean special) {
        NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.setLong("Energy", power);
        NBTTagCompound fluid = new NBTTagCompound();
        fluid.setString("FluidName", "biofuel");
        fluid.setInteger("Amount", fuel);
        tagCompound.setTag("Fluid", fluid);
        tagCompound.setBoolean("Special", special);
        tagCompound.setString("Selected", DrillTier.getTierBraquet(power).getLeft().name());
        stack.setTagCompound(tagCompound);
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
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Fluid") && stack.getTagCompound().getCompoundTag("Fluid").hasKey("Amount")) {
                stack.getTagCompound().getCompoundTag("Fluid").setInteger("Amount", getFuelFromStack(stack) - FUEL_CONSUMPTION);
            }
        } else {
            stack.getTagCompound().setLong("Energy", stack.getTagCompound().getLong("Energy") - POWER_CONSUMPTION);
        }
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        if (entityLiving instanceof EntityPlayer) {
            RayTraceResult rayTraceResult = RayTraceUtils.rayTraceSimple(worldIn, entityLiving, 16, 0);
            EnumFacing facing = rayTraceResult.sideHit;
            DrillTier currentTier = getSelectedDrillTier(stack);
            Pair<BlockPos, BlockPos> area = getArea(pos, facing, currentTier, true);
            BlockPos.getAllInBox(area.getLeft(), area.getRight()).forEach(blockPos -> {
                if (enoughFuel(stack) && worldIn.getTileEntity(blockPos) == null && entityLiving instanceof EntityPlayerMP && !worldIn.isAirBlock(blockPos)) {
                    IBlockState tempState = worldIn.getBlockState(blockPos);
                    Block block = tempState.getBlock();
                    if (block.getBlockHardness(tempState, worldIn, blockPos) < 0) return;
                    int xp = ForgeHooks.onBlockBreakEvent(worldIn, ((EntityPlayerMP) entityLiving).interactionManager.getGameType(), (EntityPlayerMP) entityLiving, blockPos);
                    if (xp >= 0 && block.removedByPlayer(tempState, worldIn, blockPos, (EntityPlayer) entityLiving, true)) {
                        block.onPlayerDestroy(worldIn, blockPos, tempState);
                        block.harvestBlock(worldIn, (EntityPlayer) entityLiving, blockPos, tempState, null, stack);
                        block.dropXpOnBlockBreak(worldIn, blockPos, xp);
                        consumeFuel(stack);
                    }
                }
            });
            worldIn.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(area.getLeft(), area.getRight()).grow(1)).forEach(entityItem -> {
                entityItem.setPositionAndUpdate(entityLiving.posX, entityLiving.posY, entityLiving.posZ);
                entityItem.setPickupDelay(0);
            });
            worldIn.getEntitiesWithinAABB(EntityXPOrb.class, new AxisAlignedBB(area.getLeft(), area.getRight()).grow(1)).forEach(entityXPOrb -> entityXPOrb.setPositionAndUpdate(entityLiving.posX, entityLiving.posY, entityLiving.posZ));
        }
        return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
    }

    public DrillTier getSelectedDrillTier(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey("Selected") ? DrillTier.valueOf(stack.getTagCompound().getString("Selected")) : DrillTier.getTierBraquet(getPowerFromStack(stack)).getLeft();
    }

    public void setSelectedDrillTier(ItemStack stack, DrillTier tier) {
        stack.getTagCompound().setString("Selected", tier.name());
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand handIn) {
        if (player.isSneaking()) {
            player.playSound(SoundEvents.BLOCK_LEVER_CLICK, 0.5f, 0.5f);
            ItemStack stack = player.getHeldItem(handIn);
            DrillTier next = getSelectedDrillTier(stack).getNext(DrillTier.getTierBraquet(getPowerFromStack(stack)).getLeft());
            player.sendStatusMessage(new TextComponentString(new TextComponentTranslation("text.industrialforegoing.display.current_area").getUnformattedText() + " " + getFormattedArea(next, next.getRadius())), true);
            setSelectedDrillTier(stack, next);
        }
        return super.onItemRightClick(worldIn, player, handIn);
    }

    public Pair<BlockPos, BlockPos> getArea(BlockPos pos, EnumFacing facing, DrillTier currentTier, boolean withDepth) {
        int radius = currentTier.radius;
        BlockPos bottomLeft = pos.offset(facing.getAxis() == EnumFacing.Axis.Y ? EnumFacing.SOUTH : EnumFacing.DOWN, radius).offset(facing.getAxis() == EnumFacing.Axis.Y ? EnumFacing.WEST : facing.rotateYCCW(), radius);
        BlockPos topRight = pos.offset(facing.getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : EnumFacing.UP, radius).offset(facing.getAxis() == EnumFacing.Axis.Y ? EnumFacing.EAST : facing.rotateY(), radius);
        if (facing.getAxis() != EnumFacing.Axis.Y && radius > 0) {
            bottomLeft = bottomLeft.offset(EnumFacing.UP, radius - 1);
            topRight = topRight.offset(EnumFacing.UP, radius - 1);
        }
        if (currentTier == DrillTier.ARTIFACT && withDepth) {
            topRight = topRight.offset(facing.getOpposite(), radius);
        }
        return Pair.of(bottomLeft, topRight);
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
        if (slot == EntityEquipmentSlot.MAINHAND) {
            DrillTier drillTier = DrillTier.getTierBraquet(getPowerFromStack(stack)).getLeft();
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", 2 + drillTier.getRadius(), 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -2.5D, 0));
        }
        return multimap;
    }

    public void configuration(Configuration config) {
        int i = 0;
        for (DrillTier value : DrillTier.values()) {
            value.setPowerNeeded(Long.parseLong(config.getString(i + "_" + value.name, Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "infinity_drill" + Configuration.CATEGORY_SPLITTER + "power_values", value.powerNeeded + "", "")));
            value.setRadius(config.getInt(i + "_" + value.name, Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "infinity_drill" + Configuration.CATEGORY_SPLITTER + "radius", value.radius, 0, Integer.MAX_VALUE, ""));
            ++i;
        }
    }

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
            return new TextComponentTranslation("text.industrialforegoing.tooltip.infinitydrill." + name).getUnformattedText();
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

        private InfinityDrillCapabilityProvider(ItemStack stack) {
            tank = new FluidHandlerItemStack(stack, 1_000_000) {
                @Override
                public boolean canFillFluidType(FluidStack fluid) {
                    return fluid != null && fluid.getFluid() != null && fluid.getFluid().equals(FluidsRegistry.BIOFUEL);
                }

                @Override
                public boolean canDrainFluidType(FluidStack fluid) {
                    return false;
                }
            };
            energyStorage = new InfinityDrillEnergyStorage() {
                @Override
                public long getLongEnergyStored() {
                    if (stack.hasTagCompound()) {
                        return Math.min(stack.getTagCompound().getLong("Energy"), DrillTier.ARTIFACT.getPowerNeeded());
                    } else {
                        return 0;
                    }
                }

                @Override
                public void setEnergyStored(long energy) {
                    if (!stack.hasTagCompound()) {
                        stack.setTagCompound(new NBTTagCompound());
                    }
                    stack.getTagCompound().setLong("Energy", Math.min(energy, DrillTier.ARTIFACT.getPowerNeeded()));
                }
            };
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY || capability == CapabilityEnergy.ENERGY;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) return (T) tank;
            if (capability == CapabilityEnergy.ENERGY) return (T) energyStorage;
            return null;
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
            return capacity > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) capacity;
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

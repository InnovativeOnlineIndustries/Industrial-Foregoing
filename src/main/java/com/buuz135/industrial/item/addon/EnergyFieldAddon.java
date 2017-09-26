package com.buuz135.industrial.item.addon;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.CustomElectricMachine;
import com.buuz135.industrial.tile.block.EnergyFieldProviderBlock;
import com.buuz135.industrial.tile.world.EnergyFieldProviderTile;
import com.buuz135.industrial.utils.RecipeUtils;
import lombok.Getter;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.ndrei.teslacorelib.tileentities.SidedTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;


public class EnergyFieldAddon extends CustomAddon {

    @Getter
    private int maxPower;
    @Getter
    private int transfer;

    public EnergyFieldAddon() {
        super("energy_field_addon");
        this.maxPower = 100000;
        this.transfer = 1600;
        this.setMaxStackSize(1);
    }

    @Override
    public boolean canBeAddedTo(SidedTileEntity machine) {
        return machine instanceof CustomElectricMachine && ((CustomElectricMachine) machine).canAcceptEnergyFieldAddon() && !(machine instanceof EnergyFieldProviderTile);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new EnergyCapabilityProvider(stack, this);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
            IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
            tooltip.add("Charge: " + new DecimalFormat().format(storage.getEnergyStored()) + " / " + new DecimalFormat().format(storage.getMaxEnergyStored()));
            if (getLinkedBlockPos(stack) != null) {
                BlockPos pos = getLinkedBlockPos(stack);
                tooltip.add("Linked Pos: x=" + pos.getX() + " y=" + pos.getY() + " z=" + pos.getZ());
            } else {
                tooltip.add("Right Click into Energy Field Provider to link.");
            }
        }
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
            IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
            if (storage != null)
                return (storage.getMaxEnergyStored() - storage.getEnergyStored()) / (double) storage.getMaxEnergyStored();
        }
        return 0;
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        Color color = new Color(52428);
        return MathHelper.rgb(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
    }

    public BlockPos getLinkedBlockPos(ItemStack stack) {
        if (stack.hasTagCompound()) {
            return BlockPos.fromLong(stack.getTagCompound().getLong("BlockPos"));
        }
        return null;
    }

    public void setLinkedPos(BlockPos pos, ItemStack stack) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setLong("BlockPos", pos.toLong());
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.getBlockState(pos).getBlock() instanceof EnergyFieldProviderBlock) { //TODO
            setLinkedPos(pos, player.getHeldItem(hand));
            return EnumActionResult.SUCCESS;
        }
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "rpr", "pup", "rpr",
                'r', Items.REDSTONE,
                'p', ItemRegistry.pinkSlime,
                'u', new ItemStack(ItemRegistry.rangeAddonItem, 1, 9));
    }

    private static class EnergyCapabilityProvider implements ICapabilityProvider {

        private CustomEnergyStorage storage;

        public EnergyCapabilityProvider(ItemStack stack, EnergyFieldAddon addon) {
            this.storage = new CustomEnergyStorage(addon.maxPower, addon.transfer, addon.transfer) {
                @Override
                public int getEnergyStored() {
                    if (stack.hasTagCompound()) {
                        return stack.getTagCompound().getInteger("Energy");
                    } else {
                        return 0;
                    }
                }

                @Override
                public void setEnergyStored(int energy) {
                    if (!stack.hasTagCompound()) {
                        stack.setTagCompound(new NBTTagCompound());
                    }

                    stack.getTagCompound().setInteger("Energy", energy);
                }


            };
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return this.getCapability(capability, facing) != null;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == CapabilityEnergy.ENERGY) {
                return (T) this.storage;
            }
            return null;
        }
    }

    public static class CustomEnergyStorage extends EnergyStorage {

        public CustomEnergyStorage(int capacity, int maxReceive, int maxExtract) {
            super(capacity, maxReceive, maxExtract);
        }

        public int extractEnergyInternal(int maxExtract, boolean simulate) {
            int before = this.maxExtract;
            this.maxExtract = Integer.MAX_VALUE;

            int toReturn = this.extractEnergy(maxExtract, simulate);

            this.maxExtract = before;
            return toReturn;
        }

        public int receiveEnergyInternal(int maxReceive, boolean simulate) {
            int before = this.maxReceive;
            this.maxReceive = Integer.MAX_VALUE;

            int toReturn = this.receiveEnergy(maxReceive, simulate);

            this.maxReceive = before;
            return toReturn;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (!this.canReceive()) {
                return 0;
            }
            int energy = this.getEnergyStored();

            int energyReceived = Math.min(this.getMaxEnergyStored() - energy, Math.min(this.maxReceive, maxReceive));
            if (!simulate) {
                this.setEnergyStored(energy + energyReceived);
            }

            return energyReceived;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if (!this.canExtract()) {
                return 0;
            }
            int energy = this.getEnergyStored();

            int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
            if (!simulate) {
                this.setEnergyStored(energy - energyExtracted);
            }
            return energyExtracted;
        }

        public void readFromNBT(NBTTagCompound compound) {
            this.setEnergyStored(compound.getInteger("Energy"));
        }

        public void writeToNBT(NBTTagCompound compound) {
            compound.setInteger("Energy", this.getEnergyStored());
        }

        public void setEnergyStored(int energy) {
            this.energy = energy;
        }
    }
}

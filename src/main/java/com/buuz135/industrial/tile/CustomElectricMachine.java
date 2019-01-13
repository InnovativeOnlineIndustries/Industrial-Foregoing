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
package com.buuz135.industrial.tile;

import com.buuz135.industrial.item.addon.EnergyFieldAddon;
import com.buuz135.industrial.item.addon.movility.TransferAddon;
import com.buuz135.industrial.jei.JEIHelper;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.api.IAcceptsTransferAddons;
import com.buuz135.industrial.tile.block.CustomOrientedBlock;
import com.buuz135.industrial.tile.block.EnergyFieldProviderBlock;
import com.buuz135.industrial.tile.world.EnergyFieldProviderTile;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.ndrei.teslacorelib.compatibility.FontRendererUtil;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.SideDrawerPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.items.SpeedUpgradeTier1;
import net.ndrei.teslacorelib.items.SpeedUpgradeTier2;
import net.ndrei.teslacorelib.tileentities.ElectricMachine;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CustomElectricMachine extends ElectricMachine implements IAcceptsTransferAddons {

    protected CustomElectricMachine(int typeId) {
        super(typeId);
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        List<EnumFacing> facings = new ArrayList<>();
        facings.addAll(Arrays.asList(EnumFacing.values()));
        Arrays.stream(EnumDyeColor.values()).forEach(enumDyeColor -> this.getSideConfig().setSidesForColor(enumDyeColor, facings));
    }

    @Override
    protected int getEnergyForWork() {
        return this.getBlockType() instanceof CustomOrientedBlock ? ((CustomOrientedBlock) this.getBlockType()).getEnergyForWork() : Integer.MAX_VALUE;
    }

    @Override
    protected int getEnergyForWorkRate() {
        return this.getBlockType() instanceof CustomOrientedBlock ? ((CustomOrientedBlock) this.getBlockType()).getEnergyRate() : Integer.MAX_VALUE;
    }

    @Override
    protected long getMaxEnergy() {
        return this.getBlockType() instanceof CustomOrientedBlock ? ((CustomOrientedBlock) this.getBlockType()).getEnergyBuffer() : 50000;
    }

    @Override
    protected long getEnergyInputRate() {
        return 4000;
    }

    public int speedUpgradeLevel() {
        if (this.hasAddon(SpeedUpgradeTier2.class)) return 2;
        if (this.hasAddon(SpeedUpgradeTier1.class)) return 1;
        return 0;
    }

    @Override
    protected int getMinimumWorkTicks() {
        return 4;
    }

    @NotNull
    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer<?> container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
        if (JEIHelper.isInstalled()) pieces.add(new SideDrawerPiece(1) {

            @Override
            protected void renderState(BasicTeslaGuiContainer<?> basicTeslaGuiContainer, int i, BoundingRectangle boundingRectangle) {
                container.bindDefaultTexture();
                FontRendererUtil.INSTANCE.getFontRenderer().drawString("J", boundingRectangle.getLeft() + 6, boundingRectangle.getTop() + 4, 4210751);
            }

            @Override
            protected void clicked() {
                JEIHelper.openBlockUses(new ItemStack(CustomElectricMachine.this.getBlockType()));
            }
        });
        return pieces;
    }

    @Override
    public void protectedUpdate() {
        super.protectedUpdate();
        if (this.world.isRemote) return;
        if (world.getTotalWorldTime() % 2 == 0 && hasAddon(EnergyFieldAddon.class)) {
            ItemStack addon = getAddonStack(EnergyFieldAddon.class);
            if (addon.hasCapability(CapabilityEnergy.ENERGY, null)) {
                IEnergyStorage storage = addon.getCapability(CapabilityEnergy.ENERGY, null);
                storage.extractEnergy((int) this.getEnergyStorage().givePower(storage.extractEnergy(512, true)), false);
                BlockPos pos = ItemRegistry.energyFieldAddon.getLinkedBlockPos(addon);
                if (this.world.getBlockState(pos).getBlock() instanceof EnergyFieldProviderBlock && this.world.isAreaLoaded(pos, pos)) {
                    EnergyFieldProviderTile tile = (EnergyFieldProviderTile) this.world.getTileEntity(pos);
                    if (tile.getWorkingArea().grow(1).contains(new Vec3d(this.pos.getX(), this.pos.getY(), this.pos.getZ()))) {
                        float pull = tile.consumeWorkEnergy(Math.min(storage.getMaxEnergyStored() - storage.getEnergyStored(), 1000));
                        storage.receiveEnergy((int) pull, false);
                    }
                }
                this.forceSync();
            }
        }
        if (world.getTotalWorldTime() % 10 == 0 && this.getAddonItems() != null) {
            workTransferAddon(this, this.getAddonItems());
        }
    }

    public boolean canAcceptEnergyFieldAddon() {
        return !hasAddon(EnergyFieldAddon.class);
    }


    @Override
    public boolean canAcceptAddon(TransferAddon addon) {
        return !this.hasAddon(addon.getClass()) || (this.getAddon(addon.getClass()) != null && this.getAddon(addon.getClass()).getMode() != addon.getMode());
    }
}

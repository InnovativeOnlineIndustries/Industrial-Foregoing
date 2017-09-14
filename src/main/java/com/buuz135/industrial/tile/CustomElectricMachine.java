package com.buuz135.industrial.tile;

import com.buuz135.industrial.jei.JEIHelper;
import com.buuz135.industrial.tile.block.CustomOrientedBlock;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
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

public abstract class CustomElectricMachine extends ElectricMachine {

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
}

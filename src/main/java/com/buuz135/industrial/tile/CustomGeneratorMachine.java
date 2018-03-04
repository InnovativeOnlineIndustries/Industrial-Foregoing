package com.buuz135.industrial.tile;

import com.buuz135.industrial.item.addon.movility.TransferAddon;
import com.buuz135.industrial.jei.JEIHelper;
import com.buuz135.industrial.tile.api.IAcceptsTransferAddons;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.ndrei.teslacorelib.compatibility.FontRendererUtil;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.SideDrawerPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.tileentities.ElectricGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CustomGeneratorMachine extends ElectricGenerator implements IAcceptsTransferAddons {

    private int tick;

    protected CustomGeneratorMachine(int typeId) {
        super(typeId);
        tick = 0;
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        List<EnumFacing> facings = new ArrayList<>();
        facings.addAll(Arrays.asList(EnumFacing.values()));
        Arrays.stream(EnumDyeColor.values()).forEach(enumDyeColor -> this.getSideConfig().setSidesForColor(enumDyeColor, facings));
    }

    @Override
    protected long getEnergyOutputRate() {
        return Integer.MAX_VALUE;
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
                JEIHelper.openBlockUses(new ItemStack(CustomGeneratorMachine.this.getBlockType()));
            }
        });
        return pieces;
    }

    @NotNull
    @Override
    public NBTTagCompound writeToNBT(@NotNull NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setInteger("Tick", tick);
        return compound;
    }

    @Override
    public void readFromNBT(@NotNull NBTTagCompound compound) {
        super.readFromNBT(compound);
        tick = compound.getInteger("Tick");
    }

    @Override
    public void protectedUpdate() {
        super.protectedUpdate();
        if (this.world.isRemote) return;
        if (tick % 10 == 0 && this.getAddonItems() != null) {
            workTransferAddon(this, this.getAddonItems());
        }
        ++tick;
        if (tick >= 20) {
            tick = 0;
        }
    }

    @Override
    public boolean canAcceptAddon(TransferAddon addon) {
        return !this.hasAddon(addon.getClass()) || (this.getAddon(addon.getClass()) != null && this.getAddon(addon.getClass()).getMode() != addon.getMode());
    }
}

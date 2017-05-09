package com.buuz135.industrial.tile;

import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.items.IItemHandler;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.containers.FilteredSlot;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.TiledRenderedGuiPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandler;

import java.util.List;

public class CustomColoredItemHandler extends ColoredItemHandler {

    private int posX;
    private int posY;
    private int slotsX;
    private int slotsY;

    public CustomColoredItemHandler(IItemHandler handler, EnumDyeColor color, String name, int posX, int posY, int slotsX, int slotsY) {
        super(handler, color, name, new BoundingRectangle(posX,posY,18*slotsX,18*slotsY));
        this.posX = posX;
        this.posY = posY;
        this.slotsX = slotsX;
        this.slotsY = slotsY;
    }

    @Override
    public List<Slot> getSlots(BasicTeslaContainer container) {
        List<Slot> slots = super.getSlots(container);
        BoundingRectangle box = this.getBoundingBox();
        int i = 0;
        for (int y = 0; y < slotsY; y++) {
            for (int x = 0; x < slotsX; x++) {
                slots.add(new FilteredSlot(this.getItemHandlerForContainer(), i, box.getLeft() + 1 + x * 18, box.getTop() + 1 + y * 18));
                ++i;
            }
        }
        return slots;
    }

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
        BoundingRectangle box = this.getBoundingBox();
        pieces.add(new TiledRenderedGuiPiece(box.getLeft(), box.getTop(), 18, 18, slotsX, slotsY, BasicTeslaGuiContainer.MACHINE_BACKGROUND, 108, 225, EnumDyeColor.ORANGE));
        return pieces;
    }
}

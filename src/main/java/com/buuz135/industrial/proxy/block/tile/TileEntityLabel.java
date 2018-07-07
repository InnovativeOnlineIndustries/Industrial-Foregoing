package com.buuz135.industrial.proxy.block.tile;


import net.minecraft.nbt.NBTTagCompound;

import java.text.DecimalFormat;
import java.util.function.Function;

public class TileEntityLabel extends TileBase {

    private static DecimalFormat formatterWithUnits = new DecimalFormat("####0.#");
    private FormatType formatType = FormatType.STACKS;

    public static String getFormatedBigNumber(int number) {
        if (number >= 1000000000) { //MILLION
            float numb = number / 1000000000F;
            return formatterWithUnits.format(numb) + "B";
        } else if (number >= 1000000) { //MILLION
            float numb = number / 1000000F;
            if (number > 100000000) numb = Math.round(numb);
            return formatterWithUnits.format(numb) + "M";
        } else if (number >= 1000) { //THOUSANDS
            float numb = number / 1000F;
            if (number > 100000) numb = Math.round(numb);
            return formatterWithUnits.format(numb) + "K";
        }
        return String.valueOf(number);
    }

    public FormatType getFormatType() {
        return formatType;
    }

    public void setFormatType(FormatType formatType) {
        this.formatType = formatType;
        this.world.notifyBlockUpdate(getPos(), getWorld().getBlockState(getPos()), getWorld().getBlockState(getPos()), 3);
        markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.formatType = FormatType.valueOf(compound.getString("Format"));
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setString("Format", formatType.name());
        return compound;
    }

    public enum FormatType {
        STACKS(integer -> integer == 0 ? "0" : (integer >= 64 ? getFormatedBigNumber(integer / 64) + " x64" : "") + (integer >= 64 && integer % 64 != 0 ? " + " : "") + (integer % 64 != 0 ? integer % 64 : "")),
        MILL(TileEntityLabel::getFormatedBigNumber),
        FLUID(integer -> getFormatedBigNumber(integer / 1000) + " b");

        private final Function<Integer, String> format;

        FormatType(Function<Integer, String> format) {
            this.format = format;
        }

        public Function<Integer, String> getFormat() {
            return format;
        }
    }
}

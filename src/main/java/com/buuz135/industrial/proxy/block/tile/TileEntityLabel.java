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

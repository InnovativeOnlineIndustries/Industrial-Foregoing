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
package com.buuz135.industrial.utils.explosion;

import net.minecraft.core.BlockPos;
import org.joml.Vector3f;

/**
 * Class copied and adapted from Draconic Evolution https://github.com/brandon3055/BrandonsCore/blob/master/src/main/java/com/brandon3055/brandonscore/lib/ShortPos.java
 */
public class ShortPos2 {

    private BlockPos relativeTo;

    public ShortPos2(BlockPos relativeTo) {
        this.relativeTo = relativeTo;
    }

    /**
     * This is the main method for converting a relative position to an integer.<br>
     * Max allowed x/z difference between the two positions is 2048. Anything greater than this will result in a broken output but will not throw an error.<br><br>
     *
     * <pre>
     * Byte Format:
     *  Y 8 bits     X 12 bits       Z 12 bits
     * [11111111] [11111111 1111] [1111 11111111]
     * </pre>
     *
     * @param position   The position.
     * @param relativeTo The the position to calculate the relative pos from. You will need this pos to convert the output from this method back to an actual block pos.
     * @return the relative deference between the inputs as an integer.
     */
    public static int getIntPos(BlockPos position, BlockPos relativeTo) {
        if (position.getY() > 255) {
            position = new BlockPos(position.getX(), 255, position.getY());
        } else if (position.getY() < -64) {
            position = new BlockPos(position.getX(), 0, position.getY());
        }
        int xp = (position.getX() - relativeTo.getX()) + 2048;
        int yp = position.getY();
        int zp = (position.getZ() - relativeTo.getZ()) + 2048;

        return (yp << 24) | (xp << 12) | zp;
    }

    public static int getIntPos(Vector3f position, BlockPos relativeTo) {
        if (position.y() > 255) {
            position.setComponent(1,255);
        } else if (position.y() < -64) {
            position.setComponent(1,0);
        }
        int xp = ((int) position.x() - relativeTo.getX()) + 2048;
        int yp = (int) position.y();
        int zp = ((int) position.z() - relativeTo.getZ()) + 2048;
        return (yp << 24) | (xp << 12) | zp;
    }

    /**
     * This is the main method for converting from a relative integer position back to an actual block position.
     *
     * @param intPos     The integer position calculated by getIntPos
     * @param relativeTo the position to calculate the output pos relative to. This should be the same pos that was used when calculating the intPos to get back the original absolute pos.
     * @return The absolute block position.
     */
    public static BlockPos getBlockPos(int intPos, BlockPos relativeTo) {
        int yp = (intPos >> 24 & 0xFF);
        int xp = (intPos >> 12 & 0xFFF) - 2048;
        int zp = (intPos & 0xFFF) - 2048;

        int finalX = relativeTo.getX() + xp;
        int finalZ = relativeTo.getZ() + zp;

        return new BlockPos(finalX, yp, finalZ); //-1 so it gives a proper position
    }

    public BlockPos getRelativeTo() {
        return relativeTo;
    }

    public void setRelativeTo(BlockPos relativeTo) {
        this.relativeTo = relativeTo;
    }

    public int getIntPos(BlockPos pos) {
        return getIntPos(pos, relativeTo);
    }

    public int getIntPos(Vector3f pos) {
        return getIntPos(pos, relativeTo);
    }

    public BlockPos getActualPos(int intPos) {
        return getBlockPos(intPos, relativeTo);
    }
}
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
package com.buuz135.industrial.proxy.block;

import net.minecraft.util.math.AxisAlignedBB;

public class Cuboid {
    public final double minX;
    public final double minY;
    public final double minZ;
    public final double maxX;
    public final double maxY;
    public final double maxZ;
    public final int identifier;
    public final boolean collidable;

    public Cuboid(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, int identifier) {
        this(minX, minY, minZ, maxX, maxY, maxZ, identifier, true);
    }

    public Cuboid(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this(minX, minY, minZ, maxX, maxY, maxZ, -1, true);
    }

    public Cuboid(Cuboid box) {
        this(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, box.identifier, box.collidable);
    }

    public Cuboid(AxisAlignedBB bb) {
        this(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, -1, true);
    }

    public Cuboid(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, int identifier, boolean collidable) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.identifier = identifier;
        this.collidable = collidable;
    }

    public AxisAlignedBB aabb() {
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
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
package com.buuz135.industrial.utils;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TagUtil {

    public static <T> boolean hasTag(T type, ITag.INamedTag<T> tag) {
        return tag.contains(type); //contains
    }

    public static TagCollection<Block> getAllBlockTags(World world) {
        return world.getTags().getBlocks();
    }

    public static TagCollection<Item> getAllItemTags(World world) {
        return world.getTags().getItems();
    }

    public static TagCollection<Fluid> getAllFluidTags(World world) {
        return world.getTags().getFluids();
    }

    public static <T> Collection<T> getAllEntries(ITag.INamedTag<T>... tags) {
        if (tags.length == 0)
            return Collections.emptyList();
        if (tags.length == 1)
            return tags[0].getAllElements(); //getAllElements
        List<T> list = new ArrayList<>();
        for (Tag.INamedTag<T> tag : tags) {
            list.addAll(tag.getAllElements());
        }
        return list;
    }

    public static <T> Collection<T> getAllEntries(Tag<T> tag) {
        return tag.getAllElements();
    }
}
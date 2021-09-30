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
package com.buuz135.industrial.utils;

import com.mojang.authlib.GameProfile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.FakePlayer;

import java.util.UUID;

public class IFFakePlayer extends FakePlayer {

    private static final UUID uuid = UUID.fromString("ec5b5875-ebb5-4b47-833b-0de37ac9e6d7");

    private static GameProfile PROFILE = new GameProfile(uuid, "[IF]");

    public IFFakePlayer(ServerLevel worldIn) {
        super(worldIn, PROFILE);
    }

    public boolean placeBlock(Level world, BlockPos pos, ItemStack stack) {
        this.setItemInHand(InteractionHand.MAIN_HAND, stack);
        return ForgeHooks.onPlaceItemIntoWorld(new UseOnContext(this, InteractionHand.MAIN_HAND, new BlockHitResult(new Vec3(0, 0, 0), Direction.DOWN, pos, false))) == InteractionResult.SUCCESS;
    }
}

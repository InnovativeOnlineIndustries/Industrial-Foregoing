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

package com.buuz135.industrial.proxy.event;

import com.buuz135.industrial.item.infinity.item.ItemInfinityDrill;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.IdentityHashMap;
import java.util.Map;

public class InfinityDrillEventHandler {

    /**
     * How many BreakEvent dispatches are currently in progress for each player. A non-zero value means that a block
     * is being broken by some event listener, not directly by the player.
     * </br>
     * Keyed by a {@code Player} object instead of UUID to remove possible interactions with
     * {@code FakePlayer} with the same UUID.
     * </br>
     * Server thread only.
     */
    private static final Map<Player, Integer> BREAK_EVENT_DEPTH = new IdentityHashMap<>();

    public static boolean isInsideBreakEvent(Player player) {
        return BREAK_EVENT_DEPTH.containsKey(player);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onBreakEventStart(BlockEvent.BreakEvent event) {
        BREAK_EVENT_DEPTH.merge(event.getPlayer(), 1, Integer::sum);
    }

    /**
     * In addition to decrementing the counter, this handler also contains the creative counterpart of {@link ItemInfinityDrill#mineBlock}.
     * In creative, {@link ServerPlayerGameMode#destroyBlock} removes the block without calling {@code mineBlock},
     * so we call it ourselves once every other listener has run.
     * It is only called for the outermost BreakEvent,
     * which also rules out the nested breaks of the drill's own area, so no further checks are needed.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onBreakEventEnd(BlockEvent.BreakEvent event) {
        try {
            // Not cancelled: no protection denied it, and no other mod took the break over.
            // Depth 1 (checked before the decrement): the player's own break, not one nested in another mod's
            // area mining or in the drill's own area.
            if (!event.isCanceled()
                    && BREAK_EVENT_DEPTH.getOrDefault(event.getPlayer(), 0) == 1
                    && event.getPlayer() instanceof ServerPlayer player
                    && player.isCreative()
            ) {
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof ItemInfinityDrill drill) {
                    drill.mineArea(stack, player.level(), event.getPos(), player);
                }
            }
        } finally {
            BREAK_EVENT_DEPTH.computeIfPresent(event.getPlayer(), (player, depth) -> depth > 1 ? depth - 1 : null);
        }
    }

    /** Safety guard */
    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        if (!BREAK_EVENT_DEPTH.isEmpty()) BREAK_EVENT_DEPTH.clear();
    }
}

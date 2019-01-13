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
package com.buuz135.industrial.proxy.event;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.proxy.BlockRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SkullHandler {

    @SubscribeEvent
    public void onBlockPlayerPlace(BlockEvent.PlaceEvent event) {
        if (event.getPlayer().isCreative()) return;
        if (!BlockRegistry.witherBuilderBlock.isHCWither()) return;
        if (event.getPlayer() instanceof FakePlayer && event.getPlayer().equals(IndustrialForegoing.getFakePlayer(event.getWorld())))
            return;
        if (!(event.getPlacedBlock().getBlock().equals(Blocks.SKULL) && Blocks.SKULL.getMetaFromState(event.getState()) == 1))
            return;
        event.setCanceled(true);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        if (!BlockRegistry.witherBuilderBlock.isHCWither()) return;
        if (event.getItemStack().isItemEqual(new ItemStack(Items.SKULL, 1, 1))) {
            event.getToolTip().add(TextFormatting.RED + new TextComponentTranslation("text.industrialforegoing.tooltip.no_wither_skull").getUnformattedText());
        }
    }
}

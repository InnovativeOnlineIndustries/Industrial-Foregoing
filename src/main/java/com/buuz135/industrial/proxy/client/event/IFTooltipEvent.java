package com.buuz135.industrial.proxy.client.event;

import com.buuz135.industrial.tile.block.IHasAdvancedTooltip;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Blocks;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class IFTooltipEvent {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        if (Block.getBlockFromItem(event.getItemStack().getItem()) == Blocks.AIR) return;
        Block block = Block.getBlockFromItem(event.getItemStack().getItem());
        if (block instanceof IHasAdvancedTooltip && !((IHasAdvancedTooltip) block).getTooltip(event.getItemStack()).isEmpty()) {
            if (!GuiScreen.isShiftKeyDown()) {
                event.getToolTip().add("Hold " + TextFormatting.YELLOW + TextFormatting.ITALIC + "Shift" + TextFormatting.RESET + TextFormatting.GRAY + " for Details");
            } else {
                event.getToolTip().addAll(((IHasAdvancedTooltip) block).getTooltip(event.getItemStack()));
            }
        }
    }
}

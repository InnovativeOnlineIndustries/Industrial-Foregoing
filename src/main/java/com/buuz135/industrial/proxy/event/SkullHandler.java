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

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        if (!BlockRegistry.witherBuilderBlock.isHCWither()) return;
        if (event.getItemStack().isItemEqual(new ItemStack(Items.SKULL, 1, 1))) {
            event.getToolTip().add(TextFormatting.RED + new TextComponentTranslation("text.industrialforegoing.tooltip.no_wither_skull").getUnformattedText());
        }
    }
}

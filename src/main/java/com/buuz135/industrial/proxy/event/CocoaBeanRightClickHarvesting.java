package com.buuz135.industrial.proxy.event;

import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class CocoaBeanRightClickHarvesting {

    @SubscribeEvent
    public static void onPlayerRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (!BlockRegistry.plantInteractorBlock.isRightClickCocoBeansEnabled()) return;
        if (event.getWorld().isRemote) return;
        if (event.getHand() != EnumHand.MAIN_HAND) return;
        if (event.getEntityPlayer() == null) return;
        IBlockState state = event.getWorld().getBlockState(event.getPos());
        if (state.getBlock() instanceof BlockCocoa && state.getValue(BlockCocoa.AGE) == 2) {
            ItemStack main = event.getEntityPlayer().getHeldItemMainhand();
            int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, main);
            NonNullList<ItemStack> drops = NonNullList.create();
            state.getBlock().getDrops(drops, event.getWorld(), event.getPos(), state, fortune);
            if (drops.size() > 0) {
                drops.get(0).shrink(1);
            }
            ForgeEventFactory.fireBlockHarvesting(drops, event.getWorld(), event.getPos(), state, fortune, 1f, false, event.getEntityPlayer());
            event.getWorld().setBlockState(event.getPos(), state.withProperty(BlockCocoa.AGE, 0));
            for (ItemStack stack : drops) ItemHandlerHelper.giveItemToPlayer(event.getEntityPlayer(), stack);
        }
    }
}

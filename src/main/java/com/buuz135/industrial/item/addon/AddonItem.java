package com.buuz135.industrial.item.addon;

import com.buuz135.industrial.item.IFCustomItem;
import com.hrznstudio.titanium.block.tile.MachineTile;
import com.hrznstudio.titanium.tab.TitaniumTab;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;

public abstract class AddonItem extends IFCustomItem {
    public AddonItem(String name, TitaniumTab tab, Properties builder) {
        super(name, tab, builder);
    }

    public AddonItem(String name, TitaniumTab tab) {
        super(name, tab);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide) {
            var blockpos = context.getClickedPos();
            var entity = context.getLevel().getBlockEntity(blockpos);
            if (entity instanceof MachineTile<?> machineTile) {
                var stack = context.getItemInHand().copyWithCount(1);
                if (machineTile.canAcceptAugment(stack)) {
                    var augmentInv = machineTile.getAugmentInventory();
                    for (int i = 0; i < augmentInv.getSlots(); i++) {
                        if (augmentInv.getStackInSlot(i).isEmpty()) {
                            augmentInv.setStackInSlot(i, stack);
                            context.getItemInHand().shrink(1);
                            return InteractionResult.CONSUME_PARTIAL;
                        }
                    }
                }
            }
        }
        return super.useOn(context);
    }
}

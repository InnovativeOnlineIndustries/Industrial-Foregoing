package com.buuz135.industrial.item.infinity;

import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.function.Supplier;

public interface IInfinityDrillScreenAddons {

    List<IFactory<? extends IScreenAddon>> getScreenAddons(Supplier<ItemStack> stack);

}

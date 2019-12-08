package com.buuz135.industrial.block.agriculturehusbandry.tile;

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.gui.component.ItemGuiAddon;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.client.IGuiAddon;
import com.hrznstudio.titanium.block.tile.button.PosButton;
import com.hrznstudio.titanium.client.gui.addon.StateButtonAddon;
import com.hrznstudio.titanium.client.gui.addon.StateButtonInfo;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

import java.util.Collections;
import java.util.List;

public class AnimalBabySeparatorTile extends IndustrialAreaWorkingTile {

    @Save
    private boolean movingAdults;

    public AnimalBabySeparatorTile() {
        super(ModuleAgricultureHusbandry.ANIMAL_BABY_SEPARATOR, RangeManager.RangeType.BEHIND);
        this.movingAdults = false;
        addButton(new PosButton(42, 20, 18, 18) {
            @Override
            public List<IFactory<? extends IGuiAddon>> getGuiAddons() {
                return Collections.singletonList(() -> new StateButtonAddon(this, new StateButtonInfo(0, AssetTypes.ITEM_BACKGROUND, "Moving babies"), new StateButtonInfo(1, AssetTypes.ITEM_BACKGROUND, "Moving adults")) {
                    @Override
                    public int getState() {
                        return movingAdults ? 1 : 0;
                    }
                });
            }
        }.setPredicate((playerEntity, compoundNBT) -> movingAdults = !movingAdults));
        addGuiAddonFactory(() -> new ItemGuiAddon(42, 20) {
            @Override
            public ItemStack getItemStack() {
                return new ItemStack(movingAdults ? Items.WHEAT : Items.WHEAT_SEEDS);
            }
        }.withoutTooltip());
    }

    @Override
    public WorkAction work() {
        if (hasEnergy(400)) {
            List<AnimalEntity> mobs = this.world.getEntitiesWithinAABB(AnimalEntity.class, getWorkingArea().getBoundingBox());
            mobs.removeIf(animalEntity -> !animalEntity.isChild() == !movingAdults);
            if (mobs.size() == 0) return new WorkAction(1, 0);
            BlockPos pos = this.getPos().offset(this.getFacingDirection());
            mobs.get(0).setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

        }
        return new WorkAction(1, 0);
    }
}

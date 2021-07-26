package com.buuz135.industrial.api.transporter;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.api.IBlockContainer;
import com.buuz135.industrial.api.conveyor.gui.IGuiComponent;
import com.buuz135.industrial.gui.component.StateButtonInfo;
import com.buuz135.industrial.gui.component.custom.RegulatorFilterGuiComponent;
import com.buuz135.industrial.gui.component.custom.TexturedStateButtonGuiComponent;
import com.buuz135.industrial.proxy.block.filter.RegulatorFilter;
import com.buuz135.industrial.proxy.network.TransporterSyncMessage;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public abstract class FilteredTransporterType<TYPE, CAP> extends TransporterType {

    private RegulatorFilter<TYPE, CAP> filter;
    private boolean whitelist;
    private boolean isRegulated;

    public FilteredTransporterType(IBlockContainer container, TransporterTypeFactory factory, Direction side, TransporterTypeFactory.TransporterAction action) {
        super(container, factory, side, action);
        this.filter = createFilter();
        this.whitelist = false;
        this.isRegulated = false;
    }

    public boolean hasGui() {
        return true;
    }

    public void handleButtonInteraction(int buttonId, CompoundTag compound) {
        if (buttonId >= 0 && buttonId < filter.getFilter().length) {
            if (compound.contains("Amount")) {
                this.filter.getFilter()[buttonId].increaseAmount(compound.getInt("Amount"));
            } else {
                this.filter.setFilter(buttonId, ItemStack.of(compound));
            }
            this.getContainer().requestSync();
        }
        if (buttonId == 16) {
            whitelist = !whitelist;
            this.getContainer().requestSync();
        }
        if (buttonId == 17) {
            isRegulated = !isRegulated;
            this.getContainer().requestSync();
        }
    }

    public abstract RegulatorFilter<TYPE, CAP> createFilter();

    public void handleRenderSync(Direction origin, CompoundTag compoundNBT) {

    }

    public void syncRender(Direction origin, CompoundTag compoundNBT) {
        IndustrialForegoing.NETWORK.sendToNearby(getWorld(), getPos(), 32, new TransporterSyncMessage(getPos(), compoundNBT, getSide().get3DDataValue(), origin.get3DDataValue()));
    }

    public void addComponentsToGui(List<IGuiComponent> componentList) {
        super.addComponentsToGui(componentList);
        componentList.add(new RegulatorFilterGuiComponent(this.filter.getLocX(), this.filter.getLocY(), this.filter.getSizeX(), this.filter.getSizeY()) {
            @Override
            public RegulatorFilter<TYPE, CAP> getFilter() {
                return FilteredTransporterType.this.filter;
            }

            @Override
            public boolean isRegulator() {
                return isRegulated;
            }
        });
        ResourceLocation res = new ResourceLocation(Reference.MOD_ID, "textures/gui/machines.png");
        componentList.add(new TexturedStateButtonGuiComponent(16, 133, 20, 18, 18,
                new StateButtonInfo(0, res, 1, 214, new String[]{"whitelist"}),
                new StateButtonInfo(1, res, 20, 214, new String[]{"blacklist"})) {
            @Override
            public int getState() {
                return whitelist ? 0 : 1;
            }
        });
        componentList.add(new TexturedStateButtonGuiComponent(17, 133, 40, 18, 18,
                new StateButtonInfo(0, res, 58, 233, new String[]{"regulated_true"}),
                new StateButtonInfo(1, res, 58 + 19, 233, new String[]{"regulated_false"})) {
            @Override
            public int getState() {
                return isRegulated ? 0 : 1;
            }
        });
    }

    public boolean ignoresCollision() {
        return false;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundNBT = super.serializeNBT();
        compoundNBT.put("Filter", filter.serializeNBT());
        compoundNBT.putBoolean("Whitelist", whitelist);
        compoundNBT.putBoolean("Regulated", isRegulated);
        return compoundNBT;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Filter")) filter.deserializeNBT(nbt.getCompound("Filter"));
        whitelist = nbt.getBoolean("Whitelist");
        isRegulated = nbt.getBoolean("Regulated");
    }

    public RegulatorFilter<TYPE, CAP> getFilter() {
        return filter;
    }

    public boolean isWhitelist() {
        return whitelist;
    }

    public boolean isRegulated() {
        return isRegulated;
    }
}

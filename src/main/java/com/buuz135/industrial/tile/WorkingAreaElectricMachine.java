package com.buuz135.industrial.tile;

import com.buuz135.industrial.item.addon.RangeAddonItem;
import com.buuz135.industrial.proxy.CommonProxy;
import com.buuz135.industrial.proxy.client.ClientProxy;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.ToggleButtonPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.items.SpeedUpgradeTier1;
import net.ndrei.teslacorelib.items.SpeedUpgradeTier2;

import java.util.Arrays;
import java.util.List;

public abstract class WorkingAreaElectricMachine extends CustomElectricMachine {

    private int color;
    private boolean showArea;
    private int radius;
    private int height;
    private boolean acceptsRangeAddon;

    protected WorkingAreaElectricMachine(int typeId, int radius, int height, boolean acceptsRangeAddon) {
        super(typeId);
        color = CommonProxy.random.nextInt();
        this.radius = radius;
        this.height = height;
        this.acceptsRangeAddon = acceptsRangeAddon;
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
    }

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        List<IGuiContainerPiece> list = super.getGuiContainerPieces(container);
        list.add(new ToggleButtonPiece(153, 84, 14, 14) {
            @Override
            protected int getCurrentState() {
                return showArea ? 1 : 0;
            }

            @Override
            protected void renderState(BasicTeslaGuiContainer container, int state, BoundingRectangle box) {

            }

            @Override
            public void drawBackgroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
                super.drawBackgroundLayer(container, guiX, guiY, partialTicks, mouseX, mouseY);
                if (getCurrentState() == 0) {
                    container.mc.getTextureManager().bindTexture(ClientProxy.GUI);
                    container.drawTexturedRect(this.getLeft()-1, this.getTop()-1, 78, 1, 16, 16);
                } else {
                    container.mc.getTextureManager().bindTexture(ClientProxy.GUI);
                    container.drawTexturedRect(this.getLeft()-1, this.getTop()-1,
                            78, 17, 16, 16);
                }
            }

            @Override
            protected void clicked() {
                showArea = !showArea;
            }

            @Override
            public void drawForegroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, int mouseX, int mouseY) {
                super.drawForegroundLayer(container, guiX, guiY, mouseX, mouseY);
                if (isInside(container, mouseX, mouseY))
                    container.drawTooltip(Arrays.asList("Show working area"), mouseX - guiX, mouseY - guiY);
            }
        });

        return list;
    }

    public abstract float work();

    @Override
    protected float performWork() {
        float work = 0;
        for (int i = 0; i < getActionsWork(); ++i) {
            float temp = work();
            if (temp > work) {
                work = temp;
            }
        }
        return work;
    }

    public abstract AxisAlignedBB getWorkingArea();

    @SideOnly(Side.CLIENT)
    public int getColor() {
        return color;
    }

    public boolean isShowArea() {
        return showArea;
    }

    public int getRadius() {
        return radius + (this.hasAddon(RangeAddonItem.class) ? (this.getAddonStack(RangeAddonItem.class).getMetadata() <= 0 ? -1 : this.getAddonStack(RangeAddonItem.class).getMetadata()) : 0);
    }

    public int getHeight() {
        return height;
    }

    public boolean canAcceptRangeUpgrades() {
        return acceptsRangeAddon;
    }

    public int getActionsWork() {
        return 1 + (getRadius() / 4) * speedUpgradeLevel();
    }

    public int speedUpgradeLevel() {
        if (this.hasAddon(SpeedUpgradeTier2.class)) return 2;
        if (this.hasAddon(SpeedUpgradeTier1.class)) return 1;
        return 0;
    }
}

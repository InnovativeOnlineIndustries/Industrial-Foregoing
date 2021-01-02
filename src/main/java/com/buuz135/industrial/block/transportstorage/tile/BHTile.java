package com.buuz135.industrial.block.transportstorage.tile;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.ActiveTile;
import com.hrznstudio.titanium.client.screen.addon.BasicButtonAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.component.button.ButtonComponent;
import com.hrznstudio.titanium.util.AssetUtil;
import com.hrznstudio.titanium.util.LangUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BHTile<T extends BHTile<T>> extends ActiveTile<T> {

    @Save
    public boolean display;

    public BHTile(BasicTileBlock<T> base) {
        super(base);
        this.display = true;
        addButton(new ButtonComponent(82+ 20 * 3, 64+16, 18, 18) {
            @Override
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                return Collections.singletonList(() -> new BasicButtonAddon(this) {
                    @Override
                    public void drawBackgroundLayer(MatrixStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
                        AssetUtil.drawAsset(stack, screen, provider.getAsset(AssetTypes.ITEM_BACKGROUND), guiX + getPosX(), guiY + getPosY());
                        Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(new ItemStack(display ? Items.ENDER_EYE: Items.ENDER_PEARL), guiX + getPosX() + 1, guiY + getPosY() + 1);
                        RenderHelper.disableStandardItemLighting();
                        RenderSystem.enableAlphaTest();
                    }

                    @Override
                    public List<ITextComponent> getTooltipLines() {
                        List<ITextComponent> lines = new ArrayList<>();
                        lines.add(new StringTextComponent(TextFormatting.GOLD + LangUtil.getString("tooltip.industrialforegoing.bl." + ( display ? "show_unit_display" : "hide_unit_display"))));
                        return lines;
                    }
                });
            }
        }.setPredicate((playerEntity, compoundNBT) -> {
            this.display = !display;
            this.syncObject(this.display);
        }));
    }

    public abstract ItemStack getDisplayStack();

    public abstract String getFormatedDisplayAmount();

    public boolean shouldDisplay() {
        return display;
    }
}

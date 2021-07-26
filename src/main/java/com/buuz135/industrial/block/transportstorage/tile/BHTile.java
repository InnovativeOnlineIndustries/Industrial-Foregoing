/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
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
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;

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
                    public void drawBackgroundLayer(PoseStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
                        AssetUtil.drawAsset(stack, screen, provider.getAsset(AssetTypes.ITEM_BACKGROUND), guiX + getPosX(), guiY + getPosY());
                        Minecraft.getInstance().getItemRenderer().renderGuiItem(new ItemStack(display ? Items.ENDER_EYE: Items.ENDER_PEARL), guiX + getPosX() + 1, guiY + getPosY() + 1);
                        Lighting.turnOff();
                        RenderSystem.enableAlphaTest();
                    }

                    @Override
                    public List<Component> getTooltipLines() {
                        List<Component> lines = new ArrayList<>();
                        lines.add(new TextComponent(ChatFormatting.GOLD + LangUtil.getString("tooltip.industrialforegoing.bl." + ( display ? "show_unit_display" : "hide_unit_display"))));
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

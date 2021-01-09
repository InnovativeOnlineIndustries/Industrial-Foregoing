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
package com.buuz135.industrial.proxy.client;

import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.api.client.GenericAssetType;
import com.hrznstudio.titanium.api.client.IAsset;
import com.hrznstudio.titanium.api.client.IAssetType;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.*;

public class IndustrialAssetProvider implements IAssetProvider {

    public static final ResourceLocation ASSET_LOCATION = new ResourceLocation(Reference.MOD_ID, "textures/gui/machines.png");
    public static final IAssetType<IAsset> BUTTON_SHOW_AREA = new GenericAssetType<>(IAssetProvider.DEFAULT_PROVIDER::getAsset, IAsset.class);
    public static final IAssetType<IAsset> BUTTON_HIDE_AREA = new GenericAssetType<>(IAssetProvider.DEFAULT_PROVIDER::getAsset, IAsset.class);
    public static IndustrialAssetProvider INSTANCE = new IndustrialAssetProvider();
    private final IAsset SHOW_AREA = new IAsset() {
        @Override
        public Rectangle getArea() {
            return new Rectangle(78, 17, 14, 14);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return ASSET_LOCATION;
        }
    };

    private final IAsset HIDE_AREA = new IAsset() {
        @Override
        public Rectangle getArea() {
            return new Rectangle(78, 1, 14, 14);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return ASSET_LOCATION;
        }
    };

    @Nullable
    @Override
    public <T extends IAsset> T getAsset(IAssetType<T> assetType) {
        if (assetType == BUTTON_SHOW_AREA) return assetType.castOrDefault(SHOW_AREA);
        if (assetType == BUTTON_HIDE_AREA) return assetType.castOrDefault(HIDE_AREA);
        return DEFAULT_PROVIDER.getAsset(assetType);
    }
}

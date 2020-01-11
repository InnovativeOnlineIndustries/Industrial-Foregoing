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

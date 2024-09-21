package com.buuz135.industrial.plugin.emi.recipe;

import com.buuz135.industrial.plugin.emi.IFEmiPlugin;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.client.screen.addon.SlotsScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.DefaultAssetProvider;
import com.hrznstudio.titanium.util.AssetUtil;
import dev.emi.emi.api.neoforge.NeoForgeEmiStack;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.List;

public class BioreactorEmiRecipe extends CustomEmiRecipe {


    public BioreactorEmiRecipe(TagKey<Item> stack, FluidStack fluid) {
        super(ResourceLocation.fromNamespaceAndPath(stack.location().getNamespace(), "emi_" + stack.location().getPath()), IFEmiPlugin.BIOREACTOR_EMI_CATEGORY,
                fromInput(EmiIngredient.of(stack)),
                fromOutput(NeoForgeEmiStack.of(fluid)));
    }


    @Override
    public int getDisplayWidth() {
        return 76;
    }

    @Override
    public int getDisplayHeight() {
        return 58;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {

        widgets.addSlot(EmiIngredient.of(List.of(this.getInputs().get(0))), 1, 19);

        widgets.addTank(this.getOutputs().get(0), 59, 3, 14, 52, 1000).drawBack(false).recipeContext(this);

        widgets.addFillingArrow(26, 20, 2000);

        widgets.addDrawable(0, 0, 0, 0, (draw, mouseX, mouseY, delta) -> {
            AssetUtil.drawAsset(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.TANK_NORMAL), 57, 1);
            SlotsScreenAddon.drawAsset(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 2, 20, 0, 0, 1, integer -> Pair.of(0, 0), integer -> ItemStack.EMPTY, true, integer -> new Color(DyeColor.LIGHT_BLUE.getFireworkColor()), integer -> true, 1);
        });


    }


}

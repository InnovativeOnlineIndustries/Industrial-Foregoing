package com.buuz135.industrial.jei.category;

import com.buuz135.industrial.block.core.tile.DissolutionChamberTile;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.client.screen.addon.EnergyBarScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.SlotsScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.DefaultAssetProvider;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.util.AssetUtil;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DissolutionChamberCategory implements IRecipeCategory<DissolutionChamberRecipe> {

    public static final ResourceLocation ID = new ResourceLocation(DissolutionChamberRecipe.SERIALIZER.getRecipeType().toString());

    private final IGuiHelper guiHelper;
    private final IDrawable smallTank;
    private final IDrawable bigTank;

    public DissolutionChamberCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.smallTank = guiHelper.createDrawable(DefaultAssetProvider.DEFAULT_LOCATION, 235 + 3, 1 + 3, 12, 13);
        this.bigTank = guiHelper.createDrawable(DefaultAssetProvider.DEFAULT_LOCATION, 177 + 3, 1 + 3, 12, 50);
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public Class<? extends DissolutionChamberRecipe> getRecipeClass() {
        return DissolutionChamberRecipe.class;
    }

    @Override
    public String getTitle() {
        return "Dissolution Chamber";
    }

    @Override
    public IDrawable getBackground() {
        return this.guiHelper.createBlankDrawable(160, 82);
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setIngredients(DissolutionChamberRecipe dissolutionChamberRecipe, IIngredients iIngredients) {
        List<List<ItemStack>> input = new ArrayList<>();
        for (Ingredient.IItemList iItemList : dissolutionChamberRecipe.input) {
            input.add(new ArrayList<>(iItemList.getStacks()));
        }
        iIngredients.setInputLists(VanillaTypes.ITEM, input);
        iIngredients.setInput(VanillaTypes.FLUID, dissolutionChamberRecipe.inputFluid);
        iIngredients.setOutput(VanillaTypes.ITEM, dissolutionChamberRecipe.output);
        iIngredients.setOutput(VanillaTypes.FLUID, dissolutionChamberRecipe.outputFluid);
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, DissolutionChamberRecipe dissolutionChamberRecipe, IIngredients iIngredients) {
        for (int i = 0; i < 8; i++) {
            if (i < iIngredients.getInputs(VanillaTypes.ITEM).size()) {
                iRecipeLayout.getItemStacks().init(i, true, 23 + DissolutionChamberTile.getSlotPos(i).getLeft(), 10 + DissolutionChamberTile.getSlotPos(i).getRight());
                iRecipeLayout.getItemStacks().set(i, iIngredients.getInputs(VanillaTypes.ITEM).get(i));
            }
        }
        iRecipeLayout.getItemStacks().init(9, false, 118, 15);
        iRecipeLayout.getItemStacks().set(9, iIngredients.getOutputs(VanillaTypes.ITEM).get(0));

        iRecipeLayout.getFluidStacks().init(0, true, 33 + 12 + 3, 32 + 3, 12, 13, 8000, false, smallTank);
        iRecipeLayout.getFluidStacks().set(0, iIngredients.getInputs(VanillaTypes.FLUID).get(0));
        iRecipeLayout.getFluidStacks().init(1, false, 139 + 3, 14 + 3, 12, 50, 8000, false, bigTank);
        iRecipeLayout.getFluidStacks().set(1, iIngredients.getOutputs(VanillaTypes.FLUID).get(0));
    }

    @Override
    public void draw(DissolutionChamberRecipe recipe, double mouseX, double mouseY) {
        EnergyBarScreenAddon.drawBackground(Minecraft.getInstance().currentScreen, DefaultAssetProvider.DEFAULT_PROVIDER, 0, 12, 0, 0);
        SlotsScreenAddon.drawAsset(Minecraft.getInstance().currentScreen, DefaultAssetProvider.DEFAULT_PROVIDER, 24, 11, 0, 0, 8, integer -> DissolutionChamberTile.getSlotPos(integer), true, DyeColor.LIGHT_BLUE.getFireworkColor());
        SlotsScreenAddon.drawAsset(Minecraft.getInstance().currentScreen, DefaultAssetProvider.DEFAULT_PROVIDER, 119, 16, 0, 0, 3, integer -> Pair.of(18 * (integer % 1), 18 * (integer / 1)), true, DyeColor.ORANGE.getFireworkColor());
        AssetUtil.drawAsset(Minecraft.getInstance().currentScreen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.TANK_SMALL), 33 + 12, 32);
        AssetUtil.drawAsset(Minecraft.getInstance().currentScreen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.TANK_NORMAL), 139, 14);

        AssetUtil.drawAsset(Minecraft.getInstance().currentScreen, IAssetProvider.getAsset(DefaultAssetProvider.DEFAULT_PROVIDER, AssetTypes.PROGRESS_BAR_BACKGROUND_HORIZONTAL), 92, 41 - 8);

        int consumed = recipe.processingTime * 60;
        EnergyBarScreenAddon.drawForeground(Minecraft.getInstance().currentScreen, DefaultAssetProvider.DEFAULT_PROVIDER, 0, 12, 0, 0, consumed, (int) Math.max(50000, Math.ceil(consumed)));

    }

    @Override
    public List<String> getTooltipStrings(DissolutionChamberRecipe recipe, double mouseX, double mouseY) {
        Rectangle rec = DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.ENERGY_BACKGROUND).getArea();
        if (new Rectangle(0, 12, rec.width, rec.height).contains(mouseX, mouseY)) {
            int consumed = recipe.processingTime * 60;
            return EnergyBarScreenAddon.getTooltip(consumed, (int) Math.max(50000, Math.ceil(consumed)));
        }
        return new ArrayList<>();
    }
}

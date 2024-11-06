package com.buuz135.industrial.plugin.emi.recipe;

import com.buuz135.industrial.block.core.tile.DissolutionChamberTile;
import com.buuz135.industrial.config.machine.core.DissolutionChamberConfig;
import com.buuz135.industrial.plugin.emi.IFEmiPlugin;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.client.screen.addon.EnergyBarScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.SlotsScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.DefaultAssetProvider;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.util.AssetUtil;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DissChamberEmiRecipe extends CustomEmiRecipe {

    private final RecipeHolder<DissolutionChamberRecipe> recipe;

    public DissChamberEmiRecipe(RecipeHolder<DissolutionChamberRecipe> recipe) {
        super(recipe.id(), IFEmiPlugin.DISSOLUTION_CHAMBER_EMI_CATEGORY,
                combineIng(
                        fromInputDiss(recipe.value().input),
                        fromInput(recipe.value().inputFluid)
                ),
                fromOutputDiss(recipe.value().output.orElse(ItemStack.EMPTY), recipe.value().outputFluid.orElse(FluidStack.EMPTY)));
        this.recipe = recipe;
    }

    public static List<EmiIngredient> fromInputDiss(List<Ingredient> ingredients) {
        List<Ingredient> result = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            var stacks = new ArrayList<ItemStack>();
            for (ItemStack item : ingredient.getItems()) {
                item.getItem().onCraftedBy(item, null, null);
                stacks.add(item);
            }
            result.add(Ingredient.of(stacks.toArray(new ItemStack[0])));
        }
        return result.stream().map(EmiIngredient::of).toList();
    }

    public static List<EmiStack> fromOutputDiss(ItemStack output, FluidStack fluidStack) {
        output.getItem().onCraftedBy(output, null, null);
        var list = new ArrayList<EmiStack>();
        list.add(EmiStack.of(output));
        list.add(EmiStack.of(fluidStack.getFluid(), fluidStack.getComponentsPatch(), fluidStack.getAmount()));
        return list;
    }

    @Override
    public int getDisplayWidth() {
        return 160;
    }

    @Override
    public int getDisplayHeight() {
        return 82;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        for (int i = 0; i < this.getInputs().size() - 1; i++) {
            widgets.addSlot(this.getInputs().get(i), 23 + DissolutionChamberTile.getSlotPos(i).getLeft(), 10 + DissolutionChamberTile.getSlotPos(i).getRight());
        }
        widgets.addSlot(EmiIngredient.of(List.of(this.getOutputs().get(0))), 118, 15).recipeContext(this);
        widgets.addTank(this.getInputs().get(this.getInputs().size() - 1), 33 + 12 + 2, 32 + 2, 14, 15, 1000).drawBack(false);

        widgets.addTank(this.getOutputs().get(1), 139 + 2, 14 + 2, 14, 52, 1000).backgroundTexture(DefaultAssetProvider.DEFAULT_LOCATION, 177 + 3, 1 + 3).drawBack(false).recipeContext(this);

        widgets.addDrawable(0, 0, 0, 0, (draw, mouseX, mouseY, delta) -> {
            EnergyBarScreenAddon.drawBackground(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 0, 12, 0, 0);

            SlotsScreenAddon.drawAsset(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 24, 11, 0, 0, 8, DissolutionChamberTile::getSlotPos, integer -> ItemStack.EMPTY, true, integer -> new Color(DyeColor.LIGHT_BLUE.getFireworkColor()), integer -> true, 1);
            SlotsScreenAddon.drawAsset(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 119, 16, 0, 0, 3, integer -> Pair.of(18 * (integer % 1), 18 * (integer / 1)), integer -> ItemStack.EMPTY, true, integer -> new Color(DyeColor.ORANGE.getFireworkColor()), integer -> true, 1);

            AssetUtil.drawAsset(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.TANK_SMALL), 33 + 12, 32);
            AssetUtil.drawAsset(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.TANK_NORMAL), 139, 14);

            AssetUtil.drawAsset(draw, Minecraft.getInstance().screen, IAssetProvider.getAsset(DefaultAssetProvider.DEFAULT_PROVIDER, AssetTypes.PROGRESS_BAR_BACKGROUND_ARROW_HORIZONTAL), 92, 41 - 8);


            int consumed = this.recipe.value().processingTime * DissolutionChamberConfig.powerPerTick;
            EnergyBarScreenAddon.drawForeground(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER, 0, 12, 0, 0, consumed, (int) Math.max(50000, Math.ceil(consumed)));
        });


        Rectangle rec = DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.ENERGY_BACKGROUND).getArea();
        int consumed = this.recipe.value().processingTime * DissolutionChamberConfig.powerPerTick;
        widgets.addTooltipText(EnergyBarScreenAddon.getTooltip(consumed, (int) Math.max(50000, Math.ceil(consumed))), 0, 12, rec.width, rec.height);

    }


}

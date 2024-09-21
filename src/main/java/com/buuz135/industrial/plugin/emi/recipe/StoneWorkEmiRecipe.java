package com.buuz135.industrial.plugin.emi.recipe;

import com.buuz135.industrial.plugin.emi.IFEmiPlugin;
import com.buuz135.industrial.plugin.jei.StoneWorkWrapper;
import com.buuz135.industrial.utils.Reference;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class StoneWorkEmiRecipe extends CustomEmiRecipe {

    private final StoneWorkWrapper wrapper;

    public StoneWorkEmiRecipe(StoneWorkWrapper stoneWorkWrapper) {
        super(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "stonework_" + BuiltInRegistries.ITEM.getKey(stoneWorkWrapper.input().getItem()).getPath() + "_" + BuiltInRegistries.ITEM.getKey(stoneWorkWrapper.output().getItem()).getPath()), IFEmiPlugin.STONE_WORK_EMI_CATEGORY,
                fromInput(EmiIngredient.of(Ingredient.of(stoneWorkWrapper.input()))),
                fromOutput(EmiStack.of(stoneWorkWrapper.output())));
        this.wrapper = stoneWorkWrapper;
    }

    @Override
    public int getDisplayWidth() {
        return 160;
    }

    @Override
    public int getDisplayHeight() {
        return 26;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(new EmiTexture(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "textures/gui/jei.png"), 94, 0, 160, 26), 0, 0);

        widgets.addSlot(EmiIngredient.of(List.of(this.getInputs().get(0))), 0, 4);

        widgets.addSlot(this.getOutputs().get(0), 138, 4).drawBack(false).recipeContext(this);

        widgets.addDrawable(0, 0, 0, 0, (draw, mouseX, mouseY, delta) -> {
            for (int i = 0; i < wrapper.modes().size(); i++) {
                draw.renderItem(wrapper.modes().get(i).getIcon(), 29 + i * 24, 5);
            }
        });
    }


}

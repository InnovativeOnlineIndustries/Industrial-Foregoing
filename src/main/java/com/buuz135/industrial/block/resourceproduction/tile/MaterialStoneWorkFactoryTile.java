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
package com.buuz135.industrial.block.resourceproduction.tile;

import com.buuz135.industrial.block.tile.IndustrialProcessingTile;
import com.buuz135.industrial.config.machine.resourceproduction.MaterialStoneWorkFactoryConfig;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.recipe.StoneWorkGenerateRecipe;
import com.buuz135.industrial.utils.CraftingUtils;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.BasicButtonAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.component.button.ButtonComponent;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.util.AssetUtil;
import com.hrznstudio.titanium.util.LangUtil;
import com.hrznstudio.titanium.util.RecipeUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class MaterialStoneWorkFactoryTile extends IndustrialProcessingTile<MaterialStoneWorkFactoryTile> {

    public static ResourceLocation DEFAULT = new ResourceLocation(Reference.MOD_ID, "stonework_generate/cobblestone");

    private int maxProgress;
    private int powerPerOperation;

    public static StoneWorkAction[] ACTION_RECIPES = new StoneWorkAction[]{
            new StoneWorkAction(new ItemStack(Blocks.FURNACE), (world1, itemStack) -> {
                FurnaceRecipe recipe = RecipeUtil.getSmelingRecipeFor(world1, itemStack);
                if (recipe != null) {
                    return recipe.getRecipeOutput();
                }
                return ItemStack.EMPTY;
            }, 1, "smelt"),
            new StoneWorkAction(new ItemStack(Items.DIAMOND_PICKAXE), CraftingUtils::getCrushOutput, 1, "crush"),
            new StoneWorkAction(new ItemStack(Blocks.OAK_PLANKS), (world1, stack) -> CraftingUtils.findOutput(2, stack, world1), 4, "small_craft"),
            new StoneWorkAction(new ItemStack(Blocks.CRAFTING_TABLE), (world1, stack) -> CraftingUtils.findOutput(3, stack, world1), 9, "big_craft"),
            new StoneWorkAction(new ItemStack(Blocks.BARRIER), (world1, stack) -> ItemStack.EMPTY, 0, "none")
    };

    @Save
    private SidedFluidTankComponent<MaterialStoneWorkFactoryTile> water;
    @Save
    private SidedFluidTankComponent<MaterialStoneWorkFactoryTile> lava;
    @Save
    private SidedInventoryComponent<MaterialStoneWorkFactoryTile> inventoryGenerator;
    @Save
    private String generatorRecipe;
    @Save
    private SidedInventoryComponent<MaterialStoneWorkFactoryTile> inventoryFirst;
    @Save
    private int firstRecipeId;
    @Save
    private SidedInventoryComponent<MaterialStoneWorkFactoryTile> inventorySecond;
    @Save
    private int secondRecipeId;
    @Save
    private SidedInventoryComponent<MaterialStoneWorkFactoryTile> inventoryThird;
    @Save
    private int thirdRecipeId;
    @Save
    private SidedInventoryComponent<MaterialStoneWorkFactoryTile> inventoryFourth;
    @Save
    private int fourthRecipeId;

    public MaterialStoneWorkFactoryTile() {
        super(ModuleResourceProduction.MATERIAL_STONEWORK_FACTORY, 48, 40);
        addTank(water = (SidedFluidTankComponent<MaterialStoneWorkFactoryTile>) new SidedFluidTankComponent<MaterialStoneWorkFactoryTile>("water", MaterialStoneWorkFactoryConfig.maxWaterTankSize, 30, 23, 0)
                .setColor(DyeColor.BLUE)
                .setTankType(FluidTankComponent.Type.SMALL)
                .setTankAction(FluidTankComponent.Action.FILL)
                .setComponentHarness(this)
                .setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(Fluids.WATER)));
        addTank(lava = (SidedFluidTankComponent<MaterialStoneWorkFactoryTile>) new SidedFluidTankComponent<MaterialStoneWorkFactoryTile>("lava", MaterialStoneWorkFactoryConfig.maxLavaTankSize, 30, 55, 1)
                .setColor(DyeColor.ORANGE)
                .setTankType(FluidTankComponent.Type.SMALL)
                .setTankAction(FluidTankComponent.Action.FILL)
                .setComponentHarness(this)
                .setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(Fluids.LAVA)));
        this.generatorRecipe = DEFAULT.toString();
        addButton(new ButtonComponent(54, 64, 18, 18) {
            @Override
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                return Collections.singletonList(() -> new BasicButtonAddon(this) {
                    @Override
                    public void drawBackgroundLayer(MatrixStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
                        getRecipe().ifPresent(recipe -> {
                            AssetUtil.drawAsset(stack, screen, provider.getAsset(AssetTypes.ITEM_BACKGROUND), guiX + getPosX(), guiY + getPosY());
                            //RenderSystem.setupGui3DDiffuseLighting();
                            Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(recipe.output, guiX + getPosX() + 1, guiY + getPosY() + 1);
                            RenderHelper.disableStandardItemLighting();
                            RenderSystem.enableAlphaTest();
                        });
                    }

                    @Override
                    public List<ITextComponent> getTooltipLines() {
                        List<ITextComponent> lines = new ArrayList<>();
                        getRecipe().ifPresent(recipe -> {
                            lines.add(new StringTextComponent(TextFormatting.GOLD + LangUtil.getString("tooltip.industrialforegoing.generating") + TextFormatting.WHITE + recipe.output.getDisplayName().getString()));
                            lines.add(new StringTextComponent(TextFormatting.GOLD + LangUtil.getString("tooltip.industrialforegoing.needs")));
                            lines.add(new StringTextComponent(TextFormatting.YELLOW + " - " + TextFormatting.WHITE + recipe.waterNeed + TextFormatting.DARK_AQUA + LangUtil.getString("tooltip.industrialforegoing.mb_of", LangUtil.getString("block.minecraft.water"))));
                            lines.add(new StringTextComponent(TextFormatting.YELLOW + " - " + TextFormatting.WHITE + recipe.lavaNeed + TextFormatting.DARK_AQUA + LangUtil.getString("tooltip.industrialforegoing.mb_of", LangUtil.getString("block.minecraft.lava"))));
                            lines.add(new StringTextComponent(TextFormatting.GOLD + LangUtil.getString("tooltip.industrialforegoing.consumes")));
                            lines.add(new StringTextComponent(TextFormatting.YELLOW + " - " + TextFormatting.WHITE + recipe.waterConsume + TextFormatting.DARK_AQUA + LangUtil.getString("tooltip.industrialforegoing.mb_of", LangUtil.getString("block.minecraft.water"))));
                            lines.add(new StringTextComponent(TextFormatting.YELLOW + " - " + TextFormatting.WHITE + recipe.lavaConsume + TextFormatting.DARK_AQUA + LangUtil.getString("tooltip.industrialforegoing.mb_of", LangUtil.getString("block.minecraft.lava"))));
                        });
                        return lines;
                    }
                });
            }
        }.setPredicate((playerEntity, compoundNBT) -> {
            getNextRecipe();
            markForUpdate();
        }));
        addInventory(inventoryGenerator = (SidedInventoryComponent<MaterialStoneWorkFactoryTile>) new SidedInventoryComponent<MaterialStoneWorkFactoryTile>("inventoryGenerator", 74, 23, 2, 2)
                .setColor(DyeColor.LIME)
                .setSlotPosition(integer -> integer == 0 ? Pair.of(0, 0) : Pair.of(0, 18))
                .setComponentHarness(this));
        addInventory(inventoryFirst = (SidedInventoryComponent<MaterialStoneWorkFactoryTile>) new SidedInventoryComponent<MaterialStoneWorkFactoryTile>("inventoryFirst", 74 + 20 * 1, 23, 2, 3)
                .setColor(DyeColor.CYAN)
                .setSlotPosition(integer -> integer == 0 ? Pair.of(0, 0) : Pair.of(0, 18))
                .setComponentHarness(this));
        addInventory(inventorySecond = (SidedInventoryComponent<MaterialStoneWorkFactoryTile>) new SidedInventoryComponent<MaterialStoneWorkFactoryTile>("inventorySecond", 74 + 20  * 2, 23, 2, 4)
                .setColor(DyeColor.YELLOW)
                .setSlotPosition(integer -> integer == 0 ? Pair.of(0, 0) : Pair.of(0, 18))
                .setComponentHarness(this));
        addInventory(inventoryThird = (SidedInventoryComponent<MaterialStoneWorkFactoryTile>) new SidedInventoryComponent<MaterialStoneWorkFactoryTile>("inventoryThird", 74 + 20  * 3, 23, 2, 5)
                .setColor(DyeColor.RED)
                .setSlotPosition(integer -> integer == 0 ? Pair.of(0, 0) : Pair.of(0, 18))
                .setComponentHarness(this));
        addInventory(inventoryFourth = (SidedInventoryComponent<MaterialStoneWorkFactoryTile>) new SidedInventoryComponent<MaterialStoneWorkFactoryTile>("inventoryFour", 74 + 20  * 4, 23, 2, 6)
                .setColor(DyeColor.GREEN)
                .setSlotPosition(integer -> integer == 0 ? Pair.of(0, 0) : Pair.of(0, 18))
                .setComponentHarness(this));
        this.firstRecipeId = 0;
        this.secondRecipeId = 0;
        this.thirdRecipeId = 0;
        this.fourthRecipeId = 0;
        addButton(new ButtonComponent(62 + 20 * 1, 64, 18, 18) {
            @Override
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                return Collections.singletonList(() -> new BasicButtonAddon(this) {
                    @Override
                    public void drawBackgroundLayer(MatrixStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
                        AssetUtil.drawAsset(stack, screen, provider.getAsset(AssetTypes.ITEM_BACKGROUND), guiX + getPosX(), guiY + getPosY());
                        //RenderSystem.setupGui3DDiffuseLighting();
                        Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(ACTION_RECIPES[firstRecipeId].icon, guiX + getPosX() + 1, guiY + getPosY() + 1);
                        RenderHelper.disableStandardItemLighting();
                        RenderSystem.enableAlphaTest();
                    }

                    @Override
                    public List<ITextComponent> getTooltipLines() {
                        List<ITextComponent> lines = new ArrayList<>();
                        lines.add(new StringTextComponent(TextFormatting.GOLD + LangUtil.getString("tooltip.industrialforegoing.action") + TextFormatting.WHITE + LangUtil.getString("tooltip.industrialforegoing.stonework." + ACTION_RECIPES[firstRecipeId].getAction())));
                        return lines;
                    }
                });
            }
        }.setPredicate((playerEntity, compoundNBT) -> {
            this.firstRecipeId = (this.firstRecipeId + 1) % ACTION_RECIPES.length;
            markForUpdate();
        }));
        addButton(new ButtonComponent(62 + 20 * 2, 64, 18, 18) {
            @Override
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                return Collections.singletonList(() -> new BasicButtonAddon(this) {
                    @Override
                    public void drawBackgroundLayer(MatrixStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
                        AssetUtil.drawAsset(stack, screen, provider.getAsset(AssetTypes.ITEM_BACKGROUND), guiX + getPosX(), guiY + getPosY());
                        //RenderSystem.setupGui3DDiffuseLighting();
                        Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(ACTION_RECIPES[secondRecipeId].icon, guiX + getPosX() + 1, guiY + getPosY() + 1);
                        RenderHelper.disableStandardItemLighting();
                        RenderSystem.enableAlphaTest();
                    }

                    @Override
                    public List<ITextComponent> getTooltipLines() {
                        List<ITextComponent> lines = new ArrayList<>();
                        lines.add(new StringTextComponent(TextFormatting.GOLD + LangUtil.getString("tooltip.industrialforegoing.action") + TextFormatting.WHITE + LangUtil.getString("tooltip.industrialforegoing.stonework." + ACTION_RECIPES[secondRecipeId].getAction())));
                        return lines;
                    }
                });
            }
        }.setPredicate((playerEntity, compoundNBT) -> {
            this.secondRecipeId = (this.secondRecipeId + 1) % ACTION_RECIPES.length;
            markForUpdate();
        }));
        addButton(new ButtonComponent(62 + 20 * 3, 64, 18, 18) {
            @Override
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                return Collections.singletonList(() -> new BasicButtonAddon(this) {
                    @Override
                    public void drawBackgroundLayer(MatrixStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
                        AssetUtil.drawAsset(stack, screen, provider.getAsset(AssetTypes.ITEM_BACKGROUND), guiX + getPosX(), guiY + getPosY());
                        //RenderSystem.setupGui3DDiffuseLighting();
                        Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(ACTION_RECIPES[thirdRecipeId].icon, guiX + getPosX() + 1, guiY + getPosY() + 1);
                        RenderHelper.disableStandardItemLighting();
                        RenderSystem.enableAlphaTest();
                    }

                    @Override
                    public List<ITextComponent> getTooltipLines() {
                        List<ITextComponent> lines = new ArrayList<>();
                        lines.add(new StringTextComponent(TextFormatting.GOLD + LangUtil.getString("tooltip.industrialforegoing.action") + TextFormatting.WHITE + LangUtil.getString("tooltip.industrialforegoing.stonework." + ACTION_RECIPES[thirdRecipeId].getAction())));
                        return lines;
                    }
                });
            }
        }.setPredicate((playerEntity, compoundNBT) -> {
            this.thirdRecipeId = (this.thirdRecipeId + 1) % ACTION_RECIPES.length;
            markForUpdate();
        }));
        addButton(new ButtonComponent(62 + 20 * 4, 64, 18, 18) {
            @Override
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                return Collections.singletonList(() -> new BasicButtonAddon(this) {
                    @Override
                    public void drawBackgroundLayer(MatrixStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
                        AssetUtil.drawAsset(stack, screen, provider.getAsset(AssetTypes.ITEM_BACKGROUND), guiX + getPosX(), guiY + getPosY());
                        //RenderSystem.setupGui3DDiffuseLighting();
                        Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(ACTION_RECIPES[fourthRecipeId].icon, guiX + getPosX() + 1, guiY + getPosY() + 1);
                        RenderHelper.disableStandardItemLighting();
                        RenderSystem.enableAlphaTest();
                    }

                    @Override
                    public List<ITextComponent> getTooltipLines() {
                        List<ITextComponent> lines = new ArrayList<>();
                        lines.add(new StringTextComponent(TextFormatting.GOLD + LangUtil.getString("tooltip.industrialforegoing.action") + TextFormatting.WHITE + LangUtil.getString("tooltip.industrialforegoing.stonework." + ACTION_RECIPES[thirdRecipeId].getAction())));
                        return lines;
                    }
                });
            }
        }.setPredicate((playerEntity, compoundNBT) -> {
            this.fourthRecipeId = (this.fourthRecipeId + 1) % ACTION_RECIPES.length;
            markForUpdate();
        }));
        this.maxProgress = MaterialStoneWorkFactoryConfig.maxProgress;
        this.powerPerOperation = MaterialStoneWorkFactoryConfig.powerPerTick;
    }

    @Override
    protected EnergyStorageComponent<MaterialStoneWorkFactoryTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(MaterialStoneWorkFactoryConfig.maxStoredPower, 10, 20);
    }

    @Override
    public boolean canIncrease() {
        return getRecipe().map(recipe -> recipe.canIncrease(water, lava) && ItemHandlerHelper.insertItem(inventoryGenerator, recipe.output.copy(), true).isEmpty()).orElse(false)
                || process(inventoryThird, inventoryFourth, ACTION_RECIPES[fourthRecipeId], true)
                || process(inventorySecond, inventoryThird, ACTION_RECIPES[thirdRecipeId], true)
                || process(inventoryFirst, inventorySecond, ACTION_RECIPES[secondRecipeId], true)
                || process(inventoryGenerator, inventoryFirst, ACTION_RECIPES[firstRecipeId], true);
    }

    public Optional<StoneWorkGenerateRecipe> getRecipe(){
        Collection<StoneWorkGenerateRecipe> recipes =  RecipeUtil.getRecipes(this.world, StoneWorkGenerateRecipe.SERIALIZER.getRecipeType());
        for (StoneWorkGenerateRecipe recipe : recipes) {
            if (recipe.getId().equals(new ResourceLocation(generatorRecipe))){
                return Optional.of(recipe);
            }
        }
        return recipes.stream().filter(stoneWorkGenerateRecipe -> stoneWorkGenerateRecipe.getId().equals(DEFAULT)).findFirst();
    }

    public ResourceLocation getNextRecipe(){
        if (generatorRecipe != null){
            List<ResourceLocation> rls = RecipeUtil.getRecipes(this.world, StoneWorkGenerateRecipe.SERIALIZER.getRecipeType()).stream().map(StoneWorkGenerateRecipe::getId).collect(Collectors.toList());
            int currentIndex = rls.indexOf(new ResourceLocation(generatorRecipe));
            this.generatorRecipe = rls.get((currentIndex + 1) % rls.size()).toString();
        }
        return DEFAULT;
    }

    @Override
    public Runnable onFinish() {
        return () -> {
            if (!process(inventoryThird, inventoryFourth, ACTION_RECIPES[fourthRecipeId], false) && !process(inventorySecond, inventoryThird, ACTION_RECIPES[thirdRecipeId], false) && !process(inventoryFirst, inventorySecond, ACTION_RECIPES[secondRecipeId], false) && !process(inventoryGenerator, inventoryFirst, ACTION_RECIPES[firstRecipeId], false)) {
                getRecipe().ifPresent(recipe -> {
                    ItemStack output = recipe.output.copy();
                    if (ItemHandlerHelper.insertItem(inventoryGenerator, output, false).isEmpty()) {
                        recipe.consume(water, lava);
                    }
                });
            }
        };
    }

    @Override
    protected int getTickPower() {
        return powerPerOperation;
    }

    private boolean process(SidedInventoryComponent input, SidedInventoryComponent output, StoneWorkAction action, boolean simulate) {
        boolean full = true;
        for (int i = 0; i < output.getSlots(); i++) {
            ItemStack stack = output.getStackInSlot(i);
            if (stack.isEmpty() || stack.getCount() < stack.getMaxStackSize()) full = false;
        }
        if (full) return false;
        for (int slot = 0; slot < input.getSlots(); slot++) {
            ItemStack inputStack = input.getStackInSlot(slot);
            if (inputStack.isEmpty()) continue;
            ItemStack outputStack = action.work.apply(this.world, inputStack.copy()).copy();
            if (outputStack.isEmpty()) continue;
            if (ItemHandlerHelper.insertItem(output, outputStack, true).isEmpty()) {
                if (!simulate){
                    ItemHandlerHelper.insertItem(output, outputStack, false);
                    inputStack.shrink(action.getShrinkAmount());
                }
                return true;
            }
        }
        return false;
    }

    @Nonnull
    @Override
    public MaterialStoneWorkFactoryTile getSelf() {
        return this;
    }

    @Override
    public int getMaxProgress() {
        return maxProgress;
    }

    public static class StoneWorkAction {

        private final ItemStack icon;
        private final BiFunction<World, ItemStack, ItemStack> work;
        private final int shrinkAmount;
        private final String action;

        private StoneWorkAction(ItemStack icon, BiFunction<World, ItemStack, ItemStack> work, int shrinkAmount, String action) {
            this.icon = icon;
            this.work = work;
            this.shrinkAmount = shrinkAmount;
            this.action = action;
        }

        public ItemStack getIcon() {
            return icon;
        }

        public BiFunction<World, ItemStack, ItemStack> getWork() {
            return work;
        }

        public int getShrinkAmount() {
            return shrinkAmount;
        }

        public String getAction() {
            return action;
        }
    }
}

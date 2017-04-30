package com.buuz135.industrial.tile;

import com.buuz135.industrial.utils.ItemStackRenderUtils;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.fixes.PotionItems;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.containers.FilteredSlot;
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.TiledRenderedGuiPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandler;
import net.ndrei.teslacorelib.tileentities.ElectricMachine;

import java.util.Arrays;
import java.util.List;

public class PotionEnervatorTile extends ElectricMachine {

    private static final String NBT_ACTION = "action";

    private IFluidTank fluidTank;
    private ItemStackHandler inputGlassBottles;
    private ItemStackHandler inputIngredients;
    private ItemStackHandler outputPotions;

    private int action = 0;

    public PotionEnervatorTile() {
        super(PotionEnervatorTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        initInputInventories();
        initOutputInventories();
    }

    private void initInputInventories() {
        fluidTank = this.addFluidTank(FluidRegistry.WATER, 8000, EnumDyeColor.BLUE, "Water tank", new BoundingRectangle(44, 25, 18, 54));
        inputGlassBottles = new ItemStackHandler(1);
        this.addInventory(new ColoredItemHandler(inputGlassBottles, EnumDyeColor.ORANGE, "Glass bottles input", new BoundingRectangle(18 * 4 + 10, 25 + 18 * 2, 18, 18)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return stack.getItem().equals(Items.GLASS_BOTTLE);
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }

            @Override
            public List<Slot> getSlots(BasicTeslaContainer container) {
                List<Slot> slots = super.getSlots(container);
                BoundingRectangle box = this.getBoundingBox();
                for (int x = 0; x < this.getItemHandlerForContainer().getSlots(); x++) {
                    slots.add(new FilteredSlot(this.getItemHandlerForContainer(), x, box.getLeft() + 1 + x * 18, box.getTop() + 1));
                }
                return slots;
            }

            @Override
            public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
                List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
                BoundingRectangle box = this.getBoundingBox();
                pieces.add(new TiledRenderedGuiPiece(box.getLeft(), box.getTop(), 18, 18,
                        this.getItemHandlerForContainer().getSlots(), 1,
                        BasicTeslaGuiContainer.MACHINE_BACKGROUND, 108, 225, EnumDyeColor.ORANGE));

                return pieces;
            }
        });
        this.addInventoryToStorage(inputGlassBottles, "pot_ener_in_glass");
        inputIngredients = new ItemStackHandler(5);
        this.addInventory(new ColoredItemHandler(inputIngredients, EnumDyeColor.GREEN, "Ingredients items", new BoundingRectangle(18 * 4 + 10, 25, 18 * 5, 18)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                if (stack.getItem().equals(Items.GLASS_BOTTLE)) return false;
                if (slot == 0) {
                    return stack.getItem().equals(Items.NETHER_WART);
                } else if (!stack.getItem().equals(Items.NETHER_WART)) {
                    return true;
                }
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }

            @Override
            public List<Slot> getSlots(BasicTeslaContainer container) {
                List<Slot> slots = super.getSlots(container);
                BoundingRectangle box = this.getBoundingBox();
                for (int x = 0; x < this.getItemHandlerForContainer().getSlots(); x++) {
                    slots.add(new FilteredSlot(this.getItemHandlerForContainer(), x, box.getLeft() + 1 + x * 18, box.getTop() + 1));
                }
                return slots;
            }

            @Override
            public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
                List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
                BoundingRectangle box = this.getBoundingBox();
                pieces.add(new TiledRenderedGuiPiece(box.getLeft(), box.getTop(), 18, 18,
                        this.getItemHandlerForContainer().getSlots(), 1,
                        BasicTeslaGuiContainer.MACHINE_BACKGROUND, 108, 225, EnumDyeColor.GREEN));
                return pieces;
            }
        });
        this.addInventoryToStorage(inputIngredients, "pot_ener_in");
    }

    private void initOutputInventories() {
        outputPotions = new ItemStackHandler(3);
        this.addInventory(new ColoredItemHandler(outputPotions, EnumDyeColor.PURPLE, "Potions items", new BoundingRectangle(18 * 6 + 10, 25 + 18 * 2, 18 * 3, 18)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }

            @Override
            public List<Slot> getSlots(BasicTeslaContainer container) {
                List<Slot> slots = super.getSlots(container);
                BoundingRectangle box = this.getBoundingBox();
                for (int x = 0; x < this.getItemHandlerForContainer().getSlots(); x++) {
                    slots.add(new FilteredSlot(this.getItemHandlerForContainer(), x, box.getLeft() + 1 + x * 18, box.getTop() + 1));
                }
                return slots;
            }

            @Override
            public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
                List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);

                BoundingRectangle box = this.getBoundingBox();
                pieces.add(new TiledRenderedGuiPiece(box.getLeft(), box.getTop(), 18, 18,
                        this.getItemHandlerForContainer().getSlots(), 1,
                        BasicTeslaGuiContainer.MACHINE_BACKGROUND, 108, 225, EnumDyeColor.PURPLE));

                return pieces;
            }
        });
        this.addInventoryToStorage(outputPotions, "pot_ener_out");
    }

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
        pieces.add(new BasicRenderedGuiPiece(0, 0, 16, 16, new ResourceLocation(Reference.MOD_ID, "textures/gui/machines.png"), 16, 16) {
            @Override
            public void drawBackgroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
                //super.drawBackgroundLayer(container, guiX, guiY, partialTicks, mouseX, mouseY);
                container.mc.getTextureManager().bindTexture(new ResourceLocation(Reference.MOD_ID, "textures/gui/machines.png"));
                if (action == 0) {
                    container.drawTexturedRect(18 * 4 + 11, 44, 78, 16 * 3, 15, 15);
                } else {
                    container.drawTexturedRect(18 * (action + 3) + 11, 43, 78, 16 * 2, 15, 15);
                }
            }

            @Override
            public void drawForegroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, int mouseX, int mouseY) {
                super.drawForegroundLayer(container, guiX, guiY, mouseX, mouseY);
                if (action == 0) {
                    if (mouseX - guiX > 18 * 4 + 11 && mouseY - guiY > 44 && mouseX - guiX < 18 * 4 + 11 + 15 && mouseY - guiY <= 44 + 15)
                        container.drawTooltip(Arrays.asList("Filling with water"), mouseX - guiX, mouseY - guiY);
                } else {
                    if (mouseX - guiX > 18 * (action + 3) + 11 && mouseY - guiY > 43 && mouseX - guiX < 18 * (action + 3) + 11 + 15 && mouseY - guiY <= 43 + 15)
                        container.drawTooltip(Arrays.asList("Adding ingredient"), mouseX - guiX, mouseY - guiY);
                }
            }
        });
        pieces.add(new BasicRenderedGuiPiece(18 * 4 + 10, 25 + 18 * 2, 0, 0, new ResourceLocation(Reference.MOD_ID, "textures/gui/machines.png"), 0, 0) {
            @Override
            public void drawBackgroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
                super.drawBackgroundLayer(container, guiX, guiY, partialTicks, mouseX, mouseY);
            }

            @Override
            public void drawMiddleLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
                super.drawMiddleLayer(container, guiX, guiY, partialTicks, mouseX, mouseY);
                if (inputGlassBottles.getStackInSlot(0).isEmpty())
                    ItemStackRenderUtils.renderItemIntoGUI(new ItemStack(Items.GLASS_BOTTLE), container.getGuiLeft() + 18 * 4 + 11, container.getGuiTop() + 26 + 18 * 2, 8);
                if (inputIngredients.getStackInSlot(0).isEmpty())
                    ItemStackRenderUtils.renderItemIntoGUI(new ItemStack(Items.NETHER_WART), container.getGuiLeft() + 18 * 4 + 11, container.getGuiTop() + 26, 8);
                if (inputIngredients.getStackInSlot(1).isEmpty()) {
                    ItemStackRenderUtils.renderItemIntoGUI(new ItemStack(Items.REDSTONE), container.getGuiLeft() + 18 * 5 + 11, container.getGuiTop() + 26, 9);
                    ItemStackRenderUtils.renderItemIntoGUI(new ItemStack(Items.GUNPOWDER), container.getGuiLeft() + 18 * 5 + 11, container.getGuiTop() + 26, 8);
                }
                if (inputIngredients.getStackInSlot(2).isEmpty()) {
                    ItemStackRenderUtils.renderItemIntoGUI(new ItemStack(Items.SUGAR), container.getGuiLeft() + 18 * 6 + 11, container.getGuiTop() + 26, 9);
                    ItemStackRenderUtils.renderItemIntoGUI(new ItemStack(Items.GLOWSTONE_DUST), container.getGuiLeft() + 18 * 6 + 11, container.getGuiTop() + 26, 8);
                }
                if (inputIngredients.getStackInSlot(3).isEmpty()) {
                    ItemStackRenderUtils.renderItemIntoGUI(new ItemStack(Items.FERMENTED_SPIDER_EYE), container.getGuiLeft() + 18 * 7 + 11, container.getGuiTop() + 26, 9);
                    ItemStackRenderUtils.renderItemIntoGUI(new ItemStack(Items.MAGMA_CREAM), container.getGuiLeft() + 18 * 7 + 11, container.getGuiTop() + 26, 8);
                }
                if (inputIngredients.getStackInSlot(4).isEmpty()) {
                    ItemStackRenderUtils.renderItemIntoGUI(new ItemStack(Items.GHAST_TEAR), container.getGuiLeft() + 18 * 8 + 11, container.getGuiTop() + 26, 9);
                    ItemStackRenderUtils.renderItemIntoGUI(new ItemStack(Items.DRAGON_BREATH), container.getGuiLeft() + 18 * 8 + 11, container.getGuiTop() + 26, 8);
                }
            }
        });

        return pieces;
    }

    @Override
    public float performWork() {
        //FILL BOTTLES
        //System.out.println(action);
        if (action > 5) action = 0;
        if (action != 0 && outputPotions.getStackInSlot(0).isEmpty() && outputPotions.getStackInSlot(1).isEmpty() && outputPotions.getStackInSlot(2).isEmpty()) {
            action = 0;
            return 1;
        }
        if (action == 0 && inputGlassBottles.getStackInSlot(0).getCount() >= 3 && ItemHandlerHelper.insertItem(outputPotions, new ItemStack(Items.POTIONITEM, 3), true).isEmpty() && fluidTank.getFluidAmount() >= 3000) { //DUMMY STACK
            ItemStack bottles = new ItemStack(Items.POTIONITEM, 3);
            bottles.setTagCompound(new PotionItems().fixTagCompound(bottles.getTagCompound() == null ? new NBTTagCompound() : bottles.getTagCompound()));
            ItemHandlerHelper.insertItem(outputPotions, bottles, false);
            fluidTank.drain(3000, true);
            inputGlassBottles.getStackInSlot(0).setCount(inputGlassBottles.getStackInSlot(0).getCount() - 3);
            action = 1;
            return 1;
        } else if (action > 0) {
            ItemStack ingredient = inputIngredients.getStackInSlot(action - 1);
            if (!ingredient.isEmpty()) {
                NonNullList<ItemStack> potions = NonNullList.create();
                potions.add(outputPotions.getStackInSlot(0));
                potions.add(outputPotions.getStackInSlot(1));
                potions.add(outputPotions.getStackInSlot(2));
                if (BrewingRecipeRegistry.hasOutput(potions.get(0), ingredient)) {
                    BrewingRecipeRegistry.brewPotions(potions, ingredient, new int[]{0, 1, 2});
                    for (int i = 0; i < 3; ++i) outputPotions.setStackInSlot(i, potions.get(i));
                    ++action;
                    ingredient.setCount(ingredient.getCount() - 1);
                    if (action > 5) action = 0;
                    return 1;
                }
            }
        }
        return 0;
    }

    @Override
    protected int getEnergyForWork() {
        return 5000;
    }

    @Override
    protected int getEnergyForWorkRate() {
        return 40;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tagCompound = super.writeToNBT(compound);
        tagCompound.setInteger(NBT_ACTION, action);
        return tagCompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (!compound.hasKey(NBT_ACTION)) action = 0;
        else action = compound.getInteger(NBT_ACTION);
    }

}

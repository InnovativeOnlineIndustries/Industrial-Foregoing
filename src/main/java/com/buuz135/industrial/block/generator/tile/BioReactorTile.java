package com.buuz135.industrial.block.generator.tile;

import com.buuz135.industrial.block.tile.IndustrialWorkingTile;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleGenerator;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IGuiAddon;
import com.hrznstudio.titanium.block.tile.fluid.PosFluidTank;
import com.hrznstudio.titanium.block.tile.fluid.SidedFluidTank;
import com.hrznstudio.titanium.block.tile.inventory.SidedInvHandler;
import com.hrznstudio.titanium.block.tile.progress.PosProgressBar;
import com.hrznstudio.titanium.client.gui.addon.ProgressBarGuiAddon;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.Tag;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.Tags;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BioReactorTile extends IndustrialWorkingTile {

    public static Tag<Item>[] VALID = new Tag[]{IndustrialTags.Items.BIOREACTOR_INPUT, Tags.Items.CROPS_CARROT, Tags.Items.CROPS_POTATO, Tags.Items.CROPS_NETHER_WART, Tags.Items.DYES,
            Tags.Items.HEADS, Tags.Items.MUSHROOMS, Tags.Items.SEEDS, IndustrialTags.Items.SAPLING};

    @Save
    private SidedFluidTank biofuel;
    @Save
    private SidedFluidTank water;
    @Save
    private SidedInvHandler input;
    @Save
    private PosProgressBar bar;

    public BioReactorTile() {
        super(ModuleGenerator.BIOREACTOR);
        addTank(water = (SidedFluidTank) new SidedFluidTank("water", 16000, 45, 20, 0).
                setColor(DyeColor.CYAN).
                setTile(this).
                setTankAction(PosFluidTank.Action.FILL).
                setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(Fluids.WATER))
        );
        addInventory(input = (SidedInvHandler) new SidedInvHandler("input", 69, 22, 9, 1).
                setColor(DyeColor.BLUE).
                setRange(3, 3).
                setInputFilter((stack, integer) -> canInsert(integer, stack)).
                setTile(this)
        );
        addTank(biofuel = (SidedFluidTank) new SidedFluidTank("biofuel", 16000, 74 + 18 * 3, 20, 2).
                setColor(DyeColor.PURPLE).
                setTile(this).
                setTankAction(PosFluidTank.Action.DRAIN).
                setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(ModuleCore.BIOFUEL.getSourceFluid()))
        );
        addProgressBar(bar = new PosProgressBar(96 + 18 * 3, 20, 100) {
                    @Override
                    public List<IFactory<? extends IGuiAddon>> getGuiAddons() {
                        return Collections.singletonList(() -> new ProgressBarGuiAddon(bar.getPosX(), bar.getPosY(), this) {
                            @Override
                            public List<String> getTooltipLines() {
                                return Arrays.asList(TextFormatting.GOLD + "Efficiency: " + TextFormatting.WHITE + getEfficiency() + TextFormatting.DARK_AQUA + "%");
                            }
                        });
                    }
                }.
                        setColor(DyeColor.YELLOW).
                        setCanIncrease(tileEntity -> true).
                        setOnTickWork(() -> bar.setProgress(getEfficiency())).
                        setTile(this)
        );
    }

    @Override
    public WorkAction work() {
        return new WorkAction(1, 0);
    }

    private boolean canInsert(int slot, ItemStack stack) {
        for (int i = 0; i < input.getSlots(); i++) {
            if (i != slot && input.getStackInSlot(i).isItemEqual(stack)) {
                return false;
            }
        }
        for (Tag<Item> itemTag : VALID) {
            if (itemTag.contains(stack.getItem())) return true;
        }
        return false;
    }

    private int getEfficiency() {
        int slots = 0;
        for (int i = 0; i < input.getSlots(); i++) {
            if (!input.getStackInSlot(i).isEmpty()) {
                ++slots;
            }
        }
        return (int) ((slots / 9D) * 100);
    }

}

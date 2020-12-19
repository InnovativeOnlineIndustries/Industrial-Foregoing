package com.buuz135.industrial.block.misc.tile;

import com.buuz135.industrial.block.tile.IndustrialProcessingTile;
import com.buuz135.industrial.config.machine.misc.EnchantmentExtractorConfig;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleMisc;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.StateButtonAddon;
import com.hrznstudio.titanium.client.screen.addon.StateButtonInfo;
import com.hrznstudio.titanium.component.button.ButtonComponent;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.util.LangUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EnchantmentExtractorTile extends IndustrialProcessingTile<EnchantmentExtractorTile> {

    @Save
    private boolean extractEnchants;
    @Save
    private SidedInventoryComponent<EnchantmentExtractorTile> inputEnchantedItem;
    @Save
    private SidedInventoryComponent<EnchantmentExtractorTile> inputBook;
    @Save
    private SidedInventoryComponent<EnchantmentExtractorTile> outputEnchantedBook;
    @Save
    private SidedInventoryComponent<EnchantmentExtractorTile> outputNoEnchantedItem;
    @Save
    private SidedFluidTankComponent<EnchantmentExtractorTile> tank;
    private ButtonComponent buttonComponent;

    public EnchantmentExtractorTile() {
        super(ModuleMisc.ENCHANTMENT_EXTRACTOR, 62, 40);
        this.extractEnchants = true;
        this.addInventory(inputEnchantedItem = (SidedInventoryComponent<EnchantmentExtractorTile>) new SidedInventoryComponent<EnchantmentExtractorTile>("inputEnchantedItem", 40, 22, 1, 0).
                setColor(DyeColor.BLUE).
                setSlotLimit(1).
                setInputFilter((stack, integer) -> stack.isEnchanted()).
                setOutputFilter((stack, integer) -> false).
                setComponentHarness(this)
        );
        this.addInventory(inputBook = (SidedInventoryComponent<EnchantmentExtractorTile>) new SidedInventoryComponent<EnchantmentExtractorTile>("inputBook", 40, 58, 1, 1).
                setColor(DyeColor.BROWN).
                setInputFilter((stack, integer) -> stack.getItem().equals(Items.BOOK)).
                setOutputFilter((stack, integer) -> false).
                setSlotToItemStackRender(0, new ItemStack(Items.BOOK)).
                setComponentHarness(this)
        );
        this.addInventory(outputNoEnchantedItem = (SidedInventoryComponent<EnchantmentExtractorTile>) new SidedInventoryComponent<EnchantmentExtractorTile>("outputNoEnchantedItem", 90, 22, 3, 2).
                setColor(DyeColor.ORANGE).
                setInputFilter((stack, integer) -> false).
                setComponentHarness(this)
        );
        this.addInventory(outputEnchantedBook = (SidedInventoryComponent<EnchantmentExtractorTile>) new SidedInventoryComponent<EnchantmentExtractorTile>("outputEnchantedBook", 90, 58, 3, 3).
                setColor(DyeColor.MAGENTA).
                setInputFilter((stack, integer) -> false).
                setComponentHarness(this)
        );
        this.addTank(tank = (SidedFluidTankComponent<EnchantmentExtractorTile>) new SidedFluidTankComponent<EnchantmentExtractorTile>("essence", EnchantmentExtractorConfig.tankSize, 150, 20, 4).
                setColor(DyeColor.LIME).
                setTankAction(FluidTankComponent.Action.DRAIN).
                setComponentHarness(this).
                setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(ModuleCore.ESSENCE.getSourceFluid()))
        );
        this.addButton(buttonComponent = new ButtonComponent(62 + 4, 40 + 18, 14, 14) {
            @Override
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                return Collections.singletonList(() -> new StateButtonAddon(this,
                        new StateButtonInfo(0, AssetTypes.BUTTON_SIDENESS_ENABLED, TextFormatting.GOLD + LangUtil.getString("tooltip.industrialforegoing.enchantment_extractor.extract"), "tooltip.industrialforegoing.enchantment_extractor.extract_extra"),
                        new StateButtonInfo(1, AssetTypes.BUTTON_SIDENESS_DISABLED, TextFormatting.GOLD + LangUtil.getString("tooltip.industrialforegoing.enchantment_extractor.consume"), "tooltip.industrialforegoing.enchantment_extractor.consume_extra")) {
                    @Override
                    public int getState() {
                        return extractEnchants ? 0 : 1;
                    }
                });
            }
        }.setPredicate((playerEntity, compoundNBT) -> {
            this.extractEnchants = !this.extractEnchants;
            markForUpdate();
        }));
    }

    @Override
    public boolean canIncrease() {
        return !this.inputEnchantedItem.getStackInSlot(0).isEmpty() && !ItemStackUtils.isInventoryFull(this.outputNoEnchantedItem) && (this.extractEnchants ? !this.inputBook.getStackInSlot(0).isEmpty() && !ItemStackUtils.isInventoryFull(this.outputEnchantedBook) : this.tank.getCapacity() >= this.tank.getFluidAmount() + getEnchantmentXp(this.inputEnchantedItem.getStackInSlot(0)) * 20);
    }

    @Override
    public Runnable onFinish() {
        return () -> {
            ItemStack input = this.inputEnchantedItem.getStackInSlot(0);
            if (extractEnchants) {
                Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(input).entrySet().stream().filter((enchantmentPair) -> !enchantmentPair.getKey().isCurse()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                if (map.size() > 0) {
                    Enchantment selected = map.keySet().iterator().next();
                    ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                    EnchantmentHelper.setEnchantments(Collections.singletonMap(selected, map.get(selected)), book);
                    if (map.size() == 1) {
                        ItemStack output = removeEnchantments(input, input.getDamage(), input.getCount());
                        input.shrink(1);
                        ItemHandlerHelper.insertItem(this.outputNoEnchantedItem, output, false);
                    } else {
                        Map<Enchantment, Integer> cleanMap = EnchantmentHelper.getEnchantments(input).entrySet().stream().filter((enchantmentPair) -> !enchantmentPair.getKey().equals(selected)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                        EnchantmentHelper.setEnchantments(cleanMap, input);
                    }
                    ItemHandlerHelper.insertItem(this.outputEnchantedBook, book, false);
                    this.inputBook.getStackInSlot(0).shrink(1);
                }
            } else {
                int essence = getEnchantmentXp(input) * 20;
                ItemStack output = removeEnchantments(input, input.getDamage(), input.getCount());
                input.shrink(1);
                ItemHandlerHelper.insertItem(this.outputNoEnchantedItem, output, false);
                this.tank.fillForced(new FluidStack(ModuleCore.ESSENCE.getSourceFluid(), essence), IFluidHandler.FluidAction.EXECUTE);
            }
        };
    }

    @Override
    protected int getTickPower() {
        return EnchantmentExtractorConfig.powerPerTick;
    }

    @Override
    public int getMaxProgress() {
        return EnchantmentExtractorConfig.maxProgress;
    }

    @Override
    public EnchantmentExtractorTile getSelf() {
        return this;
    }

    @Override
    protected EnergyStorageComponent<EnchantmentExtractorTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(EnchantmentExtractorConfig.maxStoredPower, 10, 20);
    }

    private int getEnchantmentXp(ItemStack stack) {
        int xp = 0;
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);
        for (Map.Entry<Enchantment, Integer> entry : map.entrySet()) {
            Enchantment enchantment = entry.getKey();
            Integer integer = entry.getValue();
            if (!enchantment.isCurse()) {
                xp += enchantment.getMinEnchantability(integer);
            }
        }
        return xp;
    }

    private ItemStack removeEnchantments(ItemStack stack, int damage, int count) {
        ItemStack itemstack = stack.copy();
        itemstack.removeChildTag("Enchantments");
        itemstack.removeChildTag("StoredEnchantments");
        if (damage > 0) {
            itemstack.setDamage(damage);
        } else {
            itemstack.removeChildTag("Damage");
        }
        itemstack.setCount(count);
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack).entrySet().stream().filter((enchantmentPair) -> enchantmentPair.getKey().isCurse()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        EnchantmentHelper.setEnchantments(map, itemstack);
        itemstack.setRepairCost(0);
        if (itemstack.getItem() == Items.ENCHANTED_BOOK && map.size() == 0) {
            itemstack = new ItemStack(Items.BOOK);
            if (stack.hasDisplayName()) {
                itemstack.setDisplayName(stack.getDisplayName());
            }
        }

        for (int i = 0; i < map.size(); ++i) {
            itemstack.setRepairCost(RepairContainer.getNewRepairCost(itemstack.getRepairCost()));
        }

        return itemstack;
    }

}

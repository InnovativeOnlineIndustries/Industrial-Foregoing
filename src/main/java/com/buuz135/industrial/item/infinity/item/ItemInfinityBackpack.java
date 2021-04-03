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
package com.buuz135.industrial.item.infinity.item;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.capability.BackpackCapabilityProvider;
import com.buuz135.industrial.capability.MultipleFluidHandlerScreenProviderItemStack;
import com.buuz135.industrial.container.BackpackContainer;
import com.buuz135.industrial.gui.component.SlotDefinitionGuiAddon;
import com.buuz135.industrial.item.infinity.InfinityEnergyStorage;
import com.buuz135.industrial.item.infinity.InfinityTier;
import com.buuz135.industrial.item.infinity.ItemInfinity;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleTool;
import com.buuz135.industrial.module.ModuleTransportStorage;
import com.buuz135.industrial.proxy.CommonProxy;
import com.buuz135.industrial.proxy.network.BackpackOpenedMessage;
import com.buuz135.industrial.proxy.network.BackpackSyncMessage;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import com.buuz135.industrial.utils.IndustrialTags;
import com.buuz135.industrial.worlddata.BackpackDataManager;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.capability.FluidHandlerScreenProviderItemStack;
import com.hrznstudio.titanium.client.screen.addon.AssetScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.StateButtonAddon;
import com.hrznstudio.titanium.client.screen.addon.StateButtonInfo;
import com.hrznstudio.titanium.component.button.ButtonComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.itemstack.ItemStackHarnessRegistry;
import com.hrznstudio.titanium.network.locator.LocatorFactory;
import com.hrznstudio.titanium.network.locator.PlayerInventoryFinder;
import com.hrznstudio.titanium.network.locator.instance.HeldStackLocatorInstance;
import com.hrznstudio.titanium.network.locator.instance.InventoryStackLocatorInstance;
import net.minecraft.block.BlockState;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemInfinityBackpack extends ItemInfinity {

    public static int POWER_CONSUMPTION = 0;
    public static int FUEL_CONSUMPTION = 1;

    private static String NBT_MAGNET = "Magnet";
    private static String NBT_PICKUP = "AutoPickUpMode";

    public ItemInfinityBackpack() {
        super("infinity_backpack", ModuleTool.TAB_TOOL, new Properties().maxStackSize(1), POWER_CONSUMPTION, FUEL_CONSUMPTION, false);
        this.disableArea();
        EventManager.forge(EntityItemPickupEvent.class).filter(entityItemPickupEvent -> !entityItemPickupEvent.getItem().getItem().isEmpty()).process(entityItemPickupEvent -> {
            for (PlayerInventoryFinder.Target target : findAllBackpacks(entityItemPickupEvent.getPlayer())) {
                ItemStack stack = target.getFinder().getStackGetter().apply(entityItemPickupEvent.getPlayer(), target.getSlot());
                if (!stack.isEmpty()) {
                    if (stack.getItem() instanceof ItemInfinityBackpack && (getPickUpMode(stack) == 1 || getPickUpMode(stack) == 0)) {
                        BackpackDataManager manager = BackpackDataManager.getData(entityItemPickupEvent.getItem().world);
                        if (manager != null && stack.getOrCreateTag().contains("Id")) {
                            BackpackDataManager.BackpackItemHandler handler = manager.getBackpack(stack.getOrCreateTag().getString("Id"));
                            if (handler != null) {
                                ItemStack picked = entityItemPickupEvent.getItem().getItem();
                                for (int pos = 0; pos < handler.getSlots(); pos++) {
                                    ItemStack slotStack = handler.getSlotDefinition(pos).getStack().copy();
                                    slotStack.setCount(1);
                                    if (!slotStack.isEmpty() && slotStack.getStack().isItemEqual(picked) && ItemStack.areItemStackTagsEqual(slotStack, picked)) {
                                        ItemStack returned = handler.insertItem(pos, picked.copy(), false);
                                        picked.setCount(returned.getCount());
                                        entityItemPickupEvent.setResult(Event.Result.ALLOW);
                                        if (entityItemPickupEvent.getPlayer() instanceof ServerPlayerEntity) {
                                            sync(entityItemPickupEvent.getPlayer().world, stack.getOrCreateTag().getString("Id"), (ServerPlayerEntity) entityItemPickupEvent.getPlayer());
                                        }
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }).subscribe();
        EventManager.forge(PlayerXpEvent.PickupXp.class).filter(pickupXp -> pickupXp.getOrb().isAlive()).process(pickupXp -> {
            findFirstBackpack(pickupXp.getPlayer()).ifPresent(target -> {
                ItemStack stack = target.getFinder().getStackGetter().apply(pickupXp.getPlayer(), target.getSlot());
                if (!stack.isEmpty()) {
                    if (stack.getItem() instanceof ItemInfinityBackpack && (getPickUpMode(stack) == 2 || getPickUpMode(stack) == 0)) {
                        if (stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
                            ExperienceOrbEntity entity = pickupXp.getOrb();
                            IFluidHandlerItem handlerItem = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
                            if (handlerItem != null) {
                                if (handlerItem.fill(new FluidStack(ModuleCore.ESSENCE.getSourceFluid(), entity.getXpValue() * 20), IFluidHandler.FluidAction.SIMULATE) > 0){
                                    handlerItem.fill(new FluidStack(ModuleCore.ESSENCE.getSourceFluid(), entity.getXpValue() * 20), IFluidHandler.FluidAction.EXECUTE);
                                    entity.remove();
                                    pickupXp.setCanceled(true);
                                }

                            }
                        }
                    }
                }
            });
        }).subscribe();
    }

    public static int getSlotSize(ItemStack stack) {
        InfinityTier braquet = InfinityTier.getTierBraquet(getPowerFromStack(stack)).getLeft();
        return braquet == InfinityTier.ARTIFACT ? Integer.MAX_VALUE : (int) (2048 * Math.pow(8, braquet.getRadius()));
    }

    public static void sync(World world, String id, ServerPlayerEntity player) {
        IndustrialForegoing.NETWORK.get().sendTo(new BackpackSyncMessage(id, BackpackDataManager.getData(world).getBackpack(id).serializeNBT()), player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
    }

    public static boolean isMagnetEnabled(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(NBT_MAGNET);
    }

    public static void setMagnet(ItemStack stack, boolean enabled) {
        stack.getOrCreateTag().putBoolean(NBT_MAGNET, enabled);
    }

    public static int getPickUpMode(ItemStack stack) {
        return stack.getOrCreateTag().getInt(NBT_PICKUP);
    }

    public static void setPickUpMode(ItemStack stack, int mode) {
        stack.getOrCreateTag().putInt(NBT_PICKUP, mode);
    }

    /**
     * Generates a list of {@link PlayerInventoryFinder.Target}s defining the
     * {@link ItemInfinityBackpack}s present in the given {@link PlayerEntity}'s inventory.
     *
     * @param entity The {@link PlayerEntity} whose inventory should be searched for {@link ItemInfinityBackpack}s.
     * @return The list of all {@link ItemInfinityBackpack}s contained in the given {@link PlayerEntity}s
     * inventory. The returned list is ordered by inventory slot ID.
     */
    public static List<PlayerInventoryFinder.Target> findAllBackpacks(PlayerEntity entity) {
        List<PlayerInventoryFinder.Target> list = new ArrayList<>();
        for (String name : PlayerInventoryFinder.FINDERS.keySet()) {
            PlayerInventoryFinder finder = PlayerInventoryFinder.FINDERS.get(name);
            for (int i = 0; i < finder.getSlotAmountGetter().apply(entity); i++) {
                if (finder.getStackGetter().apply(entity, i).getItem() instanceof ItemInfinityBackpack) {
                    list.add(new PlayerInventoryFinder.Target(name, finder, i));
                }
            }
        }
        return Collections.unmodifiableList(list);
    }

    public static Optional<PlayerInventoryFinder.Target> findFirstBackpack(PlayerEntity entity) {
        for (String name : PlayerInventoryFinder.FINDERS.keySet()) {
            PlayerInventoryFinder finder = PlayerInventoryFinder.FINDERS.get(name);
            for (int i = 0; i < finder.getSlotAmountGetter().apply(entity); i++) {
                if (finder.getStackGetter().apply(entity, i).getItem() instanceof ItemInfinityBackpack) {
                    return Optional.of(new PlayerInventoryFinder.Target(name, finder, i));
                }
            }
        }
        return Optional.empty();
    }



    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new BackpackCapabilityProvider(stack, getTankConstructor(stack), getEnergyConstructor(stack));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity player, Hand handIn) {
        ItemStack stack = player.getHeldItem(handIn);
        if (player instanceof ServerPlayerEntity) {
            if (!stack.hasTag() || !stack.getTag().contains("Id")) {
                UUID id = UUID.randomUUID();
                CompoundNBT nbt = stack.getOrCreateTag();
                nbt.putString("Id", id.toString());
                BackpackDataManager.getData(worldIn).createBackPack(id);
                stack.setTag(nbt);
            }
            String id = stack.getTag().getString("Id");
            IndustrialForegoing.NETWORK.get().sendTo(new BackpackOpenedMessage(player.inventory.currentItem, PlayerInventoryFinder.MAIN), ((ServerPlayerEntity) player).connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
            sync(worldIn, id, (ServerPlayerEntity) player);
            NetworkHooks.openGui((ServerPlayerEntity) player, this, buffer ->
                    LocatorFactory.writePacketBuffer(buffer, new HeldStackLocatorInstance(handIn == Hand.MAIN_HAND)));
            return ActionResult.resultSuccess(player.getHeldItem(handIn));
        }
        if (CommonProxy.CONTRIBUTORS.contains(player.getUniqueID().toString())) {
            player.getHeldItem(handIn).getOrCreateTag().putBoolean("Special", true);
        }
        return super.onItemRightClick(worldIn, player, handIn);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (isMagnetEnabled(stack)) {
            for (ItemEntity itemEntity : entityIn.world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosY(), entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ()).grow(5))) {
                if (entityIn.world.isRemote) {
                    if (itemEntity.isOnGround() && worldIn.rand.nextInt(5) < 1)
                        itemEntity.world.addParticle(ParticleTypes.PORTAL, itemEntity.getPosX(), itemEntity.getPosY() + 0.5, itemEntity.getPosZ(), 0, -0.5, 0);
                } else {
                    if (!itemEntity.cannotPickup() && enoughFuel(stack)) {
                        itemEntity.setPositionAndUpdate(entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ());
                        consumeFuel(stack);
                    }
                }
            }
        }
        if (!entityIn.world.isRemote && entityIn instanceof PlayerEntity) {
            if (enoughFuel(stack)) {
                if ((((PlayerEntity) entityIn).getFoodStats().needFood() || ((PlayerEntity) entityIn).getFoodStats().getSaturationLevel() < 10) && stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
                    IFluidHandlerItem handlerItem = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElseGet(null);
                    if (handlerItem instanceof MultipleFluidHandlerScreenProviderItemStack) {
                        FluidStack fluidStack = handlerItem.getFluidInTank(2);
                        if (!fluidStack.isEmpty() && fluidStack.getAmount() >= 400) {
                            ((MultipleFluidHandlerScreenProviderItemStack) handlerItem).setFluidInTank(2, new FluidStack(ModuleCore.MEAT.getSourceFluid(), fluidStack.getAmount() - 400));
                            ((PlayerEntity) entityIn).getFoodStats().addStats(1, 1);
                            consumeFuel(stack);
                        }
                    }
                }
            }
            if (entityIn.world.getGameTime() % 10 == 0) {
                BackpackDataManager manager = BackpackDataManager.getData(entityIn.world);
                if (manager != null && stack.getOrCreateTag().contains("Id")) {
                    BackpackDataManager.BackpackItemHandler handler = manager.getBackpack(stack.getOrCreateTag().getString("Id"));
                    if (handler != null) {
                        if (getSlotSize(stack) != handler.getSlotLimit(0)) {
                            handler.setMaxAmount(getSlotSize(stack));
                        }
                        if (enoughFuel(stack)) {
                            for (int i = 0; i < BackpackDataManager.SLOT_AMOUNT; i++) {
                                if (!handler.getStackInSlot(i).isEmpty() && handler.getSlotDefinition(i).isRefillItems()) {
                                    PlayerInventory inventory = ((PlayerEntity) entityIn).inventory;
                                    for (int inv = 0; inv <= 35; inv++) {
                                        ItemStack inventoryStack = inventory.getStackInSlot(inv);
                                        if (!inventoryStack.isEmpty() && inventoryStack.getCount() < inventoryStack.getMaxStackSize() && handler.isItemValid(i, inventoryStack) && enoughFuel(stack)) {
                                            int extracting = inventoryStack.getMaxStackSize() - inventoryStack.getCount();
                                            ItemStack extractedSLot = handler.extractItem(i, extracting, false);
                                            inventoryStack.setCount(inventoryStack.getCount() + extractedSLot.getCount());
                                            if (entityIn instanceof ServerPlayerEntity)
                                                sync(entityIn.world, stack.getOrCreateTag().getString("Id"), (ServerPlayerEntity) entityIn);
                                            consumeFuel(stack);
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public IFactory<FluidHandlerScreenProviderItemStack> getTankConstructor(ItemStack stack) {
        int y = 88;
        return () -> new MultipleFluidHandlerScreenProviderItemStack(stack, 1_000_000,
                new MultipleFluidHandlerScreenProviderItemStack.TankDefinition("biofuel", -21, y + 25 * 0, fluidStack -> fluidStack.getFluid().isEquivalentTo(ModuleCore.BIOFUEL.getSourceFluid()), false, true, FluidTankComponent.Type.SMALL),
                new MultipleFluidHandlerScreenProviderItemStack.TankDefinition("essence", -21, y + 25 * 1, fluidStack -> fluidStack.getFluid().isIn(IndustrialTags.Fluids.EXPERIENCE), true, true, FluidTankComponent.Type.SMALL),
                new MultipleFluidHandlerScreenProviderItemStack.TankDefinition("meat", -21, y + 25 * 2, fluidStack -> fluidStack.getFluid().isEquivalentTo(ModuleCore.MEAT.getSourceFluid()), false, true, FluidTankComponent.Type.SMALL)
        );
    }

    @Override
    public IFactory<InfinityEnergyStorage> getEnergyConstructor(ItemStack stack) {
        return () -> new InfinityEnergyStorage(InfinityTier.ARTIFACT.getPowerNeeded(), -21, 24) {
            @Override
            public long getLongEnergyStored() {
                if (stack.hasTag()) {
                    return Math.min(stack.getTag().getLong("Energy"), InfinityTier.ARTIFACT.getPowerNeeded());
                } else {
                    return 0;
                }
            }

            @Override
            public void setEnergyStored(long energy) {
                if (!stack.hasTag()) {
                    stack.setTag(new CompoundNBT());
                }
                stack.getTag().putLong("Energy", Math.min(energy, InfinityTier.ARTIFACT.getPowerNeeded()));
            }

            @Override
            public boolean canReceive() {
                return ItemInfinity.canCharge(stack);
            }
        };
    }

    @Override
    public void handleButtonMessage(int id, PlayerEntity playerEntity, CompoundNBT compound) {
        findFirstBackpack(playerEntity).ifPresent(target -> {
            ItemStack stack = target.getFinder().getStackGetter().apply(playerEntity, target.getSlot());
            if (!stack.isEmpty()) {
                if (id == 4 && playerEntity instanceof ServerPlayerEntity) {
                    String backpackId = compound.getString("Id");
                    ItemStack cursor = playerEntity.inventory.getItemStack();
                    boolean shift = compound.getBoolean("Shift");
                    boolean ctrl = compound.getBoolean("Ctrl");
                    int button = compound.getInt("Button");
                    int slot = compound.getInt("Slot");
                    BackpackDataManager dataManager = BackpackDataManager.getData(playerEntity.world);
                    BackpackDataManager.BackpackItemHandler handler = dataManager.getBackpack(backpackId);
                    ItemStack result = ItemStack.EMPTY;
                    boolean hasCursorChanged = false;
                    if (button == 2) {//MIDDLE
                        ItemStack simulated = handler.extractItem(slot, 1, true);
                        if (!simulated.isEmpty() && (cursor.isEmpty() || ItemHandlerHelper.canItemStacksStack(simulated, cursor))) {
                            result = handler.extractItem(slot, 1, false);
                            result.setCount(cursor.getCount() + 1);
                            hasCursorChanged = true;
                        }
                    } else if (cursor.isEmpty()) {
                        int maxStack = handler.getStackInSlot(slot).getMaxStackSize();
                        if (button == 0) { //LEFT
                            if (ctrl) {
                                BackpackDataManager.SlotDefinition definition = handler.getSlotDefinition(slot);
                                definition.setVoidItems(!definition.isVoidItems());
                            } else {
                                if (shift) {
                                    if (handler.getSlotDefinition(slot).getAmount() == 0) {
                                        handler.getSlotDefinition(slot).setStack(ItemStack.EMPTY);
                                        handler.getSlotDefinition(slot).setAmount(0);
                                    } else {
                                        ItemHandlerHelper.giveItemToPlayer(playerEntity, handler.extractItem(slot, maxStack, false));
                                    }
                                } else {
                                    result = handler.extractItem(slot, maxStack, false);
                                    hasCursorChanged = true;
                                }
                            }
                        } else if (button == 1) { //RIGHT
                            if (ctrl) {
                                BackpackDataManager.SlotDefinition definition = handler.getSlotDefinition(slot);
                                definition.setRefillItems(!definition.isRefillItems());
                            } else {
                                result = handler.extractItem(slot, maxStack / 2, false);
                                hasCursorChanged = true;
                            }
                        }
                    } else {
                        if (button == 0) {
                            result = handler.insertItem(slot, cursor, false);
                            hasCursorChanged = true;
                        } else if (button == 1 && handler.insertItem(slot, ItemHandlerHelper.copyStackWithSize(cursor, 1), false).isEmpty()) {
                            cursor.shrink(1);
                            result = cursor;
                            hasCursorChanged = true;
                        }
                    }
                    if (hasCursorChanged) {
                        playerEntity.inventory.setItemStack(result);
                        ((ServerPlayerEntity) playerEntity).updateHeldItem();
                    }
                    sync(playerEntity.world, backpackId, (ServerPlayerEntity) playerEntity);
                }
                if (id == 10) {
                    setMagnet(stack, !isMagnetEnabled(stack));
                }
                if (id == 11) {
                    setPickUpMode(stack, (getPickUpMode(stack) + 1) % 4);
                }
                if (id == 3) {
                    setCanCharge(stack, !canCharge(stack));
                }
                if (id == -10) {
                    setSpecialEnabled(stack, !isSpecialEnabled(stack));
                }
            }
        });
    }

    public void addNbt(ItemStack stack, long power, int fuel, boolean special) {
        CompoundNBT tagCompound = new CompoundNBT();
        tagCompound.putLong("Energy", power);
        tagCompound.putBoolean("Special", special);
        tagCompound.putString("Selected", InfinityTier.getTierBraquet(power).getLeft().name());
        tagCompound.putBoolean("CanCharge", true);
        stack.setTag(tagCompound);
    }

    @Override
    public void addTooltipDetails(@Nullable Key key, ItemStack stack, List<ITextComponent> tooltip, boolean advanced) {
        //tooltip.add(stack.getOrCreateTag().toFormattedComponent());
        tooltip.add(new StringTextComponent(TextFormatting.GRAY + new TranslationTextComponent("text.industrialforegoing.tooltip.can_hold").getString() + ": " + TextFormatting.DARK_AQUA + NumberFormat.getInstance(Locale.ROOT).format(getSlotSize(stack)) + TextFormatting.GRAY + " " + new TranslationTextComponent("text.industrialforegoing.tooltip.items").getString()));
        super.addTooltipDetails(key, stack, tooltip, advanced);
    }

    @Override
    public int getFuelFromStack(ItemStack stack) {
        int fuelAmount = 0;
        if (stack.hasTag() && stack.getTag().contains("Tanks") && stack.getTag().getCompound("Tanks").contains("biofuel")) {
            fuelAmount = stack.getTag().getCompound("Tanks").getCompound("biofuel").getInt("Amount");
        }
        return fuelAmount;
    }

    @Override
    public boolean enoughFuel(ItemStack stack) {
        int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);
        return getFuelFromStack(stack) >= FUEL_CONSUMPTION  * ( 1 / (i + 1)) ;
    }

    @Override
    public void consumeFuel(ItemStack stack) {
        int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);
        if (getFuelFromStack(stack) >= FUEL_CONSUMPTION  * ( 1 / (i + 1)) ) {
            stack.getTag().getCompound("Tanks").getCompound("biofuel").putInt("Amount", Math.max(0, getFuelFromStack(stack) - FUEL_CONSUMPTION  * ( 1 / (i + 1)) ));
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = MultimapBuilder.hashKeys().arrayListValues().build();
        return multimap;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return Enchantments.UNBREAKING == enchantment;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return 1;
    }

    @Nullable
    @Override
    public Container createMenu(int menu, PlayerInventory p_createMenu_2_, PlayerEntity playerEntity) {
        return findFirstBackpack(playerEntity).map(target -> {
            ItemStack stack = target.getFinder().getStackGetter().apply(playerEntity, target.getSlot());
            if (!stack.isEmpty()) {
                if (!stack.hasTag() || !stack.getTag().contains("Id")) {
                    UUID id = UUID.randomUUID();
                    CompoundNBT nbt = stack.getOrCreateTag();
                    nbt.putString("Id", id.toString());
                    BackpackDataManager.getData(playerEntity.world).createBackPack(id);
                    stack.setTag(nbt);
                }
                String id = stack.getTag().getString("Id");
                return new BackpackContainer(ItemStackHarnessRegistry.getHarnessCreators().get(this).apply(stack), new InventoryStackLocatorInstance(target.getName(), target.getSlot()), new IWorldPosCallable() {
                    @Override
                    public <T> Optional<T> apply(BiFunction<World, BlockPos, T> p_221484_1_) {
                        return Optional.empty();
                    }
                }, playerEntity.inventory, menu, id);
            }
            return null;
        }).orElse(null);
    }

    @Override
    public List<IFactory<? extends IScreenAddon>> getScreenAddons(Supplier<ItemStack> stack) {
        List<IFactory<? extends IScreenAddon>> factory = new ArrayList<>();
        for (int i = 0; i < BackpackDataManager.SLOT_AMOUNT; i++) {
            int x = i % 8;
            int y = i / 8;
            int finalI = i;
            factory.add(() -> new SlotDefinitionGuiAddon(new ButtonComponent(16 + 18 * x, 21 + 18 * y, 18, 18).setId(4), finalI) {
                @Override
                public ItemStack getItemStack() {
                    return stack.get();
                }
            });
        }
        factory.add(() -> new AssetScreenAddon(AssetTypes.AUGMENT_BACKGROUND_LEFT_TALL, -27, 10, true));
        factory.add(() -> new AssetScreenAddon(AssetTypes.AUGMENT_BACKGROUND, 175, 10, true));
        int x = 181;
        int y = 19;
        factory.add(() -> new StateButtonAddon(new ButtonComponent(x, 16 + y * 0, 14, 14).setId(10), new StateButtonInfo(0, AssetTypes.BUTTON_SIDENESS_ENABLED, TextFormatting.GREEN + new TranslationTextComponent("tooltip.industrialforegoing.backpack.magnet_enabled").getString()), new StateButtonInfo(1, AssetTypes.BUTTON_SIDENESS_DISABLED, TextFormatting.RED + new TranslationTextComponent("tooltip.industrialforegoing.backpack.magnet_disabled").getString())) {
            @Override
            public int getState() {
                return isMagnetEnabled(stack.get()) ? 0 : 1;
            }
        });
        factory.add(() -> new StateButtonAddon(new ButtonComponent(x, 16 + y * 1, 14, 14).setId(11),
                new StateButtonInfo(0, AssetTypes.BUTTON_SIDENESS_ENABLED,
                        TextFormatting.GREEN + new TranslationTextComponent("tooltip.industrialforegoing.backpack.pickup_all").getString(),
                        TextFormatting.GRAY + new TranslationTextComponent("tooltip.industrialforegoing.backpack.pickup_extra").getString(),
                        TextFormatting.GRAY + new TranslationTextComponent("tooltip.industrialforegoing.backpack.pickup_extra_1").getString()),
                new StateButtonInfo(1, AssetTypes.BUTTON_SIDENESS_PULL,
                        TextFormatting.GREEN + new TranslationTextComponent("tooltip.industrialforegoing.backpack.item_pickup_enabled").getString(),
                        TextFormatting.GRAY + new TranslationTextComponent("tooltip.industrialforegoing.backpack.pickup_extra").getString(),
                        TextFormatting.GRAY + new TranslationTextComponent("tooltip.industrialforegoing.backpack.pickup_extra_1").getString()),
                new StateButtonInfo(2, AssetTypes.BUTTON_SIDENESS_PUSH,
                        TextFormatting.GREEN + new TranslationTextComponent("tooltip.industrialforegoing.backpack.xp_pickup_enabled").getString(),
                        TextFormatting.GRAY + new TranslationTextComponent("tooltip.industrialforegoing.backpack.pickup_extra").getString(),
                        TextFormatting.GRAY + new TranslationTextComponent("tooltip.industrialforegoing.backpack.pickup_extra_1").getString()),
                new StateButtonInfo(3, AssetTypes.BUTTON_SIDENESS_DISABLED,
                        TextFormatting.RED + new TranslationTextComponent("tooltip.industrialforegoing.backpack.pickup_disabled").getString(),
                        TextFormatting.GRAY + new TranslationTextComponent("tooltip.industrialforegoing.backpack.pickup_extra").getString(),
                        TextFormatting.GRAY + new TranslationTextComponent("tooltip.industrialforegoing.backpack.pickup_extra_1").getString())) {
            @Override
            public int getState() {
                return getPickUpMode(stack.get());
            }
        });
        factory.add(() -> new StateButtonAddon(new ButtonComponent(x, 16 + y * 2, 14, 14).setId(3), new StateButtonInfo(0, AssetTypes.BUTTON_SIDENESS_ENABLED, TextFormatting.GREEN + new TranslationTextComponent("text.industrialforegoing.display.charging").getString() + new TranslationTextComponent("text.industrialforegoing.display.enabled").getString()), new StateButtonInfo(1, AssetTypes.BUTTON_SIDENESS_DISABLED, TextFormatting.RED + new TranslationTextComponent("text.industrialforegoing.display.charging").getString() + new TranslationTextComponent("text.industrialforegoing.display.disabled").getString())) {
            @Override
            public int getState() {
                return ItemInfinity.canCharge(stack.get()) ? 0 : 1;
            }
        });
        if (isSpecial(stack.get())) {
            factory.add(() -> new StateButtonAddon(new ButtonComponent(x, 16 + y * 3, 14, 14).setId(-10), new StateButtonInfo(0, AssetTypes.BUTTON_SIDENESS_ENABLED, TextFormatting.GOLD + new TranslationTextComponent("text.industrialforegoing.display.special").getString()), new StateButtonInfo(1, AssetTypes.BUTTON_SIDENESS_DISABLED, TextFormatting.GOLD + new TranslationTextComponent("text.industrialforegoing.display.special").getString())) {
                @Override
                public int getState() {
                    return ModuleTool.INFINITY_BACKPACK.isSpecialEnabled(stack.get()) ? 0 : 1;
                }
            });
        }
        return factory;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        new DissolutionChamberRecipe(this.getRegistryName(),
                new Ingredient.IItemList[]{
                        new Ingredient.SingleItemList(new ItemStack(ModuleTransportStorage.BLACK_HOLE_UNIT_COMMON)),
                        new Ingredient.TagList(IndustrialTags.Items.GEAR_DIAMOND),
                        new Ingredient.SingleItemList(new ItemStack(ModuleTransportStorage.BLACK_HOLE_UNIT_COMMON)),
                        new Ingredient.SingleItemList(new ItemStack(ModuleTransportStorage.BLACK_HOLE_TANK_COMMON)),
                        new Ingredient.SingleItemList(new ItemStack(ModuleTransportStorage.BLACK_HOLE_TANK_COMMON)),
                        new Ingredient.TagList(IndustrialTags.Items.GEAR_GOLD),
                        new Ingredient.TagList(IndustrialTags.Items.GEAR_GOLD),
                        new Ingredient.TagList(IndustrialTags.Items.GEAR_GOLD),
                },
                new FluidStack(ModuleCore.PINK_SLIME.getSourceFluid(), 2000), 400, new ItemStack(this), FluidStack.EMPTY);
    }

}

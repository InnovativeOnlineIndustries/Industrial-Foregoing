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
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkHooks;

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
        super("infinity_backpack", ModuleTool.TAB_TOOL, new Properties().stacksTo(1), POWER_CONSUMPTION, FUEL_CONSUMPTION, false);
        this.disableArea();
        EventManager.forge(EntityItemPickupEvent.class).filter(entityItemPickupEvent -> !entityItemPickupEvent.getItem().getItem().isEmpty()).process(entityItemPickupEvent -> {
            for (PlayerInventoryFinder.Target target : findAllBackpacks(entityItemPickupEvent.getPlayer())) {
                ItemStack stack = target.getFinder().getStackGetter().apply(entityItemPickupEvent.getPlayer(), target.getSlot());
                if (!stack.isEmpty()) {
                    if (stack.getItem() instanceof ItemInfinityBackpack && (getPickUpMode(stack) == 1 || getPickUpMode(stack) == 0)) {
                        BackpackDataManager manager = BackpackDataManager.getData(entityItemPickupEvent.getItem().level);
                        if (manager != null && stack.getOrCreateTag().contains("Id")) {
                            BackpackDataManager.BackpackItemHandler handler = manager.getBackpack(stack.getOrCreateTag().getString("Id"));
                            if (handler != null) {
                                ItemStack picked = entityItemPickupEvent.getItem().getItem();
                                for (int pos = 0; pos < handler.getSlots(); pos++) {
                                    ItemStack slotStack = handler.getSlotDefinition(pos).getStack().copy();
                                    slotStack.setCount(1);
                                    if (!slotStack.isEmpty() && slotStack.getContainerItem().sameItem(picked) && ItemStack.tagMatches(slotStack, picked)) {
                                        ItemStack returned = handler.insertItem(pos, picked.copy(), false);
                                        picked.setCount(returned.getCount());
                                        entityItemPickupEvent.setResult(Event.Result.ALLOW);
                                        if (entityItemPickupEvent.getPlayer() instanceof ServerPlayer) {
                                            sync(entityItemPickupEvent.getPlayer().level, stack.getOrCreateTag().getString("Id"), (ServerPlayer) entityItemPickupEvent.getPlayer());
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
                            ExperienceOrb entity = pickupXp.getOrb();
                            IFluidHandlerItem handlerItem = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
                            if (handlerItem != null) {
                                if (handlerItem.fill(new FluidStack(ModuleCore.ESSENCE.getSourceFluid().get(), entity.getValue() * 20), IFluidHandler.FluidAction.SIMULATE) > 0){
                                    handlerItem.fill(new FluidStack(ModuleCore.ESSENCE.getSourceFluid().get(), entity.getValue() * 20), IFluidHandler.FluidAction.EXECUTE);
                                    entity.onClientRemoval();
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

    public static void sync(Level world, String id, ServerPlayer player) {
        IndustrialForegoing.NETWORK.get().sendTo(new BackpackSyncMessage(id, BackpackDataManager.getData(world).getBackpack(id).serializeNBT()), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
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
     * {@link ItemInfinityBackpack}s present in the given {@link Player}'s inventory.
     *
     * @param entity The {@link Player} whose inventory should be searched for {@link ItemInfinityBackpack}s.
     * @return The list of all {@link ItemInfinityBackpack}s contained in the given {@link Player}s
     * inventory. The returned list is ordered by inventory slot ID.
     */
    public static List<PlayerInventoryFinder.Target> findAllBackpacks(Player entity) {
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

    public static Optional<PlayerInventoryFinder.Target> findFirstBackpack(Player entity) {
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
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new BackpackCapabilityProvider(stack, getTankConstructor(stack), getEnergyConstructor(stack));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player player, InteractionHand handIn) {
        ItemStack stack = player.getItemInHand(handIn);
        if (player instanceof ServerPlayer) {
            if (!stack.hasTag() || !stack.getTag().contains("Id")) {
                UUID id = UUID.randomUUID();
                CompoundTag nbt = stack.getOrCreateTag();
                nbt.putString("Id", id.toString());
                BackpackDataManager.getData(worldIn).createBackPack(id);
                stack.setTag(nbt);
            }
            String id = stack.getTag().getString("Id");
            IndustrialForegoing.NETWORK.get().sendTo(new BackpackOpenedMessage(player.inventory.selected, PlayerInventoryFinder.MAIN), ((ServerPlayer) player).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
            sync(worldIn, id, (ServerPlayer) player);
            NetworkHooks.openGui((ServerPlayer) player, this, buffer ->
                    LocatorFactory.writePacketBuffer(buffer, new HeldStackLocatorInstance(handIn == InteractionHand.MAIN_HAND)));
            return InteractionResultHolder.success(player.getItemInHand(handIn));
        }
        if (CommonProxy.CONTRIBUTORS.contains(player.getUUID().toString())) {
            player.getItemInHand(handIn).getOrCreateTag().putBoolean("Special", true);
        }
        return super.use(worldIn, player, handIn);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (isMagnetEnabled(stack)) {
            for (ItemEntity itemEntity : entityIn.level.getEntitiesOfClass(ItemEntity.class, new AABB(entityIn.getX(), entityIn.getY(), entityIn.getY(), entityIn.getX(), entityIn.getY(), entityIn.getZ()).inflate(5))) {
                if (entityIn.level.isClientSide) {
                    if (itemEntity.isOnGround() && worldIn.random.nextInt(5) < 1)
                        itemEntity.level.addParticle(ParticleTypes.PORTAL, itemEntity.getX(), itemEntity.getY() + 0.5, itemEntity.getZ(), 0, -0.5, 0);
                } else {
                    if (!itemEntity.hasPickUpDelay() && enoughFuel(stack)) {
                        itemEntity.teleportTo(entityIn.getX(), entityIn.getY(), entityIn.getZ());
                        consumeFuel(stack);
                    }
                }
            }
        }
        if (!entityIn.level.isClientSide && entityIn instanceof Player) {
            if (enoughFuel(stack)) {
                if ((((Player) entityIn).getFoodData().needsFood() || ((Player) entityIn).getFoodData().getSaturationLevel() < 10) && stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
                    IFluidHandlerItem handlerItem = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElseGet(null);
                    if (handlerItem instanceof MultipleFluidHandlerScreenProviderItemStack) {
                        FluidStack fluidStack = handlerItem.getFluidInTank(2);
                        if (!fluidStack.isEmpty() && fluidStack.getAmount() >= 400) {
                            ((MultipleFluidHandlerScreenProviderItemStack) handlerItem).setFluidInTank(2, new FluidStack(ModuleCore.MEAT.getSourceFluid().get(), fluidStack.getAmount() - 400));
                            ((Player) entityIn).getFoodData().eat(1, 1);
                            consumeFuel(stack);
                        }
                    }
                }
            }
            if (entityIn.level.getGameTime() % 10 == 0) {
                BackpackDataManager manager = BackpackDataManager.getData(entityIn.level);
                if (manager != null && stack.getOrCreateTag().contains("Id")) {
                    BackpackDataManager.BackpackItemHandler handler = manager.getBackpack(stack.getOrCreateTag().getString("Id"));
                    if (handler != null) {
                        if (getSlotSize(stack) != handler.getSlotLimit(0)) {
                            handler.setMaxAmount(getSlotSize(stack));
                        }
                        if (enoughFuel(stack)) {
                            for (int i = 0; i < BackpackDataManager.SLOT_AMOUNT; i++) {
                                if (!handler.getStackInSlot(i).isEmpty() && handler.getSlotDefinition(i).isRefillItems()) {
                                    Inventory inventory = ((Player) entityIn).inventory;
                                    for (int inv = 0; inv <= 35; inv++) {
                                        ItemStack inventoryStack = inventory.getItem(inv);
                                        if (!inventoryStack.isEmpty() && inventoryStack.getCount() < inventoryStack.getMaxStackSize() && handler.isItemValid(i, inventoryStack) && enoughFuel(stack)) {
                                            int extracting = inventoryStack.getMaxStackSize() - inventoryStack.getCount();
                                            ItemStack extractedSLot = handler.extractItem(i, extracting, false);
                                            inventoryStack.setCount(inventoryStack.getCount() + extractedSLot.getCount());
                                            if (entityIn instanceof ServerPlayer)
                                                sync(entityIn.level, stack.getOrCreateTag().getString("Id"), (ServerPlayer) entityIn);
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
                new MultipleFluidHandlerScreenProviderItemStack.TankDefinition("biofuel", -21, y + 25 * 0, fluidStack -> fluidStack.getFluid().isSame(ModuleCore.BIOFUEL.getSourceFluid().get()), false, true, FluidTankComponent.Type.SMALL),
                new MultipleFluidHandlerScreenProviderItemStack.TankDefinition("essence", -21, y + 25 * 1, fluidStack -> fluidStack.getFluid().is(IndustrialTags.Fluids.EXPERIENCE), true, true, FluidTankComponent.Type.SMALL),
                new MultipleFluidHandlerScreenProviderItemStack.TankDefinition("meat", -21, y + 25 * 2, fluidStack -> fluidStack.getFluid().isSame(ModuleCore.MEAT.getSourceFluid().get()), false, true, FluidTankComponent.Type.SMALL)
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
                    stack.setTag(new CompoundTag());
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
    public void handleButtonMessage(int id, Player playerEntity, CompoundTag compound) {
        findFirstBackpack(playerEntity).ifPresent(target -> {
            ItemStack stack = target.getFinder().getStackGetter().apply(playerEntity, target.getSlot());
            if (!stack.isEmpty()) {
                if (id == 4 && playerEntity instanceof ServerPlayer) {
                    String backpackId = compound.getString("Id");
                    ItemStack cursor = playerEntity.containerMenu.getCarried();
                    boolean shift = compound.getBoolean("Shift");
                    boolean ctrl = compound.getBoolean("Ctrl");
                    int button = compound.getInt("Button");
                    int slot = compound.getInt("Slot");
                    BackpackDataManager dataManager = BackpackDataManager.getData(playerEntity.level);
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
                        playerEntity.containerMenu.setCarried(result);
                      ((ServerPlayer) playerEntity).containerMenu.broadcastChanges();
                    }
                    sync(playerEntity.level, backpackId, (ServerPlayer) playerEntity);
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
        CompoundTag tagCompound = new CompoundTag();
        tagCompound.putLong("Energy", power);
        tagCompound.putBoolean("Special", special);
        tagCompound.putString("Selected", InfinityTier.getTierBraquet(power).getLeft().name());
        tagCompound.putBoolean("CanCharge", true);
        stack.setTag(tagCompound);
    }

    @Override
    public void addTooltipDetails(@Nullable Key key, ItemStack stack, List<Component> tooltip, boolean advanced) {
        //tooltip.add(stack.getOrCreateTag().toFormattedComponent());
        tooltip.add(new TextComponent(ChatFormatting.GRAY + new TranslatableComponent("text.industrialforegoing.tooltip.can_hold").getString() + ": " + ChatFormatting.DARK_AQUA + NumberFormat.getInstance(Locale.ROOT).format(getSlotSize(stack)) + ChatFormatting.GRAY + " " + new TranslatableComponent("text.industrialforegoing.tooltip.items").getString()));
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
        int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack);
        return getFuelFromStack(stack) >= FUEL_CONSUMPTION  * ( 1 / (i + 1)) ;
    }

    @Override
    public void consumeFuel(ItemStack stack) {
        int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack);
        if (getFuelFromStack(stack) >= FUEL_CONSUMPTION  * ( 1 / (i + 1)) ) {
            stack.getTag().getCompound("Tanks").getCompound("biofuel").putInt("Amount", Math.max(0, getFuelFromStack(stack) - FUEL_CONSUMPTION  * ( 1 / (i + 1)) ));
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
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
    public AbstractContainerMenu createMenu(int menu, Inventory p_createMenu_2_, Player playerEntity) {
        return findFirstBackpack(playerEntity).map(target -> {
            ItemStack stack = target.getFinder().getStackGetter().apply(playerEntity, target.getSlot());
            if (!stack.isEmpty()) {
                if (!stack.hasTag() || !stack.getTag().contains("Id")) {
                    UUID id = UUID.randomUUID();
                    CompoundTag nbt = stack.getOrCreateTag();
                    nbt.putString("Id", id.toString());
                    BackpackDataManager.getData(playerEntity.level).createBackPack(id);
                    stack.setTag(nbt);
                }
                String id = stack.getTag().getString("Id");
                return new BackpackContainer(ItemStackHarnessRegistry.getHarnessCreators().get(ModuleTool.INFINITY_BACKPACK).apply(stack), new InventoryStackLocatorInstance(target.getName(), target.getSlot()), new ContainerLevelAccess() {
                    @Override
                    public <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> p_221484_1_) {
                        return Optional.empty();
                    }
                }, playerEntity.inventory, menu, id);
            }
            return null;
        }).orElse(null);
    }

    @OnlyIn(Dist.CLIENT)
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
        factory.add(() -> new StateButtonAddon(new ButtonComponent(x, 16 + y * 0, 14, 14).setId(10), new StateButtonInfo(0, AssetTypes.BUTTON_SIDENESS_ENABLED, ChatFormatting.GREEN + new TranslatableComponent("tooltip.industrialforegoing.backpack.magnet_enabled").getString()), new StateButtonInfo(1, AssetTypes.BUTTON_SIDENESS_DISABLED, ChatFormatting.RED + new TranslatableComponent("tooltip.industrialforegoing.backpack.magnet_disabled").getString())) {
            @Override
            public int getState() {
                return isMagnetEnabled(stack.get()) ? 0 : 1;
            }
        });
        factory.add(() -> new StateButtonAddon(new ButtonComponent(x, 16 + y * 1, 14, 14).setId(11),
                new StateButtonInfo(0, AssetTypes.BUTTON_SIDENESS_ENABLED,
                        ChatFormatting.GREEN + new TranslatableComponent("tooltip.industrialforegoing.backpack.pickup_all").getString(),
                        ChatFormatting.GRAY + new TranslatableComponent("tooltip.industrialforegoing.backpack.pickup_extra").getString(),
                        ChatFormatting.GRAY + new TranslatableComponent("tooltip.industrialforegoing.backpack.pickup_extra_1").getString()),
                new StateButtonInfo(1, AssetTypes.BUTTON_SIDENESS_PULL,
                        ChatFormatting.GREEN + new TranslatableComponent("tooltip.industrialforegoing.backpack.item_pickup_enabled").getString(),
                        ChatFormatting.GRAY + new TranslatableComponent("tooltip.industrialforegoing.backpack.pickup_extra").getString(),
                        ChatFormatting.GRAY + new TranslatableComponent("tooltip.industrialforegoing.backpack.pickup_extra_1").getString()),
                new StateButtonInfo(2, AssetTypes.BUTTON_SIDENESS_PUSH,
                        ChatFormatting.GREEN + new TranslatableComponent("tooltip.industrialforegoing.backpack.xp_pickup_enabled").getString(),
                        ChatFormatting.GRAY + new TranslatableComponent("tooltip.industrialforegoing.backpack.pickup_extra").getString(),
                        ChatFormatting.GRAY + new TranslatableComponent("tooltip.industrialforegoing.backpack.pickup_extra_1").getString()),
                new StateButtonInfo(3, AssetTypes.BUTTON_SIDENESS_DISABLED,
                        ChatFormatting.RED + new TranslatableComponent("tooltip.industrialforegoing.backpack.pickup_disabled").getString(),
                        ChatFormatting.GRAY + new TranslatableComponent("tooltip.industrialforegoing.backpack.pickup_extra").getString(),
                        ChatFormatting.GRAY + new TranslatableComponent("tooltip.industrialforegoing.backpack.pickup_extra_1").getString())) {
            @Override
            public int getState() {
                return getPickUpMode(stack.get());
            }
        });
        factory.add(() -> new StateButtonAddon(new ButtonComponent(x, 16 + y * 2, 14, 14).setId(3), new StateButtonInfo(0, AssetTypes.BUTTON_SIDENESS_ENABLED, ChatFormatting.GREEN + new TranslatableComponent("text.industrialforegoing.display.charging").getString() + new TranslatableComponent("text.industrialforegoing.display.enabled").getString()), new StateButtonInfo(1, AssetTypes.BUTTON_SIDENESS_DISABLED, ChatFormatting.RED + new TranslatableComponent("text.industrialforegoing.display.charging").getString() + new TranslatableComponent("text.industrialforegoing.display.disabled").getString())) {
            @Override
            public int getState() {
                return ItemInfinity.canCharge(stack.get()) ? 0 : 1;
            }
        });
        if (isSpecial(stack.get())) {
            factory.add(() -> new StateButtonAddon(new ButtonComponent(x, 16 + y * 3, 14, 14).setId(-10), new StateButtonInfo(0, AssetTypes.BUTTON_SIDENESS_ENABLED, ChatFormatting.GOLD + new TranslatableComponent("text.industrialforegoing.display.special").getString()), new StateButtonInfo(1, AssetTypes.BUTTON_SIDENESS_DISABLED, ChatFormatting.GOLD + new TranslatableComponent("text.industrialforegoing.display.special").getString())) {
                @Override
                public int getState() {
                    return ((ItemInfinityBackpack)ModuleTool.INFINITY_BACKPACK.get()).isSpecialEnabled(stack.get()) ? 0 : 1;
                }
            });
        }
        return factory;
    }

    @Override
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {
        new DissolutionChamberRecipe(this.getRegistryName(),
                new Ingredient.Value[]{
                        new Ingredient.ItemValue(new ItemStack(ModuleTransportStorage.BLACK_HOLE_UNIT_COMMON.getLeft().get())),
                        new Ingredient.TagValue(IndustrialTags.Items.GEAR_DIAMOND),
                        new Ingredient.ItemValue(new ItemStack(ModuleTransportStorage.BLACK_HOLE_UNIT_COMMON.getLeft().get())),
                        new Ingredient.ItemValue(new ItemStack(ModuleTransportStorage.BLACK_HOLE_TANK_COMMON.getLeft().get())),
                        new Ingredient.ItemValue(new ItemStack(ModuleTransportStorage.BLACK_HOLE_TANK_COMMON.getLeft().get())),
                        new Ingredient.TagValue(IndustrialTags.Items.GEAR_GOLD),
                        new Ingredient.TagValue(IndustrialTags.Items.GEAR_GOLD),
                        new Ingredient.TagValue(IndustrialTags.Items.GEAR_GOLD),
                },
                new FluidStack(ModuleCore.PINK_SLIME.getSourceFluid().get(), 2000), 400, new ItemStack(this), FluidStack.EMPTY);
    }

}

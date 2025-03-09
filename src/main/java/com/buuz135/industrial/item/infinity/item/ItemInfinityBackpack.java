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
import com.buuz135.industrial.block.tool.tile.InfinityBackpackTile;
import com.buuz135.industrial.container.BackpackContainer;
import com.buuz135.industrial.gui.component.SlotDefinitionGuiAddon;
import com.buuz135.industrial.item.infinity.InfinityEnergyStorage;
import com.buuz135.industrial.item.infinity.InfinityTankStorage;
import com.buuz135.industrial.item.infinity.InfinityTier;
import com.buuz135.industrial.item.infinity.ItemInfinity;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleTool;
import com.buuz135.industrial.proxy.network.BackpackOpenedMessage;
import com.buuz135.industrial.proxy.network.BackpackSyncMessage;
import com.buuz135.industrial.utils.IFAttachments;
import com.buuz135.industrial.utils.IndustrialTags;
import com.buuz135.industrial.worlddata.BackpackDataManager;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.client.IScreenAddon;
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
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class ItemInfinityBackpack extends ItemInfinity {

    public static int POWER_CONSUMPTION = 0;
    public static int FUEL_CONSUMPTION = 1;


    public ItemInfinityBackpack() {
        super("infinity_backpack", ModuleTool.TAB_TOOL, new Properties().stacksTo(1), POWER_CONSUMPTION, FUEL_CONSUMPTION, false);
        this.disableArea();
        EventManager.forge(ItemEntityPickupEvent.Pre.class).filter(entityItemPickupEvent -> !entityItemPickupEvent.getItemEntity().getItem().isEmpty()).process(entityItemPickupEvent -> {
            for (PlayerInventoryFinder.Target target : findAllBackpacks(entityItemPickupEvent.getPlayer())) {
                ItemStack stack = target.getFinder().getStackGetter().apply(entityItemPickupEvent.getPlayer(), target.getSlot());
                if (!stack.isEmpty()) {
                    if (stack.getItem() instanceof ItemInfinityBackpack && (getPickUpMode(stack) == 1 || getPickUpMode(stack) == 0)) {
                        BackpackDataManager manager = BackpackDataManager.getData(entityItemPickupEvent.getItemEntity().level());
                        if (manager != null && stack.has(IFAttachments.INFINITY_BACKPACK_ID)) {
                            BackpackDataManager.BackpackItemHandler handler = manager.getBackpack(stack.get(IFAttachments.INFINITY_BACKPACK_ID));
                            if (handler != null) {
                                ItemStack picked = entityItemPickupEvent.getItemEntity().getItem();
                                for (int pos = 0; pos < handler.getSlots(); pos++) {
                                    ItemStack slotStack = handler.getSlotDefinition(pos).getStack().copy();
                                    slotStack.setCount(1);
                                    if (!slotStack.isEmpty() && ItemStack.isSameItemSameComponents(picked, slotStack)) {
                                        ItemStack returned = handler.insertItem(pos, picked.copy(), false);
                                        picked.setCount(returned.getCount());
                                        entityItemPickupEvent.setCanPickup(TriState.FALSE);
                                        if (entityItemPickupEvent.getPlayer() instanceof ServerPlayer serverPlayer) {
                                            sync(entityItemPickupEvent.getItemEntity().level(), stack.get(IFAttachments.INFINITY_BACKPACK_ID), serverPlayer);
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
            findFirstBackpack(pickupXp.getEntity()).ifPresent(target -> {
                ItemStack stack = target.getFinder().getStackGetter().apply(pickupXp.getEntity(), target.getSlot());
                if (!stack.isEmpty()) {
                    if (stack.getItem() instanceof ItemInfinityBackpack && (getPickUpMode(stack) == 2 || getPickUpMode(stack) == 0)) {
                        var handlerItem = stack.getCapability(Capabilities.FluidHandler.ITEM);
                        if (handlerItem != null) {
                            ExperienceOrb entity = pickupXp.getOrb();
                            if (handlerItem.fill(new FluidStack(ModuleCore.ESSENCE.getSourceFluid().get(), entity.getValue() * 20), IFluidHandler.FluidAction.SIMULATE) > 0) {
                                handlerItem.fill(new FluidStack(ModuleCore.ESSENCE.getSourceFluid().get(), entity.getValue() * 20), IFluidHandler.FluidAction.EXECUTE);
                                entity.remove(Entity.RemovalReason.KILLED);
                                pickupXp.setCanceled(true);
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
        var data = BackpackDataManager.getData(world);
        if (data != null) {
            var backpack = data.getBackpack(id);
            if (backpack != null)
                IndustrialForegoing.NETWORK.sendTo(new BackpackSyncMessage(id, backpack.serializeNBT(world.registryAccess())), player);
        }
    }

    public static boolean isMagnetEnabled(ItemStack stack) {
        return stack.getOrDefault(IFAttachments.INFINITY_MAGNET_ENABLED, true);
    }

    public static void setMagnet(ItemStack stack, boolean enabled) {
        stack.set(IFAttachments.INFINITY_MAGNET_ENABLED, enabled);
    }

    public static int getPickUpMode(ItemStack stack) {
        return stack.getOrDefault(IFAttachments.INFINITY_BACKPACK_PICKUP_MODE, 0);
    }

    public static void setPickUpMode(ItemStack stack, int mode) {
        stack.set(IFAttachments.INFINITY_BACKPACK_PICKUP_MODE, mode);
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

    private static void updateBlockEntityComponents(Level level, BlockPos poa, ItemStack stack) {
        BlockEntity blockentity = level.getBlockEntity(poa);
        if (blockentity != null) {
            blockentity.applyComponentsFromItemStack(stack);
            blockentity.setChanged();
        }

    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player player, InteractionHand handIn) {
        ItemStack stack = player.getItemInHand(handIn);
        if (player instanceof ServerPlayer serverPlayer) {
            if (!stack.has(IFAttachments.INFINITY_BACKPACK_ID)) {
                UUID id = UUID.randomUUID();
                BackpackDataManager.getData(worldIn).createBackPack(id);
                stack.set(IFAttachments.INFINITY_BACKPACK_ID, id.toString());
            }
            String id = stack.get(IFAttachments.INFINITY_BACKPACK_ID);
            IndustrialForegoing.NETWORK.sendTo(new BackpackOpenedMessage(player.getInventory().selected, PlayerInventoryFinder.MAIN), serverPlayer);
            sync(worldIn, id, serverPlayer);
            serverPlayer.openMenu(this, buffer ->
                    LocatorFactory.writePacketBuffer(buffer, new HeldStackLocatorInstance(handIn == InteractionHand.MAIN_HAND)));
            return InteractionResultHolder.success(player.getItemInHand(handIn));
        }
        if (IndustrialForegoing.CAT_EARS.getPlayers().contains(player.getUUID())) {
            player.getItemInHand(handIn).set(IFAttachments.INFINITY_ITEM_SPECIAL, true);
        }
        return super.use(worldIn, player, handIn);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (isMagnetEnabled(stack)) {
            for (ItemEntity itemEntity : entityIn.level().getEntitiesOfClass(ItemEntity.class, new AABB(entityIn.getX(), entityIn.getY(), entityIn.getY(), entityIn.getX(), entityIn.getY(), entityIn.getZ()).inflate(5))) {
                if (entityIn.level().isClientSide && enoughFuel(stack)) {
                    if (itemEntity.onGround() && worldIn.random.nextInt(5) < 1)
                        itemEntity.level().addParticle(ParticleTypes.PORTAL, itemEntity.getX(), itemEntity.getY() + 0.5, itemEntity.getZ(), 0, -0.5, 0);
                } else {
                    if (!itemEntity.hasPickUpDelay() && enoughFuel(stack)) {
                        itemEntity.teleportTo(entityIn.getX(), entityIn.getY(), entityIn.getZ());
                        consumeFuel(stack);
                    }
                }
            }
        }
        if (!entityIn.level().isClientSide && entityIn instanceof Player) {
            if (enoughFuel(stack)) {
                var handlerItem = stack.getCapability(Capabilities.FluidHandler.ITEM);
                if ((((Player) entityIn).getFoodData().needsFood() || ((Player) entityIn).getFoodData().getSaturationLevel() < 10) && handlerItem != null) {
                    for (int i = 0; i < handlerItem.getTanks(); i++) {
                        FluidStack fluidStack = handlerItem.getFluidInTank(i);
                        if (!fluidStack.isEmpty() && fluidStack.getAmount() >= 400) {
                            handlerItem.drain(new FluidStack(ModuleCore.MEAT.getSourceFluid(), 400), IFluidHandler.FluidAction.EXECUTE);
                            ((Player) entityIn).getFoodData().eat(1, 1);
                            consumeFuel(stack);
                        }
                    }
                }
            }
            if (entityIn.level().getGameTime() % 10 == 0) {
                BackpackDataManager manager = BackpackDataManager.getData(entityIn.level());
                if (manager != null && stack.has(IFAttachments.INFINITY_BACKPACK_ID)) {
                    BackpackDataManager.BackpackItemHandler handler = manager.getBackpack(stack.get(IFAttachments.INFINITY_BACKPACK_ID));
                    if (handler != null) {
                        if (getSlotSize(stack) != handler.getSlotLimit(0)) {
                            handler.setMaxAmount(getSlotSize(stack));
                        }
                        if (enoughFuel(stack)) {
                            for (int i = 0; i < BackpackDataManager.SLOT_AMOUNT; i++) {
                                if (!handler.getStackInSlot(i).isEmpty() && handler.getSlotDefinition(i).isRefillItems()) {
                                    Inventory inventory = ((Player) entityIn).getInventory();
                                    for (int inv = 0; inv <= 35; inv++) {
                                        ItemStack inventoryStack = inventory.getItem(inv);
                                        if (!inventoryStack.isEmpty() && inventoryStack.getCount() < inventoryStack.getMaxStackSize() && handler.isItemValid(i, inventoryStack) && enoughFuel(stack)) {
                                            int extracting = inventoryStack.getMaxStackSize() - inventoryStack.getCount();
                                            ItemStack extractedSLot = handler.extractItem(i, extracting, false);
                                            inventoryStack.setCount(inventoryStack.getCount() + extractedSLot.getCount());
                                            if (entityIn instanceof ServerPlayer)
                                                sync(entityIn.level(), stack.get(IFAttachments.INFINITY_BACKPACK_ID), (ServerPlayer) entityIn);
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
    public IFactory<InfinityTankStorage> getTankConstructor(ItemStack stack) {
        int y = 88;
        return () -> new InfinityTankStorage(stack,
                new InfinityTankStorage.TankDefinition("biofuel", 1_000_000, -21, y + 25 * 0, fluidStack -> fluidStack.getFluid().isSame(ModuleCore.BIOFUEL.getSourceFluid().get()), true, true, FluidTankComponent.Type.SMALL, new FluidStack(ModuleCore.BIOFUEL.getSourceFluid().get(), 1000)),
                new InfinityTankStorage.TankDefinition("essence", 1_000_000, -21, y + 25 * 1, fluidStack -> fluidStack.is(IndustrialTags.Fluids.EXPERIENCE), true, true, FluidTankComponent.Type.SMALL, new FluidStack(ModuleCore.ESSENCE.getSourceFluid().get(), 1000)),
                new InfinityTankStorage.TankDefinition("meat", 1_000_000, -21, y + 25 * 2, fluidStack -> fluidStack.getFluid().isSame(ModuleCore.MEAT.getSourceFluid().get()), false, true, FluidTankComponent.Type.SMALL, new FluidStack(ModuleCore.MEAT.getSourceFluid().get(), 1000))
        );
    }

    @Override
    public IFactory<InfinityEnergyStorage> getEnergyConstructor(ItemStack stack) {
        return () -> new InfinityEnergyStorage(InfinityTier.ARTIFACT.getPowerNeeded(), -21, 24) {
            @Override
            public long getLongEnergyStored() {
                return Math.min(stack.getOrDefault(IFAttachments.INFINITY_ITEM_POWER, 0L), InfinityTier.ARTIFACT.getPowerNeeded());
            }

            @Override
            public void setEnergyStored(long energy) {
                stack.set(IFAttachments.INFINITY_ITEM_POWER, Math.min(energy, InfinityTier.ARTIFACT.getPowerNeeded()));
            }

            @Override
            public boolean canReceive() {
                return ItemInfinity.canCharge(stack);
            }
        };
    }

    public void handleButtonMessage(int id, Player playerEntity, CompoundTag compound, ItemStack stack) {
        if (id == 4 && playerEntity instanceof ServerPlayer) {
            String backpackId = compound.getString("Id");
            ItemStack cursor = playerEntity.containerMenu.getCarried();
            boolean shift = compound.getBoolean("Shift");
            boolean ctrl = compound.getBoolean("Ctrl");
            int button = compound.getInt("Button");
            int slot = compound.getInt("Slot");
            BackpackDataManager dataManager = BackpackDataManager.getData(playerEntity.level());
            BackpackDataManager.BackpackItemHandler handler = dataManager.getBackpack(backpackId);
            ItemStack result = ItemStack.EMPTY;
            boolean hasCursorChanged = false;
            if (button == 2) {//MIDDLE
                ItemStack simulated = handler.extractItem(slot, 1, true);
                if (!simulated.isEmpty() && (cursor.isEmpty() || ItemStack.isSameItemSameComponents(simulated, cursor))) {
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
                } else if (button == 1 && handler.insertItem(slot, cursor.copyWithCount(1), false).isEmpty()) {
                    cursor.shrink(1);
                    result = cursor;
                    hasCursorChanged = true;
                }
            }
            if (hasCursorChanged) {
                playerEntity.containerMenu.setCarried(result);
                ((ServerPlayer) playerEntity).containerMenu.broadcastChanges();
            }
            sync(playerEntity.level(), backpackId, (ServerPlayer) playerEntity);
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

    @Override
    public void handleButtonMessage(int id, Player playerEntity, CompoundTag compound) {
        findFirstBackpack(playerEntity).ifPresent(target -> {
            ItemStack stack = target.getFinder().getStackGetter().apply(playerEntity, target.getSlot());
            if (!stack.isEmpty()) {
                this.handleButtonMessage(id, playerEntity, compound, stack);
            }
        });
    }

    @Override
    public void addTooltipDetails(@Nullable Key key, ItemStack stack, List<Component> tooltip, boolean advanced) {
        //tooltip.add(stack.getOrCreateTag().toFormattedComponent());
        tooltip.add(Component.literal(ChatFormatting.GRAY + Component.translatable("text.industrialforegoing.tooltip.can_hold").getString() + ": " + ChatFormatting.DARK_AQUA + NumberFormat.getInstance(Locale.ROOT).format(getSlotSize(stack)) + ChatFormatting.GRAY + " " + Component.translatable("text.industrialforegoing.tooltip.items").getString()));
        super.addTooltipDetails(key, stack, tooltip, advanced);
    }

    public void addNbt(ItemStack stack, long power, int fuel, boolean special) {
        stack.set(IFAttachments.INFINITY_ITEM_POWER, power);
        stack.set(IFAttachments.INFINITY_ITEM_SPECIAL, special);
        stack.set(IFAttachments.INFINITY_ITEM_SELECTED_TIER, InfinityTier.getTierBraquet(power).getLeft());
        stack.set(IFAttachments.INFINITY_ITEM_CAN_CHARGE, true);
    }

    @Override
    public void verifyComponentsAfterLoad(ItemStack stack) {
        super.verifyComponentsAfterLoad(stack);
    }

    @Override
    public boolean enoughFuel(ItemStack stack) {
        int i = EnchantmentHelper.getItemEnchantmentLevel(IFAttachments.registryAccess().holderOrThrow(Enchantments.UNBREAKING), stack);
        return getFuelFromStack(stack) >= FUEL_CONSUMPTION * (1 / (i + 1));
    }

    @Override
    public void consumeFuel(ItemStack stack) {
        int level = EnchantmentHelper.getItemEnchantmentLevel(IFAttachments.registryAccess().holderOrThrow(Enchantments.UNBREAKING), stack);
        if (getFuelFromStack(stack) >= FUEL_CONSUMPTION * (1 / (level + 1))) {
            var capability = stack.getCapability(Capabilities.FluidHandler.ITEM);
            if (capability != null) {
                for (int i = 0; i < capability.getTanks(); i++) {
                    if (capability.getFluidInTank(i).is(ModuleCore.BIOFUEL.getSourceFluid())) {
                        capability.drain(new FluidStack(ModuleCore.BIOFUEL.getSourceFluid(), FUEL_CONSUMPTION * (1 / (level + 1))), IFluidHandler.FluidAction.EXECUTE);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return 1;
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        return ItemAttributeModifiers.EMPTY;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int menu, Inventory p_createMenu_2_, Player playerEntity) {
        return findFirstBackpack(playerEntity).map(target -> {
            ItemStack stack = target.getFinder().getStackGetter().apply(playerEntity, target.getSlot());
            if (!stack.isEmpty()) {
                if (!stack.has(IFAttachments.INFINITY_BACKPACK_ID)) {
                    var id = UUID.randomUUID();
                    BackpackDataManager.getData(playerEntity.level()).createBackPack(id);
                    stack.set(IFAttachments.INFINITY_BACKPACK_ID, id.toString());
                }
                String id = stack.get(IFAttachments.INFINITY_BACKPACK_ID);
                return new BackpackContainer(ItemStackHarnessRegistry.getHarnessCreators().get(ModuleTool.INFINITY_BACKPACK).apply(stack), new InventoryStackLocatorInstance(target.getName(), target.getSlot()), new ContainerLevelAccess() {
                    @Override
                    public <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> p_221484_1_) {
                        return Optional.empty();
                    }
                }, playerEntity.getInventory(), menu, id);
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
            factory.add(() -> new SlotDefinitionGuiAddon(new ButtonComponent(17 + 18 * x, 22 + 18 * y, 17, 17).setId(4), finalI) {
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
        factory.add(() -> new StateButtonAddon(new ButtonComponent(x, 16 + y * 0, 14, 14).setId(10), new StateButtonInfo(0, AssetTypes.BUTTON_SIDENESS_ENABLED, ChatFormatting.GREEN + Component.translatable("tooltip.industrialforegoing.backpack.magnet_enabled").getString(), ChatFormatting.DARK_GRAY + Component.translatable("tooltip.industrialforegoing.backpack.needs_biofuel").getString()), new StateButtonInfo(1, AssetTypes.BUTTON_SIDENESS_DISABLED, ChatFormatting.RED + Component.translatable("tooltip.industrialforegoing.backpack.magnet_disabled").getString())) {
            @Override
            public int getState() {
                return isMagnetEnabled(stack.get()) ? 0 : 1;
            }
        });
        factory.add(() -> new StateButtonAddon(new ButtonComponent(x, 16 + y * 1, 14, 14).setId(11),
                new StateButtonInfo(0, AssetTypes.BUTTON_SIDENESS_ENABLED,
                        ChatFormatting.GREEN + Component.translatable("tooltip.industrialforegoing.backpack.pickup_all").getString(),
                        ChatFormatting.GRAY + Component.translatable("tooltip.industrialforegoing.backpack.pickup_extra").getString(),
                        ChatFormatting.GRAY + Component.translatable("tooltip.industrialforegoing.backpack.pickup_extra_1").getString()),
                new StateButtonInfo(1, AssetTypes.BUTTON_SIDENESS_PULL,
                        ChatFormatting.GREEN + Component.translatable("tooltip.industrialforegoing.backpack.item_pickup_enabled").getString(),
                        ChatFormatting.GRAY + Component.translatable("tooltip.industrialforegoing.backpack.pickup_extra").getString(),
                        ChatFormatting.GRAY + Component.translatable("tooltip.industrialforegoing.backpack.pickup_extra_1").getString()),
                new StateButtonInfo(2, AssetTypes.BUTTON_SIDENESS_PUSH,
                        ChatFormatting.GREEN + Component.translatable("tooltip.industrialforegoing.backpack.xp_pickup_enabled").getString(),
                        ChatFormatting.GRAY + Component.translatable("tooltip.industrialforegoing.backpack.pickup_extra").getString(),
                        ChatFormatting.GRAY + Component.translatable("tooltip.industrialforegoing.backpack.pickup_extra_1").getString()),
                new StateButtonInfo(3, AssetTypes.BUTTON_SIDENESS_DISABLED,
                        ChatFormatting.RED + Component.translatable("tooltip.industrialforegoing.backpack.pickup_disabled").getString(),
                        ChatFormatting.GRAY + Component.translatable("tooltip.industrialforegoing.backpack.pickup_extra").getString(),
                        ChatFormatting.GRAY + Component.translatable("tooltip.industrialforegoing.backpack.pickup_extra_1").getString())) {
            @Override
            public int getState() {
                return getPickUpMode(stack.get());
            }
        });
        factory.add(() -> new StateButtonAddon(new ButtonComponent(x, 16 + y * 2, 14, 14).setId(3), new StateButtonInfo(0, AssetTypes.BUTTON_SIDENESS_ENABLED, ChatFormatting.GREEN + Component.translatable("text.industrialforegoing.display.charging").getString() + Component.translatable("text.industrialforegoing.display.enabled").getString()), new StateButtonInfo(1, AssetTypes.BUTTON_SIDENESS_DISABLED, ChatFormatting.RED + Component.translatable("text.industrialforegoing.display.charging").getString() + Component.translatable("text.industrialforegoing.display.disabled").getString())) {
            @Override
            public int getState() {
                return ItemInfinity.canCharge(stack.get()) ? 0 : 1;
            }
        });
        if (isSpecial(stack.get())) {
            factory.add(() -> new StateButtonAddon(new ButtonComponent(x, 16 + y * 3, 14, 14).setId(-10), new StateButtonInfo(0, AssetTypes.BUTTON_SIDENESS_ENABLED, ChatFormatting.GOLD + Component.translatable("text.industrialforegoing.display.special").getString()), new StateButtonInfo(1, AssetTypes.BUTTON_SIDENESS_DISABLED, ChatFormatting.GOLD + Component.translatable("text.industrialforegoing.display.special").getString())) {
                @Override
                public int getState() {
                    return ((ItemInfinityBackpack) ModuleTool.INFINITY_BACKPACK.get()).isSpecialEnabled(stack.get()) ? 0 : 1;
                }
            });
        }
        return factory;
    }

    @Override
    public void registerRecipe(RecipeOutput consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .pattern("CDC")
                .pattern("BPB")
                .pattern("GGG")
                .define('C', Tags.Items.CHESTS_WOODEN)
                .define('D', IndustrialTags.Items.GEAR_DIAMOND)
                .define('B', Items.BUCKET)
                .define('P', Items.ENDER_PEARL)
                .define('G', IndustrialTags.Items.GEAR_GOLD)
                .save(consumer);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        InteractionResult interactionresult = this.place(new BlockPlaceContext(context));
        if (!interactionresult.consumesAction() && context.getItemInHand().has(DataComponents.FOOD)) {
            InteractionResult interactionresult1 = super.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
            return interactionresult1 == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL : interactionresult1;
        } else {
            return interactionresult;
        }
    }

    public InteractionResult place(BlockPlaceContext context) {
        if (!context.getPlayer().isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        if (!context.canPlace()) {
            return InteractionResult.FAIL;
        } else {
            BlockPlaceContext blockplacecontext = this.updatePlacementContext(context);
            if (blockplacecontext == null) {
                return InteractionResult.FAIL;
            } else {
                BlockState blockstate = this.getPlacementState(blockplacecontext);
                if (blockstate == null) {
                    return InteractionResult.FAIL;
                } else if (!this.placeBlock(blockplacecontext, blockstate)) {
                    return InteractionResult.FAIL;
                } else {
                    BlockPos blockpos = blockplacecontext.getClickedPos();
                    Level level = blockplacecontext.getLevel();
                    Player player = blockplacecontext.getPlayer();
                    ItemStack itemstack = blockplacecontext.getItemInHand();
                    BlockState blockstate1 = level.getBlockState(blockpos);
                    if (blockstate1.is(blockstate.getBlock())) {
                        this.updateCustomBlockEntityTag(level, player, blockpos, itemstack);
                        updateBlockEntityComponents(level, blockpos, itemstack);
                        blockstate1.getBlock().setPlacedBy(level, blockpos, blockstate1, player, itemstack);
                        if (player instanceof ServerPlayer) {
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) player, blockpos, itemstack);
                        }
                    }

                    SoundType soundtype = blockstate1.getSoundType(level, blockpos, context.getPlayer());
                    level.playSound(player, blockpos, this.getPlaceSound(blockstate1, level, blockpos, context.getPlayer()), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                    level.gameEvent(GameEvent.BLOCK_PLACE, blockpos, GameEvent.Context.of(player, blockstate1));
                    itemstack.consume(1, player);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }
    }

    @Deprecated
    protected SoundEvent getPlaceSound(BlockState state) {
        return state.getSoundType().getPlaceSound();
    }

    protected SoundEvent getPlaceSound(BlockState p_state, Level world, BlockPos pos, Player entity) {
        return p_state.getSoundType(world, pos, entity).getPlaceSound();
    }

    @Nullable
    public BlockPlaceContext updatePlacementContext(BlockPlaceContext context) {
        return context;
    }

    public Block getBlock() {
        return ModuleTool.INFINITY_BACKPACK_BLOCK.getBlock();
    }

    @Nullable
    protected BlockState getPlacementState(BlockPlaceContext context) {
        BlockState blockstate = this.getBlock().getStateForPlacement(context);
        return blockstate != null && this.canPlace(context, blockstate) ? blockstate : null;
    }

    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        Player player = context.getPlayer();
        CollisionContext collisioncontext = player == null ? CollisionContext.empty() : CollisionContext.of(player);
        return (!this.mustSurvive() || state.canSurvive(context.getLevel(), context.getClickedPos())) && context.getLevel().isUnobstructed(state, context.getClickedPos(), collisioncontext);
    }

    protected boolean mustSurvive() {
        return true;
    }

    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        return context.getLevel().setBlock(context.getClickedPos(), state, 11);
    }

    public boolean updateCustomBlockEntityTag(Level level, @Nullable Player player, BlockPos pos, ItemStack stack) {
        MinecraftServer minecraftserver = level.getServer();
        if (minecraftserver == null) {
            return false;
        } else {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof InfinityBackpackTile infinityBackpackTile) {
                infinityBackpackTile.setBackpack(stack.copy());
            }
            CustomData customdata = (CustomData) stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
            if (!customdata.isEmpty()) {

                if (blockentity != null) {
                    if (!level.isClientSide && blockentity.onlyOpCanSetNbt() && (player == null || !player.canUseGameMasterBlocks())) {
                        return false;
                    }

                    return customdata.loadInto(blockentity, level.registryAccess());
                }
            }

            return false;
        }
    }
}

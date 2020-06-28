package com.buuz135.industrial.item.infinity;

import com.buuz135.industrial.item.IFCustomItem;
import com.buuz135.industrial.proxy.CommonProxy;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.ArrowButtonScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.StateButtonAddon;
import com.hrznstudio.titanium.client.screen.addon.StateButtonInfo;
import com.hrznstudio.titanium.client.screen.addon.TextScreenAddon;
import com.hrznstudio.titanium.component.button.ArrowButtonComponent;
import com.hrznstudio.titanium.component.button.ButtonComponent;
import com.hrznstudio.titanium.container.BasicAddonContainer;
import com.hrznstudio.titanium.itemstack.ItemStackHarnessRegistry;
import com.hrznstudio.titanium.network.IButtonHandler;
import com.hrznstudio.titanium.network.locator.LocatorFactory;
import com.hrznstudio.titanium.network.locator.instance.HeldStackLocatorInstance;
import com.hrznstudio.titanium.util.FacingUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemInfinity extends IFCustomItem implements INamedContainerProvider, IButtonHandler, IInfinityDrillScreenAddons {

    private final int powerConsumption;
    private final int biofuelConsumption;

    public ItemInfinity(String name, ItemGroup group, Properties builder, int powerConsumption, int biofuelConsumption) {
        super(name, group, builder);
        this.powerConsumption = powerConsumption;
        this.biofuelConsumption = biofuelConsumption;
    }

    public static long getPowerFromStack(ItemStack stack) {
        long power = 0;
        if (stack.hasTag() && stack.getTag().contains("Energy")) {
            power = stack.getTag().getLong("Energy");
        }
        return power;
    }

    public static String getFormattedArea(InfinityTier tier, int radius) {
        int diameter = radius * 2 + 1;
        return diameter + "x" + diameter + "x" + (tier == InfinityTier.ARTIFACT ? diameter : 1);
    }

    public static InfinityTier getSelectedTier(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains("Selected") ? InfinityTier.valueOf(stack.getTag().getString("Selected")) : InfinityTier.getTierBraquet(getPowerFromStack(stack)).getLeft();
    }

    public static boolean canCharge(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("CanCharge")) return stack.getTag().getBoolean("CanCharge");
        return true;
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn) {
        super.onCreated(stack, worldIn, playerIn);
        addNbt(stack, 0, 0, CommonProxy.CONTRIBUTORS.contains(playerIn.getUniqueID().toString()));
    }

    public void addNbt(ItemStack stack, long power, int fuel, boolean special) {
        CompoundNBT tagCompound = new CompoundNBT();
        tagCompound.putLong("Energy", power);
        CompoundNBT fluid = new CompoundNBT();
        fluid.putString("FluidName", "biofuel");
        fluid.putInt("Amount", fuel);
        tagCompound.put("Fluid", fluid);
        tagCompound.putBoolean("Special", special);
        tagCompound.putString("Selected", InfinityTier.getTierBraquet(power).getLeft().name());
        tagCompound.putBoolean("CanCharge", true);
        stack.setTag(tagCompound);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (isInGroup(group)) {
            for (InfinityTier value : InfinityTier.values()) {
                items.add(createStack(value.getPowerNeeded(), 0, false));
            }
            items.add(createStack(InfinityTier.ARTIFACT.getPowerNeeded(), 1_000_000, true));
        }
    }

    public ItemStack createStack(long power, int fuel, boolean special) {
        ItemStack stack = new ItemStack(this);
        addNbt(stack, power, fuel, special);
        return stack;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemEnchantability() {
        return 50;
    }

    @Override
    public UseAction getUseAction(ItemStack p_77661_1_) {
        return UseAction.BOW;
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged && !oldStack.equals(newStack);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return enoughFuel(stack) ? 10F : 0;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        if (Screen.func_231172_r_()) { //hasShiftDown
            int fuel = getFuelFromStack(stack);
            return 1 - fuel / 1_000_000D;
        } else {
            long power = getPowerFromStack(stack);
            return 1 - power / (double) InfinityTier.getTierBraquet(power).getRight().getPowerNeeded();
        }
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return Screen.func_231172_r_() ? 0xcb00ff /*Purple*/ : 0x00d0ff /*Cyan*/;
    }

    @Override
    public boolean hasTooltipDetails(@Nullable Key key) {
        return key == null;
    }

    public int getFuelFromStack(ItemStack stack) {
        int fuelAmount = 0;
        if (stack.hasTag() && stack.getTag().contains("Fluid") && stack.getTag().getCompound("Fluid").contains("Amount")) {
            fuelAmount = stack.getTag().getCompound("Fluid").getInt("Amount");
        }
        return fuelAmount;
    }

    public boolean isSpecial(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains("Special") && stack.getTag().getBoolean("Special");
    }

    public boolean enoughFuel(ItemStack stack) {
        return getFuelFromStack(stack) >= biofuelConsumption || getPowerFromStack(stack) >= powerConsumption;
    }

    public void consumeFuel(ItemStack stack) {
        if (getFuelFromStack(stack) >= biofuelConsumption) {
            stack.getTag().getCompound("Fluid").putInt("Amount", Math.max(0, stack.getTag().getCompound("Fluid").getInt("Amount") - biofuelConsumption));
        } else {
            stack.getTag().putLong("Energy", stack.getTag().getLong("Energy") - powerConsumption);
        }
    }

    public void setCanCharge(ItemStack stack, boolean canCharge) {
        stack.getTag().putBoolean("CanCharge", canCharge);
    }

    public void setSelectedDrillTier(ItemStack stack, InfinityTier tier) {
        stack.getTag().putString("Selected", tier.name());
    }

    @Override
    public void addTooltipDetails(@Nullable Key key, ItemStack stack, List<ITextComponent> tooltip, boolean advanced) {
        long power = getPowerFromStack(stack);
        Pair<InfinityTier, InfinityTier> braquet = InfinityTier.getTierBraquet(power);
        InfinityTier current = getSelectedTier(stack);
        tooltip.add(new TranslationTextComponent("text.industrialforegoing.display.current_area").func_240702_b_(" ").func_240702_b_(getFormattedArea(current, current.getRadius())).func_240699_a_(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("text.industrialforegoing.display.tier").func_240702_b_(" ").func_240702_b_(braquet.getLeft().getColor() + braquet.getLeft().getLocalizedName()).func_240699_a_(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("text.industrialforegoing.display.power").func_240702_b_(" ").func_240702_b_(NumberFormat.getNumberInstance(Locale.ROOT).format(power)).func_240702_b_("/").func_240702_b_(NumberFormat.getNumberInstance(Locale.ROOT).format(braquet.getRight().getPowerNeeded())).func_240702_b_("RF ").func_230529_a_(new TranslationTextComponent("text.industrialforegoing.display.next_tier")).func_240699_a_(TextFormatting.GRAY));
        int fuelAmount = getFuelFromStack(stack);
        tooltip.add(new TranslationTextComponent("text.industrialforegoing.display.fluid").func_240702_b_(" ").func_240702_b_(NumberFormat.getNumberInstance(Locale.ROOT).format(fuelAmount)).func_240702_b_("/").func_240702_b_(NumberFormat.getNumberInstance(Locale.ROOT).format(1000000)).func_240702_b_(" mb of Biofuel").func_240699_a_(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("text.industrialforegoing.display.max_area").func_240702_b_(" ").func_240702_b_(getFormattedArea(braquet.getLeft(), braquet.getLeft().getRadius())).func_240699_a_(TextFormatting.GRAY));
        if (canCharge(stack)) {
            tooltip.add(new TranslationTextComponent("text.industrialforegoing.display.charging").func_240699_a_(TextFormatting.GRAY).func_230529_a_(new TranslationTextComponent("text.industrialforegoing.display.enabled").func_240699_a_(TextFormatting.GREEN)));
        } else {
            tooltip.add(new TranslationTextComponent("text.industrialforegoing.display.charging").func_240699_a_(TextFormatting.GRAY).func_230529_a_(new TranslationTextComponent("text.industrialforegoing.display.disabled").func_240699_a_(TextFormatting.RED)));
        }
        if (isSpecial(stack))
            tooltip.add(new TranslationTextComponent("text.industrialforegoing.display.special").func_240699_a_(TextFormatting.GOLD));
    }

    public Pair<BlockPos, BlockPos> getArea(BlockPos pos, Direction facing, InfinityTier currentTier, boolean withDepth) {
        int radius = currentTier.getRadius();
        BlockPos bottomLeft = pos.offset(facing.getAxis() == Direction.Axis.Y ? Direction.SOUTH : Direction.DOWN, radius).offset(facing.getAxis() == Direction.Axis.Y ? Direction.WEST : facing.rotateYCCW(), radius);
        BlockPos topRight = pos.offset(facing.getAxis() == Direction.Axis.Y ? Direction.NORTH : Direction.UP, radius).offset(facing.getAxis() == Direction.Axis.Y ? Direction.EAST : facing.rotateY(), radius);
        if (facing.getAxis() != Direction.Axis.Y && radius > 0) {
            bottomLeft = bottomLeft.offset(Direction.UP, radius - 1);
            topRight = topRight.offset(Direction.UP, radius - 1);
        }
        if (currentTier == InfinityTier.ARTIFACT && withDepth) {
            topRight = topRight.offset(facing.getOpposite(), radius);
        }
        return Pair.of(bottomLeft, topRight);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = MultimapBuilder.hashKeys().arrayListValues().build();
        if (slot == EquipmentSlotType.MAINHAND) {
            InfinityTier infinityTier = InfinityTier.getTierBraquet(getPowerFromStack(stack)).getLeft();
            multimap.put(Attributes.field_233823_f_, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", 2 + infinityTier.getRadius(), AttributeModifier.Operation.ADDITION)); //AttackDamage
            multimap.put(Attributes.field_233825_h_, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -2.5D, AttributeModifier.Operation.ADDITION)); //AttackSpeed
        }
        return multimap;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(this.getTranslationKey()).func_240699_a_(TextFormatting.DARK_GRAY);
    }

    @Nullable
    @Override
    public Container createMenu(int menu, PlayerInventory p_createMenu_2_, PlayerEntity playerEntity) {
        return new BasicAddonContainer(ItemStackHarnessRegistry.getHarnessCreators().get(this).apply(playerEntity.getHeldItemMainhand()), new HeldStackLocatorInstance(true), new IWorldPosCallable() {
            @Override
            public <T> Optional<T> apply(BiFunction<World, BlockPos, T> p_221484_1_) {
                return Optional.empty();
            }
        }, playerEntity.inventory, menu);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity player, Hand handIn) {
        if (player.isCrouching()) {
            if (player instanceof ServerPlayerEntity) {
                NetworkHooks.openGui((ServerPlayerEntity) player, this, buffer ->
                        LocatorFactory.writePacketBuffer(buffer, new HeldStackLocatorInstance(handIn == Hand.MAIN_HAND)));
            }
            return ActionResult.resultSuccess(player.getHeldItem(handIn));
        }
        return super.onItemRightClick(worldIn, player, handIn);
    }

    @Override
    public boolean shouldSyncTag() {
        return true;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new InfinityCapabilityProvider(stack);
    }

    @Override
    public void handleButtonMessage(int id, PlayerEntity playerEntity, CompoundNBT compound) {
        ItemStack stack = playerEntity.getHeldItem(Hand.MAIN_HAND);
        if (!(stack.getItem() instanceof ItemInfinity)) stack = playerEntity.getHeldItem(Hand.OFF_HAND);
        if (stack.getItem() instanceof ItemInfinity) {
            if (id == 1) {
                InfinityTier prev = getSelectedTier(stack).getPrev(InfinityTier.getTierBraquet(getPowerFromStack(stack)).getLeft());
                setSelectedDrillTier(stack, prev);
            }
            if (id == 2) {
                InfinityTier next = getSelectedTier(stack).getNext(InfinityTier.getTierBraquet(getPowerFromStack(stack)).getLeft());
                setSelectedDrillTier(stack, next);
            }
            if (id == 3) {
                setCanCharge(stack, !canCharge(stack));
            }
        }
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {

    }

    @Override
    public List<IFactory<? extends IScreenAddon>> getScreenAddons(Supplier<ItemStack> stack) {
        List<IFactory<? extends IScreenAddon>> factory = new ArrayList<>();
        factory.add(() -> new ArrowButtonScreenAddon((ArrowButtonComponent) new ArrowButtonComponent(154, 20, 14, 14, FacingUtil.Sideness.RIGHT).setId(2)));
        factory.add(() -> new ArrowButtonScreenAddon((ArrowButtonComponent) new ArrowButtonComponent(54, 20, 14, 14, FacingUtil.Sideness.LEFT).setId(1)));
        factory.add(() -> new TextScreenAddon("", 54 + 14 + 4, 24, false) {
            @Override
            public String getText() {
                InfinityTier current = ItemInfinity.getSelectedTier(stack.get());
                return TextFormatting.DARK_GRAY + "Area: " + ItemInfinity.getFormattedArea(current, current.getRadius());
            }
        });
        factory.add(() -> new StateButtonAddon(new ButtonComponent(54, 38, 14, 15).setId(3), new StateButtonInfo(0, AssetTypes.BUTTON_SIDENESS_ENABLED), new StateButtonInfo(1, AssetTypes.BUTTON_SIDENESS_DISABLED)) {
            @Override
            public int getState() {
                return ItemInfinity.canCharge(stack.get()) ? 0 : 1;
            }
        });
        factory.add(() -> new TextScreenAddon("", 54 + 14 + 4, 42, false) {
            @Override
            public String getText() {
                if (ItemInfinity.canCharge(stack.get())) {//setStyle
                    return new TranslationTextComponent("text.industrialforegoing.display.charging").func_240701_a_(TextFormatting.DARK_GRAY).func_230529_a_(new TranslationTextComponent("text.industrialforegoing.display.enabled").func_240701_a_(TextFormatting.GREEN)).getString();
                } else {
                    return new TranslationTextComponent("text.industrialforegoing.display.charging").func_240701_a_(TextFormatting.DARK_GRAY).func_230529_a_(new TranslationTextComponent("text.industrialforegoing.display.disabled").func_240701_a_(TextFormatting.RED)).getString();
                }
            }
        });
        return factory;
    }
}

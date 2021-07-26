package com.buuz135.industrial.item.infinity.item;

import com.buuz135.industrial.entity.InfinityLauncherProjectileEntity;
import com.buuz135.industrial.item.infinity.ItemInfinity;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleTool;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.ArrowButtonScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.TextScreenAddon;
import com.hrznstudio.titanium.component.button.ArrowButtonComponent;
import com.hrznstudio.titanium.item.BasicItem;
import com.hrznstudio.titanium.util.FacingUtil;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.world.item.Item.Properties;

public class ItemInfinityLauncher extends ItemInfinity {

    public static int POWER_CONSUMPTION = 100000;
    public static int FUEL_CONSUMPTION = 30;

    public static String PLUNGER_NBT = "Plunger";

    public ItemInfinityLauncher(CreativeModeTab group) {
        super("infinity_launcher", group, new Properties().stacksTo(1), POWER_CONSUMPTION, FUEL_CONSUMPTION, false);
        this.disableArea();
    }

    public static float getArrowVelocity(int charge) {
        float f = (float) charge / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    @Override
    public void addNbt(ItemStack stack, long power, int fuel, boolean special) {
        super.addNbt(stack, power, fuel, special);
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putInt(PLUNGER_NBT, PlungerAction.RELEASE.getId());
        stack.setTag(nbt);
    }

    @Override
    public void addTooltipDetails(BasicItem.Key key, ItemStack stack, List<Component> tooltip, boolean advanced) {
        super.addTooltipDetails(key, stack, tooltip, advanced);
        PlungerAction action = getPlungerAction(stack);
        tooltip.add(new TranslatableComponent("text.industrialforegoing.action").withStyle(ChatFormatting.GRAY).append(new TranslatableComponent("text.industrialforegoing.launcher." + action.name().toLowerCase(Locale.ROOT)).withStyle(action.getColor())));
    }

    public PlungerAction getPlungerAction(ItemStack stack) {
        return PlungerAction.getFromId(stack.getOrCreateTag().getInt(PLUNGER_NBT));
    }

    public void setPlungerAction(ItemStack stack, PlungerAction plungerAction) {
        stack.getOrCreateTag().putInt(PLUNGER_NBT, plungerAction.getId());
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000 / 2;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == Enchantments.UNBREAKING;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (!playerIn.isShiftKeyDown()) {
            playerIn.startUsingItem(handIn);
            return InteractionResultHolder.consume(itemstack);
        }
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player) {
            int time = this.getUseDuration(stack) - timeLeft;
            float velo = getArrowVelocity(time);
            if (!((double) velo < 0.1D) && enoughFuel(stack)) {
                Player playerentity = (Player) entityLiving;
                playerentity.getCooldowns().addCooldown(this, 20);
                if (!worldIn.isClientSide) {
                    InfinityLauncherProjectileEntity abstractarrowentity = new InfinityLauncherProjectileEntity(worldIn, playerentity, getPlungerAction(stack), getSelectedTier(stack).getRadius());
                    abstractarrowentity.shootFromRotation(playerentity, playerentity.xRot, playerentity.yRot, 0.0F, velo * 3.0F, 1.0F);
                    if (velo == 1.0F) {
                        abstractarrowentity.setCritArrow(true);
                    }
                    int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
                    if (j > 0) {
                        abstractarrowentity.setBaseDamage(abstractarrowentity.getBaseDamage() + (double) j * 0.5D + 0.5D);
                    }
                    int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
                    if (k > 0) {
                        abstractarrowentity.setKnockback(k);
                    }
                    if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0) {
                        abstractarrowentity.setSecondsOnFire(100);
                    }
                    consumeFuel(stack);
                    abstractarrowentity.pickup = AbstractArrow.Pickup.DISALLOWED;
                    worldIn.addFreshEntity(abstractarrowentity);
                }
                worldIn.playSound((Player) null, playerentity.getX(), playerentity.getY(), playerentity.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (worldIn.random.nextFloat() * 0.4F + 1.2F) + velo * 0.5F);
                playerentity.awardStat(Stats.ITEM_USED.get(this));
            }
        }
    }

    @Override
    public List<IFactory<? extends IScreenAddon>> getScreenAddons(Supplier<ItemStack> stack) {
        List<IFactory<? extends IScreenAddon>> factory = super.getScreenAddons(stack);
        factory.add(() -> new ArrowButtonScreenAddon((ArrowButtonComponent) new ArrowButtonComponent(154, 20 + 16 * 2, 14, 14, FacingUtil.Sideness.RIGHT).setId(4)));
        factory.add(() -> new ArrowButtonScreenAddon((ArrowButtonComponent) new ArrowButtonComponent(54, 20 + 16 * 2, 14, 14, FacingUtil.Sideness.LEFT).setId(5)));
        factory.add(() -> new TextScreenAddon("", 54 + 14 + 4, 24 + 16 * 2, false) {
            @Override
            public String getText() {
                PlungerAction action = getPlungerAction(stack.get());
                return ChatFormatting.DARK_GRAY + new TranslatableComponent("text.industrialforegoing.action").withStyle(ChatFormatting.GRAY).getString() + action.getColor() + new TranslatableComponent("text.industrialforegoing.launcher." + action.name().toLowerCase(Locale.ROOT)).withStyle(action.getColor()).getString();
            }
        });
        return factory;
    }

    @Override
    public void handleButtonMessage(int id, Player playerEntity, CompoundTag compound) {
        super.handleButtonMessage(id, playerEntity, compound);
        ItemStack stack = playerEntity.getItemInHand(InteractionHand.MAIN_HAND);
        PlungerAction plungerAction = getPlungerAction(stack);
        if (id == 4) {
            setPlungerAction(stack, PlungerAction.getFromId(plungerAction.getId() + 1));
        }
        if (id == 5) {
            setPlungerAction(stack, PlungerAction.getFromId(plungerAction.getId() - 1));
        }

    }

    @Override
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {
        new DissolutionChamberRecipe(this.getRegistryName(),
                new Ingredient.Value[]{
                        new Ingredient.ItemValue(new ItemStack(Items.DIAMOND_BLOCK)),
                        new Ingredient.ItemValue(new ItemStack(Items.BOW)),
                        new Ingredient.ItemValue(new ItemStack(Items.DIAMOND_BLOCK)),
                        new Ingredient.ItemValue(new ItemStack(ModuleTool.MOB_IMPRISONMENT_TOOL)),
                        new Ingredient.ItemValue(new ItemStack(ModuleCore.RANGE_ADDONS[11])),
                        new Ingredient.TagValue(IndustrialTags.Items.GEAR_GOLD),
                        new Ingredient.TagValue(IndustrialTags.Items.GEAR_GOLD),
                        new Ingredient.TagValue(IndustrialTags.Items.GEAR_GOLD),
                },
                new FluidStack(ModuleCore.PINK_SLIME.getSourceFluid(), 2000), 400, new ItemStack(this), FluidStack.EMPTY);
    }

    public enum PlungerAction {
        RELEASE(0, ChatFormatting.GREEN), CAPTURE(1, ChatFormatting.GOLD), DAMAGE(2, ChatFormatting.RED);

        private final int id;
        private final ChatFormatting color;

        PlungerAction(int id, ChatFormatting color) {
            this.id = id;
            this.color = color;
        }

        public static PlungerAction getFromId(int id) {
            if (id < 0) return DAMAGE;
            for (PlungerAction value : PlungerAction.values()) {
                if (value.id == id) {
                    return value;
                }
            }
            return RELEASE;
        }

        public int getId() {
            return id;
        }

        public ChatFormatting getColor() {
            return color;
        }
    }

}

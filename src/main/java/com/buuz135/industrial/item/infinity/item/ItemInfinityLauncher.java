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
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemInfinityLauncher extends ItemInfinity {

    public static int POWER_CONSUMPTION = 100000;
    public static int FUEL_CONSUMPTION = 30;

    public static String PLUNGER_NBT = "Plunger";

    public ItemInfinityLauncher(ItemGroup group) {
        super("infinity_launcher", group, new Properties().maxStackSize(1), POWER_CONSUMPTION, FUEL_CONSUMPTION, false);
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
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putInt(PLUNGER_NBT, PlungerAction.RELEASE.getId());
        stack.setTag(nbt);
    }

    @Override
    public void addTooltipDetails(BasicItem.Key key, ItemStack stack, List<ITextComponent> tooltip, boolean advanced) {
        super.addTooltipDetails(key, stack, tooltip, advanced);
        PlungerAction action = getPlungerAction(stack);
        tooltip.add(new TranslationTextComponent("text.industrialforegoing.action").mergeStyle(TextFormatting.GRAY).append(new TranslationTextComponent("text.industrialforegoing.launcher." + action.name().toLowerCase(Locale.ROOT)).mergeStyle(action.getColor())));
    }

    public PlungerAction getPlungerAction(ItemStack stack) {
        return PlungerAction.getFromId(stack.getOrCreateTag().getInt(PLUNGER_NBT));
    }

    public void setPlungerAction(ItemStack stack, PlungerAction plungerAction) {
        stack.getOrCreateTag().putInt(PLUNGER_NBT, plungerAction.getId());
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
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
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if (!playerIn.isSneaking()) {
            playerIn.setActiveHand(handIn);
            return ActionResult.resultConsume(itemstack);
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof PlayerEntity) {
            int time = this.getUseDuration(stack) - timeLeft;
            float velo = getArrowVelocity(time);
            if (!((double) velo < 0.1D) && enoughFuel(stack)) {
                PlayerEntity playerentity = (PlayerEntity) entityLiving;
                playerentity.getCooldownTracker().setCooldown(this, 20);
                if (!worldIn.isRemote) {
                    InfinityLauncherProjectileEntity abstractarrowentity = new InfinityLauncherProjectileEntity(worldIn, playerentity, getPlungerAction(stack), getSelectedTier(stack).getRadius());
                    abstractarrowentity.func_234612_a_(playerentity, playerentity.rotationPitch, playerentity.rotationYaw, 0.0F, velo * 3.0F, 1.0F);
                    if (velo == 1.0F) {
                        abstractarrowentity.setIsCritical(true);
                    }
                    int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
                    if (j > 0) {
                        abstractarrowentity.setDamage(abstractarrowentity.getDamage() + (double) j * 0.5D + 0.5D);
                    }
                    int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
                    if (k > 0) {
                        abstractarrowentity.setKnockbackStrength(k);
                    }
                    if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) {
                        abstractarrowentity.setFire(100);
                    }
                    consumeFuel(stack);
                    abstractarrowentity.pickupStatus = AbstractArrowEntity.PickupStatus.DISALLOWED;
                    worldIn.addEntity(abstractarrowentity);
                }
                worldIn.playSound((PlayerEntity) null, playerentity.getPosX(), playerentity.getPosY(), playerentity.getPosZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + velo * 0.5F);
                playerentity.addStat(Stats.ITEM_USED.get(this));
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
                return TextFormatting.DARK_GRAY + new TranslationTextComponent("text.industrialforegoing.action").mergeStyle(TextFormatting.GRAY).getString() + action.getColor() + new TranslationTextComponent("text.industrialforegoing.launcher." + action.name().toLowerCase(Locale.ROOT)).mergeStyle(action.getColor()).getString();
            }
        });
        return factory;
    }

    @Override
    public void handleButtonMessage(int id, PlayerEntity playerEntity, CompoundNBT compound) {
        super.handleButtonMessage(id, playerEntity, compound);
        ItemStack stack = playerEntity.getHeldItem(Hand.MAIN_HAND);
        PlungerAction plungerAction = getPlungerAction(stack);
        if (id == 4) {
            setPlungerAction(stack, PlungerAction.getFromId(plungerAction.getId() + 1));
        }
        if (id == 5) {
            setPlungerAction(stack, PlungerAction.getFromId(plungerAction.getId() - 1));
        }

    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        new DissolutionChamberRecipe(this.getRegistryName(),
                new Ingredient.IItemList[]{
                        new Ingredient.SingleItemList(new ItemStack(Items.DIAMOND_BLOCK)),
                        new Ingredient.SingleItemList(new ItemStack(Items.BOW)),
                        new Ingredient.SingleItemList(new ItemStack(Items.DIAMOND_BLOCK)),
                        new Ingredient.SingleItemList(new ItemStack(ModuleTool.MOB_IMPRISONMENT_TOOL)),
                        new Ingredient.SingleItemList(new ItemStack(ModuleCore.RANGE_ADDONS[11])),
                        new Ingredient.TagList(IndustrialTags.Items.GEAR_GOLD),
                        new Ingredient.TagList(IndustrialTags.Items.GEAR_GOLD),
                        new Ingredient.TagList(IndustrialTags.Items.GEAR_GOLD),
                },
                new FluidStack(ModuleCore.PINK_SLIME.getSourceFluid(), 2000), 400, new ItemStack(this), FluidStack.EMPTY);
    }

    public enum PlungerAction {
        RELEASE(0, TextFormatting.GREEN), CAPTURE(1, TextFormatting.GOLD), DAMAGE(2, TextFormatting.RED);

        private final int id;
        private final TextFormatting color;

        PlungerAction(int id, TextFormatting color) {
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

        public TextFormatting getColor() {
            return color;
        }
    }

}

package com.buuz135.industrial.item.infinity.item;

import com.buuz135.industrial.entity.InfinityTridentEntity;
import com.buuz135.industrial.item.infinity.InfinityTier;
import com.buuz135.industrial.item.infinity.ItemInfinity;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.ArrowButtonScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.StateButtonAddon;
import com.hrznstudio.titanium.client.screen.addon.StateButtonInfo;
import com.hrznstudio.titanium.client.screen.addon.TextScreenAddon;
import com.hrznstudio.titanium.component.button.ArrowButtonComponent;
import com.hrznstudio.titanium.component.button.ButtonComponent;
import com.hrznstudio.titanium.item.BasicItem;
import com.hrznstudio.titanium.util.FacingUtil;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemInfinityTrident extends ItemInfinity {

    private static String LOYALTY_NBT = "Loyalty";
    private static String RIPTIDE_NBT = "Riptide";
    private static String CHANNELING_NBT = "Channeling";

    private static int LOYALTY_MAX = 5;
    private static int RIPTIDE_MAX = 5;


    public static int POWER_CONSUMPTION = 100000;
    public static int FUEL_CONSUMPTION = 30;

    public ItemInfinityTrident(ItemGroup group) {
        super("infinity_trident", group, new Properties().maxStackSize(1), POWER_CONSUMPTION, FUEL_CONSUMPTION, true);
    }

    @Override
    public void addNbt(ItemStack stack, long power, int fuel, boolean special) {
        super.addNbt(stack, power, fuel, special);
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putInt(LOYALTY_NBT, 0);
        nbt.putInt(RIPTIDE_NBT, 0);
        nbt.putBoolean(CHANNELING_NBT, false);
        stack.setTag(nbt);
    }


    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000 / 2;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if (EnchantmentHelper.getRiptideModifier(itemstack) > 0 && !playerIn.isWet()) {
            return ActionResult.resultFail(itemstack);
        } else if (!playerIn.isSneaking()) {
            playerIn.setActiveHand(handIn);
            return ActionResult.resultConsume(itemstack);
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.type == EnchantmentType.WEAPON || enchantment == Enchantments.IMPALING;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof PlayerEntity) {
            PlayerEntity playerentity = (PlayerEntity) entityLiving;
            int i = this.getUseDuration(stack) - timeLeft;
            if (i >= 10) {
                int riptideModifier = getCurrentRiptide(stack);
                if (riptideModifier <= 0 && enoughFuel(stack)) {
                    if (!worldIn.isRemote) {
                        stack.damageItem(1, playerentity, (player) -> {
                            player.sendBreakAnimation(entityLiving.getActiveHand());
                        });
                        if (riptideModifier == 0) {
                            consumeFuel(stack);
                            InfinityTridentEntity tridententity = new InfinityTridentEntity(worldIn, playerentity, stack);
                            tridententity.func_234612_a_(playerentity, playerentity.rotationPitch, playerentity.rotationYaw, 0.0F, 2.5F + (float) riptideModifier * 0.5F, 1.0F);
                            if (playerentity.abilities.isCreativeMode) {
                                tridententity.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                            }

                            worldIn.addEntity(tridententity);
                            worldIn.playMovingSound((PlayerEntity) null, tridententity, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
                            if (!playerentity.abilities.isCreativeMode) {
                                playerentity.inventory.deleteStack(stack);
                            }
                        }
                    }
                }
                playerentity.addStat(Stats.ITEM_USED.get(this));
                if (riptideModifier > 0 && enoughFuel(stack)) {
                    float f7 = playerentity.rotationYaw;
                    float f = playerentity.rotationPitch;
                    float f1 = -MathHelper.sin(f7 * ((float) Math.PI / 180F)) * MathHelper.cos(f * ((float) Math.PI / 180F));
                    float f2 = -MathHelper.sin(f * ((float) Math.PI / 180F));
                    float f3 = MathHelper.cos(f7 * ((float) Math.PI / 180F)) * MathHelper.cos(f * ((float) Math.PI / 180F));
                    float f4 = MathHelper.sqrt(f1 * f1 + f2 * f2 + f3 * f3);
                    float f5 = 3.0F * ((1.0F + (float) riptideModifier * 2) / 4.0F);
                    f1 = f1 * (f5 / f4);
                    f2 = f2 * (f5 / f4);
                    f3 = f3 * (f5 / f4);
                    playerentity.addVelocity((double) f1, (double) f2, (double) f3);
                    playerentity.startSpinAttack(20);
                    if (playerentity.isOnGround()) {
                        playerentity.move(MoverType.SELF, new Vector3d(0.0D, (double) 1.1999999F, 0.0D));
                    }

                    SoundEvent soundevent;
                    if (riptideModifier >= 3) {
                        soundevent = SoundEvents.ITEM_TRIDENT_RIPTIDE_3;
                    } else if (riptideModifier == 2) {
                        soundevent = SoundEvents.ITEM_TRIDENT_RIPTIDE_2;
                    } else {
                        soundevent = SoundEvents.ITEM_TRIDENT_RIPTIDE_1;
                    }
                    worldIn.playMovingSound((PlayerEntity) null, playerentity, soundevent, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    consumeFuel(stack);
                }
            }
        }
    }

    //Trident stuff

    public int getCurrentLoyalty(ItemStack stack) {
        return stack.getOrCreateTag().getInt(LOYALTY_NBT);
    }

    public int getMaxLoyalty(ItemStack stack) {
        InfinityTier infinityTier = InfinityTier.getTierBraquet(getPowerFromStack(stack)).getLeft();
        return Math.min(Math.max(0, infinityTier.getRadius() - 1), LOYALTY_MAX);
    }

    public void setLoyalty(ItemStack stack, int level) {
        stack.getOrCreateTag().putInt(LOYALTY_NBT, level);
    }

    public int getCurrentRiptide(ItemStack stack) {
        return stack.getOrCreateTag().getInt(RIPTIDE_NBT);
    }

    public int getMaxRiptide(ItemStack stack) {
        InfinityTier infinityTier = InfinityTier.getTierBraquet(getPowerFromStack(stack)).getLeft();
        return Math.min(Math.max(0, infinityTier.getRadius() - 1), RIPTIDE_MAX);
    }

    public void setRiptide(ItemStack stack, int level) {
        stack.getOrCreateTag().putInt(RIPTIDE_NBT, level);
    }

    public boolean getCurrentChanneling(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(CHANNELING_NBT);
    }

    public boolean canChanneling(ItemStack stack) {
        return InfinityTier.getTierBraquet(getPowerFromStack(stack)).getLeft().getRadius() >= 4;
    }

    public void setChanneling(ItemStack stack, boolean enabled) {
        stack.getOrCreateTag().putBoolean(CHANNELING_NBT, enabled);
    }

    //GUI Stuff

    @Override
    public List<IFactory<? extends IScreenAddon>> getScreenAddons(Supplier<ItemStack> stack) {
        List<IFactory<? extends IScreenAddon>> factory = super.getScreenAddons(stack);
        factory.add(() -> new ArrowButtonScreenAddon((ArrowButtonComponent) new ArrowButtonComponent(154, 20 + 16 * 2, 14, 14, FacingUtil.Sideness.RIGHT).setId(4)));
        factory.add(() -> new ArrowButtonScreenAddon((ArrowButtonComponent) new ArrowButtonComponent(54, 20 + 16 * 2, 14, 14, FacingUtil.Sideness.LEFT).setId(5)));
        factory.add(() -> new TextScreenAddon("", 54 + 14 + 4, 24 + 16 * 2, false) {
            @Override
            public String getText() {
                return TextFormatting.DARK_GRAY + new TranslationTextComponent("enchantment.minecraft.loyalty").appendString(": ").appendString(getCurrentLoyalty(stack.get()) + "/" + getMaxLoyalty(stack.get())).getString();
            }
        });
        int y = 16;
        factory.add(() -> new ArrowButtonScreenAddon((ArrowButtonComponent) new ArrowButtonComponent(154, 20 + 16 * 2 + y , 14, 14, FacingUtil.Sideness.RIGHT).setId(6)));
        factory.add(() -> new ArrowButtonScreenAddon((ArrowButtonComponent) new ArrowButtonComponent(54, 20 + 16 * 2 + y, 14, 14, FacingUtil.Sideness.LEFT).setId(7)));
        factory.add(() -> new TextScreenAddon("", 54 + 14 + 4, 24 + 16 * 2 + y, false) {
            @Override
            public String getText() {
                return TextFormatting.DARK_GRAY + new TranslationTextComponent("enchantment.minecraft.riptide").appendString(": ").appendString(getCurrentRiptide(stack.get()) + "/" + getMaxRiptide(stack.get())).getString();
            }
        });
        factory.add(() -> new StateButtonAddon(new ButtonComponent(54, 20 + 16 * 2 + y * 2, 14, 14).setId(8), new StateButtonInfo(0, AssetTypes.BUTTON_SIDENESS_ENABLED), new StateButtonInfo(1, AssetTypes.BUTTON_SIDENESS_DISABLED)) {
            @Override
            public int getState() {
                return getCurrentChanneling(stack.get()) ? 0 : 1;
            }
        });
        factory.add(() -> new TextScreenAddon(TextFormatting.DARK_GRAY + new TranslationTextComponent("enchantment.minecraft.channeling").getString(), 54 + 14 + 4, 24 + 16 * 2 + y * 2, false));
        return factory;
    }

    @Override
    public void handleButtonMessage(int id, PlayerEntity playerEntity, CompoundNBT compound) {
        super.handleButtonMessage(id, playerEntity, compound);
        ItemStack stack = playerEntity.getHeldItem(Hand.MAIN_HAND);
        int current = getCurrentLoyalty(stack);
        int max = getMaxLoyalty(stack);
        if (id == 5 && current > 0) {
            setLoyalty(stack, Math.max(current - 1, 0));
        }
        if (id == 4 && current < max) {
            setRiptide(stack, 0);
            setLoyalty(stack, Math.min(max, current + 1));
        }
        current = getCurrentRiptide(stack);
        max = getMaxRiptide(stack);
        if (id == 7 && current > 0) {
            setRiptide(stack, Math.max(current - 1, 0));
        }
        if (id == 6 && current < max) {
            setLoyalty(stack, 0);
            setRiptide(stack, Math.min(max, current + 1));
        }
        if (id == 8 && canChanneling(stack)) {
            setChanneling(stack, !getCurrentChanneling(stack));
        }
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        new DissolutionChamberRecipe(this.getRegistryName(),
                new Ingredient.IItemList[]{
                        new Ingredient.SingleItemList(new ItemStack(Items.DIAMOND_BLOCK)),
                        new Ingredient.SingleItemList(new ItemStack(Items.TRIDENT)),
                        new Ingredient.SingleItemList(new ItemStack(Items.DIAMOND_BLOCK)),
                        new Ingredient.SingleItemList(new ItemStack(Items.DIAMOND_HOE)),
                        new Ingredient.SingleItemList(new ItemStack(ModuleCore.RANGE_ADDONS[11])),
                        new Ingredient.TagList(IndustrialTags.Items.GEAR_GOLD),
                        new Ingredient.TagList(IndustrialTags.Items.GEAR_GOLD),
                        new Ingredient.TagList(IndustrialTags.Items.GEAR_GOLD),
                },
                new FluidStack(ModuleCore.PINK_SLIME.getSourceFluid(), 2000), 400, new ItemStack(this), FluidStack.EMPTY);
    }

    @Override
    public void addTooltipDetails(BasicItem.Key key, ItemStack stack, List<ITextComponent> tooltip, boolean advanced) {
        super.addTooltipDetails(key, stack, tooltip, advanced);
        addTooltip(tooltip, "enchantment.minecraft.loyalty", getCurrentLoyalty(stack));
        addTooltip(tooltip, "enchantment.minecraft.riptide", getCurrentRiptide(stack));
        addTooltip(tooltip, "enchantment.minecraft.channeling", getCurrentChanneling(stack) ? 1 : 0);
    }

    private void addTooltip(List<ITextComponent> tooltip, String type, int value){
        if (value > 0){
            String level = "0";
            switch (value) {
                case 1:
                    level = "I";
                    break;
                case 2:
                    level = "II";
                    break;
                case 3:
                    level = "III";
                    break;
                case 4:
                    level = "IV";
                    break;
                case 5:
                    level = "V";
                    break;
            }
            tooltip.add(new TranslationTextComponent(type).appendString(" " + level).mergeStyle(TextFormatting.GRAY));
        }

    }

}

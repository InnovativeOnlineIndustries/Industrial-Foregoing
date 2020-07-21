package com.buuz135.industrial.block.agriculturehusbandry.tile;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.config.machine.agriculturehusbandry.MobCrusherConfig;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.module.ModuleCore;
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
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.items.ItemHandlerHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MobCrusherTile extends IndustrialAreaWorkingTile<MobCrusherTile> {

    private final Method GET_EXPERIENCE_POINTS = ObfuscationReflectionHelper.findMethod(LivingEntity.class, "func_70693_a", PlayerEntity.class);
    private final Method GET_DROP_CHANCE = ObfuscationReflectionHelper.findMethod(MobEntity.class, "func_205712_c", EquipmentSlotType.class);

    @Save
    private SidedInventoryComponent<MobCrusherTile> output;
    @Save
    private SidedFluidTankComponent<MobCrusherTile> tank;
    @Save
    private boolean dropXP;
    private ButtonComponent buttonComponent;

    public MobCrusherTile() {
        super(ModuleAgricultureHusbandry.MOB_CRUSHER, RangeManager.RangeType.BEHIND, true);
        if (!GET_EXPERIENCE_POINTS.isAccessible()) GET_EXPERIENCE_POINTS.setAccessible(true);
        if (!GET_DROP_CHANCE.isAccessible()) GET_DROP_CHANCE.setAccessible(true);
        this.dropXP = true;
        this.addTank(tank = (SidedFluidTankComponent<MobCrusherTile>) new SidedFluidTankComponent<MobCrusherTile>("essence", MobCrusherConfig.tankSize, 43, 20, 0).
                setColor(DyeColor.LIME).
                setTankAction(FluidTankComponent.Action.DRAIN).
                setComponentHarness(this).
                setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(ModuleCore.ESSENCE.getSourceFluid()))
        );
        this.addInventory(output = (SidedInventoryComponent<MobCrusherTile>) new SidedInventoryComponent<MobCrusherTile>("output", 64, 22, 6 * 3, 1).
                setColor(DyeColor.ORANGE).
                setRange(6, 3).
                setInputFilter((stack, integer) -> false).
                setComponentHarness(this)
        );
        this.addButton(buttonComponent = new ButtonComponent(154 - 18 * 2, 84, 14, 14) {
            @Override
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                return Collections.singletonList(() -> new StateButtonAddon(this,
                        new StateButtonInfo(0, AssetTypes.BUTTON_SIDENESS_ENABLED, TextFormatting.GOLD + LangUtil.getString("tooltip.industrialforegoing.mob_crusher.produce"), "tooltip.industrialforegoing.mob_crusher.produce_extra"),
                        new StateButtonInfo(1, AssetTypes.BUTTON_SIDENESS_DISABLED, TextFormatting.GOLD + LangUtil.getString("tooltip.industrialforegoing.mob_crusher.consume"), "tooltip.industrialforegoing.mob_crusher.consume_extra")) {
                    @Override
                    public int getState() {
                        return dropXP ? 0 : 1;
                    }
                });
            }
        }.setPredicate((playerEntity, compoundNBT) -> {
            this.dropXP = !this.dropXP;
            markForUpdate();
        }));
    }

    @Override
    public WorkAction work() {
        if (hasEnergy(MobCrusherConfig.powerPerOperation)) {
            List<MobEntity> mobs = this.world.getEntitiesWithinAABB(MobEntity.class, getWorkingArea().getBoundingBox());
            if (mobs.size() > 0) {
                MobEntity entity = mobs.get(0);
                FakePlayer player = IndustrialForegoing.getFakePlayer(this.world);
                int experience = 0;
                try {
                    experience = (int) GET_EXPERIENCE_POINTS.invoke(entity, player);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                int looting = 0;
                if (!dropXP) {
                    looting = this.world.rand.nextInt(4);
                    ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
                    EnchantmentHelper.setEnchantments(Collections.singletonMap(Enchantments.LOOTING, looting), sword);
                    player.setHeldItem(Hand.MAIN_HAND, sword);
                }
                //Loot from table
                DamageSource source = DamageSource.causePlayerDamage(player);
                LootTable table = this.world.getServer().getLootTableManager().getLootTableFromLocation(entity.getLootTableResourceLocation());
                LootContext.Builder context = new LootContext.Builder((ServerWorld) this.world)
                        .withRandom(this.world.rand)
                        .withParameter(LootParameters.THIS_ENTITY, entity)
                        .withParameter(LootParameters.DAMAGE_SOURCE, source)
                        .withParameter(LootParameters.POSITION, this.pos)
                        .withParameter(LootParameters.KILLER_ENTITY, player)
                        .withParameter(LootParameters.LAST_DAMAGE_PLAYER, player)
                        .withNullableParameter(LootParameters.DIRECT_KILLER_ENTITY, player);
                table.generate(context.build(LootParameterSets.ENTITY)).forEach(stack -> ItemHandlerHelper.insertItem(this.output, stack, false));
                //Drops Event
                List<ItemEntity> extra = new ArrayList<>();
                ForgeHooks.onLivingDrops(entity, source, extra, looting, true);
                extra.forEach(itemEntity -> {
                    ItemHandlerHelper.insertItem(this.output, itemEntity.getItem(), false);
                    itemEntity.remove(false);
                });
                //Drop equipment
                for (EquipmentSlotType equipmentslottype : EquipmentSlotType.values()) {
                    ItemStack itemstack = entity.getItemStackFromSlot(equipmentslottype);
                    float dropChance = 0;
                    try {
                        dropChance = (float) GET_DROP_CHANCE.invoke(entity, equipmentslottype);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    if (!itemstack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemstack) && Math.max(this.world.rand.nextFloat() - (float) looting * 0.01F, 0.0F) < dropChance) {
                        if (itemstack.isDamageable()) {
                            itemstack.setDamage(itemstack.getMaxDamage() - this.world.rand.nextInt(1 + this.world.rand.nextInt(Math.max(itemstack.getMaxDamage() - 3, 1))));
                        }
                        ItemHandlerHelper.insertItem(this.output, itemstack, false);
                    }
                }
                if (dropXP)
                    this.tank.fillForced(new FluidStack(ModuleCore.ESSENCE.getSourceFluid(), experience * 20), IFluidHandler.FluidAction.EXECUTE);
                entity.remove(false);
                player.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
                return new WorkAction(0.1f, MobCrusherConfig.powerPerOperation);
            }
        }
        return new WorkAction(1, 0);
    }

    @Override
    public MobCrusherTile getSelf() {
        return this;
    }

    @Override
    protected EnergyStorageComponent<MobCrusherTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(MobCrusherConfig.maxStoredPower, 10, 20);
    }

    @Override
    public int getMaxProgress() {
        return MobCrusherConfig.maxProgress;
    }
}

package com.buuz135.industrial.item;

import com.buuz135.industrial.api.IMachineSettings;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.item.BasicItem;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import com.hrznstudio.titanium.tab.TitaniumTab;
import net.minecraft.ChatFormatting;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class MachineSettingCopier extends IFCustomItem {

    public MachineSettingCopier(TitaniumTab tab) {
        super("machine_settings_copier", tab, new Properties().stacksTo(1));
    }

    @Override
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .pattern("PLP").pattern("LRL").pattern("PRP")
                .define('P', Items.PAPER)
                .define('R', Items.REDSTONE)
                .define('L', IndustrialTags.Items.PLASTIC)
                .save(consumer);
    }

    @Override
    public boolean hasTooltipDetails(@Nullable BasicItem.Key key) {
        return key == null;
    }

    @Override
    public void addTooltipDetails(@Nullable BasicItem.Key key, ItemStack stack, List<Component> tooltip, boolean advanced) {
        super.addTooltipDetails(key, stack, tooltip, advanced);
        if (stack.hasTag()) {
            tooltip.add(Component.translatable("text.industrialforegoing.machine_settings_copier.settings_stored").withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("text.industrialforegoing.machine_settings_copier.settings_clear").withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable("text.industrialforegoing.machine_settings_copier.settings_can_copy").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var tile = context.getLevel().getBlockEntity(context.getClickedPos());
        var player = context.getPlayer();
        var stack = context.getItemInHand();
        if (tile instanceof IMachineSettings machineSettings) {
            if (stack.hasTag()) {
                if (!context.getLevel().isClientSide()) machineSettings.loadSettings(player, stack.getTag());
                player.playSound(SoundEvents.ANVIL_USE, 0.1F, 1.0F);
                player.displayClientMessage(Component.translatable("text.industrialforegoing.machine_settings_copier.settings_stored"), true);
                return InteractionResult.SUCCESS;
            } else {
                if (!context.getLevel().isClientSide()) {
                    CompoundTag tag = new CompoundTag();
                    machineSettings.saveSettings(player, tag);
                    stack.setTag(tag);
                }
                player.playSound(SoundEvents.ARROW_HIT_PLAYER, 0.5F, 1.0F);
                player.displayClientMessage(Component.translatable("text.industrialforegoing.machine_settings_copier.settings_copied"), true);
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level p_41432_, Player player, InteractionHand hand) {
        if (player.isShiftKeyDown() && player.getItemInHand(hand).hasTag()) {
            player.getItemInHand(hand).setTag(null);
            player.playSound(SoundEvents.FIRE_EXTINGUISH, 0.5F, 1.0F);
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
        return super.use(p_41432_, player, hand);
    }
}

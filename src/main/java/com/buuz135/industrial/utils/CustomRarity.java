package com.buuz135.industrial.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.function.UnaryOperator;

public class CustomRarity {

    public static final EnumProxy<Rarity> PITY = new EnumProxy<>(
            Rarity.class, -1, "industrialforegoing:pity", (UnaryOperator<Style>) style -> style.applyFormat(ChatFormatting.GREEN)
    );
    public static final EnumProxy<Rarity> SIMPLE = new EnumProxy<>(
            Rarity.class, -1, "industrialforegoing:simple", (UnaryOperator<Style>) style -> style.applyFormat(ChatFormatting.AQUA)
    );
    public static final EnumProxy<Rarity> ADVANCED = new EnumProxy<>(
            Rarity.class, -1, "industrialforegoing:advanced", (UnaryOperator<Style>) style -> style.applyFormat(ChatFormatting.LIGHT_PURPLE)
    );
    public static final EnumProxy<Rarity> SUPREME = new EnumProxy<>(
            Rarity.class, -1, "industrialforegoing:supreme", (UnaryOperator<Style>) style -> style.applyFormat(ChatFormatting.GOLD)
    );
}

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
package com.buuz135.industrial.proxy;

import com.buuz135.industrial.proxy.event.FakePlayerRideEntityHandler;
import com.buuz135.industrial.proxy.event.MeatFeederTickHandler;
import com.buuz135.industrial.proxy.event.MobDeathHandler;
import com.buuz135.industrial.proxy.event.SkullHandler;
import com.buuz135.industrial.utils.CraftingUtils;
import com.google.gson.JsonParser;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.util.URLUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CommonProxy {

    public static final String CONTRIBUTORS_FILE = "https://raw.githubusercontent.com/Buuz135/Industrial-Foregoing/master/contributors.json";
    public static List<String> CONTRIBUTORS = new ArrayList<>();

    public static DamageSource custom = new DamageSource("if_custom") {
        @Override
        public ITextComponent getDeathMessage(LivingEntity entityLivingBaseIn) {
            return new TranslationTextComponent("text.industrialforegoing.chat.slaughter_kill", entityLivingBaseIn.getDisplayName(), TextFormatting.RESET);
        }
    };

    public void run() {
        MinecraftForge.EVENT_BUS.register(new StrawRegistry());
        MinecraftForge.EVENT_BUS.register(new MobDeathHandler());
        MinecraftForge.EVENT_BUS.register(new FakePlayerRideEntityHandler());
        MinecraftForge.EVENT_BUS.register(new SkullHandler());

        EventManager.forge(LivingEvent.LivingUpdateEvent.class).process(MeatFeederTickHandler::onTick).subscribe();

        try {
            new JsonParser().parse(URLUtil.readUrl(new URL(CONTRIBUTORS_FILE))).getAsJsonObject().get("uuid").getAsJsonArray().forEach(jsonElement -> CONTRIBUTORS.add(jsonElement.getAsString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        CraftingUtils.generateCrushedRecipes();
    }

}

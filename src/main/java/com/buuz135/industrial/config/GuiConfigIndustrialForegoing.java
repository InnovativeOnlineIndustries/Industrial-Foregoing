package com.buuz135.industrial.config;

import com.buuz135.industrial.utils.Reference;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.Arrays;
import java.util.List;

public class GuiConfigIndustrialForegoing extends GuiConfig {

    public GuiConfigIndustrialForegoing(GuiScreen parent) {
        super(parent, getConfigElements(), Reference.MOD_ID, false, false, "Industrial Foregoing Config");
    }

    public static List<IConfigElement> getConfigElements() {
        return Arrays.asList(new ConfigElement(CustomConfiguration.config.getCategory(Configuration.CATEGORY_CLIENT)), new ConfigElement(CustomConfiguration.config.getCategory(Configuration.CATEGORY_GENERAL)), new ConfigElement(CustomConfiguration.config.getCategory("machines")));
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        if (CustomConfiguration.config.hasChanged()) CustomConfiguration.config.save();
        CustomConfiguration.sync();
    }
}

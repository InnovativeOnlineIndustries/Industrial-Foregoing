package com.buuz135.industrial.config;

import com.buuz135.industrial.utils.Reference;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

public class GuiConfigIndustrialForegoing extends GuiConfig {

    public GuiConfigIndustrialForegoing(GuiScreen parent) {
        super(parent, new ConfigElement(CustomConfiguration.config.getCategory("")).getChildElements(), Reference.MOD_ID, false, false, "Industrial Foregoing Config");
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

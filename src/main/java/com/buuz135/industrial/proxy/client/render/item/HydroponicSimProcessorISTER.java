package com.buuz135.industrial.proxy.client.render.item;

import com.buuz135.industrial.item.HydroponicSimulationProcessorItem;
import com.buuz135.industrial.proxy.client.ClientProxy;
import com.buuz135.industrial.utils.IFAttachments;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HydroponicSimProcessorISTER extends IndustrialForegoingISTER {

    public static final HydroponicSimProcessorISTER INSTANCE = new HydroponicSimProcessorISTER();

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {

    }

    @Override
    public void renderByItem(HolderLookup.Provider access, @NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer, int light, int overlayLight) {
        matrix.pushPose();
        if (displayContext == ItemDisplayContext.GUI) Lighting.setupForFlatItems();
        matrix.translate(0.5, 0.5, 0.5);
        if (!Screen.hasShiftDown() || displayContext != ItemDisplayContext.GUI)
            renderItem(stack, displayContext, false, matrix, renderer, light, overlayLight, ClientProxy.HYDROPONIC_SIM_PROCESSOR);
        if (stack.has(IFAttachments.HYDROPONIC_SIMULATION_PROCESSOR)) {
            var simulation = new HydroponicSimulationProcessorItem.Simulation(stack.get(IFAttachments.HYDROPONIC_SIMULATION_PROCESSOR));
            if (!simulation.getCrop().isEmpty()) {
                if (displayContext == ItemDisplayContext.GUI) matrix.translate(0, 0, 8);
                if (!Screen.hasShiftDown()) {
                    if (displayContext == ItemDisplayContext.GUI) matrix.translate(0.25, -0.25, 0);
                    else matrix.translate(0.1, -0.1, 0.001);
                    matrix.scale(0.5f, 0.5f, 1);
                }
                Minecraft.getInstance().getItemRenderer().renderStatic(simulation.getCrop(), displayContext, light, overlayLight, matrix, renderer, Minecraft.getInstance().level, 0);
            } else if (Screen.hasShiftDown()) {
                renderItem(stack, displayContext, false, matrix, renderer, light, overlayLight, ClientProxy.HYDROPONIC_SIM_PROCESSOR);
            }
        }

        matrix.popPose();
    }
}

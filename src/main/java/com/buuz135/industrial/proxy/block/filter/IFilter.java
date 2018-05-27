package com.buuz135.industrial.proxy.block.filter;

import com.buuz135.industrial.gui.conveyor.GuiConveyor;
import mezz.jei.api.gui.IGhostIngredientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Optional;

import java.awt.*;


public interface IFilter<T extends Entity> {

    boolean acceptsInput(ItemStack stack);

    boolean matches(T entity);

    void setFilter(int slot, ItemStack stack);

    GhostSlot[] getFilter();

    NBTTagCompound serializeNBT();

    void deserializeNBT(NBTTagCompound nbt);

    @Optional.Interface(iface = "mezz.jei.api.gui.IGhostIngredientHandler$Target", modid = "JustEnoughItems", striprefs = true)
    public static class GhostSlot implements IGhostIngredientHandler.Target<ItemStack> {

        private final int x;
        private final int y;
        private int id;
        private ItemStack stack;

        public GhostSlot(int id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.stack = ItemStack.EMPTY;
        }

        public ItemStack getStack() {
            return stack;
        }

        public void setStack(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public Rectangle getArea() {
            if (Minecraft.getMinecraft().currentScreen instanceof GuiConveyor) {
                GuiConveyor gui = (GuiConveyor) Minecraft.getMinecraft().currentScreen;
                return new Rectangle(x + gui.getX(), y + gui.getY(), 18, 18);
            }
            return new Rectangle();
        }

        @Override
        public void accept(ItemStack ingredient) {
            if (Minecraft.getMinecraft().currentScreen instanceof GuiConveyor) {
                ((GuiConveyor) Minecraft.getMinecraft().currentScreen).sendMessage(id, ingredient.serializeNBT());
            }
        }
    }

}

package com.buuz135.industrial.tile.mob;

import com.buuz135.industrial.item.MobImprisonmentToolItem;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.proxy.client.infopiece.ArrowInfoPiece;
import com.buuz135.industrial.proxy.client.infopiece.VillagerTradeExchangerInfoPiece;
import com.buuz135.industrial.tile.CustomElectricMachine;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.utils.Reference;
import lombok.Getter;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VillagerTradeExchangerTile extends CustomElectricMachine {

    private static final String NBT_CURRENT = "Current";

    private IItemHandlerModifiable villager;
    @Getter
    private MerchantRecipeList merchantRecipes;
    @Getter
    private int current;
    private IItemHandlerModifiable input;
    private IItemHandlerModifiable output;

    public VillagerTradeExchangerTile() {
        super(WorkingAreaElectricMachine.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        villager = this.addSimpleInventory(1, "villager", EnumDyeColor.BROWN, "Villager Mob Imprisonment Tool", new BoundingRectangle(145, 61, 18, 18),
                (stack, integer) -> stack.getItem() instanceof MobImprisonmentToolItem && ItemRegistry.mobImprisonmentToolItem.containsEntity(stack) && ItemRegistry.mobImprisonmentToolItem.getEntityFromStack(stack, this.world, false) instanceof EntityVillager,
                (stack, integer) -> true, false, null);
        input = this.addSimpleInventory(1, "input", EnumDyeColor.BLUE, "Trade input", new BoundingRectangle(52, 61, 18, 18),
                (stack, integer) -> true, (stack, integer) -> false, false, null);
        output = this.addSimpleInventory(1, "output", EnumDyeColor.ORANGE, "Trade output", new BoundingRectangle(112, 61, 18, 18),
                (stack, integer) -> false, (stack, integer) -> true, false, null);

    }

    @Override
    public void protectedUpdate() {
        super.protectedUpdate();
        if (!villager.getStackInSlot(0).isEmpty() && villager.getStackInSlot(0).getTagCompound().hasKey("Offers")) {
            merchantRecipes = new MerchantRecipeList(villager.getStackInSlot(0).getTagCompound().getCompoundTag("Offers"));
        } else {
            merchantRecipes = null;
            current = 0;
        }
    }

    @Override
    protected float performWork() {
        if (merchantRecipes != null && merchantRecipes.size() > current) {
            MerchantRecipe recipe = merchantRecipes.get(current);
            if (ItemHandlerHelper.insertItem(output, recipe.getItemToSell().copy(), true).isEmpty()) {
                if (recipe.getItemToBuy().isItemEqual(input.getStackInSlot(0)) && input.getStackInSlot(0).getCount() >= recipe.getItemToBuy().getCount()) {
                    input.getStackInSlot(0).shrink(recipe.getItemToBuy().getCount());
                    ItemHandlerHelper.insertItem(output, recipe.getItemToSell().copy(), false);
                    return 1;
                }
            }
        }
        return 0;
    }

    @NotNull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound compound1 = super.writeToNBT(compound);
        compound1.setInteger(NBT_CURRENT, current);
        return compound1;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        current = compound.getInteger(NBT_CURRENT);
    }

    @NotNull
    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer<?> container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
        pieces.add(new VillagerTradeExchangerInfoPiece(this));
        pieces.add(new BasicRenderedGuiPiece(54 + 18 + 4, 61, 25, 18, new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 24, 5));
        pieces.add(new ArrowInfoPiece(138, 28, 17, 56, "Select previous trade") {
            @Override
            protected void clicked() {
                if (merchantRecipes == null || merchantRecipes.size() == 0) return;
                current = (current - 1);
                if (current < 0) current = merchantRecipes.size() - 1;

            }
        });
        pieces.add(new ArrowInfoPiece(156, 28, 33, 56, "Select next trade") {
            @Override
            protected void clicked() {
                if (merchantRecipes == null || merchantRecipes.size() == 0) return;
                current = (current + 1) % merchantRecipes.size();
            }
        });
        return pieces;
    }


}

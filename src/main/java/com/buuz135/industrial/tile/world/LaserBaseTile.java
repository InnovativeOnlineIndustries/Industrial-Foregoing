package com.buuz135.industrial.tile.world;

import java.util.ArrayList;
import java.util.List;

import com.buuz135.industrial.api.recipe.LaserDrillEntry;
import com.buuz135.industrial.item.LaserLensItem;
import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.proxy.client.infopiece.LaserBaseInfoPiece;
import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.CustomSidedTileEntity;
import com.buuz135.industrial.utils.ItemStackWeightedItem;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.inventory.SyncProviderLevel;

public class LaserBaseTile extends CustomSidedTileEntity {

	private static String NBT_CURRENT = "currentWork";

	private int currentWork;
	private ItemStackHandler outItems;
	private ItemStackHandler lensItems;

	public LaserBaseTile() {
		super(LaserBaseTile.class.getName().hashCode());
		currentWork = 0;
	}

	@Override
	protected void initializeInventories() {
		super.initializeInventories();
		lensItems = new ItemStackHandler(2 * 3) {
			@Override
			protected void onContentsChanged(int slot) {
				LaserBaseTile.this.markDirty();
			}
		};
		this.addInventory(new CustomColoredItemHandler(lensItems, EnumDyeColor.GREEN, "Lens items", 18 * 2, 25, 2, 3) {
			@Override
			public boolean canInsertItem(int slot, ItemStack stack) {
				return stack.getItem() instanceof LaserLensItem;
			}

			@Override
			public boolean canExtractItem(int slot) {
				return super.canExtractItem(slot);
			}
		});
		this.addInventoryToStorage(lensItems, "lensItems");

		outItems = new ItemStackHandler(3 * 6) {
			@Override
			protected void onContentsChanged(int slot) {
				LaserBaseTile.this.markDirty();
			}
		};
		this.addInventory(new CustomColoredItemHandler(outItems, EnumDyeColor.ORANGE, "Output items", 18 * 4 + 6, 25, 6, 3) {
			@Override
			public boolean canInsertItem(int slot, ItemStack stack) {
				return false;
			}

			@Override
			public boolean canExtractItem(int slot) {
				return true;
			}
		});
		this.addInventoryToStorage(outItems, "outItems");
		registerSyncIntPart(NBT_CURRENT, nbtTagInt -> currentWork = nbtTagInt.getInt(), () -> new NBTTagInt(currentWork), SyncProviderLevel.GUI);
	}

	@Override
	public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
		List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
		pieces.add(new LaserBaseInfoPiece(this, 10, 25));
		return pieces;
	}

	@Override
	protected void innerUpdate() {
		if (this.world.isRemote) return;
		if (currentWork >= getMaxWork()) {
			List<ItemStackWeightedItem> items = new ArrayList<>();
			LaserDrillEntry.LASER_DRILL_ENTRIES.forEach(entry -> {
				int increase = 0;
				float multiple = 1;
				for (int i = 0; i < lensItems.getSlots(); ++i) {
					if (!lensItems.getStackInSlot(i).isEmpty() && lensItems.getStackInSlot(i).getMetadata() == entry.getLaserMeta() && lensItems.getStackInSlot(i).getItem() instanceof LaserLensItem) {
						if (((LaserLensItem) lensItems.getStackInSlot(i).getItem()).isInverted()) {
							increase -= BlockRegistry.laserBaseBlock.getLenseChanceIncrease();
							multiple /= BlockRegistry.laserBaseBlock.getLenseChanceMultiple();
						}
						else {
							increase += BlockRegistry.laserBaseBlock.getLenseChanceIncrease();
							multiple *= BlockRegistry.laserBaseBlock.getLenseChanceMultiple();
						}
					}
				}
				items.add(new ItemStackWeightedItem(entry.getStack(), (int) (entry.getWeight() * multiple + increase)));
			});
			ItemStack stack = WeightedRandom.getRandomItem(this.world.rand, items).getStack().copy();
			if (ItemHandlerHelper.insertItem(outItems, stack, true).isEmpty()) {
				ItemHandlerHelper.insertItem(outItems, stack, false);
			}
			currentWork = 0;
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		currentWork = 0;
		if (compound.hasKey(NBT_CURRENT)) currentWork = compound.getInteger(NBT_CURRENT);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger(NBT_CURRENT, currentWork);
		return super.writeToNBT(compound);
	}

	public int getCurrentWork() {
		return currentWork;
	}

	public int getMaxWork() {
		return BlockRegistry.laserBaseBlock.getWorkNeeded();
	}

	public void increaseWork() {
		++currentWork;
		partialSync(NBT_CURRENT, true);
	}

	@Override
	protected boolean getShowPauseDrawerPiece() {
		return false;
	}

	@Override
	protected boolean getShowRedstoneControlPiece() {
		return false;
	}

	@Override
	protected boolean supportsAddons() {
		return false;
	}
}

/*
 * Copyright (c) 2022 TeamMoeg
 *
 * This file is part of Thermopolium.
 *
 * Thermopolium is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Thermopolium is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Thermopolium. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.thermopolium.container;

import java.util.function.Supplier;

import com.teammoeg.thermopolium.Contents;
import com.teammoeg.thermopolium.blocks.StewPotTileEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class StewPotContainer extends Container {
	public static class OutputSlot extends SlotItemHandler {
		public OutputSlot(IItemHandler inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return false;
		}
	};

	public static class HidableSlot extends SlotItemHandler {
		Supplier<Boolean> vs;

		public HidableSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition,
				Supplier<Boolean> visible) {
			super(itemHandler, index, xPosition, yPosition);
			vs = visible;
		}

		@Override
		public boolean isEnabled() {
			return vs.get();
		}

		@Override
		public boolean canTakeStack(PlayerEntity playerIn) {
			return vs.get();
		}

	}

	StewPotTileEntity tile;

	public StewPotTileEntity getTile() {
		return tile;
	}

	public StewPotContainer(int id, PlayerInventory inv, PacketBuffer buffer) {
		this(id, inv, (StewPotTileEntity) inv.player.world.getTileEntity(buffer.readBlockPos()));
	}

	public StewPotContainer(int id, PlayerInventory inv, StewPotTileEntity te) {
		super(Contents.SCGui.STEWPOT.get(), id);
		tile = te;
		for (int i = 0; i < 9; i++)
			this.addSlot(new HidableSlot(te.getInv(), i, 45 + (i % 3) * 18, 17 + (i / 3) * 18, () -> te.proctype != 2));
		this.addSlot(new SlotItemHandler(te.getInv(), 9, 143, 17));
		this.addSlot(new OutputSlot(te.getInv(), 10, 143, 51));

		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
		for (int i = 0; i < 9; i++)
			addSlot(new Slot(inv, i, 8 + i * 18, 142));
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			itemStack = slotStack.copy();
			if (index == 10) {
				if (!this.mergeItemStack(slotStack, 11, 47, true)) {
					return ItemStack.EMPTY;
				}
				slot.onSlotChange(slotStack, itemStack);
			} else if (index > 10) {
				if (!this.mergeItemStack(slotStack, 9, 10, false))
					if (!this.mergeItemStack(slotStack, 0, 9, false)) {
						if (index < 38)
							if (!this.mergeItemStack(slotStack, 38, 47, false))
								return ItemStack.EMPTY;
							else if (index < 47 && !this.mergeItemStack(slotStack, 11, 38, false))
								return ItemStack.EMPTY;
					}
			} else if (!this.mergeItemStack(slotStack, 11, 47, false)) {
				return ItemStack.EMPTY;
			}
			if (slotStack.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
			if (slotStack.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}
			slot.onTake(playerIn, slotStack);
		}
		return itemStack;
	}
}

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

import com.teammoeg.thermopolium.Contents.THPGui;
import com.teammoeg.thermopolium.blocks.KitchenStoveTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.ForgeHooks;

public class KitchenStoveContainer extends Container {
	public KitchenStoveTileEntity tile;
	public KitchenStoveContainer(int id, PlayerInventory inv, PacketBuffer buffer) {
		this(id,inv,(KitchenStoveTileEntity) inv.player.world.getTileEntity(buffer.readBlockPos()));
	}

	public KitchenStoveContainer(int id, PlayerInventory inv, KitchenStoveTileEntity te) {
		super(THPGui.STOVE.get(), id);
		tile = te;
		this.addSlot(new Slot(tile,0,80,49) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return ForgeHooks.getBurnTime(stack, null) > 0;
			}
		});

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
			if (index == 0) {
				if (!this.mergeItemStack(slotStack, 1, 37, true)) {
					return ItemStack.EMPTY;
				}
				slot.onSlotChange(slotStack, itemStack);
			} else if (index > 0) {
				if (!this.mergeItemStack(slotStack,0, 1, false))
					if (index < 28)
						if (!this.mergeItemStack(slotStack, 28, 37, false))
							return ItemStack.EMPTY;
						else if (index < 37 && !this.mergeItemStack(slotStack, 1, 28, false))
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

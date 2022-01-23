package com.teammoeg.thermopolium.container;

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
	public static class OutputSlot extends SlotItemHandler{
		public OutputSlot(IItemHandler inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return false;
		}
	};
	StewPotTileEntity tile;

	public StewPotTileEntity getTile() {
		return tile;
	}
	public StewPotContainer(int id,PlayerInventory inv,PacketBuffer buffer) {
		this(id,inv,(StewPotTileEntity) inv.player.world.getTileEntity(buffer.readBlockPos()));
	}
	public StewPotContainer(int id,PlayerInventory inv,StewPotTileEntity te) {
		super(Contents.SCGui.STEWPOT.get(), id);
		tile=te;
		for(int i = 0; i < 9; i++)
			this.addSlot(new SlotItemHandler(te.getInv(), i, 45+(i%3)*18, 17+(i/3)*18));
		this.addSlot(new SlotItemHandler(te.getInv(), 9,143,17));
		this.addSlot(new OutputSlot(te.getInv(),10, 143,51));

		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlot(new Slot(inv, j+i*9+9, 8+j*18, 84+i*18));
		for(int i = 0; i < 9; i++)
			addSlot(new Slot(inv, i, 8+i*18, 142));
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}

}

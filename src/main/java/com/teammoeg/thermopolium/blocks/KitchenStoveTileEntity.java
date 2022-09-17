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

package com.teammoeg.thermopolium.blocks;

import com.teammoeg.thermopolium.Main;
import com.teammoeg.thermopolium.container.KitchenStoveContainer;
import com.teammoeg.thermopolium.network.INetworkTile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeHooks;

public class KitchenStoveTileEntity extends INetworkTile implements IInventory,ITickableTileEntity, INamedContainerProvider,AbstractStove {
	private NonNullList<ItemStack> fuel=NonNullList.withSize(1,ItemStack.EMPTY);
	public int process;
	public int processMax;
	private final int speed;
	private final int maxcd;
	private int cd;
	public KitchenStoveTileEntity(TileEntityType<KitchenStoveTileEntity> tet,int spd) {
		super(tet);
		this.speed = spd;
		maxcd=100/speed;
	}

	@Override
	public void handleMessage(short type, int data) {
	}

	@Override
	public void readCustomNBT(CompoundNBT nbt, boolean isClient) {
		process=nbt.getInt("process");
		processMax=nbt.getInt("processMax");
		fuel.set(0,ItemStack.read(nbt.getCompound("fuel")));
		if(!isClient)
			cd=nbt.getInt("cd");
	}

	@Override
	public void writeCustomNBT(CompoundNBT nbt, boolean isClient) {
		nbt.putInt("process",process);
		nbt.putInt("processMax",processMax);
		nbt.put("fuel",fuel.get(0).serializeNBT());
		if(!isClient)
			nbt.putInt("cd",cd);
	}

	@Override
	public void clear() {
		fuel.clear();
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return fuel.get(0).isEmpty();
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return fuel.get(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return ItemStackHelper.getAndSplit(fuel, index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return ItemStackHelper.getAndRemove(fuel, index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
	      this.fuel.set(index, stack);
	      if (stack.getCount() > this.getInventoryStackLimit()) {
	         stack.setCount(this.getInventoryStackLimit());
	      }
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		ItemStack itemstack = fuel.get(0);
        return ForgeHooks.getBurnTime(stack,null) > 0 || stack.getItem() == Items.BUCKET && itemstack.getItem() != Items.BUCKET;
	}

	@Override
	public Container createMenu(int a, PlayerInventory b, PlayerEntity c) {
		return new KitchenStoveContainer(a,b,this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("container." + Main.MODID + ".kitchen_stove.title");
	}
	private boolean consumeFuel() {
        int time = ForgeHooks.getBurnTime(fuel.get(0), IRecipeType.SMELTING);
        if (time <= 0) {
        	process=processMax=0;
        	return false;
        }
        fuel.get(0).shrink(1);
        float ftime=time*1.0f/speed;
        float frac=MathHelper.frac(ftime);
        if(frac>0)
        	processMax=process=(int)ftime+(this.world.rand.nextDouble()<frac?1:0);
        else
        	processMax=process=(int)ftime;
        return true;
    }
	@Override
	public void tick() {
		if(!world.isRemote) {
			BlockState bs=this.getBlockState();
			boolean flag=false;
			boolean requireSync=false;
			if(process<=0&&(bs.get(KitchenStove.LIT)||bs.get(KitchenStove.ASH))) {
				bs=bs.with(KitchenStove.LIT,false).with(KitchenStove.ASH,false);
				flag=true;
			}
			boolean ie=fuel.get(0).isEmpty();
			int fs=bs.get(KitchenStove.FUELED);
			if(ie!=(fs==0)) {
				flag=true;
				bs=bs.with(KitchenStove.FUELED,ie?0:1);
			}
			if(process>0) {
				if(!bs.get(KitchenStove.ASH)) {
					flag=true;
					bs=bs.with(KitchenStove.ASH,true);
				}
				if(bs.get(KitchenStove.LIT)) {
					cd--;
					process--;
					requireSync=true;
					if(cd<=0) {
						bs=bs.with(KitchenStove.LIT,false);
						flag=true;
					}
				}
			}
			if(flag)
				this.world.setBlockState(this.getPos(),bs);
			if(requireSync)
				this.syncData();
		}
	}

	@Override
	public int requestHeat() {
		if(this.process<=0) {
			if(!consumeFuel()) {
				return 0;
			}
			process--;
		}
		BlockState bs=this.getBlockState();
		cd=maxcd;
		if(!bs.get(KitchenStove.LIT))
			this.world.setBlockState(this.getPos(),bs.with(KitchenStove.LIT,true));
		
		return speed;
	}

	@Override
	public boolean canEmitHeat() {
		return this.process>0||ForgeHooks.getBurnTime(fuel.get(0), IRecipeType.SMELTING)>0;
	}

}

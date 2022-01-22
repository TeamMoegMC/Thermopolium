package com.teammoeg.thermopolium.tiles;

import com.teammoeg.thermopolium.Contents.SCTileTypes;

import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

public class StewPotTileEntity extends TileEntity implements ITickableTileEntity {
    private ItemStackHandler inv = new ItemStackHandler(11) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (ForgeHooks.getBurnTime(stack) != 0) return true;
            return false;
        }
    };
    private FluidTank tank=new FluidTank(1250,f->f.getFluid()==Fluids.WATER);
	public StewPotTileEntity() {
		super(SCTileTypes.STEW_POT.get());
	}

	@Override
	public void tick() {
	}
	private void makeSoup() {
		
		for(int i=0;i<9;i++) {
			inv.extractItem(i,1,false);
		}
	}
}

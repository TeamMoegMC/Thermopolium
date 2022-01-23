package com.teammoeg.thermopolium.blocks;

import com.teammoeg.thermopolium.Contents.SCTileTypes;
import com.teammoeg.thermopolium.Main;
import com.teammoeg.thermopolium.container.StewPotContainer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

public class StewPotTileEntity extends TileEntity implements ITickableTileEntity,INamedContainerProvider {
    private ItemStackHandler inv = new ItemStackHandler(11) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return true;
        }
    };
    public ItemStackHandler getInv() {
		return inv;
	}
	private FluidTank tank=new FluidTank(1250,f->f.getFluid()==Fluids.WATER);
	public StewPotTileEntity() {
		super(SCTileTypes.STEW_POT.get());
	}

	public FluidTank getTank() {
		return tank;
	}
	public int i=0;
	public int process;
	public int processMax=100;
	@Override
	public void tick() {
		i+=10;
		tank.setFluid(new FluidStack(Fluids.WATER,i));
		if(i>1250)
			i=0;
		process++;
		if(process>100)
			process=0;
			
	}
	private void makeSoup() {
		
		for(int i=0;i<9;i++) {
			inv.extractItem(i,1,false);
		}
	}

	@Override
	public Container createMenu(int p1, PlayerInventory p2, PlayerEntity p3) {
		return new StewPotContainer(p1,p2,this);
	}

	@Override
	public ITextComponent getDisplayName() {
		 return new TranslationTextComponent("container."+Main.MODID+".stewpot.title");
	}
}

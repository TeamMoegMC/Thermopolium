package com.teammoeg.thermopolium.blocks;

import com.teammoeg.thermopolium.Contents.SCTileTypes;
import com.teammoeg.thermopolium.Main;
import com.teammoeg.thermopolium.container.StewPotContainer;
import com.teammoeg.thermopolium.network.INetworkTile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;

public class StewPotTileEntity extends INetworkTile implements ITickableTileEntity,INamedContainerProvider {
    private ItemStackHandler inv = new ItemStackHandler(11) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
        	if(slot<9)
        		return stack.getItem().getTags().contains(cookable);
        	if(slot==9)
        		return stack.getItem()==Items.BOWL;
            return false;
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
	public short proctype=0;
	public static final short NOP=0;
	public static final short BOILING=1;
	public static final short COOKING=2;
	public static final ResourceLocation cookable=new ResourceLocation(Main.MODID,"cookable");
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
	public boolean canAddFluid() {
		return proctype==0;
	}


	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.readCustomNBT(pkt.getNbtCompound(),true);
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		this.readCustomNBT(nbt,false);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		this.writeCustomNBT(compound,false);
		return super.write(compound);
	}
    public void readCustomNBT(CompoundNBT nbt,boolean isClient) {
    	process=nbt.getInt("process");
    	processMax=nbt.getInt("processMax");
    	proctype=nbt.getShort("worktype");
    }
    @Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		super.read(state, tag);
		this.readCustomNBT(tag,true);
	}

	public void writeCustomNBT(CompoundNBT nbt,boolean isClient) {
    	nbt.putInt("process",process);
    	nbt.putInt("processMax",processMax);
    	nbt.putShort("worktype",proctype);
    }
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT cnbt=new CompoundNBT();
		writeCustomNBT(cnbt,true);
		return new SUpdateTileEntityPacket(this.pos,3,cnbt);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt=super.getUpdateTag();
		writeCustomNBT(nbt,true);
		return nbt;
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

	@Override
	public void handleMessage(short type, int data) {
	}
	IFluidHandler handler=new IFluidHandler() {
		@Override
		public int getTanks() {
			return 1;
		}

		@Override
		public FluidStack getFluidInTank(int t) {
			if(t==0)
			return tank.getFluid();
			return FluidStack.EMPTY;
		}

		@Override
		public int getTankCapacity(int t) {
			if(t==0)
				return tank.getCapacity();
			return 0;
		}

		@Override
		public boolean isFluidValid(int t, FluidStack stack) {
			if(t==0&&canAddFluid())return tank.isFluidValid(stack);
			return false;
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			if(canAddFluid())return tank.fill(resource, action);
			return 0;
		}

		@Override
		public FluidStack drain(FluidStack resource, FluidAction action) {
			if(canAddFluid())return tank.drain(resource, action);
			return FluidStack.EMPTY;
		}

		@Override
		public FluidStack drain(int maxDrain, FluidAction action) {
			if(canAddFluid())return tank.drain(maxDrain, action);
			return FluidStack.EMPTY;
		}
		
	};
	RangedWrapper bowl=new RangedWrapper(inv,9,11) {
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if(slot==10)
				return stack;
			return super.insertItem(slot, stack, simulate);
		}
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if(slot==9)
				return ItemStack.EMPTY;
			return super.extractItem(slot, amount, simulate);
		}
	};
	RangedWrapper ingredient=new RangedWrapper(inv,0,9) {

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			return ItemStack.EMPTY;
		}
	};
	LazyOptional<IItemHandler> up=LazyOptional.of(()->ingredient);
	LazyOptional<IItemHandler> side=LazyOptional.of(()->bowl);
	LazyOptional<IFluidHandler> fl=LazyOptional.of(()->handler);
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if(cap==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if(side==Direction.UP)return up.cast();
			return this.side.cast();
		}
		if(cap==CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return fl.cast();
		return super.getCapability(cap, side);
	}
}

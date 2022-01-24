package com.teammoeg.thermopolium.blocks;

import java.util.List;

import com.teammoeg.thermopolium.Contents.SCTileTypes;
import com.teammoeg.thermopolium.Main;
import com.teammoeg.thermopolium.container.StewPotContainer;
import com.teammoeg.thermopolium.data.recipes.BoilingRecipe;
import com.teammoeg.thermopolium.data.recipes.CookingRecipe;
import com.teammoeg.thermopolium.data.recipes.DissolveRecipe;
import com.teammoeg.thermopolium.data.recipes.StewPendingContext;
import com.teammoeg.thermopolium.fluid.SoupFluid;
import com.teammoeg.thermopolium.network.INetworkTile;
import com.teammoeg.thermopolium.util.FloatemStack;
import com.teammoeg.thermopolium.util.SoupInfo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.SmokingRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;

public class StewPotTileEntity extends INetworkTile implements ITickableTileEntity, INamedContainerProvider {
	private ItemStackHandler inv = new ItemStackHandler(11) {
		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			if (slot < 9)
				return stack.getItem()==Items.POTION||stack.getItem().getTags().contains(cookable);
			if (slot == 9)
				return stack.getItem() == Items.BOWL;
			return false;
		}
	};
	private ItemStackHandler interninv=new ItemStackHandler(9);
	public ItemStackHandler getInv() {
		return inv;
	}

	private FluidTank tank = new FluidTank(1250, f -> {
		Fluid fd=f.getFluid();
		return fd instanceof SoupFluid
				||fd.getTags().contains(boilable);});
	

	public StewPotTileEntity() {
		super(SCTileTypes.STEW_POT.get());
	}

	public FluidTank getTank() {
		return tank;
	}

	public int process;
	public int processMax;
	public boolean operate = false;
	public short proctype = 0;
	public boolean rsstate = false;
	public SoupInfo current;
	public static final short NOP = 0;
	public static final short BOILING = 1;
	public static final short COOKING = 2;
	public static final short STIRING=3;
	public static final ResourceLocation boilable = new ResourceLocation(Main.MODID, "boilable");
	public static final ResourceLocation cookable = new ResourceLocation(Main.MODID, "cookable");
	@Override
	public void tick() {
		if (!world.isRemote) {
			if (processMax > 0) {
				process++;
				if (process >= processMax) {
					process = 0;
					processMax = 0;
					doWork();
				}
			} else{
				prepareWork();
			}
		}
		this.syncData();
	}

	public boolean canAddFluid() {
		return proctype == 0;
	}

	@Override
	public void readCustomNBT(CompoundNBT nbt, boolean isClient) {
		process = nbt.getInt("process");
		processMax = nbt.getInt("processMax");
		proctype = nbt.getShort("worktype");
		rsstate = nbt.getBoolean("rsstate");
		tank.readFromNBT(nbt);
	}

	@Override
	public void writeCustomNBT(CompoundNBT nbt, boolean isClient) {
		nbt.putInt("process", process);
		nbt.putInt("processMax", processMax);
		nbt.putShort("worktype", proctype);
		nbt.putBoolean("rsstate", rsstate);
		tank.writeToNBT(nbt);
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT cnbt = new CompoundNBT();
		writeCustomNBT(cnbt, true);
		return new SUpdateTileEntityPacket(this.pos, 3, cnbt);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = super.getUpdateTag();
		writeCustomNBT(nbt, true);
		return nbt;
	}

	private void prepareWork() {
		if (operate && proctype == 0) {
			operate = false;
			if (doBoil())
				proctype = 1;
			else if (makeSoup())
				proctype = 2;
		}else if(proctype==1) {
			if (makeSoup())
				proctype = 2;
			else
				proctype = 0;
		}
	}

	private void doWork() {
		if (proctype == 1) {
			finishBoil();
		} else if (proctype == 2)
			finishSoup();
	}

	private boolean doBoil() {
		BoilingRecipe recipe = BoilingRecipe.recipes.get(this.tank.getFluid().getFluid());
		if (recipe == null)
			return false;
		this.processMax = recipe.time;
		this.process = 0;
		return true;
	}

	private void finishBoil() {
		BoilingRecipe recipe = BoilingRecipe.recipes.get(this.tank.getFluid().getFluid());
		if (recipe == null)
			return;
		tank.setFluid(recipe.handle(tank.getFluid()));
	}
	private void clearIntern() {
		for(int i=0;i<9;i++)
			interninv.setStackInSlot(i,ItemStack.EMPTY);
	}
	private void adjustParts(int count) {
		float oparts=tank.getFluidAmount()/250f;
		int parts=(int) (oparts+count);
		current.adjustParts(oparts, parts);
		tank.getFluid().setAmount(parts*250);
	}
	private boolean makeSoup() {
		if (tank.getFluidAmount() < 250)
			return false;// cant boil if under one bowl
		if(current.stacks.size()>27)return false;//too much ingredients
		clearIntern();
		current=SoupFluid.getInfo(this.tank.getFluid());
		
		int oparts=tank.getFluidAmount()/250;
		int parts=oparts-1;
		int itms=0;
		int potc=current.effects.size();
		for (int i = 0; i < 9; i++) {
			ItemStack is=inv.getStackInSlot(i);
			if(!is.isEmpty()) {
				if(is.getItem()==Items.POTION) {
					potc++;
				}else
					itms++;
			}
		}
		if(itms/(float)parts+(current.getDensity()*oparts)/parts>3||potc>3) {//too dense
			return false;
		}
		boolean hasItem=false;
		for (int i = 0; i < 9; i++) {
			ItemStack is=inv.getStackInSlot(i);
			if(!is.isEmpty()) {
				if(is.getItem()==Items.POTION) {
					current.effects.addAll(PotionUtils.getEffectsFromStack(is));
				}else
					ItemHandlerHelper.insertItem(interninv,is,false);
				inv.setStackInSlot(i,is.getContainerItem());
				hasItem=true;
			}
		}
		process = 0;
		adjustParts(-1);
		if (!hasItem) {// just reduce water
			processMax = 100;
			return true;
		}
		List<SmokingRecipe> irs = this.world.getRecipeManager().getRecipesForType(IRecipeType.SMOKING);
		int[] iis=new int[9];
		int imax=9;
		for (int i = 0; i < 9; i++) {
			ItemStack is=interninv.getStackInSlot(i);
			if(is.isEmpty()) {
				imax=i;
				break;
			}
			CookInfo ci=cook(is,irs);
			if(ci!=null) {
				iis[i]=ci.i;
				interninv.setStackInSlot(i,ci.is);
			}
		}
		int tpt=100;
		for(int i=0;i<imax;i++) {
			ItemStack is=interninv.getStackInSlot(i);
			if(!is.isEmpty()) {
				for(DissolveRecipe rs:DissolveRecipe.recipes) {
					if(rs.item.test(is)) {
						iis[i]+=rs.time;
						break;
					}
				}
				boolean added=false;
				for(FloatemStack sc:current.stacks) {
					if(sc.equals(is)) {
						added=true;
						sc.setCount(sc.getCount()+is.getCount()/parts);
					}
				}
				if(!added)
					current.stacks.add(new FloatemStack(is.copy(),is.getCount()/parts));
			}
			tpt=Math.max(tpt,iis[i]);
		}
		processMax=tpt;

		return true;
	}
	private static class CookInfo{
		ItemStack is;
		int i;
		public CookInfo(ItemStack is, int i) {
			this.is = is;
			this.i = i;
		}
		public CookInfo add(CookInfo other) {
			if(other==null)return this;
			this.is=other.is;
			i+=other.i;
			return this;
		}
	}
	private CookInfo cook(ItemStack org,List<SmokingRecipe> recipes) {
		if(org.isEmpty())return null;
		for (SmokingRecipe sr : recipes) {
			if (sr.getIngredients().get(0).test(org)) {
				ItemStack ret=sr.getCraftingResult(null).copy();
				ret.setCount(org.getCount());
				return new CookInfo(ret,sr.getCookTime()).add(cook(ret,recipes));
			}
		}
		return null;
	}
	private void finishSoup() {
		Fluid fs=tank.getFluid().getFluid();
		StewPendingContext ctx=new StewPendingContext(current,fs.getRegistryName());
		for(CookingRecipe cr:CookingRecipe.recipes) {
			if(cr.matches(ctx)) {
				fs=cr.output;
				break;
			}
		}
		FluidStack fss=new FluidStack(fs,tank.getFluidAmount());
		SoupFluid.setInfo(fss,current);
		tank.setFluid(fss);
		proctype=0;
	}

	@Override
	public Container createMenu(int p1, PlayerInventory p2, PlayerEntity p3) {
		return new StewPotContainer(p1, p2, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("container." + Main.MODID + ".stewpot.title");
	}

	@Override
	public void handleMessage(short type, int data) {
		if (type == 0)
			if (this.proctype == 0)
				this.proctype = 2;
		if (type == 1) {
			if (data == 1)
				rsstate = false;
			else if (data == 2)
				rsstate = true;
		}

	}

	IFluidHandler handler = new IFluidHandler() {
		@Override
		public int getTanks() {
			return 1;
		}

		@Override
		public FluidStack getFluidInTank(int t) {
			if (t == 0)
				return tank.getFluid();
			return FluidStack.EMPTY;
		}

		@Override
		public int getTankCapacity(int t) {
			if (t == 0)
				return tank.getCapacity();
			return 0;
		}

		@Override
		public boolean isFluidValid(int t, FluidStack stack) {
			if (t == 0 && canAddFluid())
				return tank.isFluidValid(stack);
			return false;
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			if (canAddFluid())
				return tank.fill(resource, action);
			return 0;
		}

		@Override
		public FluidStack drain(FluidStack resource, FluidAction action) {
			if (canAddFluid())
				return tank.drain(resource, action);
			return FluidStack.EMPTY;
		}

		@Override
		public FluidStack drain(int maxDrain, FluidAction action) {
			if (canAddFluid())
				return tank.drain(maxDrain, action);
			return FluidStack.EMPTY;
		}

	};
	RangedWrapper bowl = new RangedWrapper(inv, 9, 11) {
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (slot == 10)
				return stack;
			return super.insertItem(slot, stack, simulate);
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if (slot == 9)
				return ItemStack.EMPTY;
			return super.extractItem(slot, amount, simulate);
		}
	};
	RangedWrapper ingredient = new RangedWrapper(inv, 0, 9) {

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			return ItemStack.EMPTY;
		}
	};
	LazyOptional<IItemHandler> up = LazyOptional.of(() -> ingredient);
	LazyOptional<IItemHandler> side = LazyOptional.of(() -> bowl);
	LazyOptional<IFluidHandler> fl = LazyOptional.of(() -> handler);

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (side == Direction.UP)
				return up.cast();
			return this.side.cast();
		}
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return fl.cast();
		return super.getCapability(cap, side);
	}
}

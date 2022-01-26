package com.teammoeg.thermopolium.blocks;

import java.util.ArrayList;
import java.util.List;

import com.teammoeg.thermopolium.Contents.SCTileTypes;
import com.teammoeg.thermopolium.Main;
import com.teammoeg.thermopolium.container.StewPotContainer;
import com.teammoeg.thermopolium.data.recipes.BoilingRecipe;
import com.teammoeg.thermopolium.data.recipes.BowlContainingRecipe;
import com.teammoeg.thermopolium.data.recipes.CookingRecipe;
import com.teammoeg.thermopolium.data.recipes.DissolveRecipe;
import com.teammoeg.thermopolium.data.recipes.StewPendingContext;
import com.teammoeg.thermopolium.fluid.SoupFluid;
import com.teammoeg.thermopolium.items.StewItem;
import com.teammoeg.thermopolium.network.INetworkTile;
import com.teammoeg.thermopolium.util.FloatemStack;
import com.teammoeg.thermopolium.util.SoupInfo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.SmokingRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
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
				return stack.getItem()==Items.POTION||CookingRecipe.isCookable(stack);
			if (slot == 9) {
				Item i=stack.getItem();
				return  i==Items.BOWL||i instanceof StewItem||stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent();
			}
			return false;
		}

		@Override
		public int getSlotLimit(int slot) {
			if (slot < 9)
				return 1;
			return super.getSlotLimit(slot);
		}
	};
	private NonNullList<ItemStack> interninv=NonNullList.withSize(9,ItemStack.EMPTY);
	public ItemStackHandler getInv() {
		return inv;
	}

	private FluidTank tank = new FluidTank(1250,CookingRecipe::isBoilable);
	

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
				if(canAddFluid())
					tryContianFluid();
			}
		}
		this.syncData();
	}
	private void tryContianFluid() {
		ItemStack is=inv.getStackInSlot(9);
		if(!is.isEmpty()&&inv.getStackInSlot(10).isEmpty()) {
			if(is.getItem()==Items.BOWL&&tank.getFluidAmount()>=250) {
				BowlContainingRecipe recipe=BowlContainingRecipe.recipes.get(this.tank.getFluid().getFluid());
				if(recipe!=null){
					is.shrink(1);
					inv.setStackInSlot(10,recipe.handle(tank.drain(250,FluidAction.EXECUTE)));
					return;
				}
			}
			FluidActionResult far=FluidUtil.tryFillContainer(is,this.tank,1250,null,true);
			if(far.isSuccess()) {
				is.shrink(1);
				if(far.getResult()!=null) 
					inv.setStackInSlot(10,far.getResult());
			}
		}
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
		inv.deserializeNBT(nbt.getCompound("inv"));
		tank.readFromNBT(nbt);
		if(!isClient) {
			if(nbt.contains("current")) {
				current=new SoupInfo(nbt.getCompound("current"));
				
		        ListNBT tagList = nbt.getList("Items", Constants.NBT.TAG_COMPOUND);
		        for (int i = 0; i < tagList.size(); i++)
		        {
		            CompoundNBT itemTags = tagList.getCompound(i);
		            int slot = itemTags.getInt("Slot");
		            if (slot >= 0 && slot < interninv.size())
		            {
		            	interninv.set(slot, ItemStack.read(itemTags));
		            }
		        }
			}
		}
	}

	@Override
	public void writeCustomNBT(CompoundNBT nbt, boolean isClient) {
		nbt.putInt("process", process);
		nbt.putInt("processMax", processMax);
		nbt.putShort("worktype", proctype);
		nbt.putBoolean("rsstate", rsstate);
		nbt.put("inv",inv.serializeNBT());
		tank.writeToNBT(nbt);
		if(!isClient) {
			if(current!=null)
				nbt.put("current",current.save());
	        ListNBT nbtTagList = new ListNBT();
	        for (int i = 0; i < interninv.size(); i++)
	        {
	            if (!interninv.get(i).isEmpty())
	            {
	                CompoundNBT itemTag = new CompoundNBT();
	                itemTag.putInt("Slot", i);
	                interninv.get(i).write(itemTag);
	                nbtTagList.add(itemTag);
	            }
	        }
	        nbt.put("Items", nbtTagList);
		}
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
			boolean hasItem=false;
			for (int i = 0; i < 9; i++) {
				ItemStack is=inv.getStackInSlot(i);
				if(!is.isEmpty()) {
					hasItem=true;
					break;
				}
			}
			if(!hasItem)proctype=0;
		} else if (proctype == 2||proctype==3)
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
		interninv.clear();
	}
	private void adjustParts(int count) {
		float oparts=tank.getFluidAmount()/250f;
		int parts=(int) (oparts+count);
		current.adjustParts(oparts, parts);
		tank.getFluid().setAmount(parts*250);
	}
	private boolean makeSoup() {
		if (tank.getFluidAmount() <= 250)
			return false;// cant boil if under one bowl

		
		clearIntern();
		current=SoupFluid.getInfo(this.tank.getFluid());
		if(current.stacks.size()>27)return false;//too much ingredients
		int oparts=tank.getFluidAmount()/250;
		int parts=oparts-1;
		int itms=0;
		List<EffectInstance> cr=new ArrayList<>(current.effects);
		for (int i = 0; i < 9; i++) {
			ItemStack is=inv.getStackInSlot(i);
			if(!is.isEmpty()) {
				
				if(is.getItem()==Items.POTION) {
					outer:for(EffectInstance n:PotionUtils.getEffectsFromStack(is)) {
						for(EffectInstance eff:cr) {
							if(SoupInfo.isEffectEquals(eff,n))
								continue outer;
						}
						cr.add(n);
					}
				}else if(CookingRecipe.isCookable(is))
					itms++;
				else return false;
			}
		}
		if(itms/(float)parts+(current.getDensity()*oparts)/parts>3||cr.size()>3) {//too dense
			return false;
		}
		
		process = 0;
		adjustParts(-1);
		boolean hasItem=false;
		for (int i = 0; i < 9; i++) {
			ItemStack is=inv.getStackInSlot(i);
			if(!is.isEmpty()) {
				if(is.getItem()==Items.POTION) {
					for(EffectInstance eff:PotionUtils.getEffectsFromStack(is))
						current.addEffect(eff,parts);
					inv.setStackInSlot(i,new ItemStack(Items.GLASS_BOTTLE));
				}else {
					for(int j=0;j<9;j++) {
						ItemStack ois=interninv.get(j);
						if(ois.isEmpty()) {
							interninv.set(j,is);
							break;
						}else if(ois.isItemEqual(is)&&ItemStack.areItemStackTagsEqual(ois,is)) {
							ois.setCount(ois.getCount()+is.getCount());
						}
					}
					inv.setStackInSlot(i,is.getContainerItem());
				}
				hasItem=true;
			}
		}
		if (!hasItem) {// just reduce water
			processMax = 100;
			return true;
		}
		List<SmokingRecipe> irs = this.world.getRecipeManager().getRecipesForType(IRecipeType.SMOKING);
		int[] iis=new int[9];
		int imax=9;
		outer:for (int i = 0; i < 9; i++) {
			ItemStack is=interninv.get(i);
			if(is.isEmpty()) {
				imax=i;
				break;
			}
			for(DissolveRecipe rs:DissolveRecipe.recipes) {
				if(rs.item.test(is)) {
					iis[i]=rs.time;
					continue outer;
				}
			}
			CookInfo ci=cook(is,irs);
			if(ci!=null) {
				iis[i]=ci.i;
				interninv.set(i,ci.is);
			}
		}
		int tpt=100;
		for(int i=0;i<imax;i++) {
			ItemStack is=interninv.get(i);
			if(!is.isEmpty()) {
				for(DissolveRecipe rs:DissolveRecipe.recipes) {
					if(rs.item.test(is)) {
						iis[i]+=rs.time;
						break;
					}
				}
				current.addItem(is,parts);
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
		for(CookingRecipe cr:CookingRecipe.recipes.values()) {
			int mt=cr.matches(ctx);
			if(mt!=0) {
				if(mt==2)
					current.base=fs.getRegistryName();
				fs=cr.output;
				break;
			}
		}
		FluidStack fss=new FluidStack(fs,tank.getFluidAmount());
		current.recalculateHAS();
		SoupFluid.setInfo(fss,current);
		tank.setFluid(fss);
		proctype=0;
	}
	public boolean canAddFluid(FluidStack fs) {
		if(tank.fill(fs,FluidAction.SIMULATE)==fs.getAmount()) {
			return true;
		}
		return false;
	}
	public boolean tryAddFluid(FluidStack fs) {
		int tryfill=tank.fill(fs,FluidAction.SIMULATE);
		if(tryfill>0) {
			if(tryfill==fs.getAmount()) {
				tank.fill(fs,FluidAction.EXECUTE);
				return true;
			}
			return false;
		}
		if(tank.getCapacity()-tank.getFluidAmount()<fs.getAmount())return false;
		current=SoupFluid.getInfo(tank.getFluid());
		
		if(current.merge(SoupFluid.getInfo(fs),tank.getFluidAmount()/250f,fs.getAmount()/250f)) {
			tank.getFluid().setAmount(tank.getFluidAmount()+fs.getAmount());
			this.proctype=3;
			this.process=0;
			this.processMax=100;
			return true;
		}
		return false;
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
				this.operate=true;
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

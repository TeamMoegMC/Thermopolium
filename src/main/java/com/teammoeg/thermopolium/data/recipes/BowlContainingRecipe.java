package com.teammoeg.thermopolium.data.recipes;

import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.teammoeg.thermopolium.data.IDataRecipe;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

public class BowlContainingRecipe extends IDataRecipe {
	public static Map<Fluid,BowlContainingRecipe> recipes;
	public static IRecipeType<?> TYPE;
	public static RegistryObject<IRecipeSerializer<?>> SERIALIZER;
	@Override
	public IRecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}
	@Override
	public IRecipeType<?> getType() {
		return TYPE;
	}
	public Item bowl;
	public Fluid fluid;
	public BowlContainingRecipe(ResourceLocation id,JsonObject jo) {
		super(id);
		bowl=ForgeRegistries.ITEMS.getValue(new ResourceLocation(jo.get("item").getAsString()));
		fluid=ForgeRegistries.FLUIDS.getValue(new ResourceLocation(jo.get("fluid").getAsString()));
		if(bowl==null||fluid==null)
			throw new JsonSyntaxException("Fluid or item not found: fluid: "+(fluid==null)+", item: "+(bowl==null));
	}
	public BowlContainingRecipe(ResourceLocation id,PacketBuffer pb) {
		super(id);
		bowl=ForgeRegistries.ITEMS.getValue(pb.readResourceLocation());
		fluid=ForgeRegistries.FLUIDS.getValue(pb.readResourceLocation());
	}
	public BowlContainingRecipe(ResourceLocation id, Item bowl, Fluid fluid) {
		super(id);
		this.bowl = bowl;
		this.fluid = fluid;
	}
	public void write(PacketBuffer pack) {
		pack.writeResourceLocation(bowl.getRegistryName());
		pack.writeResourceLocation(fluid.getRegistryName());
	}
	public void serialize(JsonObject jo) {
		jo.addProperty("item",bowl.getRegistryName().toString());
		jo.addProperty("fluid",fluid.getRegistryName().toString());
	}
	public ItemStack handle(Fluid f) {
		ItemStack is=new ItemStack(bowl);
		is.getOrCreateTag().putString("type",f.getRegistryName().toString());
		return is;
	}
	public ItemStack handle(FluidStack stack) {
		ItemStack is=new ItemStack(bowl);
		if(stack.hasTag())
			is.setTag(stack.getTag());
		is.getOrCreateTag().putString("type",stack.getFluid().getRegistryName().toString());
		return is;
	}
	public static FluidStack extractFluid(ItemStack item) {
		if(item.hasTag()) {
			CompoundNBT tag=item.getTag();
			if(tag.contains("type")) {
				Fluid f=ForgeRegistries.FLUIDS.getValue(new ResourceLocation(tag.getString("type")));
				if(f!=null) {
					FluidStack res=new FluidStack(f,250);
					CompoundNBT ntag=tag.copy();
					ntag.remove("type");
					if(!ntag.isEmpty())
						res.setTag(ntag);
					return res;
				}
			}
		}
		return FluidStack.EMPTY;
	}

}

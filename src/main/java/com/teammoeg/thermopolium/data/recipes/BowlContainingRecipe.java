package com.teammoeg.thermopolium.data.recipes;

import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.teammoeg.thermopolium.data.IDataRecipe;

import net.minecraft.data.IFinishedRecipe;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
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
		return is;
	}
	public ItemStack handle(FluidStack stack) {
		ItemStack is=new ItemStack(bowl);
		if(stack.hasTag())
			is.setTag(stack.getTag());
		return is;
	}

}

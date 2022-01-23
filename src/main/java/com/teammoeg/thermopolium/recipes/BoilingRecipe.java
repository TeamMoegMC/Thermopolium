package com.teammoeg.thermopolium.recipes;

import java.util.Map;

import com.google.gson.JsonObject;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

public class BoilingRecipe extends IDataRecipe {
	public static Map<Fluid,BoilingRecipe> recipes;
	public static IRecipeType<?> TYPE;
	public static RegistryObject<IRecipeSerializer<?>> SERIALIZER;
	Fluid before;
	Fluid after;
	int time;
	@Override
	public IRecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}
	@Override
	public IRecipeType<?> getType() {
		return TYPE;
	}
	public BoilingRecipe(ResourceLocation id,JsonObject jo) {
		super(id);
		before=ForgeRegistries.FLUIDS.getValue(new ResourceLocation(jo.get("from").getAsString()));
		after=ForgeRegistries.FLUIDS.getValue(new ResourceLocation(jo.get("to").getAsString()));
		time=jo.get("time").getAsInt();
	}
	public BoilingRecipe(ResourceLocation id,PacketBuffer data) {
		super(id);
		before=ForgeRegistries.FLUIDS.getValue(data.readResourceLocation());
		after=ForgeRegistries.FLUIDS.getValue(data.readResourceLocation());
		time=data.readVarInt();
	}
	public BoilingRecipe(ResourceLocation id, Fluid before, Fluid after, int time) {
		super(id);
		this.before = before;
		this.after = after;
		this.time = time;
	}
	public void write(PacketBuffer data) {
		data.writeResourceLocation(before.getRegistryName());
		data.writeResourceLocation(after.getRegistryName());
		data.writeVarInt(time);
	}
	@Override
	public void serialize(JsonObject json) {
		json.addProperty("from",before.getRegistryName().toString());
		json.addProperty("to",after.getRegistryName().toString());
		json.addProperty("time",time);
	}
}

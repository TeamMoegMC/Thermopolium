package com.teammoeg.thermopolium.data.recipes;

import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.teammoeg.thermopolium.data.IDataRecipe;

import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

public class DissolveRecipe extends IDataRecipe{
	public static List<DissolveRecipe> recipes;
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
	public Ingredient item;
	public int time;

	public DissolveRecipe(ResourceLocation id, Ingredient item, int time) {
		super(id);
		this.item = item;
		this.time = time;
	}
	public DissolveRecipe(ResourceLocation id,JsonObject jo) {
		super(id);
		item=Ingredient.deserialize(jo.get("item"));
		time=jo.get("time").getAsInt();
	}
	public DissolveRecipe(ResourceLocation id,PacketBuffer data) {
		super(id);
		item=Ingredient.read(data);
		time=data.readVarInt();
	}
	public void write(PacketBuffer data) {
		item.write(data);
		data.writeVarInt(time);
	}
	@Override
	public void serialize(JsonObject json) {
		json.add("item",item.serialize());
	}

}

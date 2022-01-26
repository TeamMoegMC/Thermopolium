package com.teammoeg.thermopolium.data.recipes;

import java.util.Map;

import com.google.gson.JsonObject;
import com.teammoeg.thermopolium.data.IDataRecipe;

import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

public class FoodValueRecipe extends IDataRecipe {
	public static Map<Item,FoodValueRecipe> recipes;
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
	public int heal;
	public float sat;
	public Item type;
	public FoodValueRecipe(ResourceLocation id, int heal, float sat, Item type) {
		super(id);
		this.heal = heal;
		this.sat = sat;
		this.type = type;
	}
	public FoodValueRecipe(ResourceLocation id,JsonObject jo) {
		super(id);
		heal=jo.get("heal").getAsInt();
		sat=jo.get("sat").getAsFloat();
	}
	public FoodValueRecipe(ResourceLocation id,PacketBuffer data) {
		super(id);
		heal=data.readVarInt();
		sat=data.readFloat();
	}
	public void write(PacketBuffer data) {
		data.writeVarInt(heal);
		data.writeFloat(sat);
	}
	@Override
	public void serialize(JsonObject json) {
		json.addProperty("heal",heal);
		json.addProperty("sat",sat);
	}
}

package com.teammoeg.thermopolium.recipes.numbers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teammoeg.thermopolium.recipes.StewNumber;
import com.teammoeg.thermopolium.recipes.StewPendingContext;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class ItemIngredient implements StewNumber {
	Ingredient i;
	public ItemIngredient(JsonElement jo) {
		i=Ingredient.deserialize(jo.getAsJsonObject().get("ingredient"));
	}

	@Override
	public Float apply(StewPendingContext t) {
		return t.getOfItem(i);
	}

	@Override
	public boolean fits(ItemStack stack) {
		return i.test(stack);
	}

	@Override
	public boolean fits(ResourceLocation type) {
		return false;
	}
	@Override
	public JsonElement serialize() {
		JsonObject th=new JsonObject();
		th.add("ingredient",i.serialize());
		return th;
	}

	@Override
	public void write(PacketBuffer buffer) {
		i.write(buffer);
	}

	public ItemIngredient(PacketBuffer buffer) {
		i=Ingredient.read(buffer);
	}

	@Override
	public String getType() {
		return "ingredient";
	}
}

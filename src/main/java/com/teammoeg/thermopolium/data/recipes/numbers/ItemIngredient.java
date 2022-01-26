package com.teammoeg.thermopolium.data.recipes.numbers;

import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teammoeg.thermopolium.data.recipes.StewNumber;
import com.teammoeg.thermopolium.data.recipes.StewPendingContext;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class ItemIngredient implements StewNumber {
	Ingredient i;
	public ItemIngredient(JsonElement jo) {
		i=Ingredient.deserialize(jo.getAsJsonObject().get("ingredient"));
	}

	public ItemIngredient(Ingredient i) {
		super();
		this.i = i;
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

	@Override
	public Stream<StewNumber> getItemRelated() {
		return Stream.of(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((i == null) ? 0 : i.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if(!(obj instanceof ItemIngredient))
			return false;
		ItemIngredient other = (ItemIngredient) obj;
		if (i == null) {
			if (other.i != null)
				return false;
		} else if (!i.equals(other.i))
			return false;
		return true;
	}

	@Override
	public Stream<ResourceLocation> getTags() {
		return Stream.empty();
	}
	
}

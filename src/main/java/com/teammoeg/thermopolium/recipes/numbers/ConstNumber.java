package com.teammoeg.thermopolium.recipes.numbers;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.teammoeg.thermopolium.recipes.StewNumber;
import com.teammoeg.thermopolium.recipes.StewPendingContext;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class ConstNumber implements StewNumber {
	float n;
	public ConstNumber(JsonElement num) {
		if(num.isJsonPrimitive())
			n=num.getAsFloat();
		else
			n=num.getAsJsonObject().get("num").getAsFloat();
	}

	@Override
	public Float apply(StewPendingContext t) {
		return n;
	}

	@Override
	public boolean fits(ItemStack stack) {
		return false;
	}

	@Override
	public boolean fits(ResourceLocation type) {
		return false;
	}

	@Override
	public JsonElement serialize() {
		return new JsonPrimitive(n);
	}
	@Override
	public void write(PacketBuffer buffer) {
		buffer.writeFloat(n);
	}

	public ConstNumber(PacketBuffer buffer) {
		n=buffer.readFloat();
	}

	@Override
	public String getType() {
		return "const";
	}
}

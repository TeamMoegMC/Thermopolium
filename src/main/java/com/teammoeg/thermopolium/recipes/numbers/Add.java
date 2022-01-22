package com.teammoeg.thermopolium.recipes.numbers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teammoeg.thermopolium.recipes.StewNumber;
import com.teammoeg.thermopolium.recipes.StewPendingContext;
import com.teammoeg.thermopolium.recipes.StewSerializer;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class Add implements StewNumber {
	List<StewNumber> nums;
	public Add(JsonElement jo) {
		if(jo.isJsonObject())
			nums=StreamSupport.stream(jo.getAsJsonObject().get("types").getAsJsonArray().spliterator(),false).map(StewSerializer::ofNumber).collect(Collectors.toList());
		else
			nums=StreamSupport.stream(jo.getAsJsonArray().spliterator(),false).map(StewSerializer::ofNumber).collect(Collectors.toList());
	}

	@Override
	public Float apply(StewPendingContext t) {
		return nums.stream().map(t::apply).reduce(0F,Float::sum);
	}

	@Override
	public boolean fits(ItemStack stack) {
		return nums.stream().anyMatch(s->s.fits(stack));
	}

	@Override
	public boolean fits(ResourceLocation type) {
		return nums.stream().anyMatch(s->s.fits(type));
	}

	@Override
	public JsonElement serialize() {
		JsonArray types=new JsonArray();
		nums.stream().map(StewNumber::serialize).forEach(types::add);
		return types;
	}
	@Override
	public void write(PacketBuffer buffer) {
		StewSerializer.writeList(buffer, nums,StewSerializer::write);
	}

	public Add(PacketBuffer buffer) {
		nums=StewSerializer.readList(buffer,StewSerializer::ofNumber);
	}

	@Override
	public String getType() {
		return "add";
	}
}

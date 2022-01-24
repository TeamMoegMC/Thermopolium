package com.teammoeg.thermopolium.data.recipes.numbers;

import java.util.List;
import com.google.gson.JsonElement;
import com.teammoeg.thermopolium.data.recipes.StewNumber;
import com.teammoeg.thermopolium.data.recipes.StewPendingContext;
import com.teammoeg.thermopolium.data.recipes.StewSerializer;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class Add implements StewNumber {
	List<StewNumber> nums;
	public Add(JsonElement jo) {
		if(jo.isJsonObject())
			nums=StewSerializer.parseJsonElmList(jo.getAsJsonObject().get("types").getAsJsonArray(),StewSerializer::ofNumber);
		else if(jo.isJsonArray())
			nums=StewSerializer.parseJsonElmList(jo.getAsJsonArray(),StewSerializer::ofNumber);
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
		return StewSerializer.toJsonList(nums,StewNumber::serialize);
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

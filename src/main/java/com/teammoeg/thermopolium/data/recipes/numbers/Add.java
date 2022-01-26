package com.teammoeg.thermopolium.data.recipes.numbers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
	public Add() {
		this(new ArrayList<>());
	}
	public Add(List<StewNumber> nums) {
		super();
		this.nums = nums;
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
	@Override
	public Stream<StewNumber> getItemRelated() {
		return nums.stream().flatMap(StewNumber::getItemRelated);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nums == null) ? 0 : nums.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if(!(obj instanceof Add))
			return false;
		Add other = (Add) obj;
		if (nums == null) {
			if (other.nums != null)
				return false;
		} else if (!nums.equals(other.nums))
			return false;
		return true;
	}
	@Override
	public Stream<ResourceLocation> getTags() {
		return nums.stream().flatMap(StewNumber::getTags);
	}

}

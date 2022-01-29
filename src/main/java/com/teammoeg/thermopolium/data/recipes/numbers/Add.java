/*
 * Copyright (c) 2022 TeamMoeg
 *
 * This file is part of Thermopolium.
 *
 * Thermopolium is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Thermopolium is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Thermopolium. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.thermopolium.data.recipes.numbers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.teammoeg.thermopolium.data.recipes.ComplexCalculated;
import com.teammoeg.thermopolium.data.recipes.StewNumber;
import com.teammoeg.thermopolium.data.recipes.StewPendingContext;
import com.teammoeg.thermopolium.data.recipes.SerializeUtil;
import com.teammoeg.thermopolium.util.FloatemTagStack;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class Add implements StewNumber, ComplexCalculated {
	List<StewNumber> nums;

	public Add(JsonElement jo) {
		if (jo.isJsonObject())
			nums = SerializeUtil.parseJsonElmList(jo.getAsJsonObject().get("types").getAsJsonArray(),
					SerializeUtil::ofNumber);
		else if (jo.isJsonArray())
			nums = SerializeUtil.parseJsonElmList(jo.getAsJsonArray(), SerializeUtil::ofNumber);
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
		/*
		 * float sum=nums.stream().map(s->{
		 * float rslt=t.compute(s);
		 * System.out.println(rslt);
		 * return rslt;
		 * }).reduce(0F,Float::sum);
		 * System.out.println(sum);
		 * return sum;
		 */
		return nums.stream().map(t::compute).reduce(0F, Float::sum);
	}

	@Override
	public boolean fits(FloatemTagStack stack) {
		return nums.stream().anyMatch(s -> s.fits(stack));
	}

	@Override
	public JsonElement serialize() {
		return SerializeUtil.toJsonList(nums, StewNumber::serialize);
	}

	@Override
	public void write(PacketBuffer buffer) {
		SerializeUtil.writeList(buffer, nums, SerializeUtil::write);
	}

	public Add(PacketBuffer buffer) {
		nums = SerializeUtil.readList(buffer, SerializeUtil::ofNumber);
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
		if (!(obj instanceof Add))
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

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

import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.teammoeg.thermopolium.data.recipes.StewNumber;
import com.teammoeg.thermopolium.data.recipes.StewPendingContext;
import com.teammoeg.thermopolium.util.FloatemTagStack;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class ConstNumber implements StewNumber {
	float n;

	public ConstNumber(JsonElement num) {
		if (num.isJsonPrimitive())
			n = num.getAsFloat();
		else
			n = num.getAsJsonObject().get("num").getAsFloat();
	}

	public ConstNumber(float n) {
		super();
		this.n = n;
	}

	@Override
	public Float apply(StewPendingContext t) {
		return n;
	}

	@Override
	public boolean fits(FloatemTagStack stack) {
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
		n = buffer.readFloat();
	}

	@Override
	public String getType() {
		return "const";
	}

	@Override
	public Stream<StewNumber> getItemRelated() {
		return Stream.empty();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(n);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ConstNumber))
			return false;
		ConstNumber other = (ConstNumber) obj;
		if (Float.floatToIntBits(n) != Float.floatToIntBits(other.n))
			return false;
		return true;
	}

	@Override
	public Stream<ResourceLocation> getTags() {
		return Stream.empty();
	}

}

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

package com.teammoeg.thermopolium.data.recipes.conditions;

import java.util.stream.Stream;

import com.google.gson.JsonObject;
import com.teammoeg.thermopolium.data.recipes.StewCondition;
import com.teammoeg.thermopolium.data.recipes.StewNumber;
import com.teammoeg.thermopolium.data.recipes.StewPendingContext;
import com.teammoeg.thermopolium.data.recipes.SerializeUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public abstract class NumberedStewCondition implements StewCondition {
	protected StewNumber number;

	public NumberedStewCondition(JsonObject obj) {
		this.number = SerializeUtil.ofNumber(obj.get("type"));
	}

	public NumberedStewCondition(StewNumber number) {
		this.number = number;
	}

	@Override
	public boolean test(StewPendingContext t) {
		return test(t, t.compute(number));
	}

	public abstract boolean test(StewPendingContext t, float n);

	@Override
	public void write(PacketBuffer buffer) {
		SerializeUtil.write(number, buffer);
	}

	public NumberedStewCondition(PacketBuffer buffer) {
		number = SerializeUtil.ofNumber(buffer);
	}

	@Override
	public JsonObject serialize() {
		JsonObject jo = new JsonObject();
		jo.addProperty("cond", getType());
		jo.add("type", number.serialize());
		return jo;
	}

	@Override
	public Stream<StewNumber> getAllNumbers() {
		return Stream.of(number);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof NumberedStewCondition))
			return false;
		NumberedStewCondition other = (NumberedStewCondition) obj;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		return true;
	}

	@Override
	public Stream<ResourceLocation> getTags() {
		return number.getTags();
	}
}

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

import com.google.gson.JsonObject;
import com.teammoeg.thermopolium.data.recipes.StewNumber;
import com.teammoeg.thermopolium.data.recipes.StewPendingContext;

import net.minecraft.network.PacketBuffer;

public class Must extends NumberedStewCondition {

	public Must(JsonObject obj) {
		super(obj);
	}

	public Must(StewNumber number) {
		super(number);
	}

	@Override
	public boolean test(StewPendingContext t, float n) {
		return n > 0;
	}

	@Override
	public JsonObject serialize() {
		JsonObject jo = super.serialize();
		return jo;
	}

	public Must(PacketBuffer buffer) {
		super(buffer);
	}

	@Override
	public String getType() {
		return "contains";
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Must))
			return false;
		if (!super.equals(obj))
			return false;
		return true;
	}
}

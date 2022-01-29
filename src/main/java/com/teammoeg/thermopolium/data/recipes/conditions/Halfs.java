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

public class Halfs extends NumberedStewCondition {
	private boolean isItem = true;

	public Halfs(JsonObject obj) {
		super(obj);
		if (obj.has("isItem"))
			isItem = obj.get("isItem").getAsBoolean();
	}

	public Halfs(StewNumber number) {
		super(number);
	}

	public Halfs(StewNumber number, boolean isItem) {
		super(number);
		this.isItem = isItem;
	}

	@Override
	public boolean test(StewPendingContext t, float n) {
		if (isItem)
			return n > t.getTotalItems() / 2;
		return n > t.getTotalTypes() / 2;
	}

	@Override
	public JsonObject serialize() {
		JsonObject jo = super.serialize();
		if (!isItem)
			jo.addProperty("isItem", isItem);
		return jo;
	}

	@Override
	public void write(PacketBuffer buffer) {
		super.write(buffer);
		buffer.writeBoolean(isItem);
	}

	public Halfs(PacketBuffer buffer) {
		super(buffer);
		isItem = buffer.readBoolean();
	}

	@Override
	public String getType() {
		return "half";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (isItem ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Halfs))
			return false;
		if (!super.equals(obj))
			return false;
		Halfs other = (Halfs) obj;
		if (isItem != other.isItem)
			return false;
		return true;
	}
}

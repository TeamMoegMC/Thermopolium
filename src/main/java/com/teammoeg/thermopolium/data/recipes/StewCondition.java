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

package com.teammoeg.thermopolium.data.recipes;

import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.gson.JsonObject;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public interface StewCondition extends Predicate<StewPendingContext> {
	public JsonObject serialize();

	public void write(PacketBuffer buffer);

	public String getType();

	public default Stream<StewNumber> getAllNumbers() {
		return Stream.empty();
	};

	public default Stream<ResourceLocation> getTags() {
		return Stream.empty();
	};
}

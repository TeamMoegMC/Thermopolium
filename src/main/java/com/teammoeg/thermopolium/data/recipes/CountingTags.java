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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.teammoeg.thermopolium.data.IDataRecipe;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

public class CountingTags extends IDataRecipe {
	public static Set<ResourceLocation> tags;
	public static IRecipeType<?> TYPE;
	public static RegistryObject<IRecipeSerializer<?>> SERIALIZER;
	public List<ResourceLocation> tag;

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public IRecipeType<?> getType() {
		return TYPE;
	}

	public CountingTags(ResourceLocation id) {
		super(id);
		tag = new ArrayList<>();
	}

	public CountingTags(ResourceLocation id, JsonObject jo) {
		super(id);
		if (jo.has("tag"))
			tag = ImmutableList.of(new ResourceLocation(jo.get("tag").getAsString()));
		else if (jo.has("tags"))
			tag = SerializeUtil.parseJsonElmList(jo.get("tags"), e -> new ResourceLocation(e.getAsString()));
	}

	public CountingTags(ResourceLocation id, PacketBuffer data) {
		super(id);
		tag = SerializeUtil.readList(data, PacketBuffer::readResourceLocation);
	}

	public void write(PacketBuffer data) {
		SerializeUtil.<ResourceLocation>writeList2(data, tag, PacketBuffer::writeResourceLocation);
	}

	@Override
	public void serializeRecipeData(JsonObject json) {
		json.add("tags", SerializeUtil.toJsonList(tag, e -> new JsonPrimitive(e.toString())));
	}

}

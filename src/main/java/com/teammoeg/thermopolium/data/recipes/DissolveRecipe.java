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

import java.util.List;
import com.google.gson.JsonObject;
import com.teammoeg.thermopolium.data.IDataRecipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

public class DissolveRecipe extends IDataRecipe {
	public static List<DissolveRecipe> recipes;
	public static IRecipeType<?> TYPE;
	public static RegistryObject<IRecipeSerializer<?>> SERIALIZER;

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public IRecipeType<?> getType() {
		return TYPE;
	}

	public Ingredient item;
	public int time;

	public DissolveRecipe(ResourceLocation id, Ingredient item, int time) {
		super(id);
		this.item = item;
		this.time = time;
	}

	public DissolveRecipe(ResourceLocation id, JsonObject jo) {
		super(id);
		item = Ingredient.deserialize(jo.get("item"));
		time = jo.get("time").getAsInt();
	}

	public boolean test(ItemStack is) {
		return item.test(is);
	}

	public DissolveRecipe(ResourceLocation id, PacketBuffer data) {
		super(id);
		item = Ingredient.read(data);
		time = data.readVarInt();
	}

	public void write(PacketBuffer data) {
		item.write(data);
		data.writeVarInt(time);
	}

	@Override
	public void serialize(JsonObject json) {
		json.add("item", item.serialize());
		json.addProperty("time", time);
	}

}

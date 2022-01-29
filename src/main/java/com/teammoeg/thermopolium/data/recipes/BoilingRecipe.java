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

import java.util.Map;

import com.google.gson.JsonObject;
import com.teammoeg.thermopolium.data.IDataRecipe;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

public class BoilingRecipe extends IDataRecipe {
	public static Map<Fluid, BoilingRecipe> recipes;
	public static IRecipeType<?> TYPE;
	public static RegistryObject<IRecipeSerializer<?>> SERIALIZER;
	public Fluid before;
	public Fluid after;
	public int time;

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public IRecipeType<?> getType() {
		return TYPE;
	}

	public BoilingRecipe(ResourceLocation id, JsonObject jo) {
		super(id);
		before = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(jo.get("from").getAsString()));
		after = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(jo.get("to").getAsString()));
		time = jo.get("time").getAsInt();
	}

	public BoilingRecipe(ResourceLocation id, PacketBuffer data) {
		super(id);
		before = data.readRegistryId();
		after = data.readRegistryId();
		time = data.readVarInt();
	}

	public BoilingRecipe(ResourceLocation id, Fluid before, Fluid after, int time) {
		super(id);
		this.before = before;
		this.after = after;
		this.time = time;
	}

	public void write(PacketBuffer data) {
		data.writeRegistryId(before);
		data.writeRegistryId(after);
		data.writeVarInt(time);
	}

	@Override
	public void serialize(JsonObject json) {
		json.addProperty("from", before.getRegistryName().toString());
		json.addProperty("to", after.getRegistryName().toString());
		json.addProperty("time", time);
	}

	public FluidStack handle(FluidStack org) {
		FluidStack fs = new FluidStack(after, org.getAmount());
		if (org.hasTag())
			fs.setTag(org.getTag());
		return fs;
	}
}

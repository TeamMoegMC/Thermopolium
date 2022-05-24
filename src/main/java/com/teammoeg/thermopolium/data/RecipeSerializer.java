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

package com.teammoeg.thermopolium.data;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.teammoeg.thermopolium.Main;
import com.teammoeg.thermopolium.data.recipes.InvalidRecipeException;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class RecipeSerializer<T extends IDataRecipe>
		extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
	BiFunction<ResourceLocation, JsonObject, T> jsfactory;
	BiFunction<ResourceLocation, PacketBuffer, T> pkfactory;
	BiConsumer<T, PacketBuffer> writer;
	static final Logger logger=LogManager.getLogger(Main.MODID+" recipe serialize");
	@Override
	public T fromJson(ResourceLocation recipeId, JsonObject json) {
		try {
			return jsfactory.apply(recipeId, json);
		}catch(InvalidRecipeException e) {
			return null;
		}
	}

	@Override
	public T fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
		return pkfactory.apply(recipeId, buffer);
	}

	@Override
	public void toNetwork(PacketBuffer buffer, T recipe) {
		writer.accept(recipe, buffer);
	}

	public RecipeSerializer(BiFunction<ResourceLocation, JsonObject, T> jsfactory,
			BiFunction<ResourceLocation, PacketBuffer, T> pkfactory, BiConsumer<T, PacketBuffer> writer) {
		this.jsfactory = jsfactory;
		this.pkfactory = pkfactory;
		this.writer = writer;
	}

}

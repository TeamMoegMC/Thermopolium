package com.teammoeg.thermopolium.data;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class RecipeSerializer<T extends IDataRecipe> extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
	BiFunction<ResourceLocation,JsonObject,T> jsfactory;
	BiFunction<ResourceLocation,PacketBuffer,T> pkfactory;
	BiConsumer<T,PacketBuffer> writer;
	@Override
	public T read(ResourceLocation recipeId, JsonObject json) {
		return jsfactory.apply(recipeId, json);
	}

	@Override
	public T read(ResourceLocation recipeId, PacketBuffer buffer) {
		return pkfactory.apply(recipeId,buffer);
	}

	@Override
	public void write(PacketBuffer buffer,T recipe) {
		writer.accept(recipe, buffer);
	}

	public RecipeSerializer(BiFunction<ResourceLocation, JsonObject, T> jsfactory,
			BiFunction<ResourceLocation, PacketBuffer, T> pkfactory, BiConsumer<T, PacketBuffer> writer) {
		this.jsfactory = jsfactory;
		this.pkfactory = pkfactory;
		this.writer = writer;
	}

}

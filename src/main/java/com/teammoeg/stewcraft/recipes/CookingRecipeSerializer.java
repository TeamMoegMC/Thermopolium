package com.teammoeg.stewcraft.recipes;

import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class CookingRecipeSerializer implements IRecipeSerializer<CookingRecipe> {
	ResourceLocation rn;
	@Override
	public IRecipeSerializer<?> setRegistryName(ResourceLocation name) {
		rn=name;
		return this;
	}

	@Override
	public ResourceLocation getRegistryName() {
		return rn;
	}

	@Override
	public Class<IRecipeSerializer<?>> getRegistryType() {
		return null;
	}

	@Override
	public CookingRecipe read(ResourceLocation recipeId, JsonObject json) {
		return null;
	}

	@Override
	public CookingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		return null;
	}

	@Override
	public void write(PacketBuffer buffer, CookingRecipe recipe) {
	}

}

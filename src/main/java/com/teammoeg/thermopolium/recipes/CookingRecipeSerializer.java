package com.teammoeg.thermopolium.recipes;

import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class CookingRecipeSerializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CookingRecipe> {

	@Override
	public CookingRecipe read(ResourceLocation recipeId, JsonObject json) {
		return new CookingRecipe(recipeId,json);
	}

	@Override
	public CookingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		return new CookingRecipe(recipeId,buffer);
	}

	@Override
	public void write(PacketBuffer buffer, CookingRecipe recipe) {
		recipe.write(buffer);
	}

}

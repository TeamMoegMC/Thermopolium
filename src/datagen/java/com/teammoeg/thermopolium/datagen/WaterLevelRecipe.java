package com.teammoeg.thermopolium.datagen;

import com.google.gson.JsonObject;

import gloridifice.watersource.registry.RecipeSerializersRegistry;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public class WaterLevelRecipe implements IFinishedRecipe {
	ResourceLocation rid;
	Ingredient igd;
	int wl,ws;
	public WaterLevelRecipe() {
	}

	public WaterLevelRecipe(ResourceLocation rid, Ingredient igd, int wl, int ws) {
		super();
		this.rid = rid;
		this.igd = igd;
		this.wl = wl;
		this.ws = ws;
	}

	@Override
	public void serialize(JsonObject json) {
		json.add("ingredient",igd.serialize());
		json.addProperty("waterLevel", wl);
		json.addProperty("waterSaturationLevel",ws);
	}

	@Override
	public ResourceLocation getID() {
		return rid;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return RecipeSerializersRegistry.WATER_LEVEL_ITEM_RECIPE_SERIALIZER.get();
	}

	@Override
	public JsonObject getAdvancementJson() {
		return null;
	}

	@Override
	public ResourceLocation getAdvancementID() {
		return null;
	}

}

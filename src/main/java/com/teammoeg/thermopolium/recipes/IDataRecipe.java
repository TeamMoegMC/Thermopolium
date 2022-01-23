package com.teammoeg.thermopolium.recipes;

import com.google.gson.JsonObject;

import net.minecraft.data.IFinishedRecipe;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class IDataRecipe implements IRecipe<IInventory>,IFinishedRecipe {
	@Override
	public ResourceLocation getID() {
		return id;
	}
	@Override
	public JsonObject getAdvancementJson() {
		return null;
	}
	@Override
	public ResourceLocation getAdvancementID() {
		return null;
	}

	ResourceLocation id;
	public IDataRecipe(ResourceLocation id) {
		this.id=id;
	}
	@Override
	public boolean matches(IInventory inv, World worldIn) {
		return false;
	}

	@Override
	public ItemStack getCraftingResult(IInventory inv) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canFit(int width, int height) {
		return false;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}
}

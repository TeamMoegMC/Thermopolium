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
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

public class BowlContainingRecipe extends IDataRecipe {
	public static Map<Fluid, BowlContainingRecipe> recipes;
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

	public Item bowl;
	public Fluid fluid;

	public BowlContainingRecipe(ResourceLocation id, JsonObject jo) {
		super(id);
		bowl = ForgeRegistries.ITEMS.getValue(new ResourceLocation(jo.get("item").getAsString()));
		fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(jo.get("fluid").getAsString()));
		if (bowl == null||bowl==Items.AIR || fluid == null||fluid==Fluids.EMPTY)
			throw new InvalidRecipeException();
	}

	public BowlContainingRecipe(ResourceLocation id, PacketBuffer pb) {
		super(id);
		bowl = pb.readRegistryId();
		fluid = pb.readRegistryId();
	}

	public BowlContainingRecipe(ResourceLocation id, Item bowl, Fluid fluid) {
		super(id);
		this.bowl = bowl;
		this.fluid = fluid;
	}

	public void write(PacketBuffer pack) {
		pack.writeRegistryId(bowl);
		pack.writeRegistryId(fluid);
	}

	public void serialize(JsonObject jo) {
		jo.addProperty("item", bowl.getRegistryName().toString());
		jo.addProperty("fluid", fluid.getRegistryName().toString());
	}

	public ItemStack handle(Fluid f) {
		ItemStack is = new ItemStack(bowl);
		is.getOrCreateTag().putString("type", f.getRegistryName().toString());
		return is;
	}

	public ItemStack handle(FluidStack stack) {
		ItemStack is = new ItemStack(bowl);
		if (stack.hasTag())
			is.setTag(stack.getTag());
		is.getOrCreateTag().putString("type", stack.getFluid().getRegistryName().toString());
		return is;
	}

	public static FluidStack extractFluid(ItemStack item) {
		if (item.hasTag()) {
			CompoundNBT tag = item.getTag();
			if (tag.contains("type")) {
				Fluid f = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(tag.getString("type")));
				if (f != null) {
					FluidStack res = new FluidStack(f, 250);
					CompoundNBT ntag = tag.copy();
					ntag.remove("type");
					if (!ntag.isEmpty())
						res.setTag(ntag);
					return res;
				}
			}
		}
		return FluidStack.EMPTY;
	}

}

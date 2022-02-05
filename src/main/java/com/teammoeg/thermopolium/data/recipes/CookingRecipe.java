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
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.google.gson.JsonObject;
import com.teammoeg.thermopolium.Main;
import com.teammoeg.thermopolium.data.IDataRecipe;
import com.teammoeg.thermopolium.fluid.SoupFluid;
import com.teammoeg.thermopolium.util.FloatemTagStack;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

public class CookingRecipe extends IDataRecipe {
	public static Set<StewNumber> cookables;
	public static Map<Fluid, CookingRecipe> recipes;
	public static List<CookingRecipe> sorted;
	public static IRecipeType<?> TYPE;
	public static RegistryObject<IRecipeSerializer<?>> SERIALIZER;
	public static final ResourceLocation cookable = new ResourceLocation(Main.MODID, "cookable");
	public static final ResourceLocation boilable = new ResourceLocation(Main.MODID, "boilable");

	public static boolean isCookable(ItemStack stack) {
		FloatemTagStack s = new FloatemTagStack(stack);
		return stack.getItem().getTags().contains(cookable) || cookables.stream().anyMatch(e -> e.fits(s));
	}

	public static boolean isBoilable(FluidStack f) {
		Fluid fd = f.getFluid();
		return fd instanceof SoupFluid || fd.getTags().contains(boilable) || recipes.keySet().contains(fd);
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public IRecipeType<?> getType() {
		return TYPE;
	}

	List<StewCondition> allow;
	List<StewCondition> deny;
	int priority = 0;
	public int time;
	float density;
	List<StewBaseCondition> base;
	public Fluid output;

	public CookingRecipe(ResourceLocation id) {
		super(id);
	}

	public CookingRecipe(ResourceLocation id, JsonObject data) {
		super(id);
		if (data.has("allow"))
			allow = SerializeUtil.parseJsonList(data.get("allow"), SerializeUtil::ofCondition);
		if (data.has("deny"))
			deny = SerializeUtil.parseJsonList(data.get("deny"), SerializeUtil::ofCondition);
		if (data.has("priority"))
			priority = data.get("priority").getAsInt();
		if (data.has("density"))
			density = data.get("density").getAsFloat();
		time = data.get("time").getAsInt();
		if (data.has("base"))
			base = SerializeUtil.parseJsonList(data.get("base"), SerializeUtil::ofBase);
		output = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(data.get("output").getAsString()));
		if(output==Fluids.EMPTY)
			throw new InvalidRecipeException();
	}

	public CookingRecipe(ResourceLocation id, PacketBuffer data) {
		super(id);
		allow = SerializeUtil.readList(data, SerializeUtil::ofCondition);
		deny = SerializeUtil.readList(data, SerializeUtil::ofCondition);
		priority = data.readVarInt();
		density = data.readFloat();
		time = data.readVarInt();
		base = SerializeUtil.readList(data, SerializeUtil::ofBase);
		output = data.readRegistryId();
	}

	public CookingRecipe(ResourceLocation id, List<StewCondition> allow, List<StewCondition> deny, int priority,
			int time, float density, List<StewBaseCondition> base, Fluid output) {
		super(id);
		this.allow = allow;
		this.deny = deny;
		this.priority = priority;
		this.time = time;
		this.density = density;
		this.base = base;
		this.output = output;
	}

	public void write(PacketBuffer data) {
		SerializeUtil.writeList(data, allow, SerializeUtil::write);
		SerializeUtil.writeList(data, deny, SerializeUtil::write);
		data.writeVarInt(priority);
		data.writeFloat(density);
		data.writeVarInt(time);
		SerializeUtil.writeList(data, base, SerializeUtil::write);
		data.writeRegistryId(output);
	}

	public int matches(StewPendingContext ctx) {
		if (ctx.getTotalItems() < density)
			return 0;
		int matchtype = 0;
		if (base != null) {
			for (StewBaseCondition e : base) {
				matchtype = ctx.compute(e);
				if (matchtype != 0)
					break;
			}
			if (matchtype == 0)
				return 0;
		}
		if (matchtype == 0)
			matchtype = 1;
		if (allow != null)
			if (!allow.stream().allMatch(ctx::compute))
				return 0;
		if (deny != null)
			if (deny.stream().anyMatch(ctx::compute))
				return 0;
		return matchtype;
	}

	@Override
	public void serialize(JsonObject json) {
		if (allow != null && !allow.isEmpty()) {
			json.add("allow", SerializeUtil.toJsonList(allow, StewCondition::serialize));
		}
		if (deny != null && !deny.isEmpty()) {
			json.add("deny", SerializeUtil.toJsonList(deny, StewCondition::serialize));
		}
		if (priority != 0)
			json.addProperty("priority", priority);
		json.addProperty("density", density);
		json.addProperty("time", time);
		if (base != null && !base.isEmpty()) {
			json.add("base", SerializeUtil.toJsonList(base, StewBaseCondition::serialize));
		}
		json.addProperty("output", output.getRegistryName().toString());
	}

	public Stream<StewNumber> getAllNumbers() {
		return Stream.concat(allow == null ? Stream.empty() : allow.stream().flatMap(StewCondition::getAllNumbers),
				deny == null ? Stream.empty() : deny.stream().flatMap(StewCondition::getAllNumbers));
	}

	public Stream<ResourceLocation> getTags() {
		return Stream.concat(allow == null ? Stream.empty() : allow.stream().flatMap(StewCondition::getTags),
				deny == null ? Stream.empty() : deny.stream().flatMap(StewCondition::getTags));
	}

	public int getPriority() {
		return priority;
	}

}

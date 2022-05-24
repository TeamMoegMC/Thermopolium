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
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.teammoeg.thermopolium.data.IDataRecipe;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

public class FluidFoodValueRecipe extends IDataRecipe {
	public static Map<ResourceLocation, FluidFoodValueRecipe> recipes;
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

	public int heal;
	public float sat;
	public List<Pair<EffectInstance, Float>> effects;
	private ItemStack repersent;
	public int parts;
	public ResourceLocation f;


	public FluidFoodValueRecipe(ResourceLocation id, int heal, float sat,ItemStack repersent, int parts, Fluid f) {
		super(id);
		this.heal = heal;
		this.sat = sat;
		this.repersent = repersent;
		this.parts = parts;
		this.f = f.getRegistryName();
	}

	public FluidFoodValueRecipe(ResourceLocation id, JsonObject jo) {
		super(id);
		heal = jo.get("heal").getAsInt();
		sat = jo.get("sat").getAsFloat();
		f=new ResourceLocation(jo.get("fluid").getAsString());
		if(jo.has("parts"))
			parts=jo.get("parts").getAsInt();
		else
			parts=1;
		effects = SerializeUtil.parseJsonList(jo.get("effects"), x -> {
			int amplifier = 0;
			if (x.has("level"))
				amplifier = x.get("level").getAsInt();
			int duration = 0;
			if (x.has("time"))
				duration = x.get("time").getAsInt();
			Effect eff = ForgeRegistries.POTIONS.getValue(new ResourceLocation(x.get("effect").getAsString()));
			if(eff==null)
				return null;
			EffectInstance effect = new EffectInstance(eff, duration, amplifier);
			float f = 1;
			if (x.has("chance"))
				f = x.get("chance").getAsInt();
			return new Pair<>(effect, f);
		});
		if(effects!=null)
			effects.removeIf(e->e==null);
		if (jo.has("item")) {
			ItemStack[] i = Ingredient.fromJson(jo.get("item")).getItems();
			if (i.length > 0)
				repersent = i[0];
		}
	}
	@Override
	public void serializeRecipeData(JsonObject json) {
		json.addProperty("heal", heal);
		json.addProperty("sat", sat);
		json.addProperty("parts",parts);
		json.addProperty("fluid",f.toString());
		if(effects!=null&&!effects.isEmpty())
		json.add("effects",SerializeUtil.toJsonList(effects,x->{
			JsonObject jo=new JsonObject();
			jo.addProperty("level",x.getFirst().getAmplifier());
			jo.addProperty("time",x.getFirst().getDuration());
			jo.addProperty("effect",x.getFirst().getEffect().getRegistryName().toString());
			jo.addProperty("chance",x.getSecond());
			return jo;
		}));
		if(repersent!=null)
			json.add("item",Ingredient.of(repersent).toJson());
				
			
	}

	public FluidFoodValueRecipe(ResourceLocation id, PacketBuffer data) {
		super(id);
		heal = data.readVarInt();
		sat = data.readFloat();
		parts=data.readVarInt();
		f=data.readResourceLocation();
		effects = SerializeUtil.readList(data,
				d -> new Pair<>(EffectInstance.load(d.readNbt()), d.readFloat()));
		repersent = SerializeUtil.readOptional(data, d -> ItemStack.of(d.readNbt())).orElse(null);
	}

	public FluidFoodValueRecipe(ResourceLocation id, int heal, float sat,
			ItemStack repersent, int parts, ResourceLocation f) {
		super(id);
		this.heal = heal;
		this.sat = sat;
		this.repersent = repersent;
		this.parts = parts;
		this.f = f;
	}

	public void write(PacketBuffer data) {
		data.writeVarInt(heal);
		data.writeFloat(sat);
		data.writeVarInt(parts);
		data.writeResourceLocation(f);
		SerializeUtil.writeList2(data, effects, (d, e) -> {
			CompoundNBT nc = new CompoundNBT();
			e.getFirst().save(nc);
			d.writeNbt(nc);
			d.writeFloat(e.getSecond());
		});
		SerializeUtil.writeOptional(data, repersent, (d, e) -> e.writeNbt(d.serializeNBT()));
	}


	public ItemStack getRepersent() {
		return repersent;
	}

	public void setRepersent(ItemStack repersent) {
		if (repersent != null)
			this.repersent = repersent.copy();
		else
			this.repersent = null;
	}
}

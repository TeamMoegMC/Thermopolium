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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.teammoeg.thermopolium.data.IDataRecipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

public class FoodValueRecipe extends IDataRecipe {
	public static Map<Item, FoodValueRecipe> recipes;
	public static IRecipeType<?> TYPE;
	public static RegistryObject<IRecipeSerializer<?>> SERIALIZER;
	public static Set<FoodValueRecipe> recipeset;

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
	public final Map<Item, Integer> processtimes;
	private ItemStack repersent;
	public transient Set<ResourceLocation> tags;

	public FoodValueRecipe(ResourceLocation id, int heal, float sat, ItemStack rps, Item... types) {
		super(id);
		this.heal = heal;
		this.sat = sat;
		processtimes = new LinkedHashMap<>();
		repersent = rps;
		for (Item i : types)
			processtimes.put(i, 0);
	}
	
	public FoodValueRecipe(ResourceLocation id, JsonObject jo) {
		super(id);
		heal = jo.get("heal").getAsInt();
		sat = jo.get("sat").getAsFloat();
		processtimes = SerializeUtil.parseJsonList(jo.get("items"), x -> {
			ResourceLocation rl=new ResourceLocation(x.get("item").getAsString());
			if(ForgeRegistries.ITEMS.containsKey(rl)) {
				Item i = ForgeRegistries.ITEMS.getValue(rl);
				int f = 0;
				if (x.has("time"))
					f = x.get("time").getAsInt();
				if(i==Items.AIR)
					return null;
				return new Pair<Item, Integer>(i, f);
			}
			return null;
		}).stream().filter(Objects::nonNull).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
		if(processtimes.isEmpty())
			throw new InvalidRecipeException();
		effects = SerializeUtil.parseJsonList(jo.get("effects"), x -> {
			ResourceLocation rl=new ResourceLocation(x.get("effect").getAsString());
			if(ForgeRegistries.POTIONS.containsKey(rl)) {
				int amplifier = 0;
				if (x.has("level"))
					amplifier = x.get("level").getAsInt();
				int duration = 0;
				if (x.has("time"))
					duration = x.get("time").getAsInt();
				Effect eff = ForgeRegistries.POTIONS.getValue(rl);
				if(eff==null)
					return null;
				EffectInstance effect = new EffectInstance(eff, duration, amplifier);
				float f = 1;
				if (x.has("chance"))
					f = x.get("chance").getAsInt();
				return new Pair<>(effect, f);
			}return null;
		}).stream().filter(Objects::nonNull).collect(Collectors.toList());
		if(effects!=null)
			effects.removeIf(e->e==null);
		if (jo.has("item")) {
			ItemStack[] i = Ingredient.deserialize(jo.get("item")).getMatchingStacks();
			if (i.length > 0)
				repersent = i[0];
		}
		
	}
	public FoodValueRecipe addEffect(EffectInstance effect, float chance) {
		if(effects==null)
			effects=new ArrayList<>();
		effects.add(Pair.of(effect, chance));
		return this;
	}
	@Override
	public void serialize(JsonObject json) {
		json.addProperty("heal", heal);
		json.addProperty("sat", sat);
		if(processtimes!=null&&!processtimes.isEmpty())
		json.add("items",SerializeUtil.toJsonList(processtimes.entrySet(),e->{
			JsonObject jo=new JsonObject();
			jo.addProperty("item",e.getKey().getRegistryName().toString());
			if(e.getValue()!=0)
				jo.addProperty("time",e.getValue());
		return jo;}));
		if(effects!=null&&!effects.isEmpty())
		json.add("effects",SerializeUtil.toJsonList(effects,x->{
			JsonObject jo=new JsonObject();
			jo.addProperty("level",x.getFirst().getAmplifier());
			jo.addProperty("time",x.getFirst().getDuration());
			jo.addProperty("effect",x.getFirst().getPotion().getRegistryName().toString());
			jo.addProperty("chance",x.getSecond());
			return jo;
		}));
		if(repersent!=null)
		json.add("item",Ingredient.fromStacks(repersent).serialize());
				
			
	}

	public FoodValueRecipe(ResourceLocation id, PacketBuffer data) {
		super(id);
		heal = data.readVarInt();
		sat = data.readFloat();
		processtimes = SerializeUtil.readList(data, d -> new Pair<>(d.<Item>readRegistryId(), d.readVarInt())).stream()
				.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
		effects = SerializeUtil.readList(data,
				d -> new Pair<>(EffectInstance.read(d.readCompoundTag()), d.readFloat()));
		repersent = SerializeUtil.readOptional(data, d -> ItemStack.read(d.readCompoundTag())).orElse(null);
	}

	public void write(PacketBuffer data) {
		data.writeVarInt(heal);
		data.writeFloat(sat);
		SerializeUtil.writeList2(data, processtimes.entrySet(), (d, e) -> {
			d.writeRegistryId(e.getKey());

			d.writeVarInt(e.getValue());
		});
		SerializeUtil.writeList2(data, effects, (d, e) -> {
			CompoundNBT nc = new CompoundNBT();
			e.getFirst().write(nc);
			d.writeCompoundTag(nc);
			d.writeFloat(e.getSecond());
		});
		SerializeUtil.writeOptional(data, repersent, (d, e) -> e.writeCompoundTag(d.serializeNBT()));
	}

	public void clearCache() {
		tags=null;
	}
	public Set<ResourceLocation> getTags() {
		if (tags == null)
			tags = processtimes.keySet().stream().map(Item::getTags).flatMap(Set::stream)
					.filter(CountingTags.tags::contains).collect(Collectors.toSet());
		return tags;
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

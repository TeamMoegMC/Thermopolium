package com.teammoeg.thermopolium.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

public class CookingRecipe extends IDataRecipe {

	public static List<CookingRecipe> recipes;
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
	List<StewCondition> allow;
	List<StewCondition> deny;
	int priority;
	int time;
	float density;
	List<StewBaseCondition> base;
	public CookingRecipe(ResourceLocation id) {
		super(id);
	}
	public CookingRecipe(ResourceLocation id,JsonObject data) {
		super(id);
		if(data.has("allow"))
			allow=StewSerializer.parseJsonList(data.get("allow"),StewSerializer::ofCondition);
		if(data.has("deny"))
			deny=StewSerializer.parseJsonList(data.get("deny"),StewSerializer::ofCondition);
		if(data.has("priority"))
			priority=data.get("priority").getAsInt();
		if(data.has("density"))
			density=data.get("density").getAsFloat();
		time=data.get("time").getAsInt();
		if(data.has("base"))
			base=StewSerializer.parseJsonList(data.get("base"),StewSerializer::ofBase);
	}
	public CookingRecipe(ResourceLocation id,PacketBuffer data) {
		super(id);
		if(data.readBoolean())
			allow=StewSerializer.readList(data,StewSerializer::ofCondition);
		if(data.readBoolean())
			deny=StewSerializer.readList(data,StewSerializer::ofCondition);
		priority=data.readVarInt();
		density=data.readFloat();
		time=data.readVarInt();
		if(data.readBoolean())
			base=StewSerializer.readList(data,StewSerializer::ofBase);
	}
	
	public CookingRecipe(ResourceLocation id, int priority,
			int time, float density, ResourceLocation result) {
		super(id);
		this.allow = new ArrayList<>();
		this.deny = new ArrayList<>();
		this.priority = priority;
		this.time = time;
		this.density = density;
		this.base = new ArrayList<>();
		this.result = result;
	}
	public void write(PacketBuffer data) {
		if(allow!=null) {
			data.writeBoolean(true);
			StewSerializer.writeList(data,allow,StewSerializer::write);
		}else data.writeBoolean(false);
		if(deny!=null) {
			data.writeBoolean(true);
			StewSerializer.writeList(data,deny,StewSerializer::write);
		}else data.writeBoolean(false);
		data.writeVarInt(priority);
		data.writeFloat(density);
		data.writeVarInt(time);
		if(base!=null) {
			data.writeBoolean(true);
			StewSerializer.writeList(data,base,StewSerializer::write);
		}else data.writeBoolean(false);
	}

	ResourceLocation result;
	public boolean matches(StewPendingContext ctx) {
		if(ctx.getTotalItems()<density)
			return false;
		if(base!=null)
			if(base.stream().noneMatch(e->e.test(ctx.getInfo().base)))
				return false;
		if(allow!=null)
			if(!allow.stream().allMatch(e->e.test(ctx)))
				return false;
		if(deny!=null)
			if(deny.stream().anyMatch(e->e.test(ctx)))
				return false;
		return true;
	}
	public ResourceLocation getResult() {
		return result;
	}
	@Override
	public void serialize(JsonObject json) {
		if(allow!=null) {
			json.add("allow",StewSerializer.toJsonList(allow,StewCondition::serialize));
		}
		if(deny!=null) {
			json.add("deny",StewSerializer.toJsonList(deny,StewCondition::serialize));
		}
		json.addProperty("priority",priority);
		json.addProperty("density",density);
		json.addProperty("time",time);
		if(base!=null) {
			json.add("base",StewSerializer.toJsonList(base,StewBaseCondition::serialize));
		}
	}




}

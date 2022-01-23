package com.teammoeg.thermopolium.recipes;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

public class CookingRecipe extends IDataRecipe {

	public static List<CookingRecipe> recipes;
	public static IRecipeType<CookingRecipe> TYPE;
	public static RegistryObject<CookingRecipeSerializer> SERIALIZER;
	List<StewCondition> allow;
	List<StewCondition> deny;
	int priority;
	float density;
	List<StewBaseCondition> base;
	public CookingRecipe(ResourceLocation id) {
		super(id);
	}
	public CookingRecipe(ResourceLocation id,JsonObject data) {
		super(id);
		if(data.has("allow"))
			allow=parseCondition(data.get("allow"));
		if(data.has("deny"))
			deny=parseCondition(data.get("deny"));
		if(data.has("priority"))
			priority=data.get("priority").getAsInt();
		if(data.has("density"))
			density=data.get("density").getAsFloat();
		if(data.has("base"))
			base=parseBase(data.get("base"));
	}
	public CookingRecipe(ResourceLocation id,PacketBuffer data) {
		super(id);
		if(data.readBoolean())
			allow=StewSerializer.readList(data,StewSerializer::ofCondition);
		if(data.readBoolean())
			deny=StewSerializer.readList(data,StewSerializer::ofCondition);
		priority=data.readVarInt();
		density=data.readFloat();
		if(data.readBoolean())
			base=StewSerializer.readList(data,StewSerializer::ofBase);
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
		if(base!=null) {
			data.writeBoolean(true);
			StewSerializer.writeList(data,base,StewSerializer::write);
		}else data.writeBoolean(false);
	}
	private List<StewCondition> parseCondition(JsonElement elm){
		if(elm.isJsonArray())
			return StreamSupport.stream(elm.getAsJsonArray().spliterator(),false)
					.map(JsonElement::getAsJsonObject)
					.map(StewSerializer::ofCondition)
					.collect(Collectors.toList());
		return ImmutableList.of(StewSerializer.ofCondition(elm.getAsJsonObject()));
	}
	private List<StewBaseCondition> parseBase(JsonElement elm){
		if(elm.isJsonArray())
			return StreamSupport.stream(elm.getAsJsonArray().spliterator(),false)
					.map(JsonElement::getAsJsonObject)
					.map(StewSerializer::ofBase)
					.collect(Collectors.toList());
		return ImmutableList.of(StewSerializer.ofBase(elm.getAsJsonObject()));
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
	public IRecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public IRecipeType<?> getType() {
		return TYPE;
	}

}

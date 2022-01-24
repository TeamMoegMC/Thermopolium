package com.teammoeg.thermopolium.data.recipes;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.teammoeg.thermopolium.data.IDataRecipe;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

public class CountingTags extends IDataRecipe {
	public static List<ResourceLocation> tags;
	public static IRecipeType<?> TYPE;
	public static RegistryObject<IRecipeSerializer<?>> SERIALIZER;
	public List<ResourceLocation> tag;
	@Override
	public IRecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}
	@Override
	public IRecipeType<?> getType() {
		return TYPE;
	}
	public CountingTags(ResourceLocation id) {
		super(id);
		tag=new ArrayList<>();
	}
	public CountingTags(ResourceLocation id,JsonObject jo) {
		super(id);
		if(jo.has("tag"))
			tag=ImmutableList.of(new ResourceLocation(jo.get("tag").getAsString()));
		else if(jo.has("tags"))
			tag=StewSerializer.parseJsonElmList(jo.get("tags"),e->new ResourceLocation(e.getAsString()));
	}
	public CountingTags(ResourceLocation id,PacketBuffer data) {
		super(id);
		tag=StewSerializer.readList(data,PacketBuffer::readResourceLocation);
	}
	public void write(PacketBuffer data) {
		StewSerializer.<ResourceLocation>writeList2(data,tag,PacketBuffer::writeResourceLocation);
	}
	@Override
	public void serialize(JsonObject json) {
		json.add("tags",StewSerializer.toJsonList(tag,e->new JsonPrimitive(e.toString())));
	}

}

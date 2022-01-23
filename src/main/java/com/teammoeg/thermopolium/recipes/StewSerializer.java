package com.teammoeg.thermopolium.recipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.teammoeg.thermopolium.recipes.baseconditions.FluidTag;
import com.teammoeg.thermopolium.recipes.baseconditions.FluidType;
import com.teammoeg.thermopolium.recipes.conditions.Halfs;
import com.teammoeg.thermopolium.recipes.conditions.Mainly;
import com.teammoeg.thermopolium.recipes.conditions.Must;
import com.teammoeg.thermopolium.recipes.numbers.Add;
import com.teammoeg.thermopolium.recipes.numbers.ConstNumber;
import com.teammoeg.thermopolium.recipes.numbers.ItemIngredient;
import com.teammoeg.thermopolium.recipes.numbers.ItemTag;
import com.teammoeg.thermopolium.recipes.numbers.ItemType;
import com.teammoeg.thermopolium.recipes.numbers.NopNumber;

import net.minecraft.network.PacketBuffer;

public class StewSerializer {
	public static Map<String,Function<JsonObject,StewCondition>> conditions=new HashMap<>();
	public static Map<String,Function<JsonElement,StewNumber>> numbers=new HashMap<>();
	public static Map<String,Function<JsonObject,StewBaseCondition>> basetypes=new HashMap<>();
	public static Map<String,Function<PacketBuffer,StewCondition>> pconditions=new HashMap<>();
	public static Map<String,Function<PacketBuffer,StewNumber>> pnumbers=new HashMap<>();
	public static Map<String,Function<PacketBuffer,StewBaseCondition>> pbasetypes=new HashMap<>();
	private StewSerializer() {
	}
	static {
		numbers.put("add",Add::new);
		numbers.put("ingredient",ItemIngredient::new);
		numbers.put("item",ItemType::new);
		numbers.put("tag",ItemTag::new);
		numbers.put("nop",NopNumber::of);
		numbers.put("const",ConstNumber::new);
		conditions.put("half",Halfs::new);
		conditions.put("mainly",Mainly::new);
		conditions.put("contains",Must::new);
		basetypes.put("tag",FluidTag::new);
		basetypes.put("fluid",FluidTag::new);
		pnumbers.put("add",Add::new);
		pnumbers.put("ingredient",ItemIngredient::new);
		pnumbers.put("item",ItemType::new);
		pnumbers.put("tag",ItemTag::new);
		pnumbers.put("nop",NopNumber::of);
		pnumbers.put("const",ConstNumber::new);
		pconditions.put("half",Halfs::new);
		pconditions.put("mainly",Mainly::new);
		pconditions.put("contains",Must::new);
		pbasetypes.put("tag",FluidTag::new);
		pbasetypes.put("fluid",FluidTag::new);
	}
	public static StewNumber ofNumber(JsonElement jsonElement) {
		if(jsonElement==null||jsonElement.isJsonNull())
			return NopNumber.INSTANCE;
		if(jsonElement.isJsonPrimitive()) {
			JsonPrimitive jp=jsonElement.getAsJsonPrimitive();
			if(jp.isString())
				return new ItemTag(jp);
			else if(jp.isNumber())
				return new ConstNumber(jp);
		}
		if(jsonElement.isJsonArray())
			return new Add(jsonElement);
		JsonObject jo=jsonElement.getAsJsonObject();
		if(jo.has("type")) {
			Function<JsonElement,StewNumber> factory=numbers.get(jo.get("type").getAsString());
			if(factory==null)return NopNumber.INSTANCE;
			return factory.apply(jo);
		}
		if(jo.has("item"))
			return new ItemType(jo);
		else if(jo.has("ingredient"))
			return new ItemIngredient(jo);
		else if(jo.has("types"))
			return new Add(jo);
		else if(jo.has("tag"))
			return new ItemTag(jo);
		return NopNumber.INSTANCE;
	}
	public static StewCondition ofCondition(JsonObject json) {
		return conditions.get(json.get("cond").getAsString()).apply(json);
	}
	public static StewBaseCondition ofBase(JsonObject jo) {
		if(jo.has("type"))
			return basetypes.get(jo.get("type").getAsString()).apply(jo);
		if(jo.has("tag"))
			return new FluidTag(jo);
		if(jo.has("fluid"))
			return new FluidType(jo);
		return null;
	}
	public static StewNumber ofNumber(PacketBuffer buffer) {
		
		return pnumbers.get(buffer.readString()).apply(buffer);
	}
	public static StewCondition ofCondition(PacketBuffer buffer) {
		return pconditions.get(buffer.readString()).apply(buffer);
	}
	public static StewBaseCondition ofBase(PacketBuffer buffer) {
		return pbasetypes.get(buffer.readString()).apply(buffer);
	}
	public static void write(StewNumber e,PacketBuffer buffer) {
		buffer.writeString(e.getType());
		e.write(buffer);
	}
	public static void write(StewCondition e,PacketBuffer buffer) {
		buffer.writeString(e.getType());
		e.write(buffer);
	}
	public static void write(StewBaseCondition e,PacketBuffer buffer) {
		buffer.writeString(e.getType());
		e.write(buffer);
	}
	public static <T> List<T> readList(PacketBuffer buffer,Function<PacketBuffer,T> func){
		int cnt=buffer.readVarInt();
		List<T> nums=new ArrayList<>(cnt);
		for(int i=0;i<cnt;i++)
			nums.add(func.apply(buffer));
		return nums;
	}
	public static <T> void writeList(PacketBuffer buffer,List<T> nums,BiConsumer<T,PacketBuffer> func){
		buffer.writeVarInt(nums.size());
		nums.forEach(e->func.accept(e,buffer));
	}
	public static <T> void writeList2(PacketBuffer buffer,List<T> nums,BiConsumer<PacketBuffer,T> func){
		buffer.writeVarInt(nums.size());
		nums.forEach(e->func.accept(buffer,e));
	}
	public static <T> List<T> parseJsonList(JsonElement elm,Function<JsonObject,T> mapper){
		if(elm.isJsonArray())
			return StreamSupport.stream(elm.getAsJsonArray().spliterator(),false)
					.map(JsonElement::getAsJsonObject)
					.map(mapper)
					.collect(Collectors.toList());
		return ImmutableList.of(mapper.apply(elm.getAsJsonObject()));
	}
	public static <T> List<T> parseJsonElmList(JsonElement elm,Function<JsonElement,T> mapper){
		if(elm.isJsonArray())
			return StreamSupport.stream(elm.getAsJsonArray().spliterator(),false)
					.map(mapper)
					.collect(Collectors.toList());
		return ImmutableList.of(mapper.apply(elm.getAsJsonObject()));
	}
	public static <T> JsonArray toJsonList(List<T> li,Function<T,JsonElement> mapper){
		JsonArray ja=new JsonArray();
		li.stream().map(mapper).forEach(ja::add);
		return ja;
	}
}

package com.teammoeg.thermopolium.data.recipes.baseconditions;

import com.google.gson.JsonObject;
import com.teammoeg.thermopolium.data.recipes.StewBaseCondition;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class FluidType implements StewBaseCondition{
	ResourceLocation of;
	public FluidType(JsonObject jo) {
		of=new ResourceLocation(jo.get("fluid").getAsString());
	}
	@Override
	public boolean test(ResourceLocation t) {
		return of.equals(t);
	}
	public JsonObject serialize() {
		JsonObject jo=new JsonObject();
		jo.addProperty("fluid",of.toString());
		return jo;
	}
	@Override
	public void write(PacketBuffer buffer) {
		buffer.writeResourceLocation(of);
	}
	public FluidType(PacketBuffer buffer) {
		of=buffer.readResourceLocation();
	}
	@Override
	public String getType() {
		return "fluid";
	}
}

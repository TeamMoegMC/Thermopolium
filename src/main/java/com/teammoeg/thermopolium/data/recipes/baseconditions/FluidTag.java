package com.teammoeg.thermopolium.data.recipes.baseconditions;

import com.google.gson.JsonObject;
import com.teammoeg.thermopolium.data.recipes.StewBaseCondition;

import net.minecraft.fluid.Fluid;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class FluidTag implements StewBaseCondition {
	ResourceLocation tag;
	public FluidTag(JsonObject jo){
		tag=new ResourceLocation(jo.get("tag").getAsString());
	}

	@Override
	public boolean test(ResourceLocation t) {
		Fluid f=ForgeRegistries.FLUIDS.getValue(t);
		if(f==null)
		return false;
		return f.getTags().contains(tag);
	}
	public JsonObject serialize() {
		JsonObject jo=new JsonObject();
		jo.addProperty("tag",tag.toString());
		return jo;
	}

	@Override
	public void write(PacketBuffer buffer) {
		buffer.writeResourceLocation(tag);
	}
	public FluidTag(PacketBuffer buffer) {
		tag=buffer.readResourceLocation();
	}

	@Override
	public String getType() {
		return "tag";
	}
}

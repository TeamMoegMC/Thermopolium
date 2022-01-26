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

	public FluidTag(ResourceLocation tag) {
		super();
		this.tag = tag;
	}
	@Override
	public Integer apply(ResourceLocation t, ResourceLocation u) {
		return test(u)?2:test(t)?1:0;
	}
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof FluidTag))
			return false;
		FluidTag other = (FluidTag) obj;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		return true;
	}

	
}

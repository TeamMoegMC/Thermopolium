package com.teammoeg.thermopolium.data.recipes.baseconditions;

import com.google.gson.JsonObject;
import com.teammoeg.thermopolium.data.recipes.StewBaseCondition;

import net.minecraft.fluid.Fluid;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class FluidTypeType implements StewBaseCondition{
	ResourceLocation of;
	public FluidTypeType(JsonObject jo) {
		of=new ResourceLocation(jo.get("base").getAsString());
	}
	
	public FluidTypeType(ResourceLocation of) {
		super();
		this.of = of;
	}
	public FluidTypeType(Fluid of) {
		super();
		this.of = of.getRegistryName();
	}
	@Override
	public Integer apply(ResourceLocation t, ResourceLocation u) {
		return test(u)?2:0;
	}
	public boolean test(ResourceLocation t) {
		return of.equals(t);
	}
	public JsonObject serialize() {
		JsonObject jo=new JsonObject();
		jo.addProperty("base",of.toString());
		return jo;
	}
	@Override
	public void write(PacketBuffer buffer) {
		buffer.writeResourceLocation(of);
	}
	public FluidTypeType(PacketBuffer buffer) {
		of=buffer.readResourceLocation();
	}
	@Override
	public String getType() {
		return "fluid_type";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((of == null) ? 0 : of.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof FluidTypeType))
			return false;
		FluidTypeType other = (FluidTypeType) obj;
		if (of == null) {
			if (other.of != null)
				return false;
		} else if (!of.equals(other.of))
			return false;
		return true;
	}


}

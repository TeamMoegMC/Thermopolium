package com.teammoeg.thermopolium.data.recipes.conditions;

import java.util.stream.Stream;

import com.google.gson.JsonObject;
import com.teammoeg.thermopolium.data.recipes.StewNumber;
import com.teammoeg.thermopolium.data.recipes.StewPendingContext;
import com.teammoeg.thermopolium.util.FloatemTagStack;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class MainlyOfType extends NumberedStewCondition{
	private final ResourceLocation type;
	public MainlyOfType(JsonObject obj) {
		super(obj);
		type=new ResourceLocation(obj.get("tag").getAsString());
	}

	

	public MainlyOfType(StewNumber obj, ResourceLocation type) {
		super(obj);
		this.type = type;
	}




	@Override
	public boolean test(StewPendingContext t, float n) {
		float thistype=t.getOfType(type);
		if(n<thistype/3)
			return false;
		return FloatemTagStack.calculateTypes(t.getItems().stream().filter(e->e.getTags().contains(type)).filter(e->!number.fits(e.getStack()))).values().stream().allMatch(e->e<n);
	}

	@Override
	public JsonObject serialize() {
		JsonObject jo=super.serialize();
		jo.addProperty("tag",type.toString());
		return jo;
	}

	@Override
	public void write(PacketBuffer buffer) {
		super.write(buffer);
		buffer.writeResourceLocation(type);
	}

	public MainlyOfType(PacketBuffer buffer) {
		super(buffer);
		type=buffer.readResourceLocation();
	}
	@Override
	public String getType() {
		return "mainlyOf";
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if(!(obj instanceof MainlyOfType))
			return false;
		if (!super.equals(obj))
			return false;
		MainlyOfType other = (MainlyOfType) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}



	@Override
	public Stream<ResourceLocation> getTags() {
		return Stream.concat(super.getTags(),Stream.of(type));
	}



}

package com.teammoeg.thermopolium.data.recipes.conditions;

import com.google.gson.JsonObject;
import com.teammoeg.thermopolium.data.recipes.StewPendingContext;

import net.minecraft.network.PacketBuffer;

public class Must extends NumberedStewCondition {

	public Must(JsonObject obj) {
		super(obj);
	}

	@Override
	public boolean test(StewPendingContext t, float n) {
		return n>0;
	}
	@Override
	public JsonObject serialize() {
		JsonObject jo=super.serialize();
		return jo;
	}

	public Must(PacketBuffer buffer) {
		super(buffer);
	}
	@Override
	public String getType() {
		return "contains";
	}
}

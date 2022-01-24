package com.teammoeg.thermopolium.data.recipes.conditions;

import com.google.gson.JsonObject;
import com.teammoeg.thermopolium.data.recipes.StewPendingContext;

import net.minecraft.network.PacketBuffer;

public class Halfs extends NumberedStewCondition{
	private boolean isItem=true;
	public Halfs(JsonObject obj) {
		super(obj);
		if(obj.has("isItem"))
			isItem=obj.get("isItem").getAsBoolean();
	}

	@Override
	public boolean test(StewPendingContext t, float n) {
		if(isItem)
			return n>=t.getTotalItems()/2;
		return n>=t.getTotalTypes()/2;
	}

	@Override
	public JsonObject serialize() {
		JsonObject jo=super.serialize();
		if(!isItem)
			jo.addProperty("isItem",isItem);
		return jo;
	}
	@Override
	public void write(PacketBuffer buffer) {
		super.write(buffer);
		buffer.writeBoolean(isItem);
	}
	public Halfs(PacketBuffer buffer) {
		super(buffer);
		isItem=buffer.readBoolean();
	}

	@Override
	public String getType() {
		return "half";
	}
}

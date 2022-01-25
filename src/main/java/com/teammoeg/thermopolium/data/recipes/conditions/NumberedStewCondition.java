package com.teammoeg.thermopolium.data.recipes.conditions;

import java.util.stream.Stream;

import com.google.gson.JsonObject;
import com.teammoeg.thermopolium.data.recipes.StewCondition;
import com.teammoeg.thermopolium.data.recipes.StewNumber;
import com.teammoeg.thermopolium.data.recipes.StewPendingContext;
import com.teammoeg.thermopolium.data.recipes.StewSerializer;
import net.minecraft.network.PacketBuffer;

public abstract class NumberedStewCondition implements StewCondition {
	private StewNumber number;
	public NumberedStewCondition(JsonObject obj) {
		this.number = StewSerializer.ofNumber(obj.get("type"));
	}
	public NumberedStewCondition(StewNumber number) {
		this.number = number;
	}
	@Override
	public boolean test(StewPendingContext t) {
		return test(t,number.apply(t));
	}
	public abstract boolean test(StewPendingContext t,float n);
	@Override
	public void write(PacketBuffer buffer) {
		number.write(buffer);
	}
	public NumberedStewCondition(PacketBuffer buffer) {
		number=StewSerializer.ofNumber(buffer);
	}
	@Override
	public JsonObject serialize() {
		JsonObject jo=new JsonObject();
		jo.addProperty("cond",getType());
		jo.add("type",number.serialize());
		return jo;
	}
	@Override
	public Stream<StewNumber> getAllNumbers(){
		return Stream.of(number);
	}
}

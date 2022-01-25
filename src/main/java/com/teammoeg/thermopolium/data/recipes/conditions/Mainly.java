package com.teammoeg.thermopolium.data.recipes.conditions;

import com.google.gson.JsonObject;
import com.teammoeg.thermopolium.data.recipes.StewNumber;
import com.teammoeg.thermopolium.data.recipes.StewPendingContext;
import com.teammoeg.thermopolium.util.FloatemStack;

import net.minecraft.network.PacketBuffer;

public class Mainly extends NumberedStewCondition{
	private boolean isItem=true;
	public Mainly(JsonObject obj) {
		super(obj);
		if(obj.has("isItem"))
			isItem=obj.get("isItem").getAsBoolean();
	}

	public Mainly(StewNumber number) {
		super(number);
	}

	public Mainly(StewNumber number, boolean isItem) {
		this(number);
		this.isItem = isItem;
	}

	@Override
	public boolean test(StewPendingContext t, float n) {
		if(isItem)
			if(n>=t.getTotalItems()/3) {
				int eqs=0;
				for(FloatemStack fs:t.getInfo().stacks) {
					float f=fs.getCount();
					if(n<f)
						return false;
					else if(n==f)
						eqs++;
				}
				return eqs<2;
			}
		else
			if(n>=t.getTotalTypes()/3) {
				int eqs=0;
				for(Float f:t.getTypes().values()) {
					if(n<f)
						return false;
					else if(n==f)
						eqs++;
				}
				return eqs<2;
			}
		return false;
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

	public Mainly(PacketBuffer buffer) {
		super(buffer);
		isItem=buffer.readBoolean();
	}
	@Override
	public String getType() {
		return "mainly";
	}
}

package com.teammoeg.thermopolium.data.recipes;

import java.util.function.Predicate;

import com.google.gson.JsonObject;

import net.minecraft.network.PacketBuffer;

public interface StewCondition extends Predicate<StewPendingContext>{
	public JsonObject serialize();
	public void write(PacketBuffer buffer);
	public String getType();
}

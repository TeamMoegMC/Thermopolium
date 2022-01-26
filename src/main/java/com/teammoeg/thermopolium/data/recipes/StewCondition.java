package com.teammoeg.thermopolium.data.recipes;

import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.gson.JsonObject;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public interface StewCondition extends Predicate<StewPendingContext>{
	public JsonObject serialize();
	public void write(PacketBuffer buffer);
	public String getType();
	public default Stream<StewNumber> getAllNumbers(){
		return Stream.empty();
	};
	public default Stream<ResourceLocation> getTags(){
		return Stream.empty();
	};
}

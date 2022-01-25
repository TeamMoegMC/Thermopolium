package com.teammoeg.thermopolium.data.recipes;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import com.google.gson.JsonObject;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public interface StewBaseCondition extends BiFunction<ResourceLocation,ResourceLocation,Integer> {
	public JsonObject serialize();
	public void write(PacketBuffer buffer);
	public String getType();
}

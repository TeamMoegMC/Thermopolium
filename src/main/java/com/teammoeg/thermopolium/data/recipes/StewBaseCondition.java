package com.teammoeg.thermopolium.data.recipes;

import java.util.function.Predicate;

import com.google.gson.JsonObject;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public interface StewBaseCondition extends Predicate<ResourceLocation> {
	public JsonObject serialize();
	public void write(PacketBuffer buffer);
	public String getType();
}

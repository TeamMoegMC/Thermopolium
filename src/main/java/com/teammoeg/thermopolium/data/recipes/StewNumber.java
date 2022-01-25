package com.teammoeg.thermopolium.data.recipes;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import com.google.gson.JsonElement;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public interface StewNumber extends Function<StewPendingContext,Float>{
	public boolean fits(ItemStack stack);
	public boolean fits(ResourceLocation type);
	public JsonElement serialize();
	public void write(PacketBuffer buffer);
	public String getType();
	public Stream<StewNumber> getItemRelated();
	
}

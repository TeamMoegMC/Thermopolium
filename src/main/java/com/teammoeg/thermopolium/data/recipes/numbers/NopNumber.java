package com.teammoeg.thermopolium.data.recipes.numbers;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.teammoeg.thermopolium.data.recipes.StewNumber;
import com.teammoeg.thermopolium.data.recipes.StewPendingContext;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class NopNumber implements StewNumber {
	public static final NopNumber INSTANCE=new NopNumber();
	public static NopNumber of(JsonElement elm) {
		return INSTANCE;
	}
	public NopNumber(JsonElement elm) {
	}

	public NopNumber() {
	}
	@Override
	public Float apply(StewPendingContext t) {
		return 0F;
	}

	@Override
	public boolean fits(ItemStack stack) {
		return false;
	}

	@Override
	public boolean fits(ResourceLocation type) {
		return false;
	}

	@Override
	public JsonElement serialize() {
		return JsonNull.INSTANCE;
	}
	@Override
	public void write(PacketBuffer buffer) {
	}
	public static NopNumber of(PacketBuffer buffer) {
		return INSTANCE;
	}
	@Override
	public String getType() {
		return "nop";
	}

}

package com.teammoeg.thermopolium.recipes.numbers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teammoeg.thermopolium.recipes.StewNumber;
import com.teammoeg.thermopolium.recipes.StewPendingContext;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemType implements StewNumber {
	Item type;
	ResourceLocation loc;
	public ItemType(JsonElement jo) {
		type=ForgeRegistries.ITEMS.getValue(loc=new ResourceLocation(jo.getAsJsonObject().get("item").getAsString()));
	}

	@Override
	public Float apply(StewPendingContext t) {
		if(type==null)return 0F;
		return t.getOfItem(type::equals);
	}

	@Override
	public boolean fits(ItemStack stack) {
		return stack.getItem().equals(type);
	}

	@Override
	public boolean fits(ResourceLocation type) {
		return false;
	}
	@Override
	public JsonElement serialize() {
		JsonObject th=new JsonObject();
		th.addProperty("item",loc.toString());
		return th;
	}

	@Override
	public void write(PacketBuffer buffer) {
		buffer.writeResourceLocation(loc);
	}
	public ItemType(PacketBuffer buffer) {
		type=ForgeRegistries.ITEMS.getValue(buffer.readResourceLocation());
	}

	@Override
	public String getType() {
		return "item";
	}
}

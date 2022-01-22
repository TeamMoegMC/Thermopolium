package com.teammoeg.thermopolium.recipes.numbers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.teammoeg.thermopolium.recipes.StewNumber;
import com.teammoeg.thermopolium.recipes.StewPendingContext;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.ResourceLocation;

public class ItemTag implements StewNumber {
	ResourceLocation tag;
	public ItemTag(JsonElement jo) {
		if(jo.isJsonObject())
			tag=new ResourceLocation(jo.getAsJsonObject().get("tag").getAsString());
		else
			tag=new ResourceLocation(jo.getAsString());
	}

	@Override
	public Float apply(StewPendingContext t) {
		return t.getOfType(tag);
	}

	@Override
	public boolean fits(ItemStack stack) {
		return stack.getItem().getTags().contains(tag);
	}

	@Override
	public boolean fits(ResourceLocation type) {
		if(type.equals(tag))return true;
		ITagCollection<Item> manager=TagCollectionManager.getManager().getItemTags();
		ITag<Item> ttag=manager.get(tag);
		ITag<Item> otag=manager.get(type);
		if(otag==null||ttag==null)return false;
		
		return ttag.getAllElements().containsAll(otag.getAllElements());
	}
	@Override
	public JsonElement serialize() {
		return new JsonPrimitive(tag.toString());
	}

	@Override
	public void write(PacketBuffer buffer) {
		buffer.writeResourceLocation(tag);
	}

	public ItemTag(PacketBuffer buffer) {
		tag=buffer.readResourceLocation();
	}

	@Override
	public String getType() {
		return "tag";
	}
}

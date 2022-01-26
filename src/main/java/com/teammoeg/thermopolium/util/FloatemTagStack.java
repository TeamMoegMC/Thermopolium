package com.teammoeg.thermopolium.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.teammoeg.thermopolium.data.recipes.CountingTags;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class FloatemTagStack {
	List<ResourceLocation> tags;
	ItemStack stack;
	float count;
	public FloatemTagStack(FloatemStack stack) {
		tags=stack.getItem().getTags().stream().filter(CountingTags.tags::contains).collect(Collectors.toList());
		this.stack=stack.stack;
		this.count=stack.count;
	}
	public List<ResourceLocation> getTags() {
		return tags;
	}
	public ItemStack getStack() {
		return stack;
	}
	public float getCount() {
		return count;
	}
	public static Map<ResourceLocation,Float> calculateTypes(Stream<FloatemTagStack> stacks){
		Map<ResourceLocation,Float> map=new HashMap<>();
		stacks.forEach(e->{
			float c=e.count;
			for(ResourceLocation tag:e.tags)
				map.merge(tag,c,Float::sum);
		});
		return map;
	}
}

package com.teammoeg.thermopolium.data.recipes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import com.teammoeg.thermopolium.util.FloatemStack;
import com.teammoeg.thermopolium.util.SoupInfo;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class StewPendingContext {
	private Map<ResourceLocation,Float> types=new HashMap<>();
	private float totalTypes;
	private float totalItems;
	private SoupInfo info;
	private ResourceLocation cur;
	public ResourceLocation getCur() {
		return cur;
	}
	public StewPendingContext(SoupInfo info,ResourceLocation current) {
		this.info = info;
		for(FloatemStack fs:info.stacks) {
			totalItems+=fs.getCount();
			addTags(fs.getItem().getTags(),fs.getCount());
		}
		cur=current;
	}
	public void addTags(Collection<ResourceLocation> tags,float num) {
		for(ResourceLocation rl:tags)
			if(CountingTags.tags.contains(rl)) {
				types.merge(rl,num,Float::sum);
				totalTypes+=num;
			}
		
	}
	public Map<ResourceLocation, Float> getTypes() {
		return types;
	}
	public float getOfType(ResourceLocation rl) {
		return types.getOrDefault(rl,0F);
	}
	public float getOfItem(Predicate<ItemStack> pred) {
		for(FloatemStack fs:info.stacks)
			if(pred.test(fs.getStack()))
				return fs.getCount();
		return 0f;
	}
	public float getTotalTypes() {
		return totalTypes;
	}

	public float getTotalItems() {
		return totalItems;
	}
	public SoupInfo getInfo() {
		return info;
	}
	public float apply(StewNumber num) {
		return num.apply(this);
	}
}

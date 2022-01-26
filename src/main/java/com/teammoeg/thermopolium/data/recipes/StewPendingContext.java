package com.teammoeg.thermopolium.data.recipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import com.teammoeg.thermopolium.util.FloatemStack;
import com.teammoeg.thermopolium.util.FloatemTagStack;
import com.teammoeg.thermopolium.util.SoupInfo;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class StewPendingContext {
	private Map<ResourceLocation,Float> types=new HashMap<>();
	private List<FloatemTagStack> items;
	private float totalTypes;
	private float totalItems;
	private SoupInfo info;
	public Map<StewNumber,Float> cachedNumbers=new HashMap<>();
	public Map<StewCondition,Boolean> cachedResults=new HashMap<>(); 
	ResourceLocation cur;
	public ResourceLocation getCur() {
		return cur;
	}
	public StewPendingContext(SoupInfo info,ResourceLocation current) {
		this.info = info;
		items=new ArrayList<>(info.stacks.size());
		for(FloatemStack fs:info.stacks) {
			FloatemTagStack fst=new FloatemTagStack(fs);
			items.add(fst);
			totalItems+=fs.getCount();
			addTags(fst);
		}
		
		cur=current;
	}
	public float calculateNumber(StewNumber sn) {
		return cachedNumbers.computeIfAbsent(sn,e->e.apply(this));
	}
	public boolean calculateCondition(StewCondition sc) {
		return cachedResults.computeIfAbsent(sc,e->e.test(this));
	}
	public void addTags(FloatemTagStack item) {
		
		for(ResourceLocation rl:item.getTags()) {
			types.merge(rl,item.getCount(),Float::sum);
			totalTypes+=item.getCount();
		}
		
	}
	public float getOfType(ResourceLocation rl) {
		return types.getOrDefault(rl,0f);
	}
	public float getOfItem(Predicate<ItemStack> pred) {
		for(FloatemTagStack fs:items)
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
	public List<FloatemTagStack> getItems() {
		return items;
	}
}

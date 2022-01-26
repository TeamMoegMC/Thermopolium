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
/**
 * For caching data and reduce calculation
 * */
public class StewPendingContext {
	private List<FloatemTagStack> items;
	private float totalTypes;
	private float totalItems;
	private SoupInfo info;
	//cache results to prevent repeat calculation
	private Map<StewNumber,Float> numbers=new HashMap<>();
	private Map<StewCondition,Boolean> results=new HashMap<>(); 
	private Map<StewBaseCondition,Integer> basetypes=new HashMap<>(); 
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
		}
		
		cur=current;
	}
	public float compute(StewNumber sn) {
		return numbers.computeIfAbsent(sn,e->e.apply(this));
	}
	public boolean compute(StewCondition sc) {
		return results.computeIfAbsent(sc,e->e.test(this));
	}
	public int compute(StewBaseCondition sbc) {
		return basetypes.computeIfAbsent(sbc,e->e.apply(info.base,cur));
	}
	public float getOfType(ResourceLocation rl) {
		return (float) items.stream().filter(e->e.getTags().contains(rl)).mapToDouble(FloatemTagStack::getCount).sum();
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

package com.teammoeg.thermopolium.api;

import java.util.List;

import com.teammoeg.thermopolium.items.StewItem;
import com.teammoeg.thermopolium.util.FloatemStack;

import net.minecraft.item.ItemStack;

public class ThermopoliumHooks {

	private ThermopoliumHooks() {
	}
	public static List<FloatemStack> getItems(ItemStack stack){
		return StewItem.getItems(stack);
	}
}

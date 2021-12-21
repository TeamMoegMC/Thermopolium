package com.teammoeg.thermopolium.items;



import com.teammoeg.thermopolium.Contents;
import com.teammoeg.thermopolium.Main;

import net.minecraft.item.Item;

public class StewItem extends Item {

	public StewItem(String name, Properties properties) {
		super(properties);
		setRegistryName(Main.MODID, name);
		Contents.registeredItems.add(this);
	}
}

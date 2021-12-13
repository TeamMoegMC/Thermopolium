package com.teammoeg.stewcraft.items;



import com.teammoeg.stewcraft.Contents;
import com.teammoeg.stewcraft.Main;

import net.minecraft.item.Item;

public class StewItem extends Item {

	public StewItem(String name, Properties properties) {
		super(properties);
		setRegistryName(Main.MODID, name);
		Contents.registeredItems.add(this);
	}
}

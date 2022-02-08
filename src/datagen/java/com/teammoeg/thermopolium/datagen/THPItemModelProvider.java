/*
 * Copyright (c) 2022 TeamMoeg
 *
 * This file is part of Thermopolium.
 *
 * Thermopolium is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Thermopolium is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Thermopolium. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.thermopolium.datagen;

import com.teammoeg.thermopolium.Contents.THPItems;
import com.teammoeg.thermopolium.Main;

import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class THPItemModelProvider extends ItemModelProvider {

	public THPItemModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
		super(generator, modid, existingFileHelper);
	}

	String[] items = new String[] { "acquacotta.png", "bisque.png", "bone_gelatin.png", "borscht.png",
			"borscht_cream.png", "congee.png", "cream_of_meat_soup.png", "cream_of_mushroom_soup.png", "custard.png",
			"dilute_soup.png", "egg_drop_soup.png", "egg_tongsui.png", "fish_chowder.png", "fish_soup.png",
			"fricassee.png", "goji_tongsui.png", "goulash.png", "gruel.png", "hodgepodge.png", "meat_soup.png",
			"mushroom_soup.png", "nail_soup.png", "nettle_soup.png", "okroshka.png", "plain_milk.png",
			"plain_water.png", "porridge.png", "poultry_soup.png", "pumpkin_soup.png", "pumpkin_soup_cream.png",
			"rice_pudding.png", "scalded_milk.png", "seaweed_soup.png", "stock.png", "stracciatella.png", "ukha.png",
			"vegetable_chowder.png", "vegetable_soup.png", "walnut_soup.png" };

	@Override
	protected void registerModels() {
		ResourceLocation par = new ResourceLocation("minecraft", "item/generated");
		for (String s : items) {
			if (!s.isEmpty()) {
				s = s.split("\\.")[0];
				super.singleTexture(s, par, "layer0", new ResourceLocation(Main.MODID, "item/" + s));
			}
		}
		simpleTexture("milk_based");
		simpleTexture("stock_based");
		simpleTexture("any_based");
		simpleTexture("water_or_stock_based");
		simpleTexture("book");
	}
	public void simpleTexture(String name) {
		super.singleTexture(name,new ResourceLocation("minecraft", "item/generated"),"layer0",new ResourceLocation(Main.MODID,"item/"+name));
	}
}

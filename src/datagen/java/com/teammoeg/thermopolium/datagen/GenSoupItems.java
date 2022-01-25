package com.teammoeg.thermopolium.datagen;
import com.teammoeg.thermopolium.Main;

import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class GenSoupItems extends ItemModelProvider {

	public GenSoupItems(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
		super(generator, modid, existingFileHelper);
	}
	String[] items=new String[]{"acquacotta.png",
			"bisque.png",
			"bone_gelatin.png",
			"borscht.png",
			"borscht_cream.png",
			"congee.png",
			"cream_of_meat_soup.png",
			"cream_of_mushroom_soup.png",
			"custard.png",
			"dilute_soup.png",
			"egg_drop_soup.png",
			"egg_tongsui.png",
			"fish_chowder.png",
			"fish_soup.png",
			"fricassee.png",
			"goji_tongsui.png",
			"goulash.png",
			"gruel.png",
			"hodgepodge.png",
			"meat_soup.png",
			"mushroom_soup.png",
			"nail_soup.png",
			"nettle_soup.png",
			"okroshka.png",
			"plain_milk.png",
			"plain_water.png",
			"porridge.png",
			"poultry_soup.png",
			"pumpkin_soup.png",
			"pumpkin_soup_cream.png",
			"rice_pudding.png",
			"scalded_milk.png",
			"seaweed_soup.png",
			"stock.png",
			"stracciatella.png",
			"ukha.png",
			"vegetable_chowder.png",
			"vegetable_soup.png",
			"walnut_soup.png"};
	@Override
	protected void registerModels() {
		ResourceLocation par=new ResourceLocation("minecraft","item/generated");
		for(String s:items) {
			if(!s.isEmpty()) {
				s=s.split("\\.")[0];
				super.singleTexture(s,par,"layer0",new ResourceLocation(Main.MODID,"item/"+s));
			}
		}
	}

}

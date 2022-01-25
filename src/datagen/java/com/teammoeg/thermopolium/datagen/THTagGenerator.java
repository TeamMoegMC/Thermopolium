package com.teammoeg.thermopolium.datagen;

import java.nio.file.Path;

import com.teammoeg.thermopolium.Main;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.TagsProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class THTagGenerator extends TagsProvider<Item> {

	public THTagGenerator(DataGenerator dataGenerator, String modId,
			ExistingFileHelper existingFileHelper) {
		super(dataGenerator,Registry.ITEM, modId, existingFileHelper);
	}

	@Override
	protected void registerTags() {
		/*Builder<Item> i=this.getOrCreateBuilder(ItemTags.createOptional(mrl("cookable"))).add(Items.EGG);
		for(Item it:ForgeRegistries.ITEMS.getValues()) {
			if(it.isFood()) {
				if(it.getRegistryName().getNamespace().equals("minecraft"))
					i.add(it);
				else
					i.addOptional(it.getRegistryName());
			}
		}*/
	}
	private Builder<Item> tag(String s){
		return this.getOrCreateBuilder(ItemTags.createOptional(mrl(s)));
	}
	private ResourceLocation mrl(String s) {
		return new ResourceLocation(Main.MODID, s);
	}
	private ResourceLocation ftag(String s) {
		return new ResourceLocation("forge",s);
	}
	private ResourceLocation mcrl(String s) {
		return new ResourceLocation(s);
	}

	@Override
	public String getName() {
		return Main.MODID+" tags";
	}

	@Override
	protected Path makePath(ResourceLocation id) {
		 return this.generator.getOutputFolder().resolve("data/" + id.getNamespace() + "/tags/items/" + id.getPath() + ".json");
	}
}

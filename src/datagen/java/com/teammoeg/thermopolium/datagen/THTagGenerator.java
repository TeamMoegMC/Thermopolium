package com.teammoeg.thermopolium.datagen;

import java.nio.file.Path;
import com.cannolicatfish.rankine.init.RankineItems;
import com.teammoeg.thermopolium.Main;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.TagsProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.Tags.IOptionalNamedTag;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.RegistryObject;

public class THTagGenerator extends TagsProvider<Item> {

	public THTagGenerator(DataGenerator dataGenerator, String modId,
			ExistingFileHelper existingFileHelper) {
		super(dataGenerator,Registry.ITEM, modId, existingFileHelper);
	}

	@SuppressWarnings("unchecked")
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
		tag("baked").add(Items.BREAD);
		tag("coreals/non_rice").addTag(otag("baked")).add(Items.WHEAT,Items.WHEAT_SEEDS)
		.addOptional(rl(RankineItems.CORN_EAR)).addOptional(rl(RankineItems.CORN_FLOUR)).addOptional(rl(RankineItems.CORN_SEEDS));
		tag("coreals/rice").addOptional(rl(RankineItems.RICE)).addOptional(rl(RankineItems.RICE_FLOUR)).addOptional(rl(RankineItems.RICE_SEEDS));
		tag("roots").add(Items.POTATO,Items.BAKED_POTATO);
		tag("vegetables").add(Items.CARROT,Items.BEETROOT,Items.PUMPKIN).addOptionalTag(frl("vegetables")).addOptionalTag(frl("vegetable"))
		.addTag(otag("roots")).addOptional(rl(RankineItems.ASPARAGUS)).addOptional(rl(RankineItems.ROASTED_ASPARAGUS));
		tag("eggs").add(Items.EGG);
		tag("crustaceans");
		tag("fish").addTag(atag(mcrl("fishes")));
		tag("seafood").add(Items.KELP,Items.DRIED_KELP).addTag(otag("fish")).addOptionalTag(mrl("crustaceans"));
		tag("meats/poultry").add(Items.CHICKEN,Items.COOKED_CHICKEN,Items.RABBIT,Items.COOKED_RABBIT);
		tag("meats/meat").add(Items.BEEF,Items.COOKED_BEEF,Items.MUTTON,Items.COOKED_MUTTON);
		tag("sugar").add(Items.SUGAR_CANE,Items.HONEYCOMB,Items.HONEY_BOTTLE);
		tag("bone").add(Items.BONE,Items.BONE_MEAL);
		tag("ice").add(Items.ICE,Items.BLUE_ICE,Items.PACKED_ICE);
		
	}
	private Builder<Item> tag(String s){
		return this.getOrCreateBuilder(ItemTags.createOptional(mrl(s)));
	}
	private ResourceLocation rl(RegistryObject<Item> it) {
		return it.getId();
	}
	private IOptionalNamedTag<Item> otag(String s) {
		return ItemTags.createOptional(mrl(s));
	}
	private IOptionalNamedTag<Item> atag(ResourceLocation s) {
		return ItemTags.createOptional(s);
	}
	private ResourceLocation mrl(String s) {
		return new ResourceLocation(Main.MODID, s);
	}
	private ResourceLocation frl(String s) {
		return new ResourceLocation("forge",s);
	}
	private IOptionalNamedTag<Item> ftag(String s) {
		return ItemTags.createOptional(new ResourceLocation("forge",s));
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

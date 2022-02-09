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

import java.nio.file.Path;
import com.cannolicatfish.rankine.init.RankineItems;
import com.teammoeg.thermopolium.Contents.THPBlocks;
import com.teammoeg.thermopolium.Contents.THPItems;
import com.teammoeg.thermopolium.Main;
import static com.teammoeg.thermopolium.datagen.THPRecipeProvider.*;

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

public class THPItemTagGenerator extends TagsProvider<Item> {

	public THPItemTagGenerator(DataGenerator dataGenerator, String modId, ExistingFileHelper existingFileHelper) {
		super(dataGenerator, Registry.ITEM, modId, existingFileHelper);
	}

	static final String fd = "farmersdelight";
	static final String sa = "stone_age:";

	@Override
	protected void registerTags() {
		/*
		 * Builder<Item>
		 * i=this.getOrCreateBuilder(ItemTags.createOptional(mrl("cookable"))).add(Items
		 * .EGG);
		 * for(Item it:ForgeRegistries.ITEMS.getValues()) {
		 * if(it.isFood()) {
		 * if(it.getRegistryName().getNamespace().equals("minecraft"))
		 * i.add(it);
		 * else
		 * i.addOptional(it.getRegistryName());
		 * }
		 * }
		 */
		tag(meats).addTag(atag(poultry)).addTag(atag(meat));
		tag(seafood).addTag(atag(fish)).addTag(atag(crustaceans));
		tag(pumpkin).addOptional(rl(fd + ":pumpkin_slice")).add(Items.PUMPKIN, Items.CARVED_PUMPKIN);
		tag(vegetables).addTag(atag(mushrooms)).addTag(atag(roots)).addTag(ftag("salad_ingredients"))
				.addTag(atag(pumpkin));
		tag(frl("raw_beef")).add(Items.BEEF);
		tag(walnut).addOptional(RankineItems.BLACK_WALNUT.getId());
		tag(baked).add(Items.BREAD).addOptional(RankineItems.TOAST.getId()).addTag(ftag("pasta"))
				.addOptional(rl(fd + ":pie_crust"));
		tag(cereals).addTag(atag(rice)).addTag(ftag("grain")).addTag(atag(baked)).add(Items.WHEAT, Items.WHEAT_SEEDS)
				.addOptional(rl(RankineItems.CORN_EAR)).addOptional(rl(RankineItems.CORN_FLOUR))
				.addOptional(rl(RankineItems.CORN_SEEDS));
		tag(rice).addTag(ftag("grain/rice")).addOptional(rl(RankineItems.RICE)).addOptional(rl(RankineItems.RICE_FLOUR))
				.addOptional(rl(RankineItems.RICE_SEEDS));
		tag(roots).add(Items.POTATO, Items.BAKED_POTATO).addTag(ftag("rootvegetables"));
		tag(vegetables).add(Items.CARROT, Items.BEETROOT, Items.PUMPKIN).addTag(atag(mushrooms))
				.addTag(ftag("vegetables")).addTag(ftag("vegetable")).addOptional(rl(RankineItems.ASPARAGUS));
		tag(eggs).add(Items.EGG).addTag(ftag("cooked_eggs"));
		tag(crustaceans);
		tag(fish).addTag(atag(mcrl("fishes"))).addTag(ftag("raw_fishes"));
		tag(seafood).add(Items.KELP, Items.DRIED_KELP);
		tag(poultry).add(Items.CHICKEN, Items.RABBIT).addTag(ftag("raw_chicken")).addTag(ftag("raw_rabbit"))
				.addOptional(rl(sa + "fowl_meat"));
		tag(meat).add(Items.BEEF, Items.MUTTON, Items.PORKCHOP, Items.ROTTEN_FLESH).addTag(ftag("bacon"))
				.addTag(ftag("raw_pork")).addTag(ftag("raw_beef")).addTag(ftag("raw_mutton"))
				.addOptional(rl(fd + ":ham")).addOptional(rl(sa + "venison")).addOptional(rl(sa + "auroch_meat"))
				.addOptional(rl(sa + "mouflon_meat")).addOptional(rl(sa + "boar_meat"))
				.addOptional(rl(sa + "mammoth_meat")).addOptional(rl(sa + "rhino_meat"))
				.addOptional(rl(sa + "tiger_meat"));
		tag(sugar).add(Items.SUGAR_CANE, Items.HONEYCOMB, Items.HONEY_BOTTLE);
		tag("bone").add(Items.BONE);
		tag("ice").add(Items.ICE, Items.BLUE_ICE, Items.PACKED_ICE);
		tag(mushrooms).add(Items.BROWN_MUSHROOM, Items.RED_MUSHROOM);
		tag("fern").add(Items.FERN, Items.LARGE_FERN);
		tag("wolfberries").addOptional(new ResourceLocation("frostedheart:wolfberries"));
		tag("stews").add(THPItems.stews.toArray(new Item[0]));
		tag("stoves").add(THPBlocks.stove1.asItem(),THPBlocks.stove2.asItem());
	}

	private Builder<Item> tag(String s) {
		return this.getOrCreateBuilder(ItemTags.createOptional(mrl(s)));
	}

	private Builder<Item> tag(ResourceLocation s) {
		return this.getOrCreateBuilder(ItemTags.createOptional(s));
	}

	private ResourceLocation rl(RegistryObject<Item> it) {
		return it.getId();
	}

	private ResourceLocation rl(String r) {
		return new ResourceLocation(r);
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
		return new ResourceLocation("forge", s);
	}

	private IOptionalNamedTag<Item> ftag(String s) {
		IOptionalNamedTag<Item> tag = ItemTags.createOptional(new ResourceLocation("forge", s));
		this.getOrCreateBuilder(tag);
		return tag;
	}

	private ResourceLocation mcrl(String s) {
		return new ResourceLocation(s);
	}

	@Override
	public String getName() {
		return Main.MODID + " item tags";
	}

	@Override
	protected Path makePath(ResourceLocation id) {
		return this.generator.getOutputFolder()
				.resolve("data/" + id.getNamespace() + "/tags/items/" + id.getPath() + ".json");
	}
}

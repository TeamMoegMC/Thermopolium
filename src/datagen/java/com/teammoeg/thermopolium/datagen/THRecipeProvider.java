package com.teammoeg.thermopolium.datagen;
/*
 * Copyright (c) 2021 TeamMoeg
 *
 * This file is part of Frosted Heart.
 *
 * Frosted Heart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Frosted Heart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Frosted Heart. If not, see <https://www.gnu.org/licenses/>.
 */

import java.util.HashMap;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import com.cannolicatfish.rankine.init.RankineItems;
import com.teammoeg.thermopolium.Main;
import com.teammoeg.thermopolium.SCFluids;
import com.teammoeg.thermopolium.data.recipes.BoilingRecipe;
import com.teammoeg.thermopolium.data.recipes.BowlContainingRecipe;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;


public class THRecipeProvider extends RecipeProvider {
	private final HashMap<String, Integer> PATH_COUNT = new HashMap<>();
	static final ResourceLocation
	rice=mrl("coreals/rice"),
	nonrice=mrl("coreals/non_rice"),
	eggs=mrl("eggs"),
	baked=mrl("coreals/baked"),
	anyWater=mrl("water"),
	vegetables=mrl("vegetables"),
	meat=mrl("meats/meat"),
	fish=mrl("seafood/fish"),
	poultry=mrl("meats/poultry"),
	seafood=mrl("seafood"),
	meats=mrl("meats"),
	sugar=mrl("sugar"),
	coreals=mrl("coreals"),
	crustaceans=mrl("seafood/crustaceans"),
	roots=mrl("vegetables/roots");
	static final Fluid 
	water=fluid(mrl("nail_soup")),
	milk=fluid(mrl("scalded_milk")),
	stock=fluid(mrl("stock"));
	public THRecipeProvider(DataGenerator generatorIn) {
		super(generatorIn);
	}

	@Override
	protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> out) {
		for (String s : SCFluids.soupfluids.keySet()) {
			ResourceLocation fs=mrl(s);
			out.accept(new BowlContainingRecipe(rl("bowl/"+s),item(fs),fluid(fs)));
		}
		out.accept(new BowlContainingRecipe(rl("bowl/plain_water"),item(mrl("plain_water")),Fluids.WATER));
		out.accept(new BowlContainingRecipe(rl("bowl/plain_milk"),item(mrl("plain_milk")),ForgeMod.MILK.get()));
		out.accept(new BoilingRecipe(rl("boil/water"),fluid(mcrl("water")),fluid(mrl("nail_soup")),100));
		out.accept(new BoilingRecipe(rl("boil/milk"),fluid(mcrl("milk")),fluid(mrl("scalded_milk")),100));
		
		
		cook("acquacotta").base().tag(anyWater).and().require().mainly().of(baked).and().then().finish(out);
		cook("congee").base().tag(anyWater).and().require().half().of(rice).and().then().dense(0.25).finish(out);
		cook("rice_pudding").base().type(milk).and().require().half().of(rice).and().then().dense(0.25).finish(out);
		cook("gruel").base().tag(anyWater).and().require().half().of(nonrice).and().then().dense(0.25).finish(out);
		cook("porridge").base().type(milk).and().require().half().of(nonrice).and().then().dense(0.25).finish(out);
		cook("egg_drop_soup").base().tag(anyWater).and().require().mainly().of(eggs).and().not().any().of(vegetables).and().then().dense(0.5).finish(out);
		cook("stracciatella").base().tag(anyWater).and().require().mainly().of(eggs).and().any().of(vegetables).and().then().dense(0.5).finish(out);
		cook("custard").base().type(milk).and().require().mainly().of(eggs).and().then().dense(0.5).finish(out);
		cook("vegetable_soup").base().tag(anyWater).and().require().mainly().of(vegetables).and().then().finish(out);
		cook("vegetable_chowder").base().type(milk).and().require().mainly().of(vegetables).and().then().finish(out);
		cook("borscht").base().tag(anyWater).and().require().mainly().of(vegetables).and().typeMainly(vegetables).of(Items.BEETROOT).and().then().finish(out);
		cook("borscht_cream").base().type(milk).and().require().mainly().of(vegetables).and().typeMainly(vegetables).of(Items.BEETROOT).and().then().finish(out);
		cook("pumpkin_soup").base().tag(anyWater).and().require().mainly().of(vegetables).and().typeMainly(vegetables).of(Items.PUMPKIN).and().then().finish(out);
		cook("pumpkin_soup_cream").base().type(milk).and().require().mainly().of(vegetables).and().typeMainly(vegetables).of(Items.PUMPKIN).and().then().finish(out);
		cook("mushroom_soup").base().tag(anyWater).and().require().mainly().of(vegetables).and().typeMainly(vegetables).of(mrl("mushroom")).and().then().finish(out);
		cook("cream_of_mushroom_soup").base().type(milk).and().require().mainly().of(vegetables).and().typeMainly(vegetables).of(mrl("mushroom")).and().then().finish(out);
		cook("seaweed_soup").base().tag(anyWater).and().require().mainly().of(Items.DRIED_KELP).and().then().finish(out);
		cook("bisque").base().tag(anyWater).and().require().mainly().of(crustaceans).and().then().finish(out);
		cook("fish_soup").base().tag(anyWater).and().require().mainly().of(fish).and().then().finish(out);
		cook("fish_chowder").base().type(milk).and().require().mainly().of(seafood).and().then().finish(out);
		cook("poultry_soup").base().tag(anyWater).and().require().mainly().of(poultry).and().then().finish(out);
		cook("fricassee").base().type(milk).and().require().mainly().of(poultry).and().then().finish(out);
		cook("meat_soup").base().tag(anyWater).and().require().mainly().of(meat).and().then().finish(out);
		cook("cream_of_meat_soup").base().type(milk).and().require().mainly().of(meat).and().then().finish(out);
		cook("hodgepodge").prio(-1).finish(out);
		cook("dilute_soup").dense(0).prio(-2).finish(out);
		cook("stock").base().type(water).and().require().mainly().of(mrl("bone")).of(poultry).and().then().finish(out);
		cook("bone_gelatin").base().type(water).and().require().half().of(Items.BONE_MEAL).and().then().dense(2).finish(out);
		cook("egg_tongsui").base().type(water).and().require().half().of(eggs).and().any().of(sugar).and().not().any().of(meats).plus(seafood).plus(vegetables)
		.plus(mrl("wolfberries")).and().then().finish(out);
		cook("walnut_soup").base().type(water).and().require().half().of(RankineItems.ROASTED_WALNUT.get()).and().any().of(sugar).and().not().any().of(meats)
		.plus(seafood).plus(vegetables).plus(mrl("wolfberries")).and().then().finish(out);
		cook("goji_tongsui").base().type(water).and().require().mainly().of(sugar).and().any().of(mrl("wolfberries")).and().not().any().of(meats).plus(seafood)
		.plus(vegetables).and().then().finish(out);
		cook("ukha").base().tag(anyWater).and().require().half().of(fish).plus(roots).and().not().any().of(meats).plus(coreals).and().then().finish(out);
		cook("goulash").base().type(stock).and().require().mainly().of(Items.COOKED_BEEF).and().any().of(vegetables).and().not().any().of(seafood).plus(coreals)
		.and().then().finish(out);
		cook("okroshka").require().half().of(vegetables).plus(meats).and().any().of(mrl("ice")).and().then().finish(out);
		cook("nettle_soup").require().half().of(mrl("fern")).and().not().any().of(seafood).plus(meats).plus(coreals).and().then().finish(out);
	}
	private CookingRecipeBuilder cook(String s) {
		return CookingRecipeBuilder.start(fluid(mrl(s)));
	}
	private Item item(ResourceLocation rl) {
		return ForgeRegistries.ITEMS.getValue(rl);
	}
	private static Fluid fluid(ResourceLocation rl) {
		return ForgeRegistries.FLUIDS.getValue(rl);
	}
	private static ResourceLocation mrl(String s) {
		return new ResourceLocation(Main.MODID, s);
	}
	private ResourceLocation ftag(String s) {
		return new ResourceLocation("forge",s);
	}
	private ResourceLocation mcrl(String s) {
		return new ResourceLocation(s);
	}
	private ResourceLocation rl(String s) {
		if (!s.contains("/"))
			s = "crafting/" + s;
		if (PATH_COUNT.containsKey(s)) {
			int count = PATH_COUNT.get(s) + 1;
			PATH_COUNT.put(s, count);
			return new ResourceLocation(Main.MODID, s + count);
		}
		PATH_COUNT.put(s, 1);
		return new ResourceLocation(Main.MODID, s);
	}
}

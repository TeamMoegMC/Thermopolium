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
		Fluid bw=fluid(mrl("nail_soup"));
		Fluid bm=fluid(mrl("scalded_milk"));
		cook("acquacotta").base().type(bw).and().require().mainly().of(mrl("baked")).and().then().finish(out);
		cook("congee").base().type(bw).and().require().half().of(mrl("coreals/rice")).and().then().dense(0.25).finish(out);
		cook("rice_pudding").base().type(bm).and().require().half().of(mrl("coreals/rice")).and().then().dense(0.25).finish(out);
		cook("gruel").base().type(bw).and().require().half().of(mrl("coreals/non_rice")).and().then().dense(0.25).finish(out);
		cook("porridge").base().type(bm).and().require().half().of(mrl("coreals/non_rice")).and().then().dense(0.25).finish(out);
		cook("egg_drop_soup").base().type(bw).and().require().mainly().of(mrl("eggs")).and().then().not().any().of(mrl("vegetables")).and().then().dense(0.5).finish(out);
		cook("stracciatella").base().type(bw).and().require().mainly().of(mrl("eggs")).and().any().of(mrl("vegetables")).and().then().dense(0.5).finish(out);
		cook("custard").base().type(bm).and().require().mainly().of(mrl("eggs")).and().then().dense(0.5).finish(out);
		cook("vegetable_soup").base().type(bw).and().require().mainly().of(mrl("vegetables")).and().then().finish(out);
		cook("vegetable_chowder").base().type(bm).and().require().mainly().of(mrl("vegetables")).and().then().finish(out);
		cook("borscht").base().type(bw).and().require().mainly().of(mrl("vegetables")).and().any().of(Items.BEETROOT).and().then().finish(out);
		cook("borscht_cream").base().type(bm).and().require().mainly().of(mrl("vegetables")).and().any().of(Items.BEETROOT).and().then().finish(out);
		cook("pumpkin_soup").base().type(bw).and().require().mainly().of(mrl("vegetables")).and().any().of(Items.PUMPKIN).and().then().finish(out);
		cook("pumpkin_soup_cream").base().type(bm).and().require().mainly().of(mrl("vegetables")).and().any().of(Items.PUMPKIN).and().then().finish(out);
		cook("mushroom_soup").base().type(bw).and().require().mainly().of(mrl("vegetables")).and().any().of(mrl("mushroom")).and().then().finish(out);
		cook("cream_of_mushroom_soup").base().type(bm).and().require().mainly().of(mrl("vegetables")).and().any().of(mrl("mushroom")).and().then().finish(out);
		cook("poultry_soup").base().type(bw).and().require().mainly().of(Items.COOKED_CHICKEN).and().then().finish(out);
		
		
	}
	private CookingRecipeBuilder cook(String s) {
		return CookingRecipeBuilder.start(fluid(mrl(s)));
	}
	private Item item(ResourceLocation rl) {
		return ForgeRegistries.ITEMS.getValue(rl);
	}
	private Fluid fluid(ResourceLocation rl) {
		return ForgeRegistries.FLUIDS.getValue(rl);
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

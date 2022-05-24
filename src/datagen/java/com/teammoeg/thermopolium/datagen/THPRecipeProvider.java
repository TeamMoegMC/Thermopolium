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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import com.cannolicatfish.rankine.init.RankineItems;
import com.google.gson.JsonObject;
import com.teammoeg.thermopolium.Contents.THPBlocks;
import com.teammoeg.thermopolium.Contents.THPItems;
import com.teammoeg.thermopolium.Main;
import com.teammoeg.thermopolium.THPFluids;
import com.teammoeg.thermopolium.data.IDataRecipe;
import com.teammoeg.thermopolium.data.recipes.BoilingRecipe;
import com.teammoeg.thermopolium.data.recipes.BowlContainingRecipe;
import com.teammoeg.thermopolium.data.recipes.CookingRecipe;
import com.teammoeg.thermopolium.data.recipes.DissolveRecipe;
import com.teammoeg.thermopolium.data.recipes.FluidFoodValueRecipe;
import com.teammoeg.thermopolium.data.recipes.FoodValueRecipe;

import net.minecraft.block.Blocks;
import net.minecraft.data.CustomRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import pam.pamhc2crops.init.ItemRegistry;

public class THPRecipeProvider extends RecipeProvider {
	private final HashMap<String, Integer> PATH_COUNT = new HashMap<>();
	static final ResourceLocation rice = mrl("cereals/rice"),
			eggs = mrl("eggs"),
			baked = mrl("cereals/baked"),
			anyWater = mrl("water"),
			vegetables = mrl("vegetables"),
			meat = mrl("meats/meat"),
			fish = mrl("seafood/fish"),
			poultry = mrl("meats/poultry"),
			seafood = mrl("seafood"),
			meats = mrl("meats"),
			sugar = mrl("sugar"),
			cereals = mrl("cereals"),
			crustaceans = mrl("seafood/crustaceans"),
			roots = mrl("vegetables/roots"),
			mushrooms = mrl("mushroom"),
			pumpkin = mrl("vegetables/pumpkin"),
			walnut = mrl("walnut");
	static final Fluid water = fluid(mrl("nail_soup")), milk = fluid(mrl("scalded_milk")), stock = fluid(mrl("stock"));
	public static List<IDataRecipe> recipes=new ArrayList<>();
	public THPRecipeProvider(DataGenerator generatorIn) {
		super(generatorIn);
	}

	@Override
	protected void buildShapelessRecipes(@Nonnull Consumer<IFinishedRecipe> outx) {
		Consumer<IDataRecipe> out=r->{
			outx.accept(new IFinishedRecipe() {

				@Override
				public void serializeRecipeData(JsonObject jo) {
					r.serializeRecipeData(jo);
				}

				@Override
				public ResourceLocation getId() {
					return r.getId();
				}

				@Override
				public IRecipeSerializer<?> getType() {
					return r.getSerializer();
				}

				@Override
				public JsonObject serializeAdvancement() {
					return null;
				}

				@Override
				public ResourceLocation getAdvancementId() {
					return null;
				}
				
			});
		};
		for (String s : THPFluids.getSoupfluids()) {
			ResourceLocation fs = mrl(s);
			out.accept(new BowlContainingRecipe(rl("bowl/" + s), item(fs), fluid(fs)));
		}
		out.accept(dissolve(RankineItems.CORN_EAR.get()));
		out.accept(new BowlContainingRecipe(rl("bowl/plain_water"), item(mrl("plain_water")), Fluids.WATER));
		out.accept(new BowlContainingRecipe(rl("bowl/plain_milk"), item(mrl("plain_milk")), ForgeMod.MILK.get()));
		out.accept(new BoilingRecipe(rl("boil/water"), fluid(mcrl("water")), fluid(mrl("nail_soup")), 100));
		out.accept(new BoilingRecipe(rl("boil/milk"), fluid(mcrl("milk")), fluid(mrl("scalded_milk")), 100));
		out.accept(new FoodValueRecipe(rl("food/mushroom"),3,3.6f,new ItemStack(Items.RED_MUSHROOM),Items.RED_MUSHROOM,Items.BROWN_MUSHROOM));
		out.accept(new FoodValueRecipe(rl("food/pumpkin"),3,6f,new ItemStack(Items.PUMPKIN),Items.PUMPKIN,Items.CARVED_PUMPKIN));
		out.accept(new FoodValueRecipe(rl("food/wheat"),3,5f,new ItemStack(Items.WHEAT),Items.WHEAT,Items.WHEAT_SEEDS));
		out.accept(new FoodValueRecipe(rl("food/fern"),1,0.5f,new ItemStack(Items.FERN),Items.FERN,Items.LARGE_FERN));
		ShapedRecipeBuilder.shaped(THPBlocks.stove1).define('D',Items.DIRT).define('S',Items.COBBLESTONE).pattern("DDD").pattern("SSS").pattern("S S").unlockedBy("has_cobblestone", has(Blocks.COBBLESTONE)).save(outx);
		ShapedRecipeBuilder.shaped(THPBlocks.stove2).define('T',Items.BRICK_SLAB).define('B',Items.BRICKS).define('C',Items.CLAY).pattern("TTT").pattern("BCB").pattern("B B").unlockedBy("has_bricks", has(Blocks.BRICKS)).save(outx);
		ShapedRecipeBuilder.shaped(THPItems.clay_pot).define('C',Items.CLAY_BALL).define('S',Items.STICK).pattern("CCC").pattern("CSC").pattern("CCC").unlockedBy("has_clay", has(Items.CLAY_BALL)).save(outx);
		net.minecraft.data.CookingRecipeBuilder.smelting(Ingredient.of(THPItems.clay_pot),THPBlocks.stew_pot,0.35f,200).unlockedBy("has_claypot",has(THPItems.clay_pot)).save(outx);
		//ShapedRecipeBuilder.shapedRecipe(THPBlocks.stew_pot).key('B',Items.BRICK).key('C',Items.CLAY_BALL).patternLine("BCB").patternLine("B B").patternLine("BBB").unlockedBy("has_brick", hasItem(Items.BRICK)).build(out);
		//ShapelessRecipeBuilder.shapelessRecipe(THPItems.BOOK).addIngredient(Items.BOOK).addIngredient(Items.BOWL).unlockedBy("has_bowl", hasItem(Items.BOWL)).build(out);
		out.accept(new FluidFoodValueRecipe(rl("fluid_food/milk"),0,1.2f,new ItemStack(Items.MILK_BUCKET),4,new ResourceLocation(Main.MODID,"scalded_milk")));
		out.accept(new FluidFoodValueRecipe(rl("fluid_food/stock"),2,5,null,4,new ResourceLocation(Main.MODID,"stock")));
		simpleFood(out,2,0.4f,Items.HONEYCOMB);
		simpleFood(out,3,5f,ItemRegistry.amaranthitem);
		simpleFood(out,3,5f,ItemRegistry.barleyitem);
		simpleFood(out,3,5f,ItemRegistry.beanitem);
		simpleFood(out,3,5f,ItemRegistry.chickpeaitem);
		simpleFood(out,3,5f,ItemRegistry.cornitem);
		simpleFood(out,3,5f,ItemRegistry.lentilitem);
		simpleFood(out,3,5f,ItemRegistry.milletitem);
		simpleFood(out,3,5f,ItemRegistry.oatsitem);
		simpleFood(out,3,5f,ItemRegistry.quinoaitem);
		simpleFood(out,3,5f,ItemRegistry.riceitem);
		simpleFood(out,3,5f,ItemRegistry.ryeitem);
		simpleFood(out,3,5f,ItemRegistry.soybeanitem);
		simpleFood(out,0,.5f,Items.BONE_MEAL);
		simpleFood(out,1,1f,Items.BONE);
		simpleFood(out,3,4f,Items.EGG);
		out=out.andThen(recipes::add);
		cook("acquacotta").high().base().tag(anyWater).and().require().mainly().of(baked).and().then().finish(out);
		cook("congee").med().base().tag(anyWater).and().require().half().of(rice).and().then().dense(0.25).finish(out);
		cook("rice_pudding").med().base().type(milk).and().require().half().of(rice).and().then().dense(0.25)
				.finish(out);
		cook("gruel").base().tag(anyWater).and().require().half().of(cereals).and().then().dense(0.25).finish(out);
		cook("porridge").base().type(milk).and().require().half().of(cereals).and().then().dense(0.25).finish(out);
		cook("egg_drop_soup").base().tag(anyWater).and().require().mainly().of(eggs).and().not().any().of(vegetables)
				.and().then().dense(0.5).finish(out);
		cook("stracciatella").base().tag(anyWater).and().require().mainly().of(eggs).and().any().of(vegetables).and()
				.then().dense(0.5).finish(out);
		cook("custard").base().type(milk).and().require().mainly().of(eggs).and().then().dense(0.5).finish(out);
		cook("vegetable_soup").base().tag(anyWater).and().require().mainly().of(vegetables).and().then().finish(out);
		cook("vegetable_chowder").base().type(milk).and().require().mainly().of(vegetables).and().then().finish(out);
		cook("borscht").high().base().tag(anyWater).and().require().mainly().of(vegetables).and().typeMainly(vegetables)
				.of(Items.BEETROOT).and().then().finish(out);
		cook("borscht_cream").high().base().type(milk).and().require().mainly().of(vegetables).and()
				.typeMainly(vegetables).of(Items.BEETROOT).and().then().finish(out);
		cook("pumpkin_soup").high().base().tag(anyWater).and().require().mainly().of(vegetables).and()
				.typeMainly(vegetables).of(pumpkin).and().then().finish(out);
		cook("pumpkin_soup_cream").high().base().type(milk).and().require().mainly().of(vegetables).and()
				.typeMainly(vegetables).of(pumpkin).and().then().finish(out);
		cook("mushroom_soup").high().base().tag(anyWater).and().require().mainly().of(vegetables).and()
				.typeMainly(vegetables).of(mushrooms).and().then().finish(out);
		cook("cream_of_mushroom_soup").high().base().type(milk).and().require().mainly().of(vegetables).and()
				.typeMainly(vegetables).of(mushrooms).and().then().finish(out);
		cook("seaweed_soup").med().base().tag(anyWater).and().require().mainly().of(Items.KELP).and().then().finish(out);
		cook("bisque").base().tag(anyWater).and().require().mainly().of(crustaceans).and().then().finish(out);
		cook("fish_soup").base().tag(anyWater).and().require().mainly().of(fish).and().then().finish(out);
		cook("fish_chowder").base().type(milk).and().require().mainly().of(seafood).and().then().finish(out);
		cook("poultry_soup").base().tag(anyWater).and().require().mainly().of(poultry).and().then().finish(out);
		cook("fricassee").base().type(milk).and().require().mainly().of(poultry).and().then().finish(out);
		cook("meat_soup").base().tag(anyWater).and().require().mainly().of(meat).and().then().finish(out);
		cook("cream_of_meat_soup").base().type(milk).and().require().mainly().of(meat).and().then().finish(out);
		cook("hodgepodge").prio(-1).finish(out);
		cook("dilute_soup").prio(-2).dense(0).finish(out);

		cook("stock").special().base().type(water).and().require().mainly().of(mrl("bone")).plus(poultry).and().any()
				.of(mrl("bone")).of(poultry).and().then().finish(out);
		//cook("bone_gelatin").special().high().base().type(water).and().require().half().of(Items.BONE_MEAL).and().then()
		//		.dense(3).finish(out);
		cook("egg_tongsui").special().med().base().type(water).and().require().half().of(eggs).and().any().of(sugar)
				.and().not().any().of(meats).of(seafood).of(vegetables).of(mrl("wolfberries")).and().then()
				.finish(out);
		cook("walnut_soup").special().med().base().type(water).and().require().half().of(walnut).and().any().of(sugar)
				.and().not().any().of(meats).of(seafood).of(vegetables).of(mrl("wolfberries")).and().then()
				.finish(out);
		cook("goji_tongsui").special().med().base().type(water).and().require().mainly().of(sugar).and().any()
				.of(mrl("wolfberries")).and().not().any().of(meats).of(seafood).of(vegetables).and().then()
				.finish(out);
		cook("ukha").special().med().base().tag(anyWater).and().require().half().of(fish).plus(roots).and().any().of(fish).of(roots).and().not().any()
				.of(meats).of(cereals).and().then().finish(out);
		cook("goulash").special().high().base().type(stock).and().require().mainly().of(ftag("raw_beef")).and().any()
				.of(vegetables).and().not().any().of(seafood).of(cereals).and().then().finish(out);
		cook("okroshka").special().high().require().half().of(vegetables).plus(meats).and().any().of(vegetables).of(meats).of(mrl("ice"))
		.and().then().finish(out);
		cook("nettle_soup").special().med().require().half().of(mrl("fern")).and().not().any().of(seafood).of(meats)
				.of(cereals).and().then().finish(out);
	}
	private void simpleFood(Consumer<IDataRecipe> out,int h,float s,Item i) {
		out.accept(new FoodValueRecipe(rl("food/"+i.getRegistryName().getPath()),h,s,new ItemStack(i),i));
	}
	private DissolveRecipe dissolve(Item item) {
		return new DissolveRecipe(rl("dissolve/" + item.getRegistryName().getPath()),
				Ingredient.of(new ItemStack(item)), 10);
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
		return new ResourceLocation("forge", s);
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

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import com.cannolicatfish.rankine.init.RankineItems;
import com.teammoeg.thermopolium.Contents.THPBlocks;
import com.teammoeg.thermopolium.Contents.THPItems;
import com.teammoeg.thermopolium.Main;
import com.teammoeg.thermopolium.THPFluids;
import com.teammoeg.thermopolium.data.recipes.BoilingRecipe;
import com.teammoeg.thermopolium.data.recipes.BowlContainingRecipe;
import com.teammoeg.thermopolium.data.recipes.CookingRecipe;
import com.teammoeg.thermopolium.data.recipes.DissolveRecipe;
import com.teammoeg.thermopolium.data.recipes.FluidFoodValueRecipe;
import com.teammoeg.thermopolium.data.recipes.FoodValueRecipe;

import gloridifice.watersource.WaterSource;
import gloridifice.watersource.registry.RecipeSerializersRegistry;
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
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
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
	public static List<IFinishedRecipe> recipes=new ArrayList<>();
	public THPRecipeProvider(DataGenerator generatorIn) {
		super(generatorIn);
	}

	@Override
	protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> out) {
		for (String s : THPFluids.getSoupfluids()) {
			ResourceLocation fs = mrl(s);
			out.accept(new BowlContainingRecipe(rl("bowl/" + s), item(fs), fluid(fs)));
		}
		out.accept(dissolve(RankineItems.CORN_EAR.get()));
		out.accept(new BowlContainingRecipe(rl("bowl/plain_water"), item(mrl("plain_water")), Fluids.WATER));
		out.accept(new BowlContainingRecipe(rl("bowl/plain_milk"), item(mrl("plain_milk")), ForgeMod.MILK.get()));
		out.accept(new BoilingRecipe(rl("boil/water"), fluid(mcrl("water")), fluid(mrl("nail_soup")), 100));
		out.accept(new BoilingRecipe(rl("boil/milk"), fluid(mcrl("milk")), fluid(mrl("scalded_milk")), 100));
		//rankine:honey_mushroom
		//[23:35:10] [INFO] server_scripts:the_winter_rescue/recipetypes/stoneage/tree_stump.js:38: rankine:sulfur_shelf_mushroom
		//[23:35:10] [INFO] server_scripts:the_winter_rescue/recipetypes/stoneage/tree_stump.js:38: rankine:oyster_mushroom
		//[23:35:10] [INFO] server_scripts:the_winter_rescue/recipetypes/stoneage/tree_stump.js:38: rankine:lions_mane_mushroom
		out.accept(new FoodValueRecipe(rl("food/mushroom"),3,.5f,new ItemStack(Items.RED_MUSHROOM),Items.RED_MUSHROOM,Items.BROWN_MUSHROOM,RankineItems.HONEY_MUSHROOM.get(),RankineItems.SULFUR_SHELF_MUSHROOM.get(),RankineItems.OYSTER_MUSHROOM.get(),RankineItems.LIONS_MANE_MUSHROOM.get()));
		out.accept(new FoodValueRecipe(rl("food/poison_mushroom"),3,.5f,new ItemStack(Items.RED_MUSHROOM),RankineItems.ARTIST_CONK_MUSHROOM.get(),RankineItems.TINDER_CONK_MUSHROOM.get(),RankineItems.TURKEY_TAIL_MUSHROOM.get(),RankineItems.CINNABAR_POLYPORE_MUSHROOM.get()).addEffect(new EffectInstance(Effects.POISON,100,2),1).addEffect(new EffectInstance(Effects.NAUSEA,400),1));
		out.accept(new FoodValueRecipe(rl("food/pumpkin"),3,1f,new ItemStack(Items.PUMPKIN),Items.PUMPKIN,Items.CARVED_PUMPKIN));
		out.accept(new FoodValueRecipe(rl("food/wheat"),3,.8f,new ItemStack(Items.WHEAT),Items.WHEAT,Items.WHEAT_SEEDS));
		out.accept(new FoodValueRecipe(rl("food/fern"),1,0.5f,new ItemStack(Items.FERN),Items.FERN,Items.LARGE_FERN));
		ShapedRecipeBuilder.shapedRecipe(THPBlocks.stove1).key('D',Items.DIRT).key('S',Items.COBBLESTONE).patternLine("DDD").patternLine("SSS").patternLine("S S").addCriterion("has_cobblestone", hasItem(Blocks.COBBLESTONE)).build(out);
		ShapedRecipeBuilder.shapedRecipe(THPBlocks.stove2).key('T',Items.BRICK_SLAB).key('B',Items.BRICKS).key('C',Items.CLAY).patternLine("TTT").patternLine("BCB").patternLine("B B").addCriterion("has_bricks", hasItem(Blocks.BRICKS)).build(out);
		ShapedRecipeBuilder.shapedRecipe(THPItems.clay_pot).key('C',Items.CLAY_BALL).key('S',Items.STICK).patternLine("CCC").patternLine("CSC").patternLine("CCC").addCriterion("has_clay", hasItem(Items.CLAY_BALL)).build(out);
		net.minecraft.data.CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(THPItems.clay_pot),THPBlocks.stew_pot,0.35f,200).addCriterion("has_claypot",hasItem(THPItems.clay_pot)).build(out);
		//ShapedRecipeBuilder.shapedRecipe(THPBlocks.stew_pot).key('B',Items.BRICK).key('C',Items.CLAY_BALL).patternLine("BCB").patternLine("B B").patternLine("BBB").addCriterion("has_brick", hasItem(Items.BRICK)).build(out);
		//ShapelessRecipeBuilder.shapelessRecipe(THPItems.BOOK).addIngredient(Items.BOOK).addIngredient(Items.BOWL).addCriterion("has_bowl", hasItem(Items.BOWL)).build(out);
		out.accept(new FluidFoodValueRecipe(rl("fluid_food/milk"),0,2f,new ItemStack(Items.MILK_BUCKET),4,new ResourceLocation(Main.MODID,"scalded_milk")));
		out.accept(new FluidFoodValueRecipe(rl("fluid_food/stock"),2,5f,null,4,new ResourceLocation(Main.MODID,"stock")));
		simpleFood(out,2,0.4f,Items.HONEYCOMB);
		simpleFood(out,3,.8f,ItemRegistry.amaranthitem,ItemRegistry.amaranthseeditem);
		simpleFood(out,3,.8f,ItemRegistry.barleyitem,ItemRegistry.barleyseeditem);
		simpleFood(out,3,.8f,ItemRegistry.beanitem,ItemRegistry.beanseeditem);
		simpleFood(out,3,.8f,ItemRegistry.chickpeaitem,ItemRegistry.chickpeaseeditem);
		simpleFood(out,3,.8f,ItemRegistry.cornitem,ItemRegistry.cornseeditem);
		simpleFood(out,3,.8f,ItemRegistry.lentilitem,ItemRegistry.lentilseeditem);
		simpleFood(out,3,.8f,ItemRegistry.milletitem,ItemRegistry.milletseeditem);
		simpleFood(out,3,.8f,ItemRegistry.oatsitem,ItemRegistry.oatsseeditem);
		simpleFood(out,3,.8f,ItemRegistry.quinoaitem,ItemRegistry.quinoaseeditem);
		simpleFood(out,3,.8f,ItemRegistry.riceitem,ItemRegistry.riceseeditem);
		simpleFood(out,3,.8f,ItemRegistry.ryeitem,ItemRegistry.ryeseeditem);
		simpleFood(out,3,.8f,ItemRegistry.soybeanitem,ItemRegistry.soybeanseeditem);
		simpleFood(out,3,.8f,RankineItems.BARLEY,RankineItems.BARLEY_GRAIN);
		simpleFood(out,3,.8f,RankineItems.SOYBEANS);
		simpleFood(out,3,.8f,RankineItems.CORN_EAR,RankineItems.CORN_GRAIN);
		simpleFood(out,3,.8f,RankineItems.MILLET,RankineItems.MILLET_GRAIN);
		simpleFood(out,3,.8f,RankineItems.OATS,RankineItems.OAT_GRAIN);
		simpleFood(out,3,.8f,RankineItems.RYE,RankineItems.RYE_GRAIN);
		simpleFood(out,3,.8f,RankineItems.RICE,RankineItems.RICE_GRAIN);
		simpleFood(out,3,.8f,RankineItems.SORGHUM,RankineItems.SORGHUM_GRAIN);
		simpleFood(out,3,.8f,RankineItems.WHEAT_GRAIN);
		
		simpleFood(out,0,.8f,Items.BONE_MEAL);
		simpleFood(out,1,.5f,Items.BONE);
		simpleFood(out,3,.5f,Items.EGG);
		String[] ovride=new String[] {
				"dilute_soup",
				"nail_soup",
				"plain_water"
		};
		for (Item s : THPItems.stews)
			if(!Arrays.stream(ovride).anyMatch(s.getRegistryName().getPath()::equals))
				out.accept(new WaterLevelRecipe(new ResourceLocation(Main.MODID,"water_level/"+s.getRegistryName().getPath()),Ingredient.fromItems(s),2,2));
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
	private void simpleFood(Consumer<IFinishedRecipe> out,int h,float s,Item... is) {
		out.accept(new FoodValueRecipe(rl("food/"+is[0].getRegistryName().getNamespace()+"/"+is[0].getRegistryName().getPath()),h,s,new ItemStack(is[0]),is));
	}
	@SafeVarargs
	private final void simpleFood(Consumer<IFinishedRecipe> out,int h,float s,RegistryObject<Item>... is) {
		Item[] its=new Item[is.length];
		for(int i=0;i<its.length;i++)
			its[i]=is[i].get();
		out.accept(new FoodValueRecipe(rl("food/"+is[0].getId().getNamespace()+"/"+is[0].getId().getPath()),h,s,new ItemStack(is[0].get()),its));
	}
	private DissolveRecipe dissolve(Item item) {
		return new DissolveRecipe(rl("dissolve/" + item.getRegistryName().getPath()),
				Ingredient.fromStacks(new ItemStack(item)), 10);
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

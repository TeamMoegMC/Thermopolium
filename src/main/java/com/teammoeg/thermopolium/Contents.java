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

package com.teammoeg.thermopolium;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableSet;
import com.teammoeg.thermopolium.blocks.KitchenStoveT1;
import com.teammoeg.thermopolium.blocks.KitchenStoveT2;
import com.teammoeg.thermopolium.blocks.KitchenStove;
import com.teammoeg.thermopolium.blocks.KitchenStoveTileEntity;
import com.teammoeg.thermopolium.blocks.StewPot;
import com.teammoeg.thermopolium.blocks.StewPotTileEntity;
import com.teammoeg.thermopolium.container.KitchenStoveContainer;
import com.teammoeg.thermopolium.container.StewPotContainer;
import com.teammoeg.thermopolium.data.RecipeSerializer;
import com.teammoeg.thermopolium.data.recipes.BoilingRecipe;
import com.teammoeg.thermopolium.data.recipes.BowlContainingRecipe;
import com.teammoeg.thermopolium.data.recipes.CookingRecipe;
import com.teammoeg.thermopolium.data.recipes.CountingTags;
import com.teammoeg.thermopolium.data.recipes.DissolveRecipe;
import com.teammoeg.thermopolium.data.recipes.FluidFoodValueRecipe;
import com.teammoeg.thermopolium.data.recipes.FoodValueRecipe;
import com.teammoeg.thermopolium.items.THPBlockItem;
import com.teammoeg.thermopolium.items.THPItem;
import com.teammoeg.thermopolium.items.IconItem;
import com.teammoeg.thermopolium.items.StewItem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Contents {

	public static List<Block> registeredBlocks = new ArrayList<>();
	public static List<Item> registeredItems = new ArrayList<>();
	public static List<Fluid> registeredFluids = new ArrayList<>();

	public static class THPBlocks {
		public static void init() {
		}

		public static Block stew_pot = new StewPot("stew_pot",
				Block.Properties.create(Material.ROCK).sound(SoundType.STONE).setRequiresTool()
						.harvestTool(ToolType.PICKAXE).hardnessAndResistance(2, 10).notSolid(),
				THPBlockItem::new);
		public static Block stove1 = new KitchenStove("kitchen_stove_t1",Block.Properties.create(Material.ROCK).sound(SoundType.STONE).setRequiresTool()
				.harvestTool(ToolType.PICKAXE).hardnessAndResistance(2, 10).notSolid().setLightLevel(s->s.get(KitchenStove.LIT)?7:0).setOpaque(THPBlocks::isntSolid).setSuffocates(THPBlocks::isntSolid).setBlocksVision(THPBlocks::isntSolid),
				THPTileTypes.STOVE1,
				THPBlockItem::new);
		public static Block stove2 = new KitchenStove("kitchen_stove_t2",Block.Properties.create(Material.ROCK).sound(SoundType.STONE).setRequiresTool()
				.harvestTool(ToolType.PICKAXE).hardnessAndResistance(2, 10).notSolid().setLightLevel(s->s.get(KitchenStove.LIT)?9:0).setOpaque(THPBlocks::isntSolid).setSuffocates(THPBlocks::isntSolid).setBlocksVision(THPBlocks::isntSolid),
				THPTileTypes.STOVE2,
				THPBlockItem::new);
		   private static boolean isntSolid(BlockState state, IBlockReader reader, BlockPos pos) {
			      return false;
			   }
	}

	public static class THPItems {
		public static final String[] items = new String[] { "acquacotta", "bisque", "bone_gelatin", "borscht",
				"borscht_cream", "congee", "cream_of_meat_soup", "cream_of_mushroom_soup", "custard", "dilute_soup",
				"egg_drop_soup", "egg_tongsui", "fish_chowder", "fish_soup", "fricassee", "goji_tongsui", "goulash",
				"gruel", "hodgepodge", "meat_soup", "mushroom_soup", "nail_soup", "nettle_soup", "okroshka", "porridge",
				"poultry_soup", "pumpkin_soup", "pumpkin_soup_cream", "rice_pudding", "scalded_milk", "seaweed_soup",
				"stock", "stracciatella", "ukha", "vegetable_chowder", "vegetable_soup", "walnut_soup" };
		public static final List<Item> stews = new ArrayList<>();
		public static Item anyWater=new IconItem("water_or_stock_based");
		public static Item stock=new IconItem("stock_based");
		public static Item milk=new IconItem("milk_based");
		public static Item any=new IconItem("any_based");
		public static Item clay_pot=new THPItem("clay_cistern",new Item.Properties().group(Main.itemGroup));
		//public static Item BOOK=new THPItem("book",new Item.Properties().group(Main.itemGroup));
		public static void init() {
			for (String s : items)
				new StewItem(s, new ResourceLocation(Main.MODID, s), createProps());
			new StewItem("plain_milk", new ResourceLocation("milk"), createProps());
			new StewItem("plain_water", new ResourceLocation("water"), createProps());

		}

		static Properties createProps() {
			return new Item.Properties().group(Main.itemGroup).containerItem(Items.BOWL).maxStackSize(1);
		}
		// public static Item stew_bowl=new StewItem("stew",createProps());
	}

	public static class THPTileTypes {
		public static final DeferredRegister<TileEntityType<?>> REGISTER = DeferredRegister
				.create(ForgeRegistries.TILE_ENTITIES, Main.MODID);

		public static final RegistryObject<TileEntityType<StewPotTileEntity>> STEW_POT = REGISTER.register("stew_pot",
				makeType(() -> new StewPotTileEntity(), () -> THPBlocks.stew_pot));
		public static final RegistryObject<TileEntityType<KitchenStoveTileEntity>> STOVE1 = REGISTER.register("kitchen_stove_t1",
				makeType(() -> new KitchenStoveT1(), () -> THPBlocks.stove1));
		public static final RegistryObject<TileEntityType<KitchenStoveTileEntity>> STOVE2 = REGISTER.register("kitchen_stove_t2",
				makeType(() -> new KitchenStoveT2(), () -> THPBlocks.stove2));
		private static <T extends TileEntity> Supplier<TileEntityType<T>> makeType(Supplier<T> create,
				Supplier<Block> valid) {
			return () -> new TileEntityType<>(create,ImmutableSet.of(valid.get()), null);
		}

	}

	public static class THPGui {
		public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister
				.create(ForgeRegistries.CONTAINERS, Main.MODID);
		public static final RegistryObject<ContainerType<StewPotContainer>> STEWPOT = CONTAINERS.register("stew_pot",
				() -> IForgeContainerType.create(StewPotContainer::new));
		public static final RegistryObject<ContainerType<KitchenStoveContainer>> STOVE = CONTAINERS.register("kitchen_stove",
				() -> IForgeContainerType.create(KitchenStoveContainer::new));
	}

	public static class THPRecipes {
		public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister
				.create(ForgeRegistries.RECIPE_SERIALIZERS, Main.MODID);

		static {
			CookingRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("cooking",
					() -> new RecipeSerializer<CookingRecipe>(CookingRecipe::new, CookingRecipe::new,
							CookingRecipe::write));
			BoilingRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("boiling",
					() -> new RecipeSerializer<BoilingRecipe>(BoilingRecipe::new, BoilingRecipe::new,
							BoilingRecipe::write));
			BowlContainingRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("bowl",
					() -> new RecipeSerializer<BowlContainingRecipe>(BowlContainingRecipe::new,
							BowlContainingRecipe::new, BowlContainingRecipe::write));
			DissolveRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("dissolve",
					() -> new RecipeSerializer<DissolveRecipe>(DissolveRecipe::new, DissolveRecipe::new,
							DissolveRecipe::write));
			CountingTags.SERIALIZER = RECIPE_SERIALIZERS.register("tags",
					() -> new RecipeSerializer<CountingTags>(CountingTags::new, CountingTags::new,
							CountingTags::write));
			FoodValueRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("food",
					() -> new RecipeSerializer<FoodValueRecipe>(FoodValueRecipe::new, FoodValueRecipe::new,
							FoodValueRecipe::write));
			FluidFoodValueRecipe.SERIALIZER=RECIPE_SERIALIZERS.register("fluid_food",() -> new RecipeSerializer<FluidFoodValueRecipe>(FluidFoodValueRecipe::new, FluidFoodValueRecipe::new,
					FluidFoodValueRecipe::write));
		}

		public static void registerRecipeTypes() {
			CookingRecipe.TYPE = IRecipeType.register(Main.MODID + ":stew");
			BoilingRecipe.TYPE = IRecipeType.register(Main.MODID + ":boil");
			BowlContainingRecipe.TYPE = IRecipeType.register(Main.MODID + ":bowl");
			DissolveRecipe.TYPE = IRecipeType.register(Main.MODID + ":dissolve");
			CountingTags.TYPE = IRecipeType.register(Main.MODID + ":tags");
			FoodValueRecipe.TYPE = IRecipeType.register(Main.MODID + ":food");
			FluidFoodValueRecipe.TYPE = IRecipeType.register(Main.MODID + ":fluid_food");
		}
	}
}

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

package com.teammoeg.stewcraft;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableSet;
import com.teammoeg.stewcraft.blocks.StewPot;
import com.teammoeg.stewcraft.items.SCBlockItem;
import com.teammoeg.stewcraft.items.StewItem;
import com.teammoeg.stewcraft.recipes.CookingRecipe;
import com.teammoeg.stewcraft.recipes.CookingRecipeSerializer;
import com.teammoeg.stewcraft.tiles.StewPotTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Contents {

    public static List<Block> registeredBlocks = new ArrayList<>();
    public static List<Item> registeredItems = new ArrayList<>();
    public static List<Fluid> registeredFluids = new ArrayList<>();

    public static class SCBlocks {
		public static void init() {
        }
        public static Block stew_pot=new StewPot("stew_pot", Block.Properties
                .create(Material.ROCK)
                .sound(SoundType.STONE)
                .setRequiresTool()
                .harvestTool(ToolType.PICKAXE)
                .hardnessAndResistance(2, 10)
                .notSolid(), SCBlockItem::new);
    }

    public static class SCItems {
        public static void init() {
        }
        static Properties createProps() {
        	return new Item.Properties().group(Main.itemGroup);
        }
        public static Item stew_bowl=new StewItem("stew",createProps());
    }

    public static class SCTileTypes {
        public static final DeferredRegister<TileEntityType<?>> REGISTER = DeferredRegister.create(
                ForgeRegistries.TILE_ENTITIES, Main.MODID);

        public static final RegistryObject<TileEntityType<StewPotTileEntity>> STEW_POT = REGISTER.register(
                "stew_pot", makeType(() -> new StewPotTileEntity(), () -> SCBlocks.stew_pot)
        );

        private static <T extends TileEntity> Supplier<TileEntityType<T>> makeType(Supplier<T> create, Supplier<Block> valid) {
            return makeTypeMultipleBlocks(create, () -> ImmutableSet.of(valid.get()));
        }

        private static <T extends TileEntity> Supplier<TileEntityType<T>> makeTypeMultipleBlocks(Supplier<T> create, Supplier<Collection<Block>> valid) {
            return () -> new TileEntityType<>(create, ImmutableSet.copyOf(valid.get()), null);
        }

    }

    public static class SCRecipes {
        public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(
                ForgeRegistries.RECIPE_SERIALIZERS, Main.MODID
        );

        static {
            CookingRecipe.SERIALIZER=RECIPE_SERIALIZERS.register("stewpot_cooking",CookingRecipeSerializer::new);
        }

        public static void registerRecipeTypes() {
        	CookingRecipe.TYPE = IRecipeType.register(Main.MODID + ":stew");
        }
    }
}

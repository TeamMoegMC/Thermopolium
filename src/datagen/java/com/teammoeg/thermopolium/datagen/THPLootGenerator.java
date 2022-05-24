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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.teammoeg.thermopolium.Contents.THPBlocks;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableManager;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class THPLootGenerator extends LootTableProvider {

	public THPLootGenerator(DataGenerator dataGeneratorIn) {
		super(dataGeneratorIn);
	}
    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
        return Arrays.asList(Pair.of(()->new LTBuilder(), LootParameterSets.BLOCK));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker) {
        map.forEach((name, table) -> LootTableManager.validate(validationtracker, name, table));
    }

    private static class LTBuilder extends BlockLootTables {
		@Override
        protected void addTables() {
        	dropSelf(THPBlocks.stew_pot);
        	dropSelf(THPBlocks.stove1);
        	dropSelf(THPBlocks.stove2);
        }
		ArrayList<Block> added=new ArrayList<>();
		@Override
		protected Iterable<Block> getKnownBlocks() {
			return added;
		}

		@Override
		public void dropOther(Block blockIn, IItemProvider drop) {
			added.add(blockIn);
			super.dropOther(blockIn, drop);
		}

    }
}

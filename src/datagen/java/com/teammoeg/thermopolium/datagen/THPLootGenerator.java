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
        map.forEach((name, table) -> LootTableManager.validateLootTable(validationtracker, name, table));
    }

    private static class LTBuilder extends BlockLootTables {
		@Override
        protected void addTables() {
        	registerDropSelfLootTable(THPBlocks.stew_pot);
        	registerDropSelfLootTable(THPBlocks.stove1);
        	registerDropSelfLootTable(THPBlocks.stove2);
        }
		ArrayList<Block> added=new ArrayList<>();
		@Override
		protected Iterable<Block> getKnownBlocks() {
			return added;
		}

		@Override
		public void registerDropping(Block blockIn, IItemProvider drop) {
			added.add(blockIn);
			super.registerDropping(blockIn, drop);
		}

    }
}

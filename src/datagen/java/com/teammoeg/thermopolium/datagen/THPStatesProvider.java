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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import com.google.common.collect.ImmutableList;
import com.teammoeg.thermopolium.Contents.THPBlocks;
import com.teammoeg.thermopolium.blocks.KitchenStove;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelBuilder.Perspective;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder.PartBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class THPStatesProvider extends BlockStateProvider {
    protected static final List<Vector3i> COLUMN_THREE = ImmutableList.of(BlockPos.ZERO, BlockPos.ZERO.up(), BlockPos.ZERO.up(2));

    protected static final Map<ResourceLocation, String> generatedParticleTextures = new HashMap<>();
    protected final ExistingFileHelper existingFileHelper;
    String modid;
    public THPStatesProvider(DataGenerator gen,String modid,ExistingFileHelper exFileHelper)
    {
        super(gen,modid, exFileHelper);
        this.modid=modid;
        this.existingFileHelper = exFileHelper;
    }

	@Override
	protected void registerStatesAndModels() {
		horizontalAxisBlock(THPBlocks.stew_pot,bmf("cistern"));
		
		horizontalMultipart(horizontalMultipart(this.getMultipartBuilder(THPBlocks.stove1),bmf("kitchen_stove_i"))
				.part().modelFile(bmf("kitchen_stove_ash")).addModel().condition(KitchenStove.LIT,false).condition(KitchenStove.ASH,true).end()
				.part().modelFile(bmf("kitchen_stove_hot_ash")).addModel().condition(KitchenStove.LIT,true).end()
				,bmf("kitchen_stove_charcoal"),i->i.condition(KitchenStove.FUELED,1));
		horizontalMultipart(horizontalMultipart(this.getMultipartBuilder(THPBlocks.stove2),bmf("kitchen_stove_ii"))
		.part().modelFile(bmf("kitchen_stove_ash")).addModel().condition(KitchenStove.LIT,false).condition(KitchenStove.ASH,true).end()
		.part().modelFile(bmf("kitchen_stove_hot_ash")).addModel().condition(KitchenStove.LIT,true).end()
		,bmf("kitchen_stove_charcoal"),i->i.condition(KitchenStove.FUELED,1));
		itemModel(THPBlocks.stove1,bmf("kitchen_stove_i"));
		itemModel(THPBlocks.stove2,bmf("kitchen_stove_ii"));
		itemModel(THPBlocks.stew_pot,bmf("cistern"));
	}
    public ModelFile bmf(String name) {
    	return new ModelFile.ExistingModelFile(new ResourceLocation(this.modid,"block/"+name),existingFileHelper);
    }
    public void simpleBlockItem(Block b, ModelFile model)
    {
        simpleBlockItem(b, new ConfiguredModel(model));
    }

    protected void simpleBlockItem(Block b, ConfiguredModel model)
    {
        simpleBlock(b, model);
        itemModel(b, model.model);
    }
    public void horizontalAxisBlock(Block block, ModelFile mf) {
        getVariantBuilder(block)
            .partialState().with(BlockStateProperties.HORIZONTAL_AXIS, Axis.Z)
                .modelForState().modelFile(mf).uvLock(true).addModel()
            .partialState().with(BlockStateProperties.HORIZONTAL_AXIS, Axis.X)
                .modelForState().modelFile(mf).uvLock(true).rotationY(90).addModel();
    }
    public MultiPartBlockStateBuilder horizontalMultipart(MultiPartBlockStateBuilder block,ModelFile mf){
    	return horizontalMultipart(block,mf,UnaryOperator.identity());
    }
    public MultiPartBlockStateBuilder horizontalMultipart(MultiPartBlockStateBuilder block,ModelFile mf,UnaryOperator<PartBuilder> act) {
    	for(Direction d:BlockStateProperties.HORIZONTAL_FACING.getAllowedValues())
    		block=act.apply(block.part().modelFile(mf)
            .rotationY(((int) d.getHorizontalAngle()) % 360)
            .uvLock(true).addModel().condition(BlockStateProperties.HORIZONTAL_FACING,d)).end();
    	return block;
    }
    protected void itemModel(Block block, ModelFile model)
    {
        itemModels().getBuilder(block.getRegistryName().getPath()).parent(model);
    }
}

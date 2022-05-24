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

package com.teammoeg.thermopolium.blocks;

import java.util.Random;
import java.util.function.BiFunction;
import com.teammoeg.thermopolium.Contents;
import com.teammoeg.thermopolium.Main;
import com.teammoeg.thermopolium.data.recipes.CountingTags;
import com.teammoeg.thermopolium.data.recipes.FoodValueRecipe;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.block.AbstractBlock.Properties;

public class KitchenStove extends Block {
	public final String name;
	protected int lightOpacity;
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final BooleanProperty ASH = BooleanProperty.create("ash");
	public static final IntegerProperty FUELED = IntegerProperty.create("fueled", 0, 2);
	private final RegistryObject<TileEntityType<KitchenStoveTileEntity>> te;

	public KitchenStove(String name, Properties blockProps, RegistryObject<TileEntityType<KitchenStoveTileEntity>> ste,
			BiFunction<Block, Item.Properties, Item> createItemBlock) {
		super(blockProps);
		this.name = name;
		lightOpacity = 15;
		te = ste;
		ResourceLocation registryName = createRegistryName();
		setRegistryName(registryName);

		Contents.registeredBlocks.add(this);
		Item item = createItemBlock.apply(this, new Item.Properties().tab(Main.itemGroup));
		if (item != null) {
			item.setRegistryName(registryName);
			Contents.registeredItems.add(item);
		}
	}
	@Override
	public VoxelShape getVisualShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
		return VoxelShapes.empty();
	}
	@Override
	@OnlyIn(Dist.CLIENT)
	public float getShadeBrightness(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return 1.0F;
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		ActionResultType p = super.use(state, worldIn, pos, player, handIn, hit);
		if (p.consumesAction())
			return p;
		KitchenStoveTileEntity tileEntity = (KitchenStoveTileEntity) worldIn.getBlockEntity(pos);
		/*for(Item i:ForgeRegistries.ITEMS) {
			if(CountingTags.tags.stream().anyMatch(i.getTags()::contains)&&!i.isFood()&&FoodValueRecipe.recipes.get(i)==null)
				System.out.println(i.getRegistryName());
		}*/
		if (handIn == Hand.MAIN_HAND) {
			if (tileEntity != null && !worldIn.isClientSide)
				NetworkHooks.openGui((ServerPlayerEntity) player, tileEntity, tileEntity.getBlockPos());
			return ActionResultType.SUCCESS;
		}
		return p;
	}

	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos bp, Random rand) {
		TileEntity te = worldIn.getBlockEntity(bp);
		if (stateIn.getValue(LIT)) {
			double d0 = bp.getX();
			double d1 = bp.getY();
			double d2 = bp.getZ();
			if (rand.nextDouble() < 0.2D) {
				worldIn.playLocalSound(d0, d1, d2, SoundEvents.FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F,
						false);
			}
			worldIn.addParticle(ParticleTypes.FLAME, d0 + rand.nextDouble(), bp.getY() + 1, d2 + rand.nextDouble(),
					0.0D, 0.0D, 0.0D);
			if (rand.nextDouble() < 0.5D) {
				worldIn.addParticle(ParticleTypes.SMOKE, d0 + .5, bp.getY() + 1, d2 + .5, rand.nextDouble() * .5 - .25,
						rand.nextDouble() * .125, rand.nextDouble() * .5 - .25);

			}
		}
	}

	public ResourceLocation createRegistryName() {
		return new ResourceLocation(Main.MODID, name);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return te.get().create();
	}

	@Override
	public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		TileEntity tileEntity = worldIn.getBlockEntity(pos);
		if (tileEntity instanceof KitchenStoveTileEntity && state.getBlock() != newState.getBlock()) {
			KitchenStoveTileEntity te = (KitchenStoveTileEntity) tileEntity;
			ItemStack is = te.getItem(0);
			if (!is.isEmpty())
				super.popResource(worldIn, pos, is);
		}
		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

	@Override
	protected void createBlockStateDefinition(net.minecraft.state.StateContainer.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING).add(LIT).add(FUELED).add(ASH);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.defaultBlockState().setValue(LIT, false).setValue(ASH, false).setValue(FUELED, 0).setValue(FACING,
				context.getHorizontalDirection().getOpposite());

	}

	static final VoxelShape shape = VoxelShapes.or(
			VoxelShapes.or(Block.box(0, 0, 0, 16, 14, 16), Block.box(0, 14, 0, 2, 16, 16)),
			VoxelShapes.or(Block.box(0, 14, 0, 16, 16, 2), VoxelShapes
					.or(Block.box(14, 14, 0, 16, 16, 16), Block.box(0, 14, 14, 16, 16, 16))));

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos,
			ISelectionContext context) {
		return shape;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return shape;
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}

}
